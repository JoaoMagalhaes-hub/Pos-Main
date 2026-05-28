package com.agenda.service;

import com.agenda.dto.ContatoRequest;
import com.agenda.dto.ContatoResponse;
import com.agenda.exception.ContatoNaoEncontradoException;
import com.agenda.exception.EmailJaExisteException;
import com.agenda.model.Contato;
import com.agenda.model.TipoContato;
import com.agenda.repository.ContatoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContatoService {

    private static final Logger log = LoggerFactory.getLogger(ContatoService.class);

    private final ContatoRepository repository;

    public ContatoService(ContatoRepository repository) {
        this.repository = repository;
    }

    public ContatoResponse incluir(ContatoRequest request) {
        Contato contato = toEntity(request);
        contato.setDataCadastro(LocalDateTime.now());
        contato.setAtivo(true);
        Contato salvo = repository.save(contato);
        log.info("Contato incluído: id={}, nome={}", salvo.getId(), salvo.getNome());
        return ContatoResponse.fromEntity(salvo);
    }

    public ContatoResponse buscarPorId(Long id) {
        return repository.findById(id)
                .map(ContatoResponse::fromEntity)
                .orElseThrow(() -> new ContatoNaoEncontradoException(id));
    }

    public List<ContatoResponse> listar() {
        return repository.findByAtivoTrue().stream()
                .map(ContatoResponse::fromEntity)
                .toList();
    }

    public List<ContatoResponse> pesquisar(String tipoBusca, String valor) {
        List<Contato> resultado = switch (tipoBusca.toLowerCase()) {
            case "nome" -> repository.findByNomeContainingIgnoreCase(valor);
            case "email" -> repository.findByEmailContainingIgnoreCase(valor);
            case "telefone" -> repository.findByTelefoneContaining(valor);
            case "tipo" -> {
                try {
                    yield repository.findByTipo(TipoContato.valueOf(valor.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Tipo inválido: " + valor + ". Use: FAMILIA, AMIGO, TRABALHO ou OUTRO");
                }
            }
            case "id" -> {
                try {
                    yield repository.findById(Long.parseLong(valor)).map(List::of).orElse(List.of());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("ID inválido: " + valor);
                }
            }
            default -> throw new IllegalArgumentException(
                    "Tipo de busca inválido: " + tipoBusca + ". Use: nome, email, telefone, tipo ou id");
        };
        return resultado.stream().map(ContatoResponse::fromEntity).toList();
    }

    public ContatoResponse editar(Long id, ContatoRequest request) {
        Contato contato = repository.findById(id)
                .orElseThrow(() -> new ContatoNaoEncontradoException(id));

        if (request.getNome() != null && !request.getNome().isBlank()) {
            contato.setNome(request.getNome());
        }
        if (request.getTelefone() != null && !request.getTelefone().isBlank()) {
            contato.setTelefone(request.getTelefone());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (repository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new EmailJaExisteException(request.getEmail());
            }
            contato.setEmail(request.getEmail());
        }
        if (request.getEndereco() != null && !request.getEndereco().isBlank()) {
            contato.setEndereco(request.getEndereco());
        }
        if (request.getIdade() != null && request.getIdade() >= 0) {
            contato.setIdade(request.getIdade());
        }
        if (request.getTipo() != null) {
            contato.setTipo(request.getTipo());
        }

        Contato salvo = repository.save(contato);
        log.info("Contato editado: id={}", salvo.getId());
        return ContatoResponse.fromEntity(salvo);
    }

    public ContatoResponse excluir(Long id) {
        Contato contato = repository.findById(id)
                .orElseThrow(() -> new ContatoNaoEncontradoException(id));

        if (contato.getTipo() == TipoContato.FAMILIA) {
            throw new IllegalStateException("Contatos do tipo FAMILIA não podem ser desativados");
        }

        contato.setAtivo(false);
        Contato salvo = repository.save(contato);
        log.info("Contato desativado: id={}, nome={}", id, contato.getNome());
        return ContatoResponse.fromEntity(salvo);
    }

    public ContatoResponse ativar(Long id) {
        Contato contato = repository.findById(id)
                .orElseThrow(() -> new ContatoNaoEncontradoException(id));

        contato.setAtivo(true);
        Contato salvo = repository.save(contato);
        log.info("Contato reativado: id={}, nome={}", id, contato.getNome());
        return ContatoResponse.fromEntity(salvo);
    }

    private Contato toEntity(ContatoRequest request) {
        Contato contato = new Contato();
        contato.setNome(request.getNome());
        contato.setTelefone(request.getTelefone());
        contato.setEmail(request.getEmail());
        contato.setEndereco(request.getEndereco());
        contato.setIdade(request.getIdade() != null ? request.getIdade() : 0);
        contato.setTipo(request.getTipo());
        return contato;
    }
}
