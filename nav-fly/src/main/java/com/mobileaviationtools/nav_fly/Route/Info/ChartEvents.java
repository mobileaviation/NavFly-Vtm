package com.mobileaviationtools.nav_fly.Route.Info;

import com.mobileaviationtools.airnavdata.Entities.Chart;

public interface ChartEvents {
    public void OnChartCheckedEvent(Chart chart, Boolean checked);
    public void OnChartSelected(Chart chart);
}
