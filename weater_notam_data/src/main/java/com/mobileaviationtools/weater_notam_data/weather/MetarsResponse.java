package com.mobileaviationtools.weater_notam_data.weather;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;

@JacksonXmlRootElement(localName = "response")
public class MetarsResponse {
    @JacksonXmlElementWrapper(localName = "METAR")
    private ArrayList<Metar> metars;
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