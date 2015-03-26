package com.github.compto_bouffe.api;

/**
 * Created by Frederic Hamel on 3/13/15.
 *
 * Cette classe decrits un produit tel que donne par l'api.
 */
public class Product {
    private String name;
    private String desc;
    private String upc;

    // Chaine contenant le format avec unite et est egal a none lorsque le produit vient a l'unite.
    private String size;

    /**
     * Construit un produit.
     * @param name Le nom du produit
     * @param desc La description du produit.
     * @param upc Le code upc utilise pour cherche les informations sur les nutriments.
     * @param size La taille du produit. (ex. 16 oz)
     */
    public Product(String name, String desc, String upc, String size)
    {
        this.name = name;
        this.desc = desc;
        this.upc = upc;
        this.size = size;
    }

    /**
     * Getter du nom.
     * @return le nom du produit.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Getter de la description.
     * @return la description du produit.
     */
    public String getDesc()
    {
        return desc;
    }

    /**
     * Getter du upc.
     * @return le upc code associer au produit.
     */
    String getUpc()
    {
        return upc;
    }

    /**
     * Getter de la taille du produit.
     * @return le format du produit.
     */
    public String getSize()
    {
        return size;
    }
}
