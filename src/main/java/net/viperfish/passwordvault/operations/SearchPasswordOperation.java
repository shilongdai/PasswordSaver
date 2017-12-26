/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.operations;

import java.util.Collection;
import net.viperfish.passwordvault.core.Operation;
import net.viperfish.passwordvault.core.PasswordDatabase;
import net.viperfish.passwordvault.core.PasswordEntry;

/**
 *
 * @author sdai
 */
final class SearchPasswordOperation implements Operation<Collection<PasswordEntry>> {

    private String keyword;
    private PasswordDatabase db;
    
    public SearchPasswordOperation(String keyword, PasswordDatabase db) {
        this.keyword = keyword;
        this.db = db;
    }
    
    @Override
    public void undo() {
    }

    @Override
    public Collection<PasswordEntry> call() throws Exception {
        return db.find(keyword);
    }
    
}
