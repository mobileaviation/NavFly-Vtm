package com.example.aircraft.Types;

import com.example.aircraft.Aircraft;

public class PiperArcher implements Aircraft {
    public Integer Vx()  { return  76; }          //Knots
    public Integer Vy()  { return  64; }         //Knots
    public Integer Vs() { return 48; }          //Knots
    public Integer Vso() { return  44; }        //Knots
    public Integer Vne() { return  154; }       //Knots
    public Integer Vno() { return  125; }        //Knots
    public Integer IndicatedAirspeed() { return 110; } //Knots

    public Integer climbRate() { return  700; }  // Feet per Minute
}
