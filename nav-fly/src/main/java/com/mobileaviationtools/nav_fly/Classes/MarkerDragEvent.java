package com.mobileaviationtools.nav_fly.Classes;

import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;

public interface MarkerDragEvent {
    void StartMarkerDrag(MarkerItem marker);
    void MarkerDragging(MarkerItem marker, GeoPoint newLocation);
    void EndMarkerDrag(MarkerItem markerItem, GeoPoint newLocation);
}
