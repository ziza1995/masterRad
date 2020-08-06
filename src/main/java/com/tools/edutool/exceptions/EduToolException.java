package com.tools.edutool.exceptions;


public class EduToolException extends RuntimeException  {
    public EduToolException(String exMessage) {
        super(exMessage);
    }

    public EduToolException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }
}

