package com.mobileaviationtools.nav_fly.Tracks;

import com.mobileaviationtools.nav_fly.Route.HeightMap.ExtCoordinate;
import com.mobileaviationtools.nav_fly.Route.HeightMap.TrackPoints;

import org.oscim.core.GeoPoint;
import org.oscim.layers.PathLayer;
import org.oscim.map.Map;

public class PlaybackLayer extends PathLayer {
    public PlaybackLayer(Map map, int lineColor, float lineWidth) {
        super(map, lineColor, lineWidth);
    }

    public void DrawTrack(TrackPoints points)
    {
        for (ExtCoordinate e: points)
            this.addPoint(new GeoPoint(e.y, e.x));
    }

    public void ClearTrack()
    {
        this.clearPath();
    }
}
