/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.crypt;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import net.viperfish.passwordvault.core.PasswordDatabase;
import net.viperfish.passwordvault.core.PasswordEntry;
import net.viperfish.passwordvault.errors.CompromisedDataException;
import net.viperfish.passwordvault.errors.CryptoUsageException;
import net.viperfish.passwordvault.errors.PersistenceException;

/**
 *
 * @author sdai
 */
public class EncryptedPasswordEntryDatabase implements PasswordDatabase {

	private PasswordDatabase db;
	private AEADBlockCipher cipher;
	private byte[] key;

	public EncryptedPasswordEntryDatabase(PasswordDatabase db, byte[] key) throws SQLException {
		this.db = db;
		cipher = BlockCiphers.useAEADMode(BlockCiphers.getBlockCipherEngine("AES"), "GCM");
		this.key = key;
	}

	private byte[] getAEADIntegrityBytes(PasswordEntry e) {
		ByteBuffer bf = ByteBuffer.allocate(e.getDescription().getBytes(StandardCharsets.UTF_8).length
				+ e.getName().getBytes(StandardCharsets.UTF_8).length
				+ e.getType().toString().getBytes(StandardCharsets.UTF_8).length
				+ e.getUrl().getBytes(StandardCharsets.UTF_8).length);
		bf.put(e.getDescription().getBytes(StandardCharsets.UTF_8)).put(e.getName().getBytes(StandardCharsets.UTF_8))
				.put(e.getType().toString().getBytes(StandardCharsets.UTF_8))
				.put(e.getUrl().getBytes(StandardCharsets.UTF_8));
		return bf.array();
	}

	private void encryptedPassword(PasswordEntry e)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		byte[] iv = CryptUtils.INSTANCE.generateNonce(BlockCiphers.getNounceSize("GCM"));
		cipher.init(true, new AEADParameters(new KeyParameter(key), 128, iv));

		byte[] encrypted = CryptUtils.INSTANCE.transformData(cipher, e.getPassword().getBytes(StandardCharsets.UTF_8),
				getAEADIntegrityBytes(e));
		e.setIv(Base64.encodeBase64URLSafeString(iv));
		e.setPassword(Base64.encodeBase64URLSafeString(encrypted));

	}

	private void decryptePassword(PasswordEntry e)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		byte[] iv = Base64.decodeBase64(e.getIv());
		cipher.init(false, new AEADParameters(new KeyParameter(key), 128, iv));

		byte[] decrypt = CryptUtils.INSTANCE.transformData(cipher, Base64.decodeBase64(e.getPassword()),
				getAEADIntegrityBytes(e));
		e.setPassword(new String(decrypt, StandardCharsets.UTF_8));
	}

	@Override
	public PasswordEntry save(PasswordEntry p) throws PersistenceException {
		try {
			PasswordEntry plainEntry = new PasswordEntry(p, 0);
			encryptedPassword(p);
			db.save(p);
			plainEntry.setId(p.getId());
			plainEntry.setIv(p.getIv());
			return plainEntry;
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
			throw new CryptoUsageException();
		}
	}

	@Override
	public void delete(int id) throws PersistenceException {
		db.delete(id);
	}

	@Override
	public PasswordEntry get(int id) throws PersistenceException {
		PasswordEntry e = PasswordEntry.NULL;
		try {
			e = db.get(id);
			if (e == PasswordEntry.NULL) {
				return e;
			}
			decryptePassword(e);
			return e;
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
			throw new CompromisedDataException(e);
		}
	}

	@Override
	public PasswordEntry update(int id, PasswordEntry update) throws PersistenceException {
		PasswordEntry plainEntry = new PasswordEntry(update, id);
		try {
			encryptedPassword(update);
			db.update(id, update);
			plainEntry.setIv(update.getIv());
			return plainEntry;
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
			throw new CryptoUsageException(ex);
		}
	}

	@Override
	public Collection<PasswordEntry> getAll() throws PersistenceException {
		PasswordEntry current = null;
		try {
			List<PasswordEntry> result = new LinkedList<>();
			for (PasswordEntry e : db.getAll()) {
				current = e;
				decryptePassword(e);
				result.add(e);
			}
			return result;
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
			throw new CompromisedDataException(current);
		}
	}

	@Override
	public Collection<PasswordEntry> find(String keyword) throws PersistenceException {
		PasswordEntry currentEntry = null;
		try {
			List<PasswordEntry> result = new LinkedList<PasswordEntry>();
			for (PasswordEntry e : db.find(keyword)) {
				currentEntry = e;
				decryptePassword(e);
				result.add(e);
			}
			return result;
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
			throw new CompromisedDataException(currentEntry);
		}
	}

	@Override
	public void close() throws Exception {
		db.close();
	}

}
