package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.mobileaviationtools.airnavdata.Classes.NavaidType;
import com.mobileaviationtools.airnavdata.Classes.NavaidTypeConverter;

@Entity(tableName = "tbl_Navaids",
        indices = {@Index(name = "naviad_location_index", value = {"latitude_deg","longitude_deg"}),
                @Index(name="navaid_ident_index", value = {"ident"}),
                @Index(name="navaid_airport_id_index", value = {"associated_airport_id"}),
                @Index(name="navaid_id_index", value = {"id"})})
public class Navaid {
    public Navaid()
    {

    }

    @Ignore
    public Integer index;

    @PrimaryKey
    public Integer id;
    public String filename;
    public String ident;
    public String name;
    @TypeConverters({NavaidTypeConverter.class})
    public NavaidType type;
    public double frequency_khz;
    public double latitude_deg;
    public double longitude_deg;
    public Integer elevation_ft;
    public String iso_country;
    public double dme_frequency_khz;
    public double dme_channel;
    public double dme_latitude_deg;
    public double dme_longitude_deg;
    public Integer dme_elevation_ft;
    public double slaved_variation_deg;
    public double magnetic_variation_deg;
    public String usageType;
    public String power;
    public String associated_airport;
    public Integer associated_airport_id;

    public void setType(String typeString)
    {
        NavaidType a = NavaidType.VOR;
        if (typeString.equals("DME")) a = NavaidType.DME;
        if (typeString.equals("NDB")) a = NavaidType.NDB;
        if (typeString.equals("NDB-DME")) a = NavaidType.NDB_DME;
        if (typeString.equals("TACAN")) a = NavaidType.TACAN;
        if (typeString.equals("VOR-DME")) a = NavaidType.VOR_DME;
        if (typeString.equals("VORTAC")) a = NavaidType.VORTAC;

        type = a;
    }
}
