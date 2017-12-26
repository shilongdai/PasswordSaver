/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.operations;

import net.viperfish.passwordvault.core.PasswordEntry;
import net.viperfish.passwordvault.core.PasswordType;
import net.viperfish.passwordvault.core.RAMPasswordDatabase;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sdai
 */
public class EditPasswordOperationTest {

    private RAMPasswordDatabase passwordDB = new RAMPasswordDatabase();

    @Test
    public void testEditPassword() throws Exception {
        PasswordEntry e = new PasswordEntry(0, "test", "test description", "example.com", PasswordType.INTERNET, "password", "");
        e = passwordDB.save(e);

        PasswordEntry update = new PasswordEntry(e.getId(), "test", "edit desc", "example2.com", PasswordType.SYSTEM, "password2", "");
        EditPasswordOperation ops = new EditPasswordOperation(e.getId(), update, passwordDB);
        PasswordEntry edited = ops.call();

        Assert.assertEquals(update, edited);
        Assert.assertEquals(edited, passwordDB.get(e.getId()));

        ops.undo();

        Assert.assertEquals(e, passwordDB.get(e.getId()));
    }
}
