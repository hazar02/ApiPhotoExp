package com.example.azarchatit.facebookphotosexp;

/**
 * Created by azar on 10/01/2017.
 */

public class Photo {

    private String name;
    private String urlphoto;
    private int count;


    public Photo(String name, String urlphoto, int count) {
        this.name = name;
        this.urlphoto = urlphoto;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlphoto() {
        return urlphoto;
    }

    public void setUrlphoto(String urlphoto) {
        this.urlphoto = urlphoto;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "name='" + name + '\'' +
                ", urlphoto='" + urlphoto + '\'' +
                '}';
    }


}
