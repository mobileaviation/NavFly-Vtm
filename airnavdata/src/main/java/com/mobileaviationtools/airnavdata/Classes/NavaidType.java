package com.mobileaviationtools.airnavdata.Classes;

import com.google.gson.annotations.SerializedName;

public enum NavaidType {
    DME,
    NDB,
    @SerializedName("NDB-DME") NDB_DME,
    TACAN,
    VOR,
    @SerializedName("VOR-DME") VOR_DME,
    VORTAC

}
