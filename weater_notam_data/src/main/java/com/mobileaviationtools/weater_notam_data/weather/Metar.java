package com.mobileaviationtools.weater_notam_data.weather;

import android.content.Context;
import android.util.Log;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Root(name="METAR")
public class Metar {
    public Metar()
    {
        temp_c = 0;
        altim_in_hg = 0;
        visibility_statute_mi = 0;
        elevation_m = 0;
    }

    @Element(required = false)
    public String raw_text;

    @Element(required = false)
    public String observation_time;
    public Date GetObservationTime()
    {
        // 2015-06-11T11:13:00Z
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        try {
            return format.parse(observation_time);
        } catch (ParseException e) {
            return new Date();
        }
    }

    @Element(required = false)
    public float latitude;
    @Element(required = false)
    public float longitude;
    @Element(required = false)
    public float distance_to_org_m;
    @Element(required = false)
    public float temp_c;
    @Element(required = false)
    public float dewpoint_c;
    @Element(required = false)
    public Integer wind_dir_degrees;
    @Element(required = false)
    public Integer wind_speed_kt;
    @Element(required = false)
    public Integer wind_gust_kt;
    @Element(required = false)
    public float visibility_statute_mi;
    @Element(required = false)
    public float altim_in_hg;
    @Element(required = false)
    public float sea_level_pressure_mb;
    @Element(required = false)
    public String quality_control_flags;
    @Element(required = false)
    public String wx_string;

    @ElementList(required = false, name = "sky_condition", inline = true)
    public List<sky_condition_class> sky_condition;

    @Root(name="sky_condition")
    static class sky_condition_class
    {
        @Attribute(required = false)
        public Integer cloud_base_ft_agl;
        @Attribute(required = false)
        public String sky_cover;
    }

    @Element(required = false)
    public String flight_category;
    @Element(required = false)
    public float three_hr_pressure_tendency_mb;
    @Element(required = false)
    public float maxT_c;
    @Element(required = false)
    public float minT_c;
    @Element(required = false)
    public float maxT24hr_c;
    @Element(required = false)
    public float minT24hr_c;
    @Element(required = false)
    public float precip_in;
    @Element(required = false)
    public float pcp3hr_in;
    @Element(required = false)
    public float pcp6hr_in;
    @Element(required = false)
    public float pcp24hr_in;
    @Element(required = false)
    public float snow_in;
    @Element(required = false)
    public Integer vert_vis_ft;
    @Element(required = false)
    public String metar_type;
    @Element(required = false)
    public float elevation_m;

}

