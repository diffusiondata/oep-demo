package com.push;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.Ignore;
import org.junit.Test;

public class UDPTest {

    private String ip = "192.168.53.109";
    private int port = 8899;

    @Test
    @Ignore
    public void sendCommand() throws Exception {

        byte[][] bs = { { 0x41, 0x00, 0x55 }, { 0x42, 0x00, 0x55 } };
        for (byte[] bs2 : bs) {

            send(bs2, ip);
            Thread.sleep(500);
        }

        for (byte i = 0; i <= 255; i++) {
            System.out.printf("%02X%n", i);
            byte[] bs2 = { 0x40, i, 0x55 };
            send(bs2, ip);
            Thread.sleep(250);
        }
    }

    private void send(byte[] cmd, String address) {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(cmd, cmd.length, InetAddress.getByName(address), port);
            DatagramSocket socket = new DatagramSocket();
            socket.send(datagramPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
