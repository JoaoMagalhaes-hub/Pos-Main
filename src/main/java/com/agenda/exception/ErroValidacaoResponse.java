package com.agenda.exception;

import java.time.Instant;
import java.util.List;

public record ErroValidacaoResponse(Instant timestamp, int status, List<String> mensagens) {

    public static ErroValidacaoResponse of(int status, List<String> mensagens) {
        return new ErroValidacaoResponse(Instant.now(), status, List.copyOf(mensagens));
    }

    public static ErroValidacaoResponse badRequest(List<String> mensagens) {
        return of(400, mensagens);
    }
}
