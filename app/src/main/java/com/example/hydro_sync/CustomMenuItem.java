package com.example.hydro_sync;

public class CustomMenuItem {
    private String name;
    private int iconResId;

    public CustomMenuItem(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}
