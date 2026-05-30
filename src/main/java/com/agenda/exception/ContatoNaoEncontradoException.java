package com.agenda.exception;

public class ContatoNaoEncontradoException extends RuntimeException {

    public ContatoNaoEncontradoException(Long id) {
        super("Contato não encontrado: id=" + id);
    }
}
