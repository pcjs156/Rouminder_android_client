package com.example.rouminder.firebase.exception;

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
