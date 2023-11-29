package br.com.validations.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BrazilianState {
    
    String message() default "Estado inválido";
    String code() default "Brazilian State Annotation";
    boolean stateRegistration() default false;
    
}
