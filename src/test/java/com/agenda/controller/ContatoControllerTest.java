package com.agenda.controller;

import com.agenda.dto.ContatoRequest;
import com.agenda.dto.ContatoResponse;
import com.agenda.exception.ContatoNaoEncontradoException;
import com.agenda.model.TipoContato;
import com.agenda.repository.ContatoRepository;
import com.agenda.service.ContatoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContatoController.class)
class ContatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContatoService service;

    // necessário para satisfazer o EmailUnicoValidator que depende do repositório
    @MockBean
    private ContatoRepository repository;

    @Test
    void incluir_deveRetornar201QuandoDadosValidos() throws Exception {
        ContatoRequest request = buildRequest("Maria", "11999998888", "maria@email.com", TipoContato.AMIGO);
        ContatoResponse response = buildResponse(1L, "Maria", "maria@email.com", TipoContato.AMIGO, true);
        when(repository.existsByEmail("maria@email.com")).thenReturn(false);
        when(service.incluir(any())).thenReturn(response);

        mockMvc.perform(post("/contatos/incluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void incluir_deveRetornar400QuandoNomeEstaAusente() throws Exception {
        ContatoRequest request = buildRequest(null, "11999998888", "maria@email.com", TipoContato.AMIGO);

        mockMvc.perform(post("/contatos/incluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagens").isArray());
    }

    @Test
    void buscarPorId_deveRetornar200QuandoContatoExiste() throws Exception {
        ContatoResponse response = buildResponse(1L, "Maria", "maria@email.com", TipoContato.AMIGO, true);
        when(service.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/contatos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    void buscarPorId_deveRetornar404QuandoContatoNaoExiste() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new ContatoNaoEncontradoException(99L));

        mockMvc.perform(get("/contatos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listar_deveRetornar200ComListaDeContatos() throws Exception {
        ContatoResponse response = buildResponse(1L, "João", "joao@email.com", TipoContato.AMIGO, true);
        when(service.listar()).thenReturn(List.of(response));

        mockMvc.perform(get("/contatos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João"))
                .andExpect(jsonPath("$[0].ativo").value(true));
    }

    @Test
    void editar_deveRetornar200QuandoContatoExiste() throws Exception {
        ContatoRequest request = new ContatoRequest();
        request.setNome("Ana Paula");
        ContatoResponse response = buildResponse(1L, "Ana Paula", "ana@email.com", TipoContato.AMIGO, true);
        when(service.editar(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/contatos/editar/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Paula"));
    }

    @Test
    void editar_deveRetornar404QuandoContatoNaoEncontrado() throws Exception {
        when(service.editar(eq(99L), any())).thenThrow(new ContatoNaoEncontradoException(99L));

        mockMvc.perform(put("/contatos/editar/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ContatoRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagens[0]").value("Contato não encontrado: id=99"));
    }

    @Test
    void excluir_deveRetornar200ComContatoDesativado() throws Exception {
        ContatoResponse response = buildResponse(1L, "Carlos", "carlos@email.com", TipoContato.AMIGO, false);
        when(service.excluir(1L)).thenReturn(response);

        mockMvc.perform(delete("/contatos/excluir/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    void ativar_deveRetornar200ComContatoAtivado() throws Exception {
        ContatoResponse response = buildResponse(1L, "Pedro", "pedro@email.com", TipoContato.TRABALHO, true);
        when(service.ativar(1L)).thenReturn(response);

        mockMvc.perform(patch("/contatos/1/ativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));
    }

    private ContatoRequest buildRequest(String nome, String telefone, String email, TipoContato tipo) {
        ContatoRequest request = new ContatoRequest();
        request.setNome(nome);
        request.setTelefone(telefone);
        request.setEmail(email);
        request.setTipo(tipo);
        return request;
    }

    private ContatoResponse buildResponse(Long id, String nome, String email, TipoContato tipo, boolean ativo) {
        return new ContatoResponse(id, nome, "11999998888", email, null, 0, tipo, LocalDateTime.now(), ativo);
    }
}
