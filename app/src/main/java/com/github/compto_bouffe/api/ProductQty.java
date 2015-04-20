package com.github.compto_bouffe.api;

public class ProductQty {
    private Product product;
    private int qte;

    public ProductQty(Product p, int q) {
        this.product = p;
        this.qte = q;
    }

    public void add() {
        ++qte;
    }

    public void sub() {
        if(qte > 0)
            --qte;
    }

    public Product getProduct() {
        return product;
    }

    public int getQte() {
        return qte;
    }
}