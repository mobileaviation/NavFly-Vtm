package com.mobileaviationtools.nav_fly.Settings.Services;

import com.mobileaviationtools.airnavdata.AirnavUserSettingsDatabase;
import com.mobileaviationtools.airnavdata.Classes.PropertiesGroup;
import com.mobileaviationtools.airnavdata.Classes.PropertiesName;
import com.mobileaviationtools.airnavdata.Entities.Property;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Location.LocationProviderType;

//import org.jeo.carto.Prop;

public class LocationProviderService {
    public LocationProviderService(GlobalVars vars)
    {
        this.vars = vars;
        db = AirnavUserSettingsDatabase.getInstance(vars.context);
    }

    private GlobalVars vars;
    private AirnavUserSettingsDatabase db;

    public void storeProviderType(LocationProviderType type)
    {

        Property p = db.getProperties().getPropertyByGroupAndName(PropertiesGroup.location_provider.toString(),
                PropertiesName.connection_type.toString());

        boolean insert = false;
        if (p == null) {
            p = new Property();
            p.groupname = PropertiesGroup.location_provider;
            p.name = PropertiesName.connection_type;
            insert = true;
        }

        p.value1 = type.toString();

        UpdateInsertProperty(insert, p);
    }

    public void storeNetworkConnectionInfo(String ipaddress, String port)
    {
        Property p = db.getProperties().getPropertyByGroupAndName(PropertiesGroup.location_provider.toString(),
                PropertiesName.server_network_address.toString());

        boolean insert = false;
        if (p==null)
        {
            p = new Property();
            p.groupname = PropertiesGroup.location_provider;
            p.name = PropertiesName.server_network_address;
            insert = true;
        }

        p.value1 = ipaddress;
        p.value2 = port;

        UpdateInsertProperty(insert, p);
    }

    private void UpdateInsertProperty(Boolean insert, Property property)
    {
        if(insert) db.getProperties().InsertProperty(property);
        else db.getProperties().UpdateProperty(property);
    }

    public LocationProviderType getLocationProviderType()
    {
        Property p = db.getProperties().getPropertyByGroupAndName(PropertiesGroup.location_provider.toString(),
                PropertiesName.connection_type.toString());

        if (p != null) return LocationProviderType.valueOf(p.value1);
        else
        return LocationProviderType.gps;
    }

    public String[] getNetwerkInfo()
    {
        Property p = db.getProperties().getPropertyByGroupAndName(PropertiesGroup.location_provider.toString(),
                PropertiesName.server_network_address.toString());

        String[] ret = new String[2];
        if (p!= null) {
            ret[0] = p.value1;
            ret[1] = p.value2;
        }
        else{
            ret[0] = "0.0.0.0";
            ret[1] = "81";
        }

        return ret;
    }
}
