package co.edu.uptc.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Representa una operación de inversión (compra) realizada por un inversionista sobre un activo.
 *
 * <p>El campo {@code purchasePrice} representa el desembolso total de la operación (precio unitario × cantidad)
 * en el momento de la compra.</p>
 */
public class Investment {
    private String id;
    private String inversionistId;
    private String assetId;
    private double amount;
    private double purchasePrice;
    private LocalDate date;
    private LocalTime time;

    /** Constructor vacío requerido para serialización. */
    public Investment() {
    }

    /**
     * Crea una inversión con todos sus datos persistibles.
     *
     * @param id identificador de la inversión
     * @param inversionistId identificador del inversionista
     * @param assetId identificador del activo
     * @param amount cantidad de unidades compradas
     * @param purchasePrice desembolso total de la compra
     * @param date fecha de la operación
     * @param time hora de la operación
     */
    public Investment(String id, String inversionistId, String assetId, double amount,
            double purchasePrice, LocalDate date, LocalTime time) {
        this.id = id;
        this.inversionistId = inversionistId;
        this.assetId = assetId;
        this.amount = amount;
        this.purchasePrice = purchasePrice;
        this.date = date;
        this.time = time;
    }
    /** @return identificador de la inversión. */
    public String getId() {
        return id;
    }
    /** @param id nuevo identificador de la inversión. */
    public void setId(String id) {
        this.id = id;
    }
    /** @return identificador del inversionista asociado. */
    public String getInversionistId() {
        return inversionistId;
    }
    /** @param inversionistId nuevo identificador del inversionista asociado. */
    public void setInversionistId(String inversionistId) {
        this.inversionistId = inversionistId;
    }
    /** @return identificador del activo asociado. */
    public String getAssetId() {
        return assetId;
    }
    /** @param assetId nuevo identificador del activo asociado. */
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
    /** @return cantidad de unidades compradas. */
    public double getAmount() {
        return amount;
    }
    /** @param amount nueva cantidad de unidades compradas. */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    /** @return desembolso total de la compra (no unitario). */
    public double getPurchasePrice() {
        return purchasePrice;
    }
    /** @param purchasePrice nuevo desembolso total de la compra. */
    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice= purchasePrice;
    }
    /** @return fecha de la operación. */
    public LocalDate getDate() {
        return date;
    }
    /** @param date nueva fecha de la operación. */
    public void setDate(LocalDate date) {
        this.date = date;
    }
    /** @return hora de la operación. */
    public LocalTime getTime() {
        return time;
    }
    /** @param time nueva hora de la operación. */
    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Inversion [id=" + id + ", inversionistId=" + inversionistId + ", assetId=" + assetId + ", amount="
                + amount + ", purchasePrice=" + purchasePrice + ", date=" + date + ", time=" + time + "]";
    }

    
}
