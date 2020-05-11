package posrtScan;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.*;

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.Stack;

public class SynScan {
    String ip;
    int head, tail;
    static int myPort = new Random().nextInt(5535) + 50000;
    static String msg = "";
    static Stack<Integer> openPort;
    JpcapCaptor jpcap = null;
    JpcapSender jpsend = null;

//    public static void main(String[] args) throws UnknownHostException {
//        String ip = "47.110.130.169";
//        int head = 22, tail = 22;
//        SynScan sy = new SynScan(ip, head, tail);
//        sy.scan(ip, 12345);
//    }

    public SynScan(String ip, int head, int tail) {
        this.ip = ip;
        this.head = head;
        this.tail = tail;
        this.openPort = new Stack<Integer>();
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        JpcapSender jpsend = null;
        int caplen = 1512;
        boolean promiscCheck = true;//混杂模式
        int to_ms = 500;//超时时间

        try {
            this.jpsend = JpcapSender.openDevice(devices[6]);
            this.jpcap = JpcapCaptor.openDevice(devices[6], caplen, promiscCheck, to_ms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stack<Integer> synScan() throws IOException, InterruptedException {

        int num = tail - head + 1;
        for (int i = head; i <= tail; i++) {
            scan(ip, i);
        }
        while (synRec.count != num) {
            Thread.sleep(100);
        }
        return openPort;
    }

    public void scan(String hostname, int port) throws UnknownHostException {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        JpcapSender jpsend = null;
//        int caplen = 1512;
//        boolean promiscCheck = true;//混杂模式
//        int to_ms = 100;//超时时间
//        int j = 0;
//        boolean flag = false;
//
//        try {
//            jpsend = JpcapSender.openDevice(devices[5]);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        TCPPacket syn = new TCPPacket(myPort, port, 0, 0, false, false, false,
                false, true, false, false, false, 1024, 0);
        syn.setIPv4Parameter(0, false, false, false, 0, false,
                false, false, 0, 61185, 64, IPPacket.IPPROTO_TCP,
                devices[6].addresses[5].address, InetAddress.getByName(hostname));
        syn.data = msg.getBytes();
        EthernetPacket ep = new EthernetPacket();
        ep.frametype = EthernetPacket.ETHERTYPE_IP;
        ep.src_mac = devices[6].mac_address;
//        ep.dst_mac = "58:69:6c:ec:e3:67".getBytes();
        ep.dst_mac = new byte[]{(byte) 0x58, (byte) 0x69, (byte) 0x6c, (byte) 0xec, (byte) 0xe3,
                (byte) 0x67};
        //本机的锐捷网卡的多播地址

        syn.datalink = ep;
        new Thread(new synRec(hostname, port, openPort, jpcap)).start();
        jpsend.sendPacket(syn);
    }
}

class synRec implements Runnable {
    String hostname;
    int port;
    Stack<Integer> openPort;
    JpcapCaptor jpcap;
    int timeout = 500;
    static int count = 0;

    public synRec(String hostname, int port, Stack<Integer> openPort, JpcapCaptor jpcap) {
        this.hostname = hostname;
        this.port = port;
        this.openPort = openPort;
        this.jpcap = jpcap;
    }

    @Override
    public void run() {
//        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        JpcapCaptor jpcap = null;
//        int caplen = 1512;
//        boolean promiscCheck = true;//混杂模式
//        int to_ms = 100;//超时时间

        try {
//            即将打开的设备名,从设备上一次读取的最大字节数,说明是否将设备设为混杂模式的Boolean值,和以后调用processPacket()方法要使用到的超时值.
//            jpcap = JpcapCaptor.openDevice(devices[5], caplen, promiscCheck, to_ms);
            jpcap.setFilter("host " + hostname, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        long endTime;
        while (true) {
            endTime = System.currentTimeMillis();
            if (endTime - startTime >= timeout) break;
            Packet packet = jpcap.getPacket();
            if (packet instanceof IPPacket && ((IPPacket) packet).version == 4) {
                IPPacket ip = (IPPacket) packet;// 强转
                String protocol = "";
                if ((int) ip.protocol == 6) {
                    protocol = "TCP";
                    TCPPacket tcp = (TCPPacket) ip;
                    if (tcp.ack && tcp.syn){
                        openPort.push(tcp.src_port);
//                        System.out.println("findit");
                    }
                    break;
                }
            }
        }
        synchronized (this) {
            count++;
        }

    }
}
