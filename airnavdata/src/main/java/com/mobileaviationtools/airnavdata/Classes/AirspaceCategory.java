package com.mobileaviationtools.airnavdata.Classes;

import android.graphics.Color;

public enum AirspaceCategory {
    // outlineColor, outlineWidth, strokeColor, strokeWidth, fillColor, zoomlevel, bufferWidth, visible
    A (Color.BLACK, 0, 0xAF000000,3, Color.TRANSPARENT, 0 , 0d , true),
    AWY (Color.GREEN, 2, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d , false),
    B (Color.BLACK, 0, 0xBF000000,2, Color.TRANSPARENT, 0, 0d,  true),
    C (0xFFE4A19E, 1, 0x60E4A19E,2, Color.TRANSPARENT, 0, 0d, true),
    CTR (Color.BLACK, 2, 0xA0E4A19E ,6, 0x30E4A19E, 0, -0.005d, true),
    D (Color.BLACK, 0, 0xFFE4A19E ,4, 0x30E4A19, 0, 0d, true),
    DANGER (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, 0, 0d, true),
    Q (Color.BLACK, 0, 0xFFC06E8E,4, 0x50C06E8E, 0, 0d, true),
    E (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, 0d, true),
    F (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, 0d, true),
    G (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, 0d, true),
    GSEC (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d, false),
    GP (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d, false),
    GLIDING (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d, false),
    OTH (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d, false),
    RESTRICTED (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, 0, 0d, true),
    R (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, 0, 0d, true),
    TMA (Color.BLACK, 0, 0xFF7C6D92,4, Color.TRANSPARENT, 0, 0d, true),
    TMZ (Color.BLACK, 0, 0xFFC3819E,5, Color.TRANSPARENT, 0, 0d, true),
    TSA (Color.BLACK, 0, 0xFF7C6D92,4, Color.TRANSPARENT, 0, 0d, true),
    WAVE (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d, true),
    W (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d, false),
    PROHIBITED (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, 0, 0d, true),
    P (Color.BLACK, 0, 0xFFFBA642,2, 0x10FBA642, 0, 0d, true),
    FIR (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, 0d, true),
    UIR (Color.BLACK, 0, 0xAF000000,2, Color.TRANSPARENT, 0, 0d, true),
    RMZ (Color.BLACK, 0, 0xFFC06E8E,4, Color.TRANSPARENT, 0, 0d, true),
    Z (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d, false),
    ZP (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d, false),
    UKN (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d, false),
    IGA (Color.BLACK, 0, Color.YELLOW,4, Color.TRANSPARENT, 0, 0d, false);

    private Integer outlineColor;
    private Integer outlineWidth;
    private Integer strokeColor;
    private Integer strokeWidth;
    private Integer fillColor;
    private Integer zoomLevel;
    private Boolean visible;
    private Double bufferWidth;
    AirspaceCategory(Integer outlineColor,
                     Integer outlineWidth,
                     Integer strokeColor,
                     Integer strokeWidth,
                     Integer fillColor,
                     Integer zoomLevel,
                     Double bufferWidth,
                     Boolean visible)
    {
        this.outlineColor = outlineColor;
        this.outlineWidth = outlineWidth;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.fillColor = fillColor;
        this.zoomLevel = zoomLevel;
        this.bufferWidth = bufferWidth;
        this.visible = visible;
    }

    public Integer getOutlineColor(){return outlineColor;}
    public Integer getOutlineWidth(){return outlineWidth;}
    public Integer getFillColor() {return fillColor;}
    public Integer getStrokeColor(){return strokeColor;}
    public Integer getStrokeWidth(){return strokeWidth;}
    public Boolean getVisible(){return visible;}
    public Integer getZoomLevel() { return zoomLevel; }
    public Double getBufferWidth() { return bufferWidth; }

    @Override
    public String toString() {
        return super.toString();
    }
}
