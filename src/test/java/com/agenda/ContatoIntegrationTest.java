package com.agenda;

import com.agenda.dto.ContatoRequest;
import com.agenda.model.TipoContato;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContatoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void incluir_devePersistirERetornarContatoComId() throws Exception {
        ContatoRequest request = buildRequest("João", "11999998888", "joao@email.com", TipoContato.AMIGO);

        mockMvc.perform(post("/contatos/incluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.ativo").value(true))
                .andExpect(jsonPath("$.dataCadastro").isNotEmpty());
    }

    @Test
    void listar_deveRetornarApenasContatosAtivos() throws Exception {
        ContatoRequest request = buildRequest("Ana", "11988887777", "ana@email.com", TipoContato.TRABALHO);

        mockMvc.perform(post("/contatos/incluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/contatos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Ana"))
                .andExpect(jsonPath("$[0].ativo").value(true));
    }

    @Test
    void buscarPorId_deveRetornarContatoIncluidoAnteriormente() throws Exception {
        ContatoRequest request = buildRequest("Carlos", "11977776666", "carlos@email.com", TipoContato.AMIGO);

        String json = mockMvc.perform(post("/contatos/incluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(get("/contatos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos"));
    }

    @Test
    void buscarPorId_deveRetornar404ParaIdInexistente() throws Exception {
        mockMvc.perform(get("/contatos/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagens[0]").value("Contato não encontrado: id=9999"));
    }

    @Test
    void excluir_deveDesativarContatoERemoveLoDaListagem() throws Exception {
        ContatoRequest request = buildRequest("Pedro", "11966665555", "pedro@email.com", TipoContato.AMIGO);

        String json = mockMvc.perform(post("/contatos/incluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(delete("/contatos/excluir/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(false));

        mockMvc.perform(get("/contatos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").isEmpty());
    }

    @Test
    void ativar_deveReativarContatoDesativadoEApareceNaListagem() throws Exception {
        ContatoRequest request = buildRequest("Lucia", "11955554444", "lucia@email.com", TipoContato.TRABALHO);

        String json = mockMvc.perform(post("/contatos/incluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(delete("/contatos/excluir/" + id))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/contatos/" + id + "/ativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").value(true));

        mockMvc.perform(get("/contatos/listar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")].nome").value("Lucia"));
    }

    @Test
    void excluir_deveRetornar400ParaContatoDoTipoFamilia() throws Exception {
        ContatoRequest request = buildRequest("Mãe", "11944443333", "mae@email.com", TipoContato.FAMILIA);

        String json = mockMvc.perform(post("/contatos/incluir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(json).get("id").asLong();

        mockMvc.perform(delete("/contatos/excluir/" + id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagens[0]").value("Contatos do tipo FAMILIA não podem ser desativados"));
    }

    private ContatoRequest buildRequest(String nome, String telefone, String email, TipoContato tipo) {
        ContatoRequest request = new ContatoRequest();
        request.setNome(nome);
        request.setTelefone(telefone);
        request.setEmail(email);
        request.setTipo(tipo);
        return request;
    }
}
