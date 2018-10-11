package org.oscim.core;

public class GeoPointConvertion {
    public class DMS
    {
        public Long degrees;
        public Long minutes;
        public Long seconds;
        public String direction;
    }

    public class GeoPointDMS
    {
        public DMS latitude;
        public DMS longitude;
    }

    public GeoPointDMS getGeoPointDMS(GeoPoint location)
    {
        GeoPointDMS pointDMS = new GeoPointDMS();
        pointDMS.latitude = getDms(location.getLatitude());
        pointDMS.longitude = getDms(location.getLongitude());
        return pointDMS;
    }

    private DMS getDms(double val)
    {
        DMS dms = new DMS();
        dms.degrees = Math.round(val);
        double t1 = (val - dms.degrees) * 60;
        dms.minutes = Math.round(t1);
        dms.seconds = Math.round((t1 - dms.minutes) * 60);

        return dms;
    }
}
