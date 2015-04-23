package com.github.compto_bouffe;

// Cette classe contient simplement les informations sur un plat, quel qu'il soit.
/**
 * Created by sabrinaouaret on 04/04/15.
 */
public class Plats {

    private String nom, upc;
    private int old_qte, qte;
    private boolean selected;

    public Plats(String nom, String upc, int qte){
        this.nom=nom;
        this.qte = qte >= 0 ? qte : 0;
        this.upc=upc;
        this.old_qte=this.qte;
        this.selected=false;
    }

    public String getNom(){
        return this.nom;
    }

    public String getUpc() {
        return this.upc;
    }

    public int getQte(){
        return this.qte;
    }

    public void setQte(int qte){
        if(qte >= 0)
            this.qte=qte;
    }

    public boolean isModified() {
        return this.old_qte != this.qte;
    }

    public boolean isSelected(){
        return this.getQte() == 0 || this.selected;
    }

    public void setSelected(boolean selected){
        this.selected=selected;
    }

}
