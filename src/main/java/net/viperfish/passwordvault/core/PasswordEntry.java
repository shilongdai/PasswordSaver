/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault.core;

import java.util.Objects;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 * @author sdai
 */
@DatabaseTable(tableName = "PasswordEntry")
public final class PasswordEntry {

	public static PasswordEntry NULL = new PasswordEntry(-1, "None", "None", "None", PasswordType.SYSTEM, "", "");

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String description;
	@DatabaseField
	private String url;
	@DatabaseField
	private PasswordType type;
	@DatabaseField
	private String password;
	@DatabaseField
	private String iv;

	public PasswordEntry() {
		id = 0;
		name = "";
		description = "";
		url = "";
		type = PasswordType.GENERAL;
		password = "";
		iv = "";
	}

	public PasswordEntry(int id, String name, String description, String url, PasswordType type, String password,
			String iv) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.url = url;
		this.type = type;
		this.password = password;
		this.iv = iv;
	}

	public PasswordEntry(PasswordEntry src, int id) {
		this.id = id;
		this.name = src.name;
		this.description = src.description;
		this.url = src.url;
		this.type = src.type;
		this.password = src.password;
		this.iv = src.iv;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public PasswordType getType() {
		return type;
	}

	public String getPassword() {
		return password;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setType(PasswordType type) {
		this.type = type;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 41 * hash + this.id;
		hash = 41 * hash + Objects.hashCode(this.name);
		hash = 41 * hash + Objects.hashCode(this.description);
		hash = 41 * hash + Objects.hashCode(this.url);
		hash = 41 * hash + Objects.hashCode(this.type);
		hash = 41 * hash + Objects.hashCode(this.password);
		hash = 41 * hash + Objects.hashCode(this.iv);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PasswordEntry other = (PasswordEntry) obj;
		if (this.id != other.id) {
			return false;
		}
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		if (!Objects.equals(this.description, other.description)) {
			return false;
		}
		if (!Objects.equals(this.url, other.url)) {
			return false;
		}
		if (!Objects.equals(this.password, other.password)) {
			return false;
		}
		if (!Objects.equals(this.iv, other.iv)) {
			return false;
		}
		if (this.type != other.type) {
			return false;
		}
		return true;
	}

}
