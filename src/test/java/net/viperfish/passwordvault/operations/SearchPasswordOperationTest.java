/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.operations;

import java.util.HashSet;
import java.util.Set;
import net.viperfish.passwordvault.core.PasswordEntry;
import net.viperfish.passwordvault.core.PasswordType;
import net.viperfish.passwordvault.core.RAMPasswordDatabase;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author sdai
 */
public class SearchPasswordOperationTest {

    private RAMPasswordDatabase passwordDB = new RAMPasswordDatabase();

    @Test
    public void testSearchPasswordOperation() throws Exception {
        PasswordEntry e = new PasswordEntry(0, "test", "test description", "example.com", PasswordType.INTERNET, "password", "");
        PasswordEntry e1 = new PasswordEntry(0, "abc", "asdg", "example1.com", PasswordType.INTERNET, "password1", "");
        PasswordEntry e2 = new PasswordEntry(0, "test2", "test description2", "example2.com", PasswordType.INTERNET, "password2", "");

        e = passwordDB.save(e);
        e1 = passwordDB.save(e1);
        e2 = passwordDB.save(e2);

        SearchPasswordOperation ops = new SearchPasswordOperation("test", passwordDB);
        Set<PasswordEntry> result = new HashSet<>(ops.call());

        Assert.assertEquals(true, result.contains(e));
        Assert.assertEquals(false, result.contains(e1));
        Assert.assertEquals(true, result.contains(e2));
    }
}
