package com.mobileaviationtools.weater_notam_data.notams;

public class Notam {
    public String facilityDesignator;
    public String notamNumber;
    public String featureName;
    public String issueDate;
    public String startDate;
    public String endDate;
    public String source;
    public String sourceType;
    public String icaoMessage;   //1
    public String traditionalMessage; //2
    public String plainLanguageMessage;  //3
    public String traditionalMessageFrom4thWord;
    public String icaoId;
    public String accountId;
    public String airportName;
    public Boolean procedure;
    public Long userID;
    public Long transactionID;
    public Boolean cancelledOrExpired;
    public Boolean digitalTppLink;
    public String status;
    public String keyword;
    public Boolean snowtam;
    public String geometry;
    public Boolean digitallyTransformed;
    public Boolean contractionsExpandedForPlainLanguage;
    public String messageDisplayed;
    public Boolean moreThan300Chars;
    public Boolean showingFullText;
    public String mapPointer;
    public Long locID;
    public Boolean defaultIcao;
    public Long crossoverTransactionID;
    public String crossoverAccountID;
    public Long requestID;
}


//        "facilityDesignator" : "EHAM",
//        "notamNumber" : "A0957/18",
//        "featureName" : "International",
//        "issueDate" : "08/15/2018 1349",
//        "startDate" : "08/15/2018 1349",
//        "endDate" : "12/20/2018 1400EST",
//        "source" : "USNS",
//        "sourceType" : "I",
//        "icaoMessage" : "A0957/18 NOTAMR A0558/18\nQ) EHAA/QLXAS/IV/M  /A /000/999/5218N00446E005\nA) EHAM B) 1808151349 C) 1812201400 EST\nE) TWY A12 CL LGT U/S ABM ACFT STAND E75.\nIF RVR IS BLW 350M MARSHALLER GUIDANCE REQUIRED.",
//        "traditionalMessage" : " ",
//        "plainLanguageMessage" : " ",
//        "traditionalMessageFrom4thWord" : "TWY A12 CL LGT U/S ABM ACFT STAND E75.\nIF RVR IS BLW 350M MARSHALLER GUIDANCE REQUIRED.",
//        "icaoId" : "EHAM",
//        "accountId" : "877",
//        "airportName" : "SCHIPHOL",
//        "procedure" : false,
//        "userID" : 0,
//        "transactionID" : 50588627,
//        "cancelledOrExpired" : false,
//        "digitalTppLink" : false,
//        "status" : "Active",
//        "contractionsExpandedForPlainLanguage" : false,
//        "keyword" : "INTERNATIONAL",
//        "snowtam" : false,
//        "geometry" : "POINT(4424693.42950144 5012157.20418167)",
//        "digitallyTransformed" : false,
//        "messageDisplayed" : "concise",
//        "moreThan300Chars" : false,
//        "showingFullText" : false,
//        "mapPointer" : "POINT(4.76388900000004 52.308613)",
//        "locID" : 351623,
//        "defaultIcao" : false,
//        "crossoverTransactionID" : 0,
//        "crossoverAccountID" : "",
//        "requestID" : 0