package com.oracle.smarthome.cep;

public class House {

    private static boolean heaterOn;
    private static boolean lightOn;

    public static synchronized String turnOnHeater() {
        System.out.println("Turning Heater On");
        if (heaterOn) {
            System.out.println("    Already ON");
            return "on";
        }
        heaterOn = true;

        return "on";
    }

    public static synchronized String turnOffHeater() {
        System.out.println("Turning Heater Off");
        if (!heaterOn) {
            System.out.println("    Already Off");
            return "off";
        }
        heaterOn = false;

        return "off";
    }

    public static synchronized String turnOnLight() {
        System.out.println("Turning Lights ON");
        if (lightOn) {
            System.out.println("    Already ON");
            return "on";
        }
        lightOn = true;

        return "on";
    }

    public static synchronized String turnOffLight() {
        System.out.println("Turning Lights Off");
        if (!lightOn) {
            System.out.println("    Already OFF");
            return "off";
        }
        lightOn = false;

        return "off";
    }
}
