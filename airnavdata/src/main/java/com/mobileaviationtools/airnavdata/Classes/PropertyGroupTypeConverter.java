package com.mobileaviationtools.airnavdata.Classes;

import android.arch.persistence.room.TypeConverter;

public class PropertyGroupTypeConverter {
    @TypeConverter
    public static PropertiesGroup toProperiesGroup(String group)
    {
        return PropertiesGroup.valueOf(group);
    }

    @TypeConverter
    public static String toString(PropertiesGroup group)
    {
        return group.toString();
    }
}
