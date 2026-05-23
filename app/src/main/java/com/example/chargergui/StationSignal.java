package com.example.chargergui;

public enum StationSignal {
    BOOTING("Amber", "#D97706", "Connecting"),
    READY("Green", "#16803C", "Available"),
    AUTHORIZE("Blue", "#2563EB", "Authorize"),
    PLUG_IN("Amber", "#D97706", "Plug in"),
    CHARGING("Blue", "#0E7490", "Charging"),
    COMPLETE("White", "#F8FAFC", "Complete"),
    FAULT("Red", "#B42318", "Attention");

    private final String colorName;
    private final String colorHex;
    private final String label;

    StationSignal(String colorName, String colorHex, String label) {
        this.colorName = colorName;
        this.colorHex = colorHex;
        this.label = label;
    }

    public String getColorName() {
        return colorName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public String getLabel() {
        return label;
    }
}
