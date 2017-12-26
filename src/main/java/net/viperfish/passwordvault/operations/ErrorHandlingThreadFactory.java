package net.viperfish.passwordvault.operations;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

import net.viperfish.passwordvault.core.ErrorHandler;

final class ErrorHandlingThreadFactory implements ThreadFactory {

	static class ErrorHandlerImpl implements ErrorHandler {

		@Override
		public void handle(Exception error) {
		}

	}

	private ErrorHandler handler;
	private int current;

	public ErrorHandlingThreadFactory() {
		handler = new ErrorHandlerImpl();
		current = 0;
	}

	public void setErrorHandler(ErrorHandler eh) {
		this.handler = eh;
	}

	public ErrorHandler getErrorHandler() {
		return handler;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r);
		t.setName("worker-" + current++);
		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				if (e instanceof Exception) {
					handler.handle((Exception) e);
				}
				e.printStackTrace();
			}
		});
		return t;
	}

}
