package com.mobileaviationtools.nav_fly.Classes;

public class FileHelpers {
    public static String replaceExtention(String filename, String newExt)
    {
        String[] splitFile = filename.split("\\.");
        String newFile = "";
        if (splitFile.length>0) {
            for (int i = 0; i < splitFile.length - 1; i++) {
                newFile = newFile + splitFile[i] + "." ;
            }
            return (newExt.startsWith(".")) ?
                    newFile.substring(0, newFile.length()-1) + newExt :
                    newFile + newExt;
        }
        else
            return filename + newExt;
    }
}
