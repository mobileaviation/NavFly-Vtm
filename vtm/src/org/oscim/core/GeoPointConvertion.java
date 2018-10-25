package org.oscim.core;

public class GeoPointConvertion {
    public class DMS
    {
        public DMS()
        {
            degrees = 0l;
            minutes = 0l;
            seconds = 0l;
            direction = "N";
        }

        public Long degrees;
        public Long minutes;
        public Long seconds;
        public String direction;
    }

    public class GeoPointDMS
    {
        public GeoPointDMS()
        {
            latitude = new DMS();
            latitude.direction = "N";
            longitude = new DMS();
            longitude.direction = "W";
        }

        public DMS latitude;
        public DMS longitude;
    }

    public GeoPointDMS getGeoPointDMS(GeoPoint location)
    {
        GeoPointDMS pointDMS = new GeoPointDMS();
        pointDMS.latitude = getDms(location.getLatitude(), true);
        pointDMS.longitude = getDms(location.getLongitude(), false);
        return pointDMS;
    }

    public String getStringFormattedDMS(GeoPoint location)
    {
        GeoPointDMS dms = getGeoPointDMS(location);
        String loc = dms.latitude.direction + dms.latitude.degrees.toString() + "º"
        + dms.latitude.minutes.toString() + "'"  + dms.latitude.seconds.toString()  + "\""
        + dms.longitude.direction + String.format("%03d", dms.longitude.degrees) + "º"
        //        + dms.longitude.degrees.toString() + "º"
                + dms.longitude.minutes.toString() + "'"  + dms.longitude.seconds.toString() + "\"";

        return loc;
    }

    public GeoPointDMS getNewGeoPointDMS()
    {
        return new GeoPointDMS();
    }

    private DMS getDms(double val, boolean lat)
    {
        DMS dms = new DMS();
        dms.degrees = (long)val;
        double t1 = (val - dms.degrees) * 60;
        dms.minutes = (long)t1;
        double s = (t1 - dms.minutes) * 60;
        dms.seconds = Math.round(s);

        if (val>0)
            dms.direction = (lat)? "N" : "E";
        else
            dms.direction = (lat)? "S" : "W";

        return dms;
    }
}
