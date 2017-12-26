/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.errors;

/**
 *
 * @author sdai
 */
public class PersistenceException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8933412637623824837L;

	public PersistenceException() {
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
    
}
