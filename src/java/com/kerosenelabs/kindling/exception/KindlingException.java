package com.kerosenelabs.kindling.exception;

public class KindlingException extends Exception {
    public KindlingException(Throwable e) {
        super(e);
    }

    public KindlingException(String message) {
        super(message);
    }
}
