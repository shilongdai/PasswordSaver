/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.core;

import java.util.Collection;

import net.viperfish.passwordvault.errors.PersistenceException;

/**
 *
 * @author sdai
 */
public interface PasswordDatabase extends AutoCloseable {
	public PasswordEntry save(PasswordEntry p) throws PersistenceException;

	public void delete(int id) throws PersistenceException;

	public PasswordEntry get(int id) throws PersistenceException;

	public PasswordEntry update(int id, PasswordEntry update) throws PersistenceException;

	public Collection<PasswordEntry> getAll() throws PersistenceException;

	public Collection<PasswordEntry> find(String keyword) throws PersistenceException;
}
