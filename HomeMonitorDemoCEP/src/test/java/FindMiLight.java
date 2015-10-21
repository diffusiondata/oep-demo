import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.junit.Test;

public class FindMiLight {

    private static byte[] lightOn = { 0x42, 0x00, 0x55 };
    private static byte[] lightOff = { 0x41, 0x40, 0x55 };
    private int milightPort = 8899;

    @Test
    public void findLight() throws Exception {
        String milightAddress = "192.168.52.101";
            System.out.printf("trying %s ...%n", milightAddress);
            sendMilight(milightAddress, lightOn);
            Thread.sleep(1000);
            sendMilight(milightAddress, lightOff);
            Thread.sleep(1000);
    }

    private void sendMilight(String milightAddress, byte[] cmd) {
        try {
            DatagramPacket datagramPacket = new DatagramPacket(cmd, cmd.length, InetAddress.getByName(milightAddress), milightPort);
            DatagramSocket socket = new DatagramSocket();
            socket.send(datagramPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
