package com.agenda.service;

import com.agenda.dto.ContatoRequest;
import com.agenda.dto.ContatoResponse;
import com.agenda.exception.ContatoNaoEncontradoException;
import com.agenda.exception.EmailJaExisteException;
import com.agenda.model.Contato;
import com.agenda.model.TipoContato;
import com.agenda.repository.ContatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContatoServiceTest {

    @Mock
    private ContatoRepository repository;

    @InjectMocks
    private ContatoService service;

    @Test
    void incluir_deveSalvarContatoComAtivoTrueEDataCadastroPreenchida() {
        ContatoRequest request = buildRequest("Maria", "11999998888", "maria@email.com", TipoContato.AMIGO);
        Contato salvo = buildContato(1L, "Maria", "maria@email.com", TipoContato.AMIGO, true);
        when(repository.save(any())).thenReturn(salvo);

        ContatoResponse response = service.incluir(request);

        assertThat(response.nome()).isEqualTo("Maria");
        assertThat(response.ativo()).isTrue();
        verify(repository).save(argThat(c -> c.isAtivo() && c.getDataCadastro() != null));
    }

    @Test
    void buscarPorId_deveRetornarContatoQuandoEncontrado() {
        Contato contato = buildContato(1L, "Maria", "maria@email.com", TipoContato.AMIGO, true);
        when(repository.findById(1L)).thenReturn(Optional.of(contato));

        ContatoResponse response = service.buscarPorId(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Maria");
    }

    @Test
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ContatoNaoEncontradoException.class);
    }

    @Test
    void listar_deveRetornarApenasContatosAtivos() {
        Contato ativo = buildContato(1L, "João", "joao@email.com", TipoContato.AMIGO, true);
        when(repository.findByAtivoTrue()).thenReturn(List.of(ativo));

        List<ContatoResponse> resultado = service.listar();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("João");
        verify(repository).findByAtivoTrue();
    }

    @Test
    void editar_deveAtualizarApenasOsCamposInformados() {
        Contato existente = buildContato(1L, "Ana", "ana@email.com", TipoContato.AMIGO, true);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByEmailAndIdNot(any(), any())).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ContatoRequest request = new ContatoRequest();
        request.setNome("Ana Paula");

        ContatoResponse response = service.editar(1L, request);

        assertThat(response.nome()).isEqualTo("Ana Paula");
        assertThat(response.email()).isEqualTo("ana@email.com");
    }

    @Test
    void editar_deveLancarExcecaoQuandoContatoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.editar(99L, new ContatoRequest()))
                .isInstanceOf(ContatoNaoEncontradoException.class);
    }

    @Test
    void editar_deveLancarExcecaoQuandoEmailJaExisteEmOutroContato() {
        Contato existente = buildContato(1L, "Ana", "ana@email.com", TipoContato.AMIGO, true);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByEmailAndIdNot("outro@email.com", 1L)).thenReturn(true);

        ContatoRequest request = new ContatoRequest();
        request.setEmail("outro@email.com");

        assertThatThrownBy(() -> service.editar(1L, request))
                .isInstanceOf(EmailJaExisteException.class);
    }

    @Test
    void excluir_deveDesativarContatoEmVezDeExcluirFisicamente() {
        Contato contato = buildContato(1L, "Carlos", "carlos@email.com", TipoContato.AMIGO, true);
        when(repository.findById(1L)).thenReturn(Optional.of(contato));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ContatoResponse response = service.excluir(1L);

        assertThat(response.ativo()).isFalse();
        verify(repository).save(argThat(c -> !c.isAtivo()));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void excluir_deveLancarExcecaoParaContatoDoTipoFamilia() {
        Contato familia = buildContato(1L, "Pai", "pai@email.com", TipoContato.FAMILIA, true);
        when(repository.findById(1L)).thenReturn(Optional.of(familia));

        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("FAMILIA");
    }

    @Test
    void excluir_deveLancarExcecaoQuandoContatoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
                .isInstanceOf(ContatoNaoEncontradoException.class);
    }

    @Test
    void ativar_deveReativarContatoInativo() {
        Contato inativo = buildContato(1L, "Pedro", "pedro@email.com", TipoContato.TRABALHO, false);
        when(repository.findById(1L)).thenReturn(Optional.of(inativo));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ContatoResponse response = service.ativar(1L);

        assertThat(response.ativo()).isTrue();
        verify(repository).save(argThat(Contato::isAtivo));
    }

    @Test
    void ativar_deveLancarExcecaoQuandoContatoNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ativar(99L))
                .isInstanceOf(ContatoNaoEncontradoException.class);
    }

    private ContatoRequest buildRequest(String nome, String telefone, String email, TipoContato tipo) {
        ContatoRequest request = new ContatoRequest();
        request.setNome(nome);
        request.setTelefone(telefone);
        request.setEmail(email);
        request.setTipo(tipo);
        return request;
    }

    private Contato buildContato(Long id, String nome, String email, TipoContato tipo, boolean ativo) {
        Contato contato = new Contato();
        contato.setId(id);
        contato.setNome(nome);
        contato.setEmail(email);
        contato.setTipo(tipo);
        contato.setAtivo(ativo);
        return contato;
    }
}
