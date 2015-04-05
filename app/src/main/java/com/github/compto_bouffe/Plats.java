package com.github.compto_bouffe;

/**
 * Created by sabrinaouaret on 04/04/15.
 */
public class Plats {
    private String nom;
    private boolean selected;

    public Plats(String nom){
        this.nom=nom;
    }

    public String getNom(){
        return this.nom;
    }

    public boolean isSelected(){
        return this.selected;
    }

    public void setSelected(boolean selected){
        this.selected=selected;
    }
}
