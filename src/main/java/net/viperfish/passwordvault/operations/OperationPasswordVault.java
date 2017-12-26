/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.operations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.generators.SCrypt;

import net.viperfish.passwordvault.core.ErrorHandler;
import net.viperfish.passwordvault.core.Operation;
import net.viperfish.passwordvault.core.PasswordDatabase;
import net.viperfish.passwordvault.core.PasswordEntry;
import net.viperfish.passwordvault.core.PasswordVault;
import net.viperfish.passwordvault.crypt.BlockCiphers;
import net.viperfish.passwordvault.crypt.BytesPair;
import net.viperfish.passwordvault.crypt.CryptUtils;
import net.viperfish.passwordvault.crypt.EncryptedPasswordEntryDatabase;
import net.viperfish.passwordvault.db.H2PasswordEntryDatabase;
import net.viperfish.passwordvault.errors.CompromisedDataException;
import net.viperfish.passwordvault.errors.CryptoUsageException;
import net.viperfish.passwordvault.errors.PersistenceException;

/**
 *
 * @author sdai
 */
public class OperationPasswordVault implements PasswordVault, AutoCloseable {

	private PasswordDatabase db;
	private String dbPath;
	private Path keyPath;
	private List<Operation<?>> history;
	private ExecutorService workerThread;
	private ErrorHandlingThreadFactory threadFactory;

	public OperationPasswordVault(String dbPath, Path keyFilePath) {
		this.dbPath = dbPath;
		this.keyPath = keyFilePath;
		history = new LinkedList<Operation<?>>();
		threadFactory = new ErrorHandlingThreadFactory();
		workerThread = Executors.newSingleThreadExecutor(threadFactory);
	}

	private byte[] deriveKeyEncryptingKey(String password, byte[] salt) {
		byte[] kek = SCrypt.generate(password.getBytes(StandardCharsets.UTF_8), salt, 1048576, 8, 1, 32);
		return kek;
	}

	public byte[] decryptMasterKey(String password)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException, PersistenceException {
		BytesPair keyAndSalt;
		try {
			keyAndSalt = BytesPair.fromBase64(new String(Files.readAllBytes(keyPath), StandardCharsets.UTF_8));
		} catch (IllegalArgumentException | IOException e) {
			throw new PersistenceException(e);
		}
		byte[] kek = deriveKeyEncryptingKey(password, keyAndSalt.getSecond());
		byte[] plainKey = CryptUtils.INSTANCE.ecbDecrypt(keyAndSalt.getFirst(), kek,
				BlockCiphers.getBlockCipherEngine("AES"));
		return plainKey;
	}

	private byte[] generateAndWriteMasterKey(String password) throws PersistenceException {
		SecureRandom rand = new SecureRandom();
		byte[] key = new byte[32];
		rand.nextBytes(key);
		byte[] salt = new byte[16];
		rand.nextBytes(salt);
		byte[] kek = deriveKeyEncryptingKey(password, salt);
		try {
			byte[] encrypteKey = CryptUtils.INSTANCE.ecbCrypt(key, kek, BlockCiphers.getBlockCipherEngine("AES"));
			BytesPair toFile = BytesPair.create(encrypteKey, salt);
			Files.write(keyPath, toFile.encodeBase64().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
					StandardOpenOption.WRITE);
			return key;
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
			// this should not happen if it is not a programming usage
			// mistake
			throw new CryptoUsageException(e);
		} catch (IOException e) {
			throw new PersistenceException(e);
		}
	}

	private void initDatabase(byte[] key) throws SQLException {
		H2PasswordEntryDatabase rawDB = new H2PasswordEntryDatabase();
		rawDB.connect("jdbc:h2:" + dbPath, "", "");
		db = new EncryptedPasswordEntryDatabase(rawDB, key);
	}

	@Override
	public boolean unlock(String password) throws PersistenceException {
		try {
			byte[] finalKeyParam;
			if (exists()) {
				try {
					finalKeyParam = decryptMasterKey(password);
				} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				throw new PersistenceException(keyPath.toString() + " does not exists");
			}
			initDatabase(finalKeyParam);
			return true;
		} catch (SQLException ex) {
			throw new PersistenceException(ex);
		}
	}

	@Override
	public void create(String password) throws PersistenceException {
		if (exists()) {
			throw new PersistenceException(keyPath.toString() + " exists");
		}
		try {
			initDatabase(generateAndWriteMasterKey(password));
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	@Override
	public boolean exists() {
		return keyPath.toFile().exists();
	}

	@Override
	public void addEntry(PasswordEntry e) {
		Operation<PasswordEntry> addOperation = new AddPasswordOperation(e, db);
		history.add(addOperation);
		workerThread.submit(addOperation);
	}

	@Override
	public void deleteEntry(int id) {
		DeletePasswordOperation delOperation = new DeletePasswordOperation(id, db);
		history.add(delOperation);
		workerThread.submit(delOperation);
	}

	@Override
	public void updateEntry(int id, PasswordEntry e) {
		EditPasswordOperation ops = new EditPasswordOperation(id, e, db);
		history.add(ops);
		workerThread.submit(ops);
	}

	@Override
	public PasswordEntry getEntry(int id) throws CompromisedDataException {
		GetPasswordOperation ops = new GetPasswordOperation(id, db);
		history.add(ops);
		return executeOperation(ops, PasswordEntry.NULL);
	}

	@Override
	public Collection<PasswordEntry> getAll() throws CompromisedDataException {
		GetAllPasswordOperation ops = new GetAllPasswordOperation(db);
		history.add(ops);
		return executeOperation(ops, new LinkedList<PasswordEntry>());
	}

	@Override
	public Collection<PasswordEntry> search(String keyword) throws CompromisedDataException {
		SearchPasswordOperation searchOps = new SearchPasswordOperation(keyword, db);
		history.add(searchOps);
		return executeOperation(searchOps, new LinkedList<PasswordEntry>());
	}

	@Override
	public void setErrorHandler(ErrorHandler handler) {
		this.threadFactory.setErrorHandler(handler);
	}

	@Override
	public void close() throws Exception {
		workerThread.shutdown();
		try {
			int count = 0;
			while (!workerThread.awaitTermination(200, TimeUnit.MILLISECONDS)) {
				if (count++ > 25) {
					workerThread.shutdownNow();
					break;
				}
			}
		} catch (InterruptedException e) {
			workerThread.shutdownNow();
		}
		db.close();
	}

	private void handleFutureException(ExecutionException e) throws CompromisedDataException {
		if (e.getCause() instanceof CompromisedDataException) {
			throw (CompromisedDataException) e.getCause();
		} else if (e.getCause() instanceof Exception) {
			threadFactory.getErrorHandler().handle((Exception) e.getCause());
		} else {
			throw new RuntimeException(e.getCause());
		}
	}

	private <R> R executeOperation(Operation<R> ops, R nullValue) throws CompromisedDataException {
		Future<R> future = workerThread.submit(ops);
		try {
			return future.get();
		} catch (InterruptedException e) {
			return nullValue;
		} catch (ExecutionException e) {
			handleFutureException(e);
			return nullValue;
		}
	}

}
