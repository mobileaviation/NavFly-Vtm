package com.mobileaviationtools.nav_fly.Classes;

import com.mobileaviationtools.airnavdata.Api.RetrofitTest;

public enum Direction {
    N,
    N_NE,
    NE,
    E_NE,
    E,
    E_SE,
    SE,
    S_SE,
    S,
    S_SW,
    SW,
    W_SW,
    W,
    W_NW,
    NW,
    N_NW;

    @Override
    public String toString() {
        return super.toString().replace("_", "-");
    }

    public static Direction parseHeading(Double heading)
    {
        Direction dir = Direction.N;

        if (heading<11.25 && heading>=348.75) return Direction.N;
        if (heading<348.75 && heading>=326.25) return Direction.N_NW;
        if (heading<326.25 && heading>=303.75) return Direction.NW;
        if (heading<303.75 && heading>=281.25) return Direction.W_NW;
        if (heading<281.25 && heading>=258.75) return Direction.W;
        if (heading<258.75 && heading>=236.25) return Direction.W_SW;
        if (heading<236.25 && heading>=213.75) return Direction.SW;
        if (heading<213.75 && heading>=191.25) return Direction.S_SW;
        if (heading<191.25 && heading>=168.75) return Direction.S;
        if (heading<168.75 && heading>=146.25) return Direction.S_SE;
        if (heading<146.25 && heading>=123.75) return Direction.SE;
        if (heading<123.75 && heading>=101.25) return Direction.E_SE;
        if (heading<101.25 && heading>=78.75) return Direction.E;
        if (heading<78.75 && heading>=56.25) return Direction.E_NE;
        if (heading<56.25 && heading>=33.75) return Direction.NE;
        if (heading<33.75 && heading>=11.25) return Direction.N_NE;

        return dir;
    }
}
