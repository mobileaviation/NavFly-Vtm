package com.mobileaviationtools.nav_fly.Layers;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.Markers.Route.RouteLegSymbol;
import com.mobileaviationtools.nav_fly.Route.Leg;

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.Drawable;
import org.oscim.layers.vector.geometries.LineDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;

import java.util.ArrayList;

public class DeviationLineLayer extends VectorLayer {
    public DeviationLineLayer(Map map) {
        super(map);
    }

    private Integer selectedLegColor = 0xFFFFBC00;
    private Integer lineWidth = 10;
    private Drawable deviationLineDrawable1;
    private Drawable deviationLineDrawable2;

    private MarkerItem legSymbolItem;

    private ItemizedLayer symbolLayer;
    private GlobalVars vars;

    private Leg deviationLeg;

    public void setupLayers(GlobalVars vars)
    {
        this.vars = vars;
        MarkerSymbol s = null;
        symbolLayer = new ItemizedLayer(vars.map, s);

        vars.map.layers().add(this);
        vars.map.layers().add(symbolLayer);
    }

    public void drawDeviationLine(FspLocation startPoint, FspLocation endPoint)
    {
        clearLine();
        Style lineStyle1 = Style.builder()
                .fixed(true)
                .strokeColor(selectedLegColor)
                .strokeWidth(lineWidth)
                .build();

        ArrayList<GeoPoint> points = new ArrayList<>();
        points.add(startPoint.getGeopoint());
        points.add(endPoint.getGeopoint());
        deviationLineDrawable1 = new LineDrawable(points, lineStyle1);

        Style lineStyle2 = Style.builder()
                .fixed(true)
                .strokeColor(Color.DKGRAY)
                .strokeWidth(lineWidth + 4)
                .build();
        deviationLineDrawable2 = new LineDrawable(points, lineStyle2);

        if (legSymbolItem != null) symbolLayer.removeItem(legSymbolItem);

        deviationLeg = new Leg(startPoint.getGeopoint(), endPoint.getGeopoint(), vars.context);
        legSymbolItem = new MarkerItem("DeviationSymbol", "DeviationSymbol", endPoint.getGeopoint());
        legSymbolItem.setMarker(deviationLeg.symbol);
        legSymbolItem.setRotation((float)deviationLeg.getBearing()+90);
        symbolLayer.addItem(legSymbolItem);
        symbolLayer.update();

        this.add(deviationLineDrawable1);
        this.add(deviationLineDrawable2);
    }

    private void clearLine()
    {
        if (deviationLineDrawable1 != null)
        {
            this.remove(deviationLineDrawable1);
            this.remove(deviationLineDrawable2);
        }
    }
}
