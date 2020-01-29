package com.example.kos;

public class ConstrThemeRecycler {
    String name;
    String[] colors;
    int ColorBolder, ColorIcon, IdSwitch;

    public ConstrThemeRecycler(String[] colors) {
        IdSwitch = Integer.parseInt(colors[0]);
        this.name = colors[1];
        ColorIcon = Integer.parseInt(colors[2]);
        ColorBolder = Integer.parseInt(colors[3]);
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public String[] getColors() {
        return colors;
    }

    public int getColorBolder() {
        return ColorBolder;
    }

    public int getColorIcon() {
        return ColorIcon;
    }

    public int getIdSwitch() {
        return IdSwitch;
    }
}
