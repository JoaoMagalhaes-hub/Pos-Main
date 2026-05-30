package com.agenda.exception;

public class EmailJaExisteException extends RuntimeException {

    public EmailJaExisteException(String email) {
        super("Já existe um contato com o email: " + email);
    }
}
