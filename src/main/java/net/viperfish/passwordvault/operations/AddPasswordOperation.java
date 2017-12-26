/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.operations;

import net.viperfish.passwordvault.core.Operation;
import net.viperfish.passwordvault.core.PasswordDatabase;
import net.viperfish.passwordvault.core.PasswordEntry;
import net.viperfish.passwordvault.errors.PersistenceException;

/**
 *
 * @author sdai
 */
final class AddPasswordOperation implements Operation<PasswordEntry> {

    private PasswordDatabase db;
    private PasswordEntry e;
    
    public AddPasswordOperation(PasswordEntry password, PasswordDatabase passwordDB) {
        this.db = passwordDB;
        this.e = password;
    }
    
    @Override
    public void undo() {
        try {
            db.delete(e.getId());
        } catch (PersistenceException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public PasswordEntry call() throws Exception {
        e = db.save(e);
        return e;
    }
    
}
