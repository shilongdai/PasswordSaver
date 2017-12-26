/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.crypt;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.SQLException;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.Assert;
import org.junit.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.viperfish.passwordvault.core.PasswordEntry;
import net.viperfish.passwordvault.core.PasswordType;
import net.viperfish.passwordvault.db.H2PasswordEntryDatabase;

/**
 *
 * @author sdai
 */
public class EncryptedH2PasswordDatabaseTest {

	private JdbcConnectionSource connSource;
	private SecureRandom rand;
	private AEADBlockCipher cipher;

	public EncryptedH2PasswordDatabaseTest() throws SQLException {
		connSource = new JdbcConnectionSource("jdbc:h2:mem:passwd");
		cipher = BlockCiphers.useAEADMode(BlockCiphers.getBlockCipherEngine("AES"), "GCM");
		TableUtils.createTable(connSource, PasswordEntry.class);
		rand = new SecureRandom();
	}

	@Test
	public void testAddEncryption() throws Exception {
		byte[] key = new byte[32];
		rand.nextBytes(key);
		H2PasswordEntryDatabase h2DB = new H2PasswordEntryDatabase();
		h2DB.connect("jdbc:h2:mem:passwd", "", "");
		EncryptedPasswordEntryDatabase db = new EncryptedPasswordEntryDatabase(h2DB, key);
		PasswordEntry e = new PasswordEntry(0, "test", "test description", "example.com", PasswordType.INTERNET,
				"password", "");
		ByteBuffer bf = ByteBuffer.allocate(e.getDescription().getBytes(StandardCharsets.UTF_8).length
				+ e.getName().getBytes(StandardCharsets.UTF_8).length
				+ e.getType().toString().getBytes(StandardCharsets.UTF_8).length
				+ e.getUrl().getBytes(StandardCharsets.UTF_8).length);
		bf.put(e.getDescription().getBytes(StandardCharsets.UTF_8)).put(e.getName().getBytes(StandardCharsets.UTF_8))
				.put(e.getType().toString().getBytes(StandardCharsets.UTF_8))
				.put(e.getUrl().getBytes(StandardCharsets.UTF_8));
		e = db.save(e);
		byte[] iv = Base64.decodeBase64(e.getIv());
		cipher.init(true, new AEADParameters(new KeyParameter(key), 128, iv));
		byte[] encrypted = CryptUtils.INSTANCE.transformData(cipher, "password".getBytes(StandardCharsets.UTF_8),
				bf.array());
		Assert.assertEquals(Base64.encodeBase64URLSafeString(encrypted), h2DB.get(e.getId()).getPassword());
		Assert.assertEquals("password", db.get(e.getId()).getPassword());
		db.close();
	}

}
