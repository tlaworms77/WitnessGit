package com.witness.user.data;

/**
 * Created by yarolegovich on 07.03.2017.
 */

public class Item {

    private final int id;
    private final String mainTitle;
    private final String subTitle;
    private final int image;

    public Item(int id, String mainTitle, String subTitle, int image) {
        this.id = id;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return mainTitle;
    }

    public String getPrice() {
        return subTitle;
    }

    public int getImage() {
        return image;
    }
}
