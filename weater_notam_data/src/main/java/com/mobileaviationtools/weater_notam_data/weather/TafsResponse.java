package com.mobileaviationtools.weater_notam_data.weather;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "response")
public class TafsResponse {
    @Element(name="data_source")
    public MetarsResponse.data_source data_source;

    @Element(name="time_taken_ms")
    public Long time_taken_ms;

    @ElementList(name="data")
    public List<Taf> data;

    @Root
    static class data_source {
        @Attribute(name="name")
        public String name;
    }
}
