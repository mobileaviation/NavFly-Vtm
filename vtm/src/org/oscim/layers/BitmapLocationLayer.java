package org.oscim.layers;

import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.map.Map;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.renderer.LayerRenderer;

public class BitmapLocationLayer extends GenericLayer implements Map.UpdateListener {
    /**
     * @param map      ...
     * @param renderer ...
     */
    public BitmapLocationLayer(Map map, LayerRenderer renderer) {
        super(map, new BitmapRenderer());
    }

    public BitmapRenderer getBitmapRenderer() {
        return (BitmapRenderer) mRenderer;
    }

    @Override
    public void onMapEvent(Event e, MapPosition mapPosition) {
        int i=0;
        getBitmapRenderer().update(new GLViewport());

    }
}
