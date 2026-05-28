package com.agenda.repository;

import com.agenda.model.Contato;
import com.agenda.model.TipoContato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Contato> findByNomeContainingIgnoreCase(String nome);

    List<Contato> findByEmailContainingIgnoreCase(String email);

    List<Contato> findByTelefoneContaining(String telefone);

    List<Contato> findByTipo(TipoContato tipo);

    List<Contato> findByAtivoTrue();
}
