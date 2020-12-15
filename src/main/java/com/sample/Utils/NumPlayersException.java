package com.sample.Utils;

public class NumPlayersException extends Throwable {
	
	private static final long serialVersionUID = 82675296306610059L;
	
	public NumPlayersException() {
        super();
    }

    public NumPlayersException(String message) {
        super(message);
    }

    public NumPlayersException(String message, Throwable cause) {
        super(message, cause);
    }

    public NumPlayersException(Throwable cause) {
        super(cause);
    }

    protected NumPlayersException(String message, Throwable cause, boolean enableSuppression, boolean     writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
