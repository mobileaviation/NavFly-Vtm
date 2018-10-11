package com.mobileaviationtools.weater_notam_data.weather;

import android.util.Log;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Root(name = "TAF")
public class Taf {
    public Taf()
    {
        elevation_m = 0;
    }

    private String TAG = "TAF";


    @Element(required = false)
    public String raw_text;

    @Element(required = false)
    public String issue_time;

    public Date GetIssueTime()
    {
        // 2015-06-11T11:13:00Z
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        try {
            return format.parse(issue_time);
        } catch (ParseException e) {
            Log.e(TAG, "Taf: Date parse error");
            return new Date();
        }
    }

    @Element(required = false)
    public String bulletin_time;
    @Element(required = false)
    public String valid_time_from;
    @Element(required = false)
    public String valid_time_to;
    @Element(required = false)
    public float latitude;
    @Element(required = false)
    public float longitude;
    @Element(required = false)
    public float elevation_m;
    @Element(required = false)
    public String remarks;

    @Element(required = false)
    public float distance_to_org_m;

    @ElementList(required = false, inline = true)
    public List<forecast_class> forecast;

    @Root(name="forecast")
    static class forecast_class
    {
        @Element(required = false)
        public String fcst_time_from;
        @Element(required = false)
        public String fcst_time_to;
        @Element(required = false)
        public String change_indicator;
        @Element(required = false)
        public Integer probability;
        @Element(required = false)
        public Integer wind_dir_degrees;
        @Element(required = false)
        public Integer wind_speed_kt;
        @Element(required = false)
        public Integer wind_gust_kt;
        @Element(required = false)
        public Integer wind_shear_hgt_ft_agl;
        @Element(required = false)
        public Integer wind_shear_dir_degrees;
        @Element(required = false)
        public Integer wind_shear_speed_kt;
        @Element(required = false)
        public float visibility_statute_mi;
        @Element(required = false)
        public float altim_in_hg;
        @Element(required = false)
        public Integer vert_vis_ft;
        @Element(required = false)
        public String wx_string;
        @Element(required = false)
        public String not_decoded;

        @ElementList(required = false, inline = true)
        public List<sky_condition_class> sky_condition;
        @Root(name = "sky_condition")
        static class sky_condition_class
        {
            @Attribute(required = false)
            public Integer cloud_base_ft_agl;
            @Attribute(required = false)
            public String sky_cover;
            @Attribute(required = false)
            public String cloud_type;
        }

        @ElementList(required = false, inline = true)
        public List<turbulence_condition_class> turbulence_condition;
        @Root(name = "turbulence_condition")
        static class turbulence_condition_class
        {
            @Attribute(required = false)
            public String turbulence_intensity;
            @Attribute(required = false)
            public Integer turbulence_min_alt_ft_agl;
            @Attribute(required = false)
            public Integer turbulence_max_alt_ft_agl;
        }

        @ElementList(required = false, inline = true)
        public List<icing_condition_class> icing_condition;
        @Root(name = "icing_condition")
        static class icing_condition_class
        {
            @Attribute(required = false)
            public String icing_intensity;
            @Attribute(required = false)
            public Integer icing_min_alt_ft_agl;
            @Attribute(required = false)
            public Integer icing_max_alt_ft_agl;
        }

        @ElementList(required = false, inline = true)
        public List<temperature_class> temperature;
        @Root(name = "temperature")
        static class temperature_class
        {
            @Attribute(required = false)
            public String valid_time;
            @Attribute(required = false)
            public float sfc_temp_c;
            @Attribute(required = false)
            public float max_temp_c;
            @Attribute(required = false)
            public float min_temp_c;
        }

    }

}