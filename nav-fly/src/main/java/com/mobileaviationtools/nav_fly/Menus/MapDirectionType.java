package com.mobileaviationtools.nav_fly.Menus;

public enum MapDirectionType {
    north,
    flight,
    free;

    public static MapDirectionType getNextDirectionType(MapDirectionType e)
    {
        int index = e.ordinal();
        int nextIndex = index + 1;
        MapDirectionType[] mapDirectionTypes = MapDirectionType.values();
        nextIndex %= mapDirectionTypes.length;
        return mapDirectionTypes[nextIndex];
    }
}
