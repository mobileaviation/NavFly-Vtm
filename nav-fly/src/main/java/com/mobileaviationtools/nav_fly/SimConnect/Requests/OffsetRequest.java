package com.mobileaviationtools.nav_fly.SimConnect.Requests;

import com.mobileaviationtools.nav_fly.Location.FSUIPCDataType;

public class OffsetRequest {
    public String Address;
    public String DataGroup;
    public String DataType;
    public String Value;

    public static OffsetRequest getInstance(Integer address, String dataGroup, FSUIPCDataType dataType)
    {
        OffsetRequest offsetRequest = new OffsetRequest();
        offsetRequest.Address = Integer.toString(address);
        offsetRequest.DataGroup = dataGroup;
        offsetRequest.DataType = dataType.toString();
        offsetRequest.Value = "-";
        return offsetRequest;
    }
}
