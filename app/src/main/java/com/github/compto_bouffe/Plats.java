package com.github.compto_bouffe;

// Cette classe contient les informations d'un plat.
public class Plats {

    private String nom, upc;
    private int old_qte, qte;
    private boolean selected;

    /**
     * Constructeur
     * @param nom le nom du produit
     * @param upc le code du produit
     * @param qte sa quantite
     */
    public Plats(String nom, String upc, int qte){
        this.nom=nom;
        this.qte = qte >= 0 ? qte : 0;
        this.upc=upc;
        this.old_qte=this.qte;
        this.selected=false;
    }

    /**
     * Getter nom
     * @return nom
     */
    public String getNom(){
        return this.nom;
    }

    /**
     * Getter upc
     * @return upc
     */
    public String getUpc() {
        return this.upc;
    }

    /**
     * Getter quantite
     * @return qte
     */
    public int getQte(){
        return this.qte;
    }

    /**
     * Setter qte
     * @param qte
     */
    public void setQte(int qte){
        if(qte >= 0)
            this.qte=qte;
    }

    /**
     * Setter selected
     * @param selected
     */
    public void setSelected(boolean selected){
        this.selected=selected;
    }

    /**
     * Methode qui teste si l'ancienne quantite est egale a la nouvelle
     * @return boolean
     */
    public boolean isModified() {
        return this.old_qte != this.qte;
    }

    /**
     * Methode qui teste si la quantite est a 0 ou si le produit est selectionne
     * @return boolean
     */
    public boolean isSelected(){
        return this.getQte() == 0 || this.selected;
    }

}
