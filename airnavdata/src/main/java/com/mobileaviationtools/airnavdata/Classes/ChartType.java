package com.mobileaviationtools.airnavdata.Classes;

import java.io.File;

public enum ChartType {
    png,
    jpg,
    pdf,
    mbtiles,
    unknown;

    @Override
    public String toString() {
        return super.toString();
    }

    public static ChartType getTypeByExtention(File file)
    {
        if (file.getName().toLowerCase().endsWith("png")) return png;
        if (file.getName().toLowerCase().endsWith("jpg")) return jpg;
        if (file.getName().toLowerCase().endsWith("pdf")) return pdf;
        if (file.getName().toLowerCase().endsWith("mbtiles")) return png;
        return unknown;
    }
}
