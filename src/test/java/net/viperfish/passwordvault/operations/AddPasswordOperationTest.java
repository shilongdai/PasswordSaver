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
public class AddPasswordOperationTest {

    private RAMPasswordDatabase ramDatabase = new RAMPasswordDatabase();

    @Test
    public void testAddPassword() throws Exception {
        PasswordEntry e = new PasswordEntry(0, "test", "test description", "example.com", PasswordType.INTERNET, "password", "");
        AddPasswordOperation ops = new AddPasswordOperation(e, ramDatabase);
        PasswordEntry opsResult = ops.call();
        PasswordEntry inDb = ramDatabase.get(opsResult.getId());

        Assert.assertEquals(opsResult, inDb);
        Assert.assertEquals(opsResult, new PasswordEntry(e, inDb.getId()));

        ops.undo();
        Assert.assertEquals(PasswordEntry.NULL, ramDatabase.get(inDb.getId()));
    }
}
