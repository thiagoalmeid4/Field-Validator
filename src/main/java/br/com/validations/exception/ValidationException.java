package br.com.validations.exception;

import java.util.ArrayList;
import java.util.List;

import br.com.validations.Error;

public class ValidationException extends RuntimeException {
    
    private List<Error> validationMessages = new ArrayList<>();

    public List<Error> getErrors() {
        return validationMessages;
    }

    public ValidationException (List<Error> validationMessages) {
        this.validationMessages = validationMessages;
    }

    @Override
    public String getMessage() {
        String message = "Erro ao validar atributos";
        for(int i = 0 ;i < validationMessages.size(); i++) {
            message += "\n"+validationMessages.get(i).message();
        }
        return message;
    }
    
}
