package com.agenda.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroValidacaoResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> mensagens = Stream.concat(
                ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage),
                ex.getBindingResult().getGlobalErrors().stream().map(ObjectError::getDefaultMessage)
        ).toList();
        log.warn("Erros de validação: {}", mensagens);
        return ResponseEntity.badRequest().body(ErroValidacaoResponse.badRequest(mensagens));
    }

    @ExceptionHandler(ContatoNaoEncontradoException.class)
    public ResponseEntity<ErroValidacaoResponse> handleContatoNaoEncontrado(ContatoNaoEncontradoException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErroValidacaoResponse.of(HttpStatus.NOT_FOUND.value(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(EmailJaExisteException.class)
    public ResponseEntity<ErroValidacaoResponse> handleEmailJaExiste(EmailJaExisteException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(ErroValidacaoResponse.badRequest(List.of(ex.getMessage())));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroValidacaoResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(ErroValidacaoResponse.badRequest(List.of(ex.getMessage())));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroValidacaoResponse> handleIllegalState(IllegalStateException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.badRequest().body(ErroValidacaoResponse.badRequest(List.of(ex.getMessage())));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroValidacaoResponse> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Requisição inválida: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                ErroValidacaoResponse.badRequest(List.of("Valor inválido no corpo da requisição. Verifique os tipos aceitos.")));
    }
}
