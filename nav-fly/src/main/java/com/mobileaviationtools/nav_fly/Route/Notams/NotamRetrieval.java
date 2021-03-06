package com.mobileaviationtools.nav_fly.Route.Notams;

import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavAirportInfoDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Notam;
//import com.mobileaviationtools.airnavdata.Entities.Route;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.Classes.Helpers;
import com.mobileaviationtools.nav_fly.Classes.MapperHelper;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Route.Route;
import com.mobileaviationtools.weater_notam_data.notams.NotamCount;
import com.mobileaviationtools.weater_notam_data.notams.NotamCounts;
import com.mobileaviationtools.weater_notam_data.notams.NotamResponseEvent;
import com.mobileaviationtools.weater_notam_data.notams.Notams;
import com.mobileaviationtools.weater_notam_data.services.NotamService;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotamRetrieval {
    public NotamRetrieval(GlobalVars vars)
    {
        this.vars = vars;
        setNotamResponseEvent();
    }

    private String TAG = "NotamRetrieval";

    private GlobalVars vars;
    private NotamResponseEvent notamResponseEvent;
    private NotamResponseEvent notamsRetrievedResponseEvent;
    private Integer notamsRetrieved;

    public void setNotamsRetrievedResponseEvent(NotamResponseEvent event)
    {
        notamsRetrievedResponseEvent = event;
    }
    private NotamCounts notamCounts;
    public NotamCounts getNotamCounts()
    {
        return notamCounts;
    }

    private void setNotamResponseEvent()
    {
        notamResponseEvent = new NotamResponseEvent() {
            @Override
            public void OnNotamsResponse(Notams notams, NotamCount count, String message) {

                AirnavAirportInfoDatabase db = AirnavAirportInfoDatabase.getInstance(vars.context);
                Airport a = MapperHelper.getAirport(notams.notamList[0].icaoId, vars.context);
                Log.i(TAG, "Retrieved Notams for: " + notams.notamList[0].icaoId + " Notams count: " + notams.totalNotamCount.toString());

                for (com.mobileaviationtools.weater_notam_data.notams.Notam n : notams.notamList) {
                    Notam db_notam = MapperHelper.getNotamEntity(n, a);
                    db.getNotam().InsertNotam(db_notam);
                }
                notamsRetrieved++;

                if (notamsRetrieved == notamCounts.counts.length) {
                    Log.i(TAG, "All Notams retrieved");
                    if (notamsRetrievedResponseEvent != null)
                        notamsRetrievedResponseEvent.OnNotamsCountResponse(notamCounts, message);
                }

            }

            @Override
            public void OnNotamsCountResponse(NotamCounts counts, String message) {
                notamCounts = counts;
                Log.i(TAG, "Retrieved Notams counts object: totalNumberOfNotams: " + counts.totalNumberOfNOTAMs.toString());
                //if (notamsRetrievedResponseEvent != null) notamsRetrievedResponseEvent.OnNotamsCountResponse(notamCounts, message);
                retrieveNotams(notamCounts);
            }

            @Override
            public void OnFailure(String message) {
                if (notamsRetrievedResponseEvent != null) notamsRetrievedResponseEvent.OnFailure(message);
            }
        };
    }

    private NotamCounts retrieveNotamCountsFromDB(GeoPoint location, Long radius)
    {
        NotamCounts counts = new NotamCounts();

        Long m = radius * 1609;
        Geometry c = GeometryHelpers.getCircle(location, m);

        ArrayList<NotamCount> countList = getCountsListByGeometry(c);

        if (countList.size()>0) {
            counts.counts = countList.toArray(new NotamCount[countList.size()]);
            counts.totalNumberOfNOTAMs = Long.valueOf(counts.counts.length);

            return counts;
        }

        return null;
    }

    private NotamCounts retrieveNotamCountsByBufferFromDB(Geometry buffer)
    {
        ArrayList<NotamCount> countList = getCountsListByGeometry(buffer);
        NotamCounts counts = new NotamCounts();

        if (countList.size()>0) {
            counts.counts = countList.toArray(new NotamCount[countList.size()]);
            counts.totalNumberOfNOTAMs = Long.valueOf(counts.counts.length);

            return counts;
        }

        return null;
    }

    private ArrayList<NotamCount> getCountsListByGeometry(Geometry geometry) {
        ArrayList<NotamCount> countList = new ArrayList<>();
        AirnavAirportInfoDatabase db = AirnavAirportInfoDatabase.getInstance(vars.context);

        Geometry b = geometry.getEnvelope();
        if (b.getNumPoints() > 3) {
            Coordinate[] coordinates = b.getCoordinates();
            List<Notam> stations = db.getNotam().getStationsListWithinBoundsLimit(coordinates[0].x,
                    coordinates[2].x,
                    coordinates[2].y,
                    coordinates[0].y);
            for (Notam n : stations) {
                Geometry p = new GeometryFactory().createPoint(new Coordinate(n.longitude, n.latitude));
                if (geometry.contains(p)) {
                    NotamCount count = new NotamCount();
                    count.icaoId = n.icaoId;
                    count.name = n.airportName;
                    count.notamCount = 0l;
                    count.notamsRetrievedDate = new Date(n.loadedDate);
                    countList.add(count);
                }
            }
            return countList;
        }
        return null;
    }

    private void retrieveNotams(NotamCounts counts)
    {
        for (NotamCount count : counts.counts) {
            NotamService notamService = new NotamService();
            notamService.GetNotamsByCount(count, notamResponseEvent);
        }
    }

    public void startNotamRetrieval(Boolean fromDatabase)
    {
        notamsRetrieved = 0;
        MapPosition pos = vars.map.getMapPosition();

        if (fromDatabase)
            startNotamRetrievalFromDatabaseByPoint(pos.getGeoPoint());
        else
            if (Helpers.isConnected(vars.context)) {
                NotamService notamService = new NotamService();
                notamService.GetCountsByLocationAndRadius(pos.getGeoPoint(), 100l, notamResponseEvent);
            } else
                startNotamRetrievalFromDatabaseByPoint(pos.getGeoPoint());

    }

    public void startNotamRetrievalByRouteBuffer(Boolean fromDatabase, Route route)
    {
        NotamCounts counts = new NotamCounts();
        ArrayList<NotamCount> countList = new ArrayList<>();

        if (fromDatabase)
        {
            counts = retrieveNotamCountsByBufferFromDB(route.getRouteBuffer(.25d));
        }
        else {
            ArrayList<String> airports = route.getAirportWithinRouteBuffer();

            for (String n : airports) {
                NotamCount count = new NotamCount();
                count.icaoId = n;
                count.name = n;
                count.notamCount = 0l;
                count.notamsRetrievedDate = new Date();
                countList.add(count);
            }

            counts.counts = countList.toArray(new NotamCount[countList.size()]);
            counts.totalNumberOfNOTAMs = Long.valueOf(counts.counts.length);
        }

        if (notamsRetrievedResponseEvent != null)
            notamsRetrievedResponseEvent.OnNotamsCountResponse(counts, "Notam stations from routebuffer retrieved");
    }

    private void startNotamRetrievalFromDatabaseByPoint(GeoPoint pos)
    {
        NotamCounts counts = retrieveNotamCountsFromDB(pos, 100l);
        Log.i(TAG, "Notam stations from DB retrieved");
        if (notamsRetrievedResponseEvent != null)
            notamsRetrievedResponseEvent.OnNotamsCountResponse(counts, "Notam stations from DB retrieved");
    }



}
