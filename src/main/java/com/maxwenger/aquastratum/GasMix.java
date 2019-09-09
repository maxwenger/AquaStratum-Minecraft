package com.maxwenger.aquastratum;

public class GasMix {
    private double FHe;
    private double FN2;
    private double FO2;

    public GasMix(double oxygenPercentage) {
        this(oxygenPercentage, 0);
    }

    public GasMix(double oxygenPercentage, double heliumPercentage) {
        setMix(oxygenPercentage, heliumPercentage);
    }

    public GasMix(String mix) {
        String[] percentages = mix.split("/");
        if (percentages.length != 2) {
            throw new IllegalArgumentException("Invalid mixture");
        }
        double oxygenPercentage = Double.parseDouble(percentages[0])/100;
        double heliumPercentage = Double.parseDouble(percentages[1])/100;

        setMix(oxygenPercentage, heliumPercentage);
    }

    public void setMix(double oxygenPercentage, double heliumPercentage) {
        if (oxygenPercentage + heliumPercentage > 1) {
            throw new IllegalArgumentException("Invalid mixture");
        }
        FHe = heliumPercentage;
        FO2 = oxygenPercentage;
        FN2 = 1 - FO2 - FHe;
    }

    public double getO2Percent() {
        return FO2;
    }

    public double getN2Percent() {
        return FN2;
    }

    public double getHePercent() {
        return FHe;
    }

    public String toString() {
        return (FO2 * 100) + "/" + (FHe * 100);
    }
}
