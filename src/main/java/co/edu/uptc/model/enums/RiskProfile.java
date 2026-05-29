package co.edu.uptc.model.enums;

/**
 * Perfiles de riesgo de inversionista y su tolerancia máxima de riesgo.
 */
public enum RiskProfile {
    CONSERVATIVE(2), //Solo tolera bajo (1) y medio-bajo (2)
    MODERATE(3),    //Tolera hasta medio-alto(3)
    AGGRESSIVE(5);   //Tolera hasta alto(5)

    private int maxRisk;

    private RiskProfile(int maxRisk) {
        this.maxRisk=maxRisk;
    }

    /**
     * Retorna el nivel máximo de riesgo permitido para el perfil.
     *
     * @return nivel máximo de riesgo tolerado
     */
    public int getMaxRisk(){
        return maxRisk;
    }

    
    

}
