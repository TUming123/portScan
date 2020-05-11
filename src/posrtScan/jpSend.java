package posrtScan;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.TCPPacket;

import java.io.IOException;

public class jpSend {
    String hostname;
    int port;

    public jpSend(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void tcpSend(TCPPacket tcp){
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        JpcapSender jpsend = null;
        int caplen = 1512;
        boolean promiscCheck = true;//混杂模式
        int to_ms = 100;//超时时间
        int j = 0;
        boolean flag = false;

        try {
            jpsend = JpcapSender.openDevice(devices[5]);
            jpsend.sendPacket(tcp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
