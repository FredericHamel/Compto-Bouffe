package com.github.compto_bouffe.api;

/**
 * Created by adkuser on 3/14/15.
 */
public class Nutriment {
    private String name;
    private String value;
    private String uom;

    /**
     * Construit un nutriments.
     * @param n le nom du nitriment.
     * @param v la quantite du nutriment.
     * @param u l'unite qu'il dans laquel v est fournit.
     */
    public Nutriment(String n, String v, String u)
    {
        this.name = n;
        this.value = v.equals("") ? "0.0": v;
        this.uom = u;
    }

    /**
     * Getter pour le nom du nutriment.
     * @return le nom.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Getter pour la valeur du nutriment.
     * @return la valeur.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Getter pour l'unite dans laquelle la valeur est fournie.
     * @return l'unite.
     */
    public String getUOM()
    {
        return uom;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Nutriment o) {
        return name.equals(o.name);
    }

    @Override
    public String toString()
    {
        return getName() + ": " + getValue() + " " + getUOM();
    }
}
