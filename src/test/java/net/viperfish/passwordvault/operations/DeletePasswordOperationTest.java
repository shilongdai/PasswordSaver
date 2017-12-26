/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.operations;

import net.viperfish.passwordvault.core.PasswordDatabase;
import net.viperfish.passwordvault.core.PasswordEntry;
import net.viperfish.passwordvault.core.PasswordType;
import net.viperfish.passwordvault.core.RAMPasswordDatabase;
import net.viperfish.passwordvault.errors.PersistenceException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sdai
 */
public class DeletePasswordOperationTest {

    private PasswordDatabase passwordDB = new RAMPasswordDatabase();

    @Test
    public void testDeletePassword() throws PersistenceException, Exception {
        PasswordEntry e = new PasswordEntry(0, "test", "test description", "example.com", PasswordType.INTERNET, "password", "");
        e = passwordDB.save(e);

        DeletePasswordOperation ops = new DeletePasswordOperation(e.getId(), passwordDB);
        PasswordEntry deleted = ops.call();
        Assert.assertEquals(deleted, e);
        Assert.assertEquals(PasswordEntry.NULL, passwordDB.get(e.getId()));
    }
}
