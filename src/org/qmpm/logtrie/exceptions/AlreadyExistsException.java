package org.qmpm.logtrie.exceptions;

public class AlreadyExistsException extends Exception {

	private static final long serialVersionUID = -7192679793557613597L;

	public AlreadyExistsException(String description) {
		super(description);
	}
}
