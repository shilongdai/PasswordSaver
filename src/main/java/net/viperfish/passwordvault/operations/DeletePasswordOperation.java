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
final class DeletePasswordOperation implements Operation<PasswordEntry> {

    private PasswordDatabase db;
    private int id;
    private PasswordEntry deleted;
    
    public DeletePasswordOperation(int id, PasswordDatabase db) {
        this.id = id;
        this.db = db;
        this.deleted = PasswordEntry.NULL;
    }
    
    @Override
    public void undo() {
        try {
            db.save(deleted);
        } catch (PersistenceException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public PasswordEntry call() throws Exception {
        deleted = db.get(id);
        db.delete(id);
        return deleted;
    }
    
}
