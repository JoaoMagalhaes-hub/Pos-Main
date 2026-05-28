package com.agenda.dto;

import com.agenda.model.Contato;
import com.agenda.model.TipoContato;
import java.time.LocalDateTime;

public record ContatoResponse(
        Long id,
        String nome,
        String telefone,
        String email,
        String endereco,
        int idade,
        TipoContato tipo,
        LocalDateTime dataCadastro,
        boolean ativo
) {
    public static ContatoResponse fromEntity(Contato contato) {
        return new ContatoResponse(
                contato.getId(),
                contato.getNome(),
                contato.getTelefone(),
                contato.getEmail(),
                contato.getEndereco(),
                contato.getIdade(),
                contato.getTipo(),
                contato.getDataCadastro(),
                contato.isAtivo()
        );
    }
}
