package co.edu.uptc.model.enums;

/**
 * Tipos de activos soportados por el sistema, cada uno con un nivel de riesgo
 * asociado.
 */
public enum AssetType {
    // 1 -> Bajo Riesgo
    // 2 -> Medio-Bajo Riesgo
    // 3 -> Medio-Alto Riesgo
    // 4 -> Alto Riesgo
    // 5 -> Muy Alto Riesgo

    BOND(1), // Renta fija (Bajo)
    BADGE(1), // Divisas estables (Bajo)
    ETF(2), // Fondos indexados diversificados (Medio-Bajo)
    PROPERTY(3), // Inmuebles (Medio-Alto)
    STOCK(4), // Acciones individuales (Alto) 
    CRYPTO(4), // Criptomonedas (Alto)
    NFT(5); // Muy Alto

    private int riskLevel;

    AssetType(int riskLevel) {
        this.riskLevel = riskLevel;
    }

    /**
     * Retorna el nivel de riesgo asociado al tipo de activo.
     *
     * @return nivel de riesgo (1 bajo .. 5 muy alto)
     */
    public int getRiskLevel() {
        return riskLevel;
    }
}
