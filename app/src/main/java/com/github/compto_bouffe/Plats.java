package com.github.compto_bouffe;

/**
 * Created by sabrinaouaret on 04/04/15.
 */
// Cette classe contient simplement les informations sur un plat, quel qu'il soit.
public class Plats {

    private String nom, qte;
    private boolean selected;

    public Plats(String nom, String qte){
        this.nom=nom;
        this.qte=qte;
        this.selected=false;
    }

    public String getNom(){
        return this.nom;
    }

    public String getQte(){
        return this.qte;
    }

    public void setQte(String qte){
        this.qte=qte;
    }

    public boolean isSelected(){
        return this.selected;
    }

    public void setSelected(boolean selected){
        this.selected=selected;
    }

}
