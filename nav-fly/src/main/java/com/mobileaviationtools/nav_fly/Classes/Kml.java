package com.mobileaviationtools.nav_fly.Classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "kml")
public class Kml {
    @Root
    public static class GroundOverlay
    {
        @Element(name = "LatLonBox")
        public LatLonBox latLonBox;

        @Root
        public static class LatLonBox
        {
            @Element
            public double north;
            @Element
            public double south;
            @Element
            public double east;
            @Element
            public double west;
        }
    }

    @Element(name = "GroundOverlay")
    public GroundOverlay groundOverlay;


}
