package com.example.k_dev_master.memorygame;

public class Card {
    private int display;
    private String tag;
    private int check;

    Card(int display, String tag) {
        this.display = display;
        this.tag = tag;
        this.check = 0;
    }

    int getDisplay() {
        return display;
    }

    String getTag() {
        return tag;
    }

    int getCheck() {
        return check;
    }

    void setDisplay(int display) {
        this.display = display;
    }

    void setTag(String tag) {
        this.tag = tag;
    }

    void setCheck(int check) {
        this.check = check;
    }
}
