package com.github.compto_bouffe;

/**
 * Created by Sabrina Ouaret on 16/04/15.
 */
public class DateInfos {


    String date, info1, info2;
    int image;

    // Obtient la date et les informations calorique sur l'objectif du jour;
    // change le fond à rouge si l'objectif n'a pas été atteint, à vert sinon.
    public DateInfos(String date, String info1, String info2){
        this.date=date;
        this.info1="OBJECTIF: "+info1 + " kcal";
        this.info2="RESULTAT: "+info2 + " kcal";

        if(Integer.parseInt(info2)<= Integer.parseInt(info1)){
            this.image = R.drawable.vert;
        }else{
            this.image = R.drawable.rouge;
        }
    }

    public String getDate(){
        return this.date;
    }

    public String getInfo1(){
        return this.info1;
    }

    public String getInfo2(){
        return this.info2;
    }

    public int getImage(){
        return this.image;
    }
}
