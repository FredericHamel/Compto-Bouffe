package com.github.compto_bouffe.api;

/**
 * Classe reprosentant un produit et sa quantite.
 */
public class ProductQty {
    private Product product;
    private int qte;

    /**
     * Constructeur d'un Produit avec quantite.
     * @param p Le produit
     * @param q la quantite.
     */
    public ProductQty(Product p, int q) {
        this.product = p;
        this.qte = q;
    }

    /**
     * Augmente la quantite de 1.
     */
    public void add() {
        ++qte;
    }

    /**
     * Soustrait la quantite de 1 en gardant
     * la quantite positive.
     */
    public void sub() {
        if(qte > 0)
            --qte;
    }

    /**
     * Accesseur pour le produit
     * @return product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Accesseur pour la quantite.
     * @return qte
     */
    public int getQte() {
        return qte;
    }
}