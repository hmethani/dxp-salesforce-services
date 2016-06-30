package com.xtivia.salesforce.singleton.exceptions;

public class FailedSearchException extends RuntimeException {

    private static final long serialVersionUID = -4431108450015623978L;

    public FailedSearchException(String message) {
        super(message);
    }

}
