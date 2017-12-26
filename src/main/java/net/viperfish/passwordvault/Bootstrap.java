/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.passwordvault;

import java.io.Console;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.viperfish.passwordvault.core.PasswordEntry;
import net.viperfish.passwordvault.core.PasswordType;
import net.viperfish.passwordvault.operations.OperationPasswordVault;

/**
 *
 * @author sdai
 */
public class Bootstrap {
	public static void main(String[] argv) {
		Path keyPath = Paths.get("key");
		String dbPath = "./passwords";
		DisplayErrorHandler handler = new DisplayErrorHandler();
		try (OperationPasswordVault vault = new OperationPasswordVault(dbPath, keyPath)) {
			vault.setErrorHandler(handler);
			Console systemConsole = System.console();
			if (!vault.exists()) {
				while (true) {
					String np = new String(systemConsole.readPassword("New Password:"));
					String repeated = new String(systemConsole.readPassword("Repeat:"));
					if (np.equals(repeated)) {
						vault.create(np);
						break;
					}
					System.out.println("Password does not match");
				}
			} else {
				String password = new String(systemConsole.readPassword("Password:"));
				while (!vault.unlock(password)) {
					systemConsole.printf("Wrong Password, try again\n");
					password = new String(systemConsole.readPassword("Password:"));
				}
			}
			while (true) {
				String command = systemConsole.readLine("PasswordVault:").trim();
				if (command.length() == 0) {
					continue;
				}
				if (command.equals("exit")) {
					break;
				}
				switch (command) {
				case "newPassword": {
					PasswordEntry toAdd = passwordEditer(PasswordEntry.NULL);
					vault.addEntry(toAdd);
					break;
				}
				case "listAll": {
					listPasswords(vault.getAll());
					break;
				}
                                case "get": {
                                        String userInput = System.console().readLine("ID:");
                                        while(userInput.trim().isEmpty()) {
                                            userInput = System.console().readLine("ID:");
                                        }
                                        PasswordEntry entry = vault.getEntry(Integer.parseInt(userInput));
                                        displayEntry(entry);
                                        break;
                                }
				default: {
					System.out.printf("The command: %s is not supported", command);
					break;
				}
				}
			}
		} catch (Exception e) {
			handler.handle(e);
		}
	}

	private static PasswordEntry passwordEditer(PasswordEntry e) {
		String name = System.console().readLine("Name[%s]:", e.getName());
		String type = System.console().readLine("Type[%s]:", e.getType().toString());
		String descryption = System.console().readLine("Description[%1.10s]:", e.getDescription());
		String URL = System.console().readLine("URL[%s]:", e.getUrl());
		String password = new String(System.console().readPassword("Password:"));
		PasswordEntry newEntry = new PasswordEntry();
                if(name.isEmpty()) {
                    name = e.getName();
                }
                if(type.isEmpty()) {
                    type = e.getType().toString();
                }
                if(descryption.isEmpty()) {
                    descryption = e.getDescription();
                }
                if(URL.isEmpty()) {
                    URL = e.getUrl();
                }
		newEntry.setName(name);
		newEntry.setDescription(descryption);
		newEntry.setUrl(URL);
		newEntry.setType(PasswordType.valueOf(type));
		newEntry.setPassword(password);
		return newEntry;
	}

	private static void listPasswords(Iterable<PasswordEntry> p) {
		System.console().printf("%3s | %20s | %20s | %20s | %20s | Password |\n", "ID", "Name", "Type", "Descryption",
				"URL");
		for (PasswordEntry entries : p) {
			System.console().printf("%3.3s | %20.20s | %20.20s | %20.20s | %20.20s | ******** |\n",
					Integer.toString(entries.getId()), entries.getName(), entries.getType().toString(),
					entries.getDescription(), entries.getUrl());
		}
	}
        
        private static void displayEntry(PasswordEntry e) {
            System.console().printf("%12s:%s", "Name", e.getName());
            System.console().printf("%12s:%s", "Type", e.getType().toString());
            System.console().printf("%12s:%s", "Description", e.getDescription());
            System.console().printf("%12s:%s", "URL", e.getUrl());
            System.console().printf("%12s:%s", "Password", e.getPassword());
        }

}
