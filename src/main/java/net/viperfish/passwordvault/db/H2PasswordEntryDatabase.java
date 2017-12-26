package net.viperfish.passwordvault.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.H2DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import net.viperfish.passwordvault.core.PasswordDatabase;
import net.viperfish.passwordvault.core.PasswordEntry;
import net.viperfish.passwordvault.errors.PersistenceException;

public class H2PasswordEntryDatabase implements PasswordDatabase {

	private Dao<PasswordEntry, Integer> passwordEntryDAO;
	private JdbcConnectionSource conSource;

	public H2PasswordEntryDatabase() {
		passwordEntryDAO = null;
	}

	public void connect(String jdbcUrl, String username, String password) throws SQLException {
		conSource = new JdbcConnectionSource(jdbcUrl, new H2DatabaseType());
		conSource.setUsername(username);
		conSource.setPassword(password);
		passwordEntryDAO = DaoManager.createDao(conSource, PasswordEntry.class);
	}

	@Override
	public PasswordEntry save(PasswordEntry p) throws PersistenceException {
		try {
			passwordEntryDAO.create(p);
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
		return p;
	}

	@Override
	public void delete(int id) throws PersistenceException {
		try {
			passwordEntryDAO.deleteById(id);
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	@Override
	public PasswordEntry get(int id) throws PersistenceException {
		PasswordEntry entry;
		try {
			entry = passwordEntryDAO.queryForId(id);
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
		if (entry == null) {
			entry = PasswordEntry.NULL;
		}
		return entry;
	}

	@Override
	public PasswordEntry update(int id, PasswordEntry update) throws PersistenceException {
		update.setId(id);
		try {
			passwordEntryDAO.createOrUpdate(update);
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
		return get(id);
	}

	@Override
	public Collection<PasswordEntry> getAll() throws PersistenceException {
		try {
			return passwordEntryDAO.queryForAll();
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}

	}

	@Override
	public Collection<PasswordEntry> find(String keyword) throws PersistenceException {
		try {
			QueryBuilder<PasswordEntry, Integer> queryBuilder = passwordEntryDAO.queryBuilder();
			queryBuilder.where().like("name", keyword).or().like("description", keyword).or().like("url", keyword).or()
					.like("type", keyword);
			PreparedQuery<PasswordEntry> preQ = queryBuilder.prepare();
			return passwordEntryDAO.query(preQ);
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	@Override
	public void close() throws IOException {
		System.out.println("Closing Connection");
		this.conSource.close();
	}

}
