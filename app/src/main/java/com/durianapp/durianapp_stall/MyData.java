package com.durianapp.durianapp_stall;

/**
 * Created by Amer S Alkatheri on 15-Jun-17.
 */

class MyData {
    //To Fetch Data from MySql
    private int id;
    private String name, image_link;

    public MyData(int id, String name, String image_link) {
        this.id = id;
        this.name = name;
        this.image_link = image_link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }
}
