package com.agenda.dto;

import com.agenda.model.TipoContato;
import com.agenda.validation.EmailUnico;
import com.agenda.validation.OnCreate;
import com.agenda.validation.OnUpdate;
import jakarta.validation.constraints.*;

@EmailUnico(groups = OnCreate.class)
public class ContatoRequest {

    @NotBlank(message = "nome é obrigatório", groups = OnCreate.class)
    @Size(min = 3, message = "nome deve ter pelo menos 3 caracteres", groups = {OnCreate.class, OnUpdate.class})
    private String nome;

    @NotBlank(message = "telefone é obrigatório", groups = OnCreate.class)
    @Pattern(regexp = "\\d{10,11}", message = "telefone inválido: use 10 ou 11 dígitos numéricos", groups = {OnCreate.class, OnUpdate.class})
    private String telefone;

    @NotBlank(message = "email é obrigatório", groups = OnCreate.class)
    @Email(message = "email inválido", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    private String endereco;

    @Min(value = 0, message = "idade inválida", groups = {OnCreate.class, OnUpdate.class})
    @Max(value = 150, message = "idade inválida", groups = {OnCreate.class, OnUpdate.class})
    private Integer idade;

    @NotNull(message = "tipo é obrigatório", groups = OnCreate.class)
    private TipoContato tipo;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public TipoContato getTipo() { return tipo; }
    public void setTipo(TipoContato tipo) { this.tipo = tipo; }
}
