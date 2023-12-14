# Field Validator

A Biblioteca fornece um conjunto de anotações para validar campos específicos em objetos Java. O principal objetivo é facilitar a validação de dados, garantindo a integridade e a consistência dos dados em diferentes contextos. A biblioteca cobre diversas validações comuns, como CPF, CNPJ, e-mail, entre outras.

### Atributos padrões: 
```bash
    String message() default "message"; #Mensagem de erro padrão quando a validação falha.
    String code() default "code"; #Código de identificação da anotação.
```


## Anotações disponíveis

```bash
@BrazilianState
```
Valida se o valor de um atributo representa um estado brasileiro válido.

### Atributos: 
```bash
    boolean stateRegistration() default false; #Define se o campo está relacionado à inscrição estadual 
```
---
```bash
@CEP
```
Valida se o valor de um atributo representa um CEP no formato válido.

---
```bash
@CNPJ
```
Valida se o valor de um atributo representa um CNPJ no formato válido.

---
```bash
@CPF
```
Valida se o valor de um atributo representa um CPF no formato válido.

---
```bash
@Email
```
Valida se o valor de um atributo representa um email no formato válido.

---
```bash
@NotNull
```
Valida se o valor de um atributo não é nulo ou vázio.

---
```bash
@Phone
```
Valida se o valor de um atributo é um número de telefone.

---
```bash
@Size
```
Valida o tamanho de um atributo.

---
```bash
@StateRegistration
```
### Atributos: 
```bash
    boolean uf() default "SP"; #Seleciona o estado para a validação da inscrição estadual. 
```
Valida a inscrição estadual de um atributo.