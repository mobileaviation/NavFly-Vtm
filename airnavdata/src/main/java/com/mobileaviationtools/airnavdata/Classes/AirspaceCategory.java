package com.mobileaviationtools.airnavdata.Classes;

import android.graphics.Color;

public enum AirspaceCategory {
    // outlineColor, outlineWidth, strokeColor, strokeWidth, fillColor, zoomlevel, visible
    A (Color.BLACK, 0, 0xAF000000,3, Color.TRANSPARENT, 0, true),
    AWY (Color.GREEN, 2, Color.YELLOW,4, Color.TRANSPARENT, 0, false),
    B (Color.BLACK, 0, 0xBF000000,2, Color.TRANSPARENT, 0, true),
    C (0xFFE4A19E, 1, 0x60E4A19E,2, Color.TRANSPARENT, 0, true),
    CTR (Color.BLACK, 2, 0xA0E4A19E ,6, 0x30E4A19E, 0, true),
    D (Color.BLACK, 0, 0xFFE4A19E ,4, 0x30E4A19, 0, true),
    DANGER (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, 0, true),
    Q (Color.BLACK, 0, 0xFFC06E8E,4, 0x50C06E8E, 0, true),
    E (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, true),
    F (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, true),
    G (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, true),
    GSEC (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false),
    GP (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false),
    GLIDING (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false),
    OTH (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false),
    RESTRICTED (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, 0, true),
    R (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, 0, true),
    TMA (Color.BLACK, 0, 0xFF7C6D92,4, Color.TRANSPARENT, 0, true),
    TMZ (Color.BLACK, 0, 0xFFC3819E,5, Color.TRANSPARENT, 0, true),
    TSA (Color.BLACK, 0, 0xFF7C6D92,4, Color.TRANSPARENT, 0, true),
    WAVE (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, true),
    W (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false),
    PROHIBITED (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, 0, true),
    P (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, 0, true),
    FIR (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, true),
    UIR (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, true),
    RMZ (Color.BLACK, 0, 0xFFC06E8E,4, Color.TRANSPARENT, 0, true),
    Z (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false),
    ZP (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false),
    UKN (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false),
    IGA (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false);

    private Integer outlineColor;
    private Integer outlineWidth;
    private Integer strokeColor;
    private Integer strokeWidth;
    private Integer fillColor;
    private Integer zoomLevel;
    private Boolean visible;
    AirspaceCategory(Integer outlineColor,
                     Integer outlineWidth,
                     Integer strokeColor,
                     Integer strokeWidth,
                     Integer fillColor,
                     Integer zoomLevel,
                     Boolean visible)
    {
        this.outlineColor = outlineColor;
        this.outlineWidth = outlineWidth;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.fillColor = fillColor;
        this.zoomLevel = zoomLevel;
        this.visible = visible;
    }

    public Integer getOutlineColor(){return outlineColor;}
    public Integer getOutlineWidth(){return outlineWidth;}
    public Integer getFillColor() {return fillColor;}
    public Integer getStrokeColor(){return strokeColor;}
    public Integer getStrokeWidth(){return strokeWidth;}
    public Boolean getVisible(){return visible;}
    public Integer getZoomLevel() { return zoomLevel; }

    @Override
    public String toString() {
        return super.toString();
    }
}
