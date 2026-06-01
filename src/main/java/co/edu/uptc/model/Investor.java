package co.edu.uptc.model;

import java.util.List;

import co.edu.uptc.model.enums.RiskProfile;
/**
 * Representa un inversionista del sistema.
 *
 * <p>Un inversionista tiene capital disponible, perfil de riesgo y una lista de inversiones registradas.</p>
 */
public class Investor {

    private String id;
    private String name;
    private String email;
    private double availableCapital;
    private RiskProfile riskProfile;
    private List<Investment> investments;

    /** Constructor vacío de la clase Investor. */
    public Investor() {
    }

    /**
     * Crea un inversionista con sus datos principales.
     *
     * @param id identificador del inversionista
     * @param name nombre completo
     * @param email correo electrónico
     * @param availableCapital capital disponible para invertir
     * @param riskProfile perfil de riesgo del inversionista
     * @param investments lista de inversiones asociadas
     */
    public Investor(String id, String name, String email, double availableCapital, RiskProfile riskProfile,
            List<Investment> investments) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.availableCapital = availableCapital;
        this.riskProfile = riskProfile;
        this.investments = investments;
    }

    /** @return identificador del inversionista. */
    public String getId() {
        return id;
    }

    /** @param id nuevo identificador del inversionista. */
    public void setId(String id) {
        this.id = id;
    }

    /** @return nombre del inversionista. */
    public String getName() {
        return name;
    }

    /** @param name nuevo nombre del inversionista. */
    public void setName(String name) {
        this.name = name;
    }

    /** @return correo del inversionista. */
    public String getEmail() {
        return email;
    }

    /** @param email nuevo correo del inversionista. */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return capital disponible. */
    public double getAvailableCapital() {
        return availableCapital;
    }

    /** @return capital disponible (Alias para compatibilidad con TableView). */
    public double getBalance() {
        return availableCapital;
    }

    /** @param availableCapital nuevo capital disponible. */
    public void setAvailableCapital(double availableCapital) {
        this.availableCapital = availableCapital;
    }

    /** @return perfil de riesgo. */
    public RiskProfile getRiskProfile() {
        return riskProfile;
    }

    /** @param riskProfile nuevo perfil de riesgo. */
    public void setRiskProfile(RiskProfile riskProfile) {
        this.riskProfile = riskProfile;
    }

    /** @return lista de inversiones asociadas. */
    public List<Investment> getInvestments() {
        return investments;
    }

    /** @param investments nueva lista de inversiones asociadas. */
    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }

    @Override
    public String toString() {
        // Aunque el toString suele ser solo para los desarrolladores, lo dejamos en inglés neutral
        return "Investor [id=" + id + ", name=" + name + ", email=" + email + ", availableCapital="
                + availableCapital + ", riskProfile=" + riskProfile + ", investments=" + investments + "]";
    }    
}