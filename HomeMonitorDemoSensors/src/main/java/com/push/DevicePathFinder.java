package com.push;

import java.io.File;
import java.io.FileInputStream;

public class DevicePathFinder {

    public static synchronized String find(String id) {
        String[] tokens = id.split(":");
        return find(tokens[0], tokens[1]);
    }

    public static synchronized String find(String idVendor, String idPoduct) {
        String path = null;

        File dir = new File("/sys/bus/usb/devices/");
        if (dir.exists() && dir.isDirectory()) {
            for (File xf : dir.listFiles()) {

                if (!isMatching(xf, idVendor, idPoduct)) {
                    continue;
                }
                System.out.println("" + idVendor + ":" + idPoduct
                        + "  matched in " + xf);
                File colonFolder = findColonFolder(xf);
                System.out.println("Anki -- Colon Folder = " + colonFolder);
                if (colonFolder == null) {
                    continue;
                }
                File usbDir = new File(colonFolder, "usb");
                if (!usbDir.exists() || !usbDir.isDirectory()) {
                    continue;
                }
                System.out.println("Anki usb-dir = " + usbDir);
                path = usbDir.listFiles()[0].getName();
                path = "/dev/" + path;
                System.out.println("Anki path = " + path);
            }
        }
        return path;
    }

    private static boolean isMatching(File xf, String idVendor, String idPoduct) {
        // System.out.println("Anki isMatching " + idVendor + " : " + idPoduct + " " + xf);
        // for (File f : xf.listFiles()) {	
        File idv = new File(xf, "idVendor");
        File idp = new File(xf, "idProduct");
        if (!idv.exists() || !idv.isFile()) {
            return false;
        }
        String idvStr = null;
        String idpStr = null;
        idvStr = new String(readFile(idv)).trim();
        idpStr = new String(readFile(idp)).trim();
        // System.out.println("Anki --- ids from files = " + idvStr + " : " +
        // idpStr);
        if (!idVendor.equals(idvStr)) {
            return false;
        }
        if (!idPoduct.equals(idpStr)) {
            return false;
        }

        return true;
    }

    private static byte[] readFile(File file) {
        byte[] b = new byte[(int) file.length()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(b);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ignore) {
                    //
                }
            }
        }
        return b;

    }

    private static File findColonFolder(File xf) {
        String name = xf.getName() + ":";
        for (File f : xf.listFiles()) {
            if (f.getName().startsWith(name)) {
                return f;
            }
        }
        return null;
    }
}

