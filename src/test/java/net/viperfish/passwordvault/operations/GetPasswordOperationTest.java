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
public class GetPasswordOperationTest {

    private RAMPasswordDatabase passwordDB = new RAMPasswordDatabase();

    @Test
    public void testGetPasswordOperation() throws Exception {
        PasswordEntry e = new PasswordEntry(0, "test", "test description", "example.com", PasswordType.INTERNET, "password", "");
        e = passwordDB.save(e);

        GetPasswordOperation ops = new GetPasswordOperation(e.getId(), passwordDB);
        PasswordEntry result = ops.call();

        Assert.assertEquals(e, result);
    }
}
