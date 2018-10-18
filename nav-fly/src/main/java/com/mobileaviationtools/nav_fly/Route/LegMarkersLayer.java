package com.mobileaviationtools.nav_fly.Route;

import android.os.AsyncTask;

import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;

import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;

public class LegMarkersLayer extends ItemizedLayer {
    private Route route;

    public LegMarkersLayer(Map map, MarkerSymbol defaultMarker) {
        super(map, defaultMarker);
    }

    private void placeMarkers()
    {
        for (Leg l : route.getLegs())
        {
            GeoPoint p = GeometryHelpers.midPoint(l.startWaypoint.point, l.endWaypoint.point);
            MarkerItem item = new MarkerItem("", "", p);
            item.setMarker(l.symbol);
            item.setRotation((float)l.getBearing()+90);
            this.addItem(item);
        }
    }

    private void removeMarkers()
    {
        this.removeAllItems();
    }

    public void UpdateLegs(Route route)
    {
        this.route = route;

        class UpdateMapAsync extends AsyncTask<Void, Void, Void>
        {
            @Override
            protected Void doInBackground(Void... voids) {
                //initVisibleTypes(mMap.getMapPosition().zoomLevel);
                removeMarkers();
                placeMarkers();
                //updateLayer();
                update();
                return null;
            }
        }

        new UpdateMapAsync().execute();
        //new UpdateMapAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }
}
