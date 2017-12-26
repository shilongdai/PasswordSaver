/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.viperfish.passwordvault.errors.PersistenceException;

/**
 *
 * @author sdai
 */
public class RAMPasswordDatabase implements PasswordDatabase {

	private Map<Integer, PasswordEntry> storage;
	private int currentID;

	public RAMPasswordDatabase() {
		storage = new HashMap<>();
		currentID = 0;
	}

	@Override
	public PasswordEntry save(PasswordEntry p) throws PersistenceException {
		PasswordEntry toSave = new PasswordEntry(p, currentID++);
		storage.put(toSave.getId(), toSave);
		return toSave;
	}

	@Override
	public void delete(int id) throws PersistenceException {
		storage.remove(id);
	}

	@Override
	public PasswordEntry get(int id) {
		PasswordEntry e = storage.get(id);
		if (e == null) {
			e = PasswordEntry.NULL;
		}
		return e;
	}

	@Override
	public PasswordEntry update(int id, PasswordEntry update) throws PersistenceException {
		delete(id);
		storage.put(id, update);
		return update;
	}

	@Override
	public Collection<PasswordEntry> getAll() {
		List<PasswordEntry> result = new LinkedList<>();
		for (Map.Entry<Integer, PasswordEntry> e : storage.entrySet()) {
			result.add(e.getValue());
		}
		return result;
	}

	@Override
	public Collection<PasswordEntry> find(String keyword) {
		List<PasswordEntry> filtered = new LinkedList<>();
		for (PasswordEntry p : this.getAll()) {
			if (p.getName().contains(keyword) || p.getDescription().contains(keyword)
					|| p.getType().toString().contains(keyword) || p.getUrl().contains(keyword)) {
				filtered.add(p);
			}
		}
		return filtered;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

}
