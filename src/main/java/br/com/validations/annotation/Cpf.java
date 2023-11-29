package br.com.validations.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cpf {
    
    String message() default "Cpf inválido";
    String code() default "Cpf Annotation";
    
}
