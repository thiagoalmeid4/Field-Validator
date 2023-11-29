package br.com.validations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import br.com.validations.annotation.BrazilianState;
import br.com.validations.annotation.Cep;
import br.com.validations.annotation.Cnpj;
import br.com.validations.annotation.Cpf;
import br.com.validations.annotation.Email;
import br.com.validations.annotation.NotNull;
import br.com.validations.annotation.Phone;
import br.com.validations.annotation.StateRegistration;
import br.com.validations.enums.BrazilianStates;
import br.com.validations.exception.ValidationException;

public class Validator {

    private static List<Error> errors = new ArrayList<>();

    public List<Error> getErrors() {
        return errors;
    }

    public static void target(Object object) {

        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(BrazilianState.class)) {
                    validateBrazilianState(field, object);
                } else if (field.isAnnotationPresent((Cpf.class))) {
                    validateCpf(field, object);
                } else if (field.isAnnotationPresent((Cnpj.class))) {
                    validateCnpj(field, object);
                } else if (field.isAnnotationPresent(Email.class)) {
                    validateEmail(field, object);
                } else if (field.isAnnotationPresent(Cep.class)) {
                    validateCep(field, object);
                } else if (field.isAnnotationPresent(StateRegistration.class)) {
                    validateStateRegistration(fields, field, object);
                } else if (field.isAnnotationPresent(Phone.class)) {
                    validatePhone(field, object);
                }
                if (field.isAnnotationPresent(NotNull.class)) {
                    validateNotNull(field, object);
                }

            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (errors.size() > 0) {
            throw new ValidationException(errors);
        }
    }

    private static void validatePhone(Field field, Object object)
            throws IllegalArgumentException, IllegalAccessException {
        Phone phoneAnnotation = field.getAnnotation(Phone.class);
        String value = (String) field.get(object);
        if (value != null && !value.isBlank()) {
            if (!value.matches(
                    "^\\(?(\\d{2})\\)? ?(?:9\\d{4}-\\d{4}|9\\d{8}|\\d{4}-\\d{4}|\\d{8}|9\\d{4}\\d{4}|\\d{9}|9\\d{8}|\\d{8})$")) {
                errors.add(new Error(phoneAnnotation.code(), phoneAnnotation.message()));
            }
        }
    }

    private static void validateCpf(Field field, Object object)
            throws IllegalArgumentException, IllegalAccessException {
        boolean result = true;
        Cpf cpfAnnotation = field.getAnnotation(Cpf.class);
        String cpf = (String) field.get(object);
        if (cpf != null && !cpf.isBlank()) {
            // Remove caracteres não numéricos
            cpf = cpf.replaceAll("[^0-9]", "");
            // Verifica se o CPF tem 11 dígitos
            if (cpf.length() != 11) {
                result = false;
            } else {
                // Calcula o primeiro dígito verificador
                int soma = 0;
                for (int i = 0; i < 9; i++) {
                    soma += (cpf.charAt(i) - '0') * (10 - i);
                }
                int primeiroDigito = 11 - (soma % 11);
                if (primeiroDigito >= 10) {
                    primeiroDigito = 0;
                }

                // Calcula o segundo dígito verificador
                soma = 0;
                for (int i = 0; i < 10; i++) {
                    soma += (cpf.charAt(i) - '0') * (11 - i);
                }
                int segundoDigito = 11 - (soma % 11);
                if (segundoDigito >= 10) {
                    segundoDigito = 0;
                }
                // Verifica se os dígitos verificadores calculados são iguais aos dígitos do CPF
                result = (cpf.charAt(9) - '0' == primeiroDigito) && (cpf.charAt(10) - '0' == segundoDigito);
            }
        }
        if (!result) {
            errors.add(new Error(cpfAnnotation.code(), cpfAnnotation.message()));
        }
    }

    private static void validateEmail(Field field, Object object)
            throws IllegalArgumentException, IllegalAccessException {
        Email emailAnnotation = field.getAnnotation(Email.class);
        String email = (String) field.get(object);
        if (email != null && !email.isBlank()) {
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                errors.add(new Error(emailAnnotation.code(), emailAnnotation.message()));
            }
        }
    }

    private static void validateCep(Field field, Object object)
            throws IllegalArgumentException, IllegalAccessException {
        Cep cepAnnotation = field.getAnnotation(Cep.class);
        String cep = (String) field.get(object);
        if (cep != null && !cep.isBlank()) {
            if (!cep.matches("^\\d{5}-\\d{3}$")) {
                errors.add(new Error(cepAnnotation.code(), cepAnnotation.message()));
            }
        }
    }

    private static void validateNotNull(Field field, Object object)
            throws IllegalArgumentException, IllegalAccessException {
        NotNull notNull = field.getAnnotation(NotNull.class);
        String value = (String) field.get(object);
        if (value == null || value.isBlank()) {
            errors.add(new Error(notNull.code(), notNull.message() + " (" + field.getName() + ")"));
        }
    }

    private static void validateCnpj(Field field, Object object)
            throws IllegalArgumentException, IllegalAccessException {
        boolean result = true;

        Cnpj cnpjAnnotation = field.getAnnotation(Cnpj.class);
        String cnpj = (String) field.get(object);
        if (cnpj != null && !cnpj.isBlank()) {
            // Remove caracteres não numéricos
            cnpj = cnpj.replaceAll("[^0-9]", "");
            // Verifica se o CNPJ tem 14 dígitos
            if (cnpj.length() != 14) {
                result = false;
            } else {
                // Calcula o primeiro dígito verificador
                int[] pesosPrimeiroDigito = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
                int soma = 0;
                for (int i = 0; i < 12; i++) {
                    soma += (cnpj.charAt(i) - '0') * pesosPrimeiroDigito[i];
                }
                int resto = soma % 11;
                int primeiroDigitoVerificador = (resto < 2) ? 0 : (11 - resto);

                // Calcula o segundo dígito verificador
                int[] pesosSegundoDigito = { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
                soma = 0;
                for (int i = 0; i < 13; i++) {
                    soma += (cnpj.charAt(i) - '0') * pesosSegundoDigito[i];
                }
                resto = soma % 11;
                int segundoDigitoVerificador = (resto < 2) ? 0 : (11 - resto);

                // Verifica se os dígitos verificadores calculados são iguais aos dígitos do
                // CNPJ
                result = (cnpj.charAt(12) - '0' == primeiroDigitoVerificador) &&
                        (cnpj.charAt(13) - '0' == segundoDigitoVerificador);
            }
        }
        if (!result) {
            errors.add(new Error(cnpjAnnotation.code(), cnpjAnnotation.message()));
        }
    }

    private static void validateBrazilianState(Field field, Object object)
            throws IllegalArgumentException, IllegalAccessException {
        BrazilianState stateAnnotation = field.getAnnotation(BrazilianState.class);
        String state = (String) field.get(object);
        if (state != null && !state.isBlank()) {
            if (BrazilianStates.getByName(state) == null) {
                errors.add(new Error(stateAnnotation.code(), stateAnnotation.message()));
            }
        }
    }

    private static void validateStateRegistration(Field[] fields, Field field, Object object)
            throws IllegalArgumentException, IllegalAccessException {
        StateRegistration stateRegistration = field.getAnnotation(StateRegistration.class);
        String value = (String) field.get(object);
        if (value != null && !value.isBlank()) {
            for(Field f : fields) {
                if(f.isAnnotationPresent(BrazilianState.class) && 
                    f.getAnnotation(BrazilianState.class).stateRegistration()) {
                    String state = (String) f.get(object);
                    if(state != null && !state.isBlank()) {
                        if(BrazilianStates.getByName(state) == null) {
                            errors.add(new Error(stateRegistration.code(), stateRegistration.message()));
                        }else if(!validaInscricaoEstadual(value, BrazilianStates.getByName(state))){
                            errors.add(new Error(stateRegistration.code(), stateRegistration.message()));
                        }
                    }
                }else if(f.isAnnotationPresent(BrazilianState.class) && 
                    !f.getAnnotation(BrazilianState.class).stateRegistration()){
                    if(!validaInscricaoEstadual(value, stateRegistration.uf())){
                        errors.add(new Error(stateRegistration.code(), stateRegistration.message()));
                    }
                }
            }
        }
    }

    private static String removeMascaraIe(String ie) {
        String strIE = "";
        for (int i = 0; i < ie.length(); i++) {
            if (Character.isDigit(ie.charAt(i))) {
                strIE += ie.charAt(i);
            }
        }
        return strIE;
    }

    private static boolean validaInscricaoEstadual(String inscricaoEstadual, String siglaUf) {
        boolean result = true;
        if (inscricaoEstadual != null && !inscricaoEstadual.equals("")) {
            String strIE = removeMascaraIe(inscricaoEstadual);
            siglaUf = siglaUf.toUpperCase();
            if (siglaUf.equals("AC")) {
                result = validaIEAcre(strIE);
            } else if (siglaUf.equals("AL")) {
                result = validaIEAlagoas(strIE);
            } else if (siglaUf.equals("AP")) {
                result = validaIEAmapa(strIE);
            } else if (siglaUf.equals("AM")) {
                result = validaIEAmazonas(strIE);
            } else if (siglaUf.equals("BA")) {
                result = validaIEBahia(strIE);
            } else if (siglaUf.equals("CE")) {
                result = validaIECeara(strIE);
            } else if (siglaUf.equals("ES")) {
                result = validaIEEspiritoSanto(strIE);
            } else if (siglaUf.equals("GO")) {
                result = validaIEGoias(strIE);
            } else if (siglaUf.equals("MA")) {
                result = validaIEMaranhao(strIE);
            } else if (siglaUf.equals("MT")) {
                result = validaIEMatoGrosso(strIE);
            } else if (siglaUf.equals("MS")) {
                result = validaIEMatoGrossoSul(strIE);
            } else if (siglaUf.equals("MG")) {
                result = validaIEMinasGerais(strIE);
            } else if (siglaUf.equals("PA")) {
                result = validaIEPara(strIE);
            } else if (siglaUf.equals("PB")) {
                result = validaIEParaiba(strIE);
            } else if (siglaUf.equals("PR")) {
                result = validaIEParana(strIE);
            } else if (siglaUf.equals("PE")) {
                result = validaIEPernambuco(strIE);
            } else if (siglaUf.equals("PI")) {
                result = validaIEPiaui(strIE);
            } else if (siglaUf.equals("RJ")) {
                result = validaIERioJaneiro(strIE);
            } else if (siglaUf.equals("RN")) {
                result = validaIERioGrandeNorte(strIE);
            } else if (siglaUf.equals("RS")) {
                result = validaIERioGrandeSul(strIE);
            } else if (siglaUf.equals("RO")) {
                result = validaIERondonia(strIE);
            } else if (siglaUf.equals("RR")) {
                result = validaIERoraima(strIE);
            } else if (siglaUf.equals("SC")) {
                result = validaIESantaCatarina(strIE);
            } else if (siglaUf.equals("SP")) {
                if (inscricaoEstadual.charAt(0) == 'P') {
                    strIE = "P" + strIE;
                }
                result = validaIESaoPaulo(strIE);
            } else if (siglaUf.equals("SE")) {
                result = validaIESergipe(strIE);
            } else if (siglaUf.equals("TO")) {
                result = validaIETocantins(strIE);
            } else if (siglaUf.equals("DF")) {
                result = validaIEDistritoFederal(strIE);
            } else {
                return false;
            }
        }
        return result;
    }

    private static boolean validaIEAcre(String ie) {
        if (ie.length() != 13) {
            return false;
        }

        for (int i = 0; i < 2; i++) {
            if (Integer.parseInt(String.valueOf(ie.charAt(i))) != i) {
                return false;
            }
        }

        int soma = 0;
        int pesoInicial = 4;
        int pesoFinal = 9;
        int d1 = 0;
        int d2 = 0;

        for (int i = 0; i < ie.length() - 2; i++) {
            if (i < 3) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicial;
                pesoInicial--;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFinal;
                pesoFinal--;
            }
        }
        d1 = 11 - (soma % 11);
        if (d1 == 10 || d1 == 11) {
            d1 = 0;
        }

        soma = d1 * 2;
        pesoInicial = 5;
        pesoFinal = 9;
        for (int i = 0; i < ie.length() - 2; i++) {
            if (i < 4) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicial;
                pesoInicial--;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFinal;
                pesoFinal--;
            }
        }

        d2 = 11 - (soma % 11);
        if (d2 == 10 || d2 == 11) {
            d2 = 0;
        }

        String dv = d1 + "" + d2;
        if (!dv.equals(ie.substring(ie.length() - 2, ie.length()))) {
            return false;
        }
        return true;
    }

    private static boolean validaIEAlagoas(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        if (!ie.substring(0, 2).equals("24")) {
            return false;
        }

        int[] digits = { 0, 3, 5, 7, 8 };
        boolean check = false;
        for (int i = 0; i < digits.length; i++) {
            if (Integer.parseInt(String.valueOf(ie.charAt(2))) == digits[i]) {
                check = true;
                break;
            }
        }
        if (!check) {
            return false;
        }

        int soma = 0;
        int peso = 9;
        int d = 0;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }
        d = ((soma * 10) % 11);
        if (d == 10) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEAmapa(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        if (!ie.substring(0, 2).equals("03")) {
            return false;

        }

        int d1 = -1;
        int soma = -1;
        int peso = 9;

        long x = Long.parseLong(ie.substring(0, ie.length() - 1));
        if (x >= 3017001L && x <= 3019022L) {
            d1 = 1;
            soma = 9;
        } else if (x >= 3000001L && x <= 3017000L) {
            d1 = 0;
            soma = 5;
        } else if (x >= 3019023L) {
            d1 = 0;
            soma = 0;
        }

        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        int d = 11 - ((soma % 11));
        if (d == 10) {
            d = 0;
        } else if (d == 11) {
            d = d1;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEAmazonas(String ie) {
        if (ie.length() != 9) {
            return false;

        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        if (soma < 11) {
            d = 11 - soma;
        } else if ((soma % 11) <= 1) {
            d = 0;
        } else {
            d = 11 - (soma % 11);
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEBahia(String ie) {
        if (ie.length() != 8 && ie.length() != 9) {
            return false;
        }

        int modulo = 10;
        int firstDigit = Integer.parseInt(String.valueOf(ie.charAt(ie.length() == 8 ? 0 : 1)));
        if (firstDigit == 6 || firstDigit == 7 || firstDigit == 9)
            modulo = 11;

        int d2 = -1;
        int soma = 0;
        int peso = ie.length() == 8 ? 7 : 8;
        for (int i = 0; i < ie.length() - 2; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        int resto = soma % modulo;

        if (resto == 0 || (modulo == 11 && resto == 1)) {
            d2 = 0;
        } else {
            d2 = modulo - resto;
        }

        int d1 = -1;
        soma = d2 * 2;
        peso = ie.length() == 8 ? 8 : 9;
        for (int i = 0; i < ie.length() - 2; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        resto = soma % modulo;

        if (resto == 0 || (modulo == 11 && resto == 1)) {
            d1 = 0;
        } else {
            d1 = modulo - resto;
        }

        String dv = d1 + "" + d2;
        if (!dv.equals(ie.substring(ie.length() - 2, ie.length()))) {
            return false;
        }
        return true;
    }

    private static boolean validaIECeara(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        d = 11 - (soma % 11);
        if (d == 10 || d == 11) {
            d = 0;
        }
        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEEspiritoSanto(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        int resto = soma % 11;
        if (resto < 2) {
            d = 0;
        } else if (resto > 1) {
            d = 11 - resto;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEGoias(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        if (!"10".equals(ie.substring(0, 2))) {
            if (!"11".equals(ie.substring(0, 2))) {
                if (!"15".equals(ie.substring(0, 2))) {
                    return false;

                }
            }
        }

        if (ie.substring(0, ie.length() - 1).equals("11094402")) {
            if (!ie.substring(ie.length() - 1, ie.length()).equals("0")) {
                if (!ie.substring(ie.length() - 1, ie.length()).equals("1")) {
                    return false;
                }
            }
        } else {

            int soma = 0;
            int peso = 9;
            int d = -1;
            for (int i = 0; i < ie.length() - 1; i++) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
                peso--;
            }

            int resto = soma % 11;
            long faixaInicio = 10103105;
            long faixaFim = 10119997;
            long insc = Long.parseLong(ie.substring(0, ie.length() - 1));
            if (resto == 0) {
                d = 0;
            } else if (resto == 1) {
                if (insc >= faixaInicio && insc <= faixaFim) {
                    d = 1;
                } else {
                    d = 0;
                }
            } else if (resto != 0 && resto != 1) {
                d = 11 - resto;
            }

            String dv = d + "";
            if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
                return false;
            }
        }
        return true;
    }

    private static boolean validaIEMaranhao(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        if (!ie.substring(0, 2).equals("12")) {
            return false;

        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        d = 11 - (soma % 11);
        if ((soma % 11) == 0 || (soma % 11) == 1) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEMatoGrosso(String ie) {
        if (ie.length() != 11) {
            return false;
        }

        int soma = 0;
        int pesoInicial = 3;
        int pesoFinal = 9;
        int d = -1;

        for (int i = 0; i < ie.length() - 1; i++) {
            if (i < 2) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicial;
                pesoInicial--;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFinal;
                pesoFinal--;
            }
        }

        d = 11 - (soma % 11);
        if ((soma % 11) == 0 || (soma % 11) == 1) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEMatoGrossoSul(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        if (!ie.substring(0, 2).equals("28")) {
            return false;
        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        int resto = soma % 11;
        int result = 11 - resto;
        if (resto == 0) {
            d = 0;
        } else if (resto > 0) {
            if (result > 9) {
                d = 0;
            } else if (result < 10) {
                d = result;
            }
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEMinasGerais(String ie) {
        if (ie.length() != 13) {
            return false;
        }

        String str = "";
        for (int i = 0; i < ie.length() - 2; i++) {
            if (Character.isDigit(ie.charAt(i))) {
                if (i == 3) {
                    str += "0";
                    str += ie.charAt(i);
                } else {
                    str += ie.charAt(i);
                }
            }
        }

        int soma = 0;
        int pesoInicio = 1;
        int pesoFim = 2;
        int d1 = -1;
        for (int i = 0; i < str.length(); i++) {
            if (i % 2 == 0) {
                int x = Integer.parseInt(String.valueOf(str.charAt(i))) * pesoInicio;
                String strX = Integer.toString(x);
                for (int j = 0; j < strX.length(); j++) {
                    soma += Integer.parseInt(String.valueOf(strX.charAt(j)));
                }
            } else {
                int y = Integer.parseInt(String.valueOf(str.charAt(i))) * pesoFim;
                String strY = Integer.toString(y);
                for (int j = 0; j < strY.length(); j++) {
                    soma += Integer.parseInt(String.valueOf(strY.charAt(j)));
                }
            }
        }

        int dezenaExata = soma;
        while (dezenaExata % 10 != 0) {
            dezenaExata++;
        }
        d1 = dezenaExata - soma;

        soma = d1 * 2;
        pesoInicio = 3;
        pesoFim = 11;
        int d2 = -1;
        for (int i = 0; i < ie.length() - 2; i++) {
            if (i < 2) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
                pesoInicio--;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
                pesoFim--;
            }
        }

        d2 = 11 - (soma % 11);
        if ((soma % 11 == 0) || (soma % 11 == 1)) {
            d2 = 0;
        }

        String dv = d1 + "" + d2;
        if (!dv.equals(ie.substring(ie.length() - 2, ie.length()))) {
            return false;
        }
        return true;
    }

    private static boolean validaIEPara(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        if (!ie.substring(0, 2).equals("15")) {
            return false;
        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        d = 11 - (soma % 11);
        if ((soma % 11) == 0 || (soma % 11) == 1) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEParaiba(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        d = 11 - (soma % 11);
        if (d == 10 || d == 11) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEParana(String ie) {
        if (ie.length() != 10) {
            return false;
        }

        int soma = 0;
        int pesoInicio = 3;
        int pesoFim = 7;
        int d1 = -1;
        for (int i = 0; i < ie.length() - 2; i++) {
            if (i < 2) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
                pesoInicio--;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
                pesoFim--;
            }
        }

        d1 = 11 - (soma % 11);
        if ((soma % 11) == 0 || (soma % 11) == 1) {
            d1 = 0;
        }

        soma = d1 * 2;
        pesoInicio = 4;
        pesoFim = 7;
        int d2 = -1;
        for (int i = 0; i < ie.length() - 2; i++) {
            if (i < 3) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
                pesoInicio--;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
                pesoFim--;
            }
        }

        d2 = 11 - (soma % 11);
        if ((soma % 11) == 0 || (soma % 11) == 1) {
            d2 = 0;
        }

        String dv = d1 + "" + d2;
        if (!dv.equals(ie.substring(ie.length() - 2, ie.length()))) {
            return false;
        }
        return true;
    }

    private static boolean validaIEPernambuco(String ie) {
        if (ie.length() != 14) {
            return false;
        }

        int soma = 0;
        int pesoInicio = 5;
        int pesoFim = 9;
        int d = -1;

        for (int i = 0; i < ie.length() - 1; i++) {
            if (i < 5) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
                pesoInicio--;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
                pesoFim--;
            }
        }

        d = 11 - (soma % 11);
        if (d > 9) {
            d -= 10;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEPiaui(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        d = 11 - (soma % 11);
        if (d == 11 || d == 10) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIERioJaneiro(String ie) {
        if (ie.length() != 8) {
            return false;
        }

        int soma = 0;
        int peso = 7;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            if (i == 0) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * 2;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
                peso--;
            }
        }

        d = 11 - (soma % 11);
        if ((soma % 11) <= 1) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIERioGrandeNorte(String ie) {
        if (ie.length() != 10 && ie.length() != 9) {
            return false;
        }

        if (!ie.substring(0, 2).equals("20")) {
            return false;
        }

        if (ie.length() == 9) {
            int soma = 0;
            int peso = 9;
            int d = -1;
            for (int i = 0; i < ie.length() - 1; i++) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
                peso--;
            }

            d = ((soma * 10) % 11);
            if (d == 10) {
                d = 0;
            }

            String dv = d + "";
            if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
                return false;
            }
        } else {
            int soma = 0;
            int peso = 10;
            int d = -1;
            for (int i = 0; i < ie.length() - 1; i++) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
                peso--;
            }
            d = ((soma * 10) % 11);
            if (d == 10) {
                d = 0;
            }

            String dv = d + "";
            if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
                return false;
            }
        }
        return true;
    }

    private static boolean validaIERioGrandeSul(String ie) {
        if (ie.length() != 10) {
            return false;
        }

        int soma = Integer.parseInt(String.valueOf(ie.charAt(0))) * 2;
        int peso = 9;
        int d = -1;
        for (int i = 1; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        d = 11 - (soma % 11);
        if (d == 10 || d == 11) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIERondonia(String ie) {
        if (ie.length() != 14) {
            return false;
        }

        int soma = 0;
        int pesoInicio = 6;
        int pesoFim = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            if (i < 5) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
                pesoInicio--;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
                pesoFim--;
            }
        }

        d = 11 - (soma % 11);
        if (d == 11 || d == 10) {
            d -= 10;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIERoraima(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        if (!ie.substring(0, 2).equals("24")) {
            return false;
        }

        int soma = 0;
        int peso = 1;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso++;
        }

        d = soma % 9;

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIESantaCatarina(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        d = 11 - (soma % 11);
        if ((soma % 11) == 0 || (soma % 11) == 1) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIESaoPaulo(String ie) {
        if (ie.length() != 12 && ie.length() != 13) {
            return false;
        }

        if (ie.length() == 12) {
            int soma = 0;
            int peso = 1;
            int d1 = -1;
            for (int i = 0; i < ie.length() - 4; i++) {
                if (i == 1 || i == 7) {
                    soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * ++peso;
                    peso++;
                } else {
                    soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
                    peso++;
                }
            }

            d1 = soma % 11;
            String strD1 = Integer.toString(d1);
            d1 = Integer.parseInt(String.valueOf(strD1.charAt(strD1.length() - 1)));

            soma = 0;
            int pesoInicio = 3;
            int pesoFim = 10;
            int d2 = -1;
            for (int i = 0; i < ie.length() - 1; i++) {
                if (i < 2) {
                    soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
                    pesoInicio--;
                } else {
                    soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
                    pesoFim--;
                }
            }

            d2 = soma % 11;
            String strD2 = Integer.toString(d2);
            d2 = Integer.parseInt(String.valueOf(strD2.charAt(strD2.length() - 1)));

            if (!ie.substring(8, 9).equals(d1 + "")) {
                return false;
            }
            if (!ie.substring(11, 12).equals(d2 + "")) {
                return false;
            }

        } else {
            if (ie.charAt(0) != 'P') {
                return false;
            }

            String strIE = ie.substring(1, 10);
            int soma = 0;
            int peso = 1;
            int d1 = -1;
            for (int i = 0; i < strIE.length() - 1; i++) {
                if (i == 1 || i == 7) {
                    soma += Integer.parseInt(String.valueOf(strIE.charAt(i))) * ++peso;
                    peso++;
                } else {
                    soma += Integer.parseInt(String.valueOf(strIE.charAt(i))) * peso;
                    peso++;
                }
            }

            d1 = soma % 11;
            String strD1 = Integer.toString(d1);
            d1 = Integer.parseInt(String.valueOf(strD1.charAt(strD1.length() - 1)));

            if (!ie.substring(9, 10).equals(d1 + "")) {
                return false;
            }
        }
        return true;
    }

    private static boolean validaIESergipe(String ie) {
        if (ie.length() != 9) {
            return false;
        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
            peso--;
        }

        d = 11 - (soma % 11);
        if (d == 11 || d == 11 || d == 10) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIETocantins(String ie) {
        if (ie.length() != 9 && ie.length() != 11) {
            return false;
        } else if (ie.length() == 9) {
            ie = ie.substring(0, 2) + "02" + ie.substring(2);
        }

        int soma = 0;
        int peso = 9;
        int d = -1;
        for (int i = 0; i < ie.length() - 1; i++) {
            if (i != 2 && i != 3) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * peso;
                peso--;
            }
        }
        d = 11 - (soma % 11);
        if ((soma % 11) < 2) {
            d = 0;
        }

        String dv = d + "";
        if (!ie.substring(ie.length() - 1, ie.length()).equals(dv)) {
            return false;
        }
        return true;
    }

    private static boolean validaIEDistritoFederal(String ie) {
        if (ie.length() != 13) {
            return false;
        }

        int soma = 0;
        int pesoInicio = 4;
        int pesoFim = 9;
        int d1 = -1;
        for (int i = 0; i < ie.length() - 2; i++) {
            if (i < 3) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
                pesoInicio--;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
                pesoFim--;
            }
        }

        d1 = 11 - (soma % 11);
        if (d1 == 11 || d1 == 10) {
            d1 = 0;
        }

        soma = d1 * 2;
        pesoInicio = 5;
        pesoFim = 9;
        int d2 = -1;
        for (int i = 0; i < ie.length() - 2; i++) {
            if (i < 4) {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoInicio;
                pesoInicio--;
            } else {
                soma += Integer.parseInt(String.valueOf(ie.charAt(i))) * pesoFim;
                pesoFim--;
            }
        }

        d2 = 11 - (soma % 11);
        if (d2 == 11 || d2 == 10) {
            d2 = 0;
        }

        String dv = d1 + "" + d2;
        if (!dv.equals(ie.substring(ie.length() - 2, ie.length()))) {
            return false;
        }
        return true;
    }
}
