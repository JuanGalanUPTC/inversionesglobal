package co.edu.uptc.model;

import co.edu.uptc.model.enums.AssetType;

public class Asset {
    private String id;
    private String name;
    private AssetType assetType;
    private double actualPrice;
    private double volatility;
    
    public Asset(String id, String name, AssetType assetType, double actualPrice, double volatility) {
        this.id = id;
        this.name = name;
        this.assetType = assetType;
        this.actualPrice = actualPrice;
        this.volatility = volatility;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    @Override
    public String toString() {
        return "Asset [id=" + id + ", name=" + name + ", assetType=" + assetType + ", actualPrice=" + actualPrice
                + ", volatility=" + volatility + "]";
    }

    
}
