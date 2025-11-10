package com.iliad.library.exception;

public class NotExistingReviewException extends Exception{

    public NotExistingReviewException() {
        super();
    }

    public NotExistingReviewException(String errorMessage) {
        super(errorMessage);
    }
}
