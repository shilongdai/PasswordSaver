/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.core;

import java.util.Collection;

import net.viperfish.passwordvault.errors.CompromisedDataException;
import net.viperfish.passwordvault.errors.PersistenceException;

/**
 *
 * @author sdai
 */
public interface PasswordVault {
	public void addEntry(PasswordEntry e);

	public void deleteEntry(int id);

	public void updateEntry(int id, PasswordEntry e);

	public PasswordEntry getEntry(int id) throws CompromisedDataException;

	public Collection<PasswordEntry> getAll() throws CompromisedDataException;

	public Collection<PasswordEntry> search(String keyword) throws CompromisedDataException;

	public void setErrorHandler(ErrorHandler handler);

	public boolean unlock(String password) throws PersistenceException;

	public void create(String password) throws PersistenceException;

	public boolean exists();
}
