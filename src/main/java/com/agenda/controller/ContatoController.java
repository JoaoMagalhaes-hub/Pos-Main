package com.agenda.controller;

import com.agenda.dto.ContatoRequest;
import com.agenda.dto.ContatoResponse;
import com.agenda.service.ContatoService;
import com.agenda.validation.OnCreate;
import com.agenda.validation.OnUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Contatos", description = "Gerenciamento de contatos da agenda")
@RestController
@RequestMapping("/contatos")
public class ContatoController {

    private final ContatoService service;

    public ContatoController(ContatoService service) {
        this.service = service;
    }

    @Operation(summary = "Inclui um novo contato")
    @PostMapping("/incluir")
    public ResponseEntity<ContatoResponse> incluir(@Validated(OnCreate.class) @RequestBody ContatoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.incluir(request));
    }

    @Operation(summary = "Busca um contato pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<ContatoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Lista todos os contatos ativos")
    @GetMapping("/listar")
    public ResponseEntity<List<ContatoResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Pesquisa contatos por critério (nome, email, telefone, tipo ou id)")
    @GetMapping("/pesquisar")
    public ResponseEntity<List<ContatoResponse>> pesquisar(
            @RequestParam String tipoBusca,
            @RequestParam String valor) {
        return ResponseEntity.ok(service.pesquisar(tipoBusca, valor));
    }

    @Operation(summary = "Edita os dados de um contato existente")
    @PutMapping("/editar/{id}")
    public ResponseEntity<ContatoResponse> editar(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody ContatoRequest request) {
        return ResponseEntity.ok(service.editar(id, request));
    }

    @Operation(summary = "Desativa um contato (soft delete)")
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<ContatoResponse> excluir(@PathVariable Long id) {
        return ResponseEntity.ok(service.excluir(id));
    }

    @Operation(summary = "Reativa um contato desativado")
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<ContatoResponse> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.ativar(id));
    }
}
