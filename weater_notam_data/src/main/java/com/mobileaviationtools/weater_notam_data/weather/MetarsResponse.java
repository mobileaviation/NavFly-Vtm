package com.mobileaviationtools.weater_notam_data.weather;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import java.util.List;

@Root(name = "response")
public class MetarsResponse {
    @Element(name="data_source")
    public data_source data_source;

    @Element(name="time_taken_ms")
    public Long time_taken_ms;

    @ElementList(name="data")
    public List<Metar> data;

    @Root
    static class data_source {
        @Attribute(name="name")
        public String name;
    }
}


//    <?xml version="1.0" encoding="UTF-8"?>
//<response xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XML-Schema-instance" version="1.2" xsi:noNamespaceSchemaLocation="http://aviationweather.gov/adds/schema/metar1_2.xsd">
//<request_index>69086709</request_index>
//<data_source name="metars" />
//<request type="retrieve" />
//<errors />
//<warnings />
//<time_taken_ms>330</time_taken_ms>
//<data num_results="26">
//<METAR>