package org.oscim.android.Utils;

import android.location.Location;

public class StringFormatter {

    public static String convertLongitude(double longitude)
    {
        StringBuilder builder = new StringBuilder();

        if (longitude < 0) {
            builder.append("W ");
        } else {
            builder.append("E ");
        }

        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        return builder.toString();

    }

    public static String convertLatitude(double latitude)
    {
        StringBuilder builder = new StringBuilder();

        if (latitude < 0) {
            builder.append("S ");
        } else {
            builder.append("N ");
        }

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        return builder.toString();

    }

    public static String convertLocation(double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();
        builder.append(convertLatitude(latitude));
        builder.append(" ");
        builder.append(convertLongitude(longitude));
        return builder.toString();
    }
}
