/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.operations;

import net.viperfish.passwordvault.core.Operation;
import net.viperfish.passwordvault.core.PasswordDatabase;
import net.viperfish.passwordvault.core.PasswordEntry;

/**
 *
 * @author sdai
 */
final class GetPasswordOperation implements Operation<PasswordEntry> {

    private int id;
    private PasswordDatabase db;
    
    public GetPasswordOperation(int id, PasswordDatabase db) {
        this.id = id;
        this.db = db;
    }
    
    @Override
    public void undo() {

    }

    @Override
    public PasswordEntry call() throws Exception {
        return db.get(id);
    }
    
}
