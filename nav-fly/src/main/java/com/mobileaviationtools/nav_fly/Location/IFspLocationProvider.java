package com.mobileaviationtools.nav_fly.Location;

public interface IFspLocationProvider {
    public boolean setup();
    public boolean start(LocationEvents locationEvents);
    public boolean stop();
}
