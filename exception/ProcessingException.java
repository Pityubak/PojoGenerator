package com.pityubak.pojogenerator.exception;

/**
 *
 * @author Pityubak
 */
public class ProcessingException extends RuntimeException{

    public ProcessingException(String message,Object... args) {
        super(String.format(message, args));
    }
    
}
