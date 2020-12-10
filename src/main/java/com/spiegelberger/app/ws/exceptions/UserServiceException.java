package com.spiegelberger.app.ws.exceptions;

public class UserServiceException extends RuntimeException{

	private static final long serialVersionUID = -2154308554923791348L;

	public UserServiceException(String message) {
		super(message);
	}
}
