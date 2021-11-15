package com.example.rouminder.firebase.model;

public class FBException extends java.lang.Exception {
    public FBException() {
        throw new RuntimeException("Stub!");
    }

    public FBException(String message) {
        throw new RuntimeException("Stub!");
    }

    public FBException(String message, Throwable cause) {
        throw new RuntimeException("Stub!");
    }

    public FBException(Throwable cause) {
        throw new RuntimeException("Stub!");
    }

    protected FBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        throw new RuntimeException("Stub!");
    }
}

public class ModelDoesNotExists extends FBException {
    public ModelDoesNotExists() {
    }

    public ModelDoesNotExists(String message) {
        super(message);
    }

    public ModelDoesNotExists(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelDoesNotExists(Throwable cause) {
        super(cause);
    }

    public ModelDoesNotExists(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
