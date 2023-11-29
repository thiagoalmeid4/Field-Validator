package br.com.validations.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {

    String message() default "O valor do atributo não pode ser vázio";
    String code() default "Empty Value Annotation"; 
    
}
