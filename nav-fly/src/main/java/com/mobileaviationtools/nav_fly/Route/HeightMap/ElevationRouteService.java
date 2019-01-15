package com.mobileaviationtools.nav_fly.Route.HeightMap;

import com.example.aircraft.ClimbAndDecent;
import com.example.aircraft.Enums.FlightStage;
import com.google.gson.Gson;
import com.mobileaviationtools.airnavdata.AirnavRouteDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Route.Route;
import com.mobileaviationtools.nav_fly.Route.Waypoint;
import com.mobileaviationtools.nav_fly.Route.WaypointType;
import com.mobileaviationtools.weater_notam_data.Elevation.ElevationResponseEvent;
import com.mobileaviationtools.weater_notam_data.Elevation.elevation;
import com.mobileaviationtools.weater_notam_data.services.ElevationService;

import org.locationtech.jts.geom.Coordinate;
import org.oscim.core.GeoPoint;

public class ElevationRouteService {
    private final double meterToFeet = 3.2808399;
    private Double pointsCount;
    private GlobalVars vars;

    public ElevationRouteService(Route route, Double pointsCount)
    {
        this.route = route;
        this.pointsCount = pointsCount;
    }

    public void startProcessElevation(Coordinate[] points, RoutePoints.RoutePointsEvents routePointsEvents, boolean fromService)
    {
        if (fromService)
            doServiceCall(points, routePointsEvents);
        else
        {
            if (route.elevation_json != null)
            {
                if (route.elevation_json.length()>10)
                {
                    elevation _elevation = new Gson().fromJson(route.elevation_json, elevation.class);
                    calculateHeights(_elevation);
                    if (routePointsEvents != null)
                        routePointsEvents.OnPointsLoaded(ElevationRouteService.this.route.getRoutePoints());
                }
                else
                {
                    doServiceCall(points, routePointsEvents);
                }
            }
            else
            {
                doServiceCall(points, routePointsEvents);
            }
        }
    }

    private Route route;

    private void doServiceCall(Coordinate[] points, final RoutePoints.RoutePointsEvents routePointsEvents)
    {
        ElevationService service = new ElevationService();
        service.getElevationsByPolyline(points, Math.round(pointsCount),
                new ElevationResponseEvent() {
                    @Override
                    public void OnElevationResponse(elevation resp, String elevationJson, String message) {
                        ElevationRouteService.this.route.elevation_json = elevationJson;
                        ElevationRouteService.this.updateDBRoute();
                        calculateHeights(resp);
                        if (routePointsEvents != null)
                            routePointsEvents.OnPointsLoaded(ElevationRouteService.this.route.getRoutePoints());
                    }

                    @Override
                    public void OnFailure(String message) {

                    }
                });
    }

    private void calculateHeights(elevation elevation)
    {
        Integer[] elv = elevation.resourceSets[0].resources[0].elevations;
        double startElevation = getStartElevation(elv[0]);

        RoutePoints routePoints = this.route.getRoutePoints();
        Integer count = (routePoints.size()<elv.length) ? routePoints.size() : elv.length;

        double climbAdd = 0;
        FlightStage flightStage = FlightStage.platform;

        for (Integer i=0; i<count; i++)
        {
            ExtCoordinate cc = routePoints.get(i);
            cc.elevation = elv[i] * meterToFeet;

            if (i>0) flightStage = routePoints.get(i-1).stage;
            if (i==1) {
                ClimbAndDecent climbAndDecent = new ClimbAndDecent();
                climbAdd = climbAndDecent.ClimbValuePerDistance(route.getAircraft(),
                       cc.distanceToNext_meter);
                flightStage = FlightStage.climb;
            }

            if (flightStage==FlightStage.platform)
            {
                cc.altitude = startElevation;
            }

            if (flightStage==FlightStage.climb)
            {
                cc.altitude = startElevation + (i*climbAdd);
                if (cc.altitude > route.getProposedAltitude())
                {
                    cc.altitude = route.getProposedAltitude();
                    cc.stage = FlightStage.cruise;
                }
                else
                {
                    cc.stage = FlightStage.climb;
                }
            }

            if (flightStage == FlightStage.cruise)
            {
                cc.altitude = route.getProposedAltitude();
                cc.stage = FlightStage.cruise;
            }
        }
    }

    private double getStartElevation(Integer startElevation)
    {
        double s = startElevation;
        if(route.get(0).type==WaypointType.airport)
        {
            Waypoint w = route.get(0);
            if (w.ref instanceof Airport)
            {
                Airport airport = (Airport)w.ref;
                s = airport.elevation_ft;
            }
        }

        return s;
    }

    private void updateDBRoute()
    {
        if (route.id>0) {
            AirnavRouteDatabase db = AirnavRouteDatabase.getInstance(route.vars.context);
            db.getRoute().UpdateRouteElevationJson(route.elevation_json, route.id);
        }
    }
}
