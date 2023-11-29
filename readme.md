# Field Validator

A Biblioteca fornece um conjunto de anotações para validar campos específicos em objetos Java. O principal objetivo é facilitar a validação de dados, garantindo a integridade e a consistência dos dados em diferentes contextos. A biblioteca cobre diversas validações comuns, como CPF, CNPJ, e-mail, entre outras.

## Anotações disponíveis

```bash
@BrazilianState
```
Valida se o valor de um atributo representa um estado brasileiro válido.

### Atributos: 
```bash
    String message() default "Estado inválido"; #Mensagem de erro padrão quando a validação falha.
    String code() default "Brazilian State Annotation"; #Código de identificação da anotação.
    boolean stateRegistration() default false; #Define se o campo está relacionado à inscrição estadual 
```
---
```bash
@CEP
```
Valida se o valor de um atributo representa um estado brasileiro válido.

### Atributos: 
```bash
    String message() default "Cep inválido"; #Mensagem de erro padrão quando a validação falha.
    String code() default "Cep Annotation"; #Código de identificação da anotação.
```
