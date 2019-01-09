package com.mobileaviationtools.weater_notam_data.Elevation;

public class elevation
{
    public class resourceSet
    {
        public class resource
        {
            public String __type;
            public Integer zoomLevel;
            public Integer[] elevations;
        }
        public Integer estimatedTotal;
        public resource[] resources;
    }

    public String authenticationResultCode;
    public String brandLogoUri;
    public String copyright;
    public resourceSet[] resourceSets;
    public Integer statusCode;
    public String statusDescription;
    public String traceId;
}

//{
//    "authenticationResultCode":"ValidCredentials",
//    "brandLogoUri":"http:\/\/dev.virtualearth.net\/Branding\/logo_powered_by.png",
//    "copyright":"Copyright © 2019 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.",
//    "resourceSets":
//        [
//                {
//                    "estimatedTotal":1,
//                    "resources":
//                    [
//                            {
//                                "__type":"ElevationData:http:\/\/schemas.microsoft.com\/search\/local\/ws\/rest\/v1",
//                                "elevations":[-5,-5,-6,-5,-2,-3,-3,-1,-2,2,1,0,-1,-1,-1,-1,-1,-2,-1,-2],
//                                "zoomLevel":8
//                            }
//                    ]
//                }
//        ],
//        "statusCode":200,
//        "statusDescription":"OK",
//        "traceId":"de2766ae15494d0fbd6ec261afd36510|DB40231435|7.7.0.0|t1.tiles.virtualearth.net, t3.tiles.virtualearth.net, t2.tiles.virtualearth.net"
//}