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
final class EditPasswordOperation implements Operation<PasswordEntry> {

    private PasswordDatabase db;
    private int id;
    private PasswordEntry toEdit;
    private PasswordEntry original;

    public EditPasswordOperation(int id, PasswordEntry toEdit, PasswordDatabase db) {
        this.db = db;
        this.id = id;
        this.toEdit = toEdit;
        this.original = PasswordEntry.NULL;
    }

    @Override
    public void undo() {
        try {
            db.update(id, original);
        } catch (PersistenceException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public PasswordEntry call() throws Exception {
        original = db.get(id);
        if (original == PasswordEntry.NULL) {
            throw new IllegalArgumentException("Password Entry " + id + " Does Not Exist");
        }
        return db.update(id, toEdit);
    }

}
