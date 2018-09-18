package com.mobileaviationtools.airnavdata.Classes;

import android.graphics.Color;

public enum AirspaceCategory {
    // outlineColor, outlineWidth, strokeColor, strokeWidth, fillColor, zoomlevel, texture, visible
    A (Color.BLACK, 0, 0xAF000000,3, Color.TRANSPARENT, 0 , false , true),
    AWY (Color.GREEN, 2, Color.YELLOW,4, Color.TRANSPARENT, 0, false , false),
    B (Color.BLACK, 0, 0xBF000000,2, Color.TRANSPARENT, 0, false,  true),
    C (Color.DKGRAY, 30, 0xA0E4A19E ,20, 0x70E4A19E ,0, true, false),
    CTR (Color.DKGRAY, 30, 0xA0E4A19E ,20, 0x70E4A19E ,0, true, true),
    //D (0xFFFBA642, 5, 0xFFFBA642, 15, 0x30E4A19, 0, true, true),
    D (Color.DKGRAY, 30, 0xA0E4A19E ,20, 0x70E4A19E ,0, true, true),
    DANGER (0xFFFBA642, 5, 0xFFFBA642, 15, 0x10FBA642, 0, true, true),
    Q (Color.BLACK, 0, 0xFFC06E8E,4, 0x50C06E8E, 0, false, true),
    E (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, false, true),
    F (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, false, true),
    G (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, false, true),
    GSEC (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false, false),
    GP (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false, false),
    GLIDING (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false, false),
    OTH (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false, false),
    RESTRICTED (0xFFFBA642, 5, 0xFFFBA642, 15, 0x10FBA642, 0, true, true),
    R (0xFFFBA642, 5, 0xFFFBA642, 15, 0x10FBA642, 0, true, true),
    TMA (Color.BLACK, 0, 0xFF7C6D92,4, Color.TRANSPARENT, 0, false, true),
    TMZ (Color.BLACK, 0, 0xFFC3819E,5, Color.TRANSPARENT, 0, false, true),
    TSA (Color.BLACK, 0, 0xFF7C6D92,4, Color.TRANSPARENT, 0, false, true),
    WAVE (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false, true),
    W (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false, false),
    PROHIBITED (0xFFFBA642, 5, 0xFFFBA642, 15, 0x10FBA642, 0, true, true),
    P (0xFFFBA642, 5, 0xFFFBA642, 15, 0x10FBA642, 0, true, true),
    FIR (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, false, true),
    UIR (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, false, true),
    RMZ (Color.BLACK, 0, 0xFFC06E8E,4, Color.TRANSPARENT, 0, false, false),
    Z (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false, false),
    ZP (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false, false),
    UKN (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false, false),
    IGA (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, false, false);

    private Integer outlineColor;
    private Integer outlineWidth;
    private Integer strokeColor;
    private Integer strokeWidth;
    private Integer fillColor;
    private Integer zoomLevel;
    private Boolean visible;
    private Boolean texture;
    AirspaceCategory(Integer outlineColor,
                     Integer outlineWidth,
                     Integer strokeColor,
                     Integer strokeWidth,
                     Integer fillColor,
                     Integer zoomLevel,
                     Boolean texture,
                     Boolean visible)
    {
        this.outlineColor = outlineColor;
        this.outlineWidth = outlineWidth;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.fillColor = fillColor;
        this.zoomLevel = zoomLevel;
        this.texture = texture;
        this.visible = visible;
    }

    public Integer getOutlineColor(){return outlineColor;}
    public Integer getOutlineWidth(){return outlineWidth;}
    public Integer getFillColor() {return fillColor;}
    public Integer getStrokeColor(){return strokeColor;}
    public Integer getStrokeWidth(){return strokeWidth;}
    public Boolean getVisible(){return visible;}
    public Integer getZoomLevel() { return zoomLevel; }
    public Boolean getTexture() { return texture; }

    @Override
    public String toString() {
        return super.toString();
    }
}
