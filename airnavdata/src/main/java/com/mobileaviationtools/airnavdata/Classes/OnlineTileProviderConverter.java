package com.mobileaviationtools.airnavdata.Classes;

import android.arch.persistence.room.TypeConverter;

import com.mobileaviationtools.airnavdata.Entities.OnlineTileProvider;

public class OnlineTileProviderConverter {
    @TypeConverter
    public static OnlineTileProviders toOnlineTileProviders(String provider)
    {
        return OnlineTileProviders.valueOf(provider);
    }

    @TypeConverter
    public static String toString(OnlineTileProviders provider)
    {
        return provider.toString();
    }
}
