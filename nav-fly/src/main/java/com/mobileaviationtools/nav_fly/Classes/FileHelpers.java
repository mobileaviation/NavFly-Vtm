package com.mobileaviationtools.nav_fly.Classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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

    public static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
}
