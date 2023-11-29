package br.com.validations.enums;

public enum BrazilianStates {
    
    ACRE ("Acre", "AC"),
    ALAGOAS ("Alagoas", "AL"),
    AMAPA ("Amapá", "AP"),
    AMAZONAS ("Amazonas", "AM"),
    BAHIA ("Bahia", "BA"),
    CEARA ("Ceará", "CE"),
    DISTRITO_FEDERAL ("Distrito Federal", "DF"),
    ESPIRITO_SANTO ("Espírito Santo", "ES"),
    GOIAS ("Goiás", "GO"),
    MARANHAO ("Maranhão", "MA"),
    MATO_GROSSO ("Mato Grosso", "MT"),
    MATO_GROSSO_DO_SUL ("Mato Grosso do Sul", "MS"),
    MINAS_GERAIS ("Minas Gerais", "MG"),
    PARA ("Pará", "PA"),
    PARAIBA ("Paraíba", "PB"),
    PARANA ("Paraná", "PR"),
    PERNAMBUCO ("Pernambuco", "PE"),
    PIAUI ("Piauí", "PI"),
    RIO_DE_JANEIRO ("Rio de Janeiro", "RJ"),
    RIO_GRANDE_DO_NORTE ("Rio Grande do Norte", "RN"),
    RIO_GRANDE_DO_SUL ("Rio Grande do Sul", "RS"),
    RONDONIA ("Rondônia", "RO"),
    RORAIMA ("Roraima", "RR"),
    SANTA_CATARINA ("Santa Catarina", "SC"),
    SAO_PAULO ("São Paulo", "SP"),
    SERGIPE ("Sergipe", "SE"),
    TOCANTINS ("Tocantins", "TO");

    private String name;
    private String uf;

    BrazilianStates(String name, String uf) {
        this.name = name;
        this.uf = uf;
    }

    public String getName() {
        return name;
    }

    public String getUf() {
        return uf;
    }

    public static String getByUf(String uf){
        for(BrazilianStates state : values()){
            if(state.getUf().equalsIgnoreCase(uf)){
                return state.name;
            }
        }
        return null;
    }

    public static String getByName(String name) {
        for (BrazilianStates state : values()) {
            if (state.getName().equalsIgnoreCase(name)) {
                return state.getUf();
            }
        }
        return null; // Retornar null se o estado não for encontrado
    }

}
