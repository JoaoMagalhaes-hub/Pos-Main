package com.agenda.validation;

import com.agenda.dto.ContatoRequest;
import com.agenda.repository.ContatoRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class EmailUnicoValidator implements ConstraintValidator<EmailUnico, ContatoRequest> {

    private final ContatoRepository repository;

    public EmailUnicoValidator(ContatoRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean isValid(ContatoRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()) {
            return true;
        }
        return !repository.existsByEmail(request.getEmail());
    }
}
