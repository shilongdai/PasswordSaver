/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.errors;

import net.viperfish.passwordvault.core.PasswordEntry;

/**
 *
 * @author sdai
 */
public class CompromisedDataException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7255632871663231599L;
	private PasswordEntry e;
    
    public CompromisedDataException(PasswordEntry e) {
        super("Compromised entry");
        this.e = e;
    }
    
    public PasswordEntry getEntry() {
        return e;
    }
    
}
