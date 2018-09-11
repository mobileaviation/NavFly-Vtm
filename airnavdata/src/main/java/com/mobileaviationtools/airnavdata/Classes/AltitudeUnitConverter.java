package com.mobileaviationtools.airnavdata.Classes;

import android.arch.persistence.room.TypeConverter;

public class AltitudeUnitConverter {
    @TypeConverter
    public static AltitudeUnit toAltitudeUnit(String unit)
    {
        return AltitudeUnit.valueOf(unit);
    }

    @TypeConverter
    public static String toString(AltitudeUnit unit)
    {
        return unit.toString();
    }
}
