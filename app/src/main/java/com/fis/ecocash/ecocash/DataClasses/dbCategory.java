package com.fis.ecocash.ecocash.DataClasses;

/**
 * Created by ashbelh on 27/4/2018.
 */

public class dbCategory {
    private  long id;
    private  String category;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "dbCategory{" +
                "id=" + id +
                ", category='" + category + '\'' +
                '}';
    }
}
