package com.example.kos;

class ConstrThemeRecycler {
    private String name;
    private final String[] colors;
    private final int ColorBolder;
    private final int ColorIcon;
    private final int IdSwitch;

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

    public void setName(String name) {
        this.name = name;
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