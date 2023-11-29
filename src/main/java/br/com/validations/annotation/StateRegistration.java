package br.com.validations.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StateRegistration {
    
    String message() default "Inscricão estadual inválida";
    String code() default "State Registration Annotation";
    String uf() default "SP";
    
}
