package net.viperfish.passwordvault;

import net.viperfish.passwordvault.core.ErrorHandler;

final class DisplayErrorHandler implements ErrorHandler {

	@Override
	public void handle(Exception error) {
		error.printStackTrace();
	}

}
