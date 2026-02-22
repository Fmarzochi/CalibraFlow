package com.calibraflow.api.infrastructure.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String cpf = value.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int sum1 = 0;
            for (int i = 0; i < 9; i++) {
                sum1 += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            }
            int digito1 = 11 - (sum1 % 11);
            if (digito1 > 9) digito1 = 0;

            int sum2 = 0;
            for (int i = 0; i < 10; i++) {
                sum2 += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            }
            int digito2 = 11 - (sum2 % 11);
            if (digito2 > 9) digito2 = 0;

            return Character.getNumericValue(cpf.charAt(9)) == digito1 &&
                    Character.getNumericValue(cpf.charAt(10)) == digito2;

        } catch (Exception e) {
            return false;
        }
    }
}