package com.mobileaviationtools.airnavdata.Classes;

public enum OnlineTileProviders {
    openstreetmap ("Openstreetmaps", "https://a.tile.openstreetmap.org/[Z]/[X]/[Y].png"),
    openstreetmap_nolabels ("Openstreetmaps No Labels","http://b.tiles.wmflabs.org/osm-no-labels/[Z]/[X]/[Y].png"),
    aaf_sectional ("AAF Sectional Chart","http://wms.chartbundle.com/tms/1.0.0/sec/[Z]/[X]/[Y].png?origin=nw"),
    aaf_terminal ("AAF Terminal Chart","http://wms.chartbundle.com/tms/1.0.0/tac/[Z]/[X]/[Y].png?origin=nw"),
    aaf_world ("AAF World Chart","http://wms.chartbundle.com/tms/1.0.0/hel/[Z]/[X]/[Y].png?origin=nw"),
    aaf_ifr_enroute_low ("AAF IFR Enroute Low Chart","http://wms.chartbundle.com/tms/1.0.0/enrl/[Z]/[X]/[Y].png?origin=nw"),
    aaf_ifr_enroute_high ("AAF IFR Enroute High Chart","http://wms.chartbundle.com/tms/1.0.0/enrh/[Z]/[X]/[Y].png?origin=nw"),
    aaf_ifr_area ("AAF IFR Area Chart","http://wms.chartbundle.com/tms/1.0.0/enra/[Z]/[X]/[Y].png?origin=nw"),
    hillshading ("Hillshading Chart","https://tiles.wmflabs.org/hillshading/[Z]/[X]/[Y].png"),
    skylines_airspaces ("Skylines Airspaces","https://skylines.aero/mapproxy/tiles/1.0.0/airspace/[Z]/[X]/[Y].png"),
    nexrad_weatherradar_5min ("Nexrad NA weather Radar 5mins old.","https://mesonet.agron.iastate.edu/cache/tile.py/1.0.0/nexrad-n0q-900913/[Z]/[X]/[Y].png");

    OnlineTileProviders(String name, String base_url) {
        this.name = name;
        this.base_url = base_url;
    }

    private String name;
    private String base_url;

    public String getName() { return name; }
    public String getBaseUrl() { return base_url; }

    @Override
    public String toString()
    {
        return this.toString();
    }

}
