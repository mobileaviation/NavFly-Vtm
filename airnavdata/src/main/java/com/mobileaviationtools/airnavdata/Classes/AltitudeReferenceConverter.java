package com.mobileaviationtools.airnavdata.Classes;

import android.arch.persistence.room.TypeConverter;

public class AltitudeReferenceConverter
{
        @TypeConverter
        public static AltitudeReference toAltitudeReference(String ref)
        {
            return AltitudeReference.valueOf(ref);
        }

        @TypeConverter
        public static String toString(AltitudeReference ref)
        {
            return ref.toString();
        }
}
