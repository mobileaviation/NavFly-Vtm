package com.mobileaviationtools.airnavdata.Classes;

import android.arch.persistence.room.TypeConverter;

public class NavaidTypeConverter {
    @TypeConverter
    public static NavaidType toNavaidType(String type)
    {
        NavaidType a = NavaidType.VOR;
        if (type.equals("DME")) a = NavaidType.DME;
        if (type.equals("NDB")) a = NavaidType.NDB;
        if (type.equals("NDB-DME")) a = NavaidType.NDB_DME;
        if (type.equals("TACAN")) a = NavaidType.TACAN;
        if (type.equals("VOR-DME")) a = NavaidType.VOR_DME;
        if (type.equals("VORTAC")) a = NavaidType.VORTAC;

        return a;
    }

    @TypeConverter
    public static String fromNavaidType(NavaidType type)
    {
        if (type == NavaidType.NDB_DME) return "NDB-DME";
        if (type == NavaidType.VOR_DME) return "VOR-DME";
        return type.toString();
    }
}
