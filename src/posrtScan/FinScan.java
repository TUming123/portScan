package posrtScan;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.*;

import java.io.IOException;
import java.net.*;
import java.util.Random;
import java.util.Stack;

public class FinScan {
    String ip;
    int head,tail;
    static int myPort = new Random().nextInt(5535)+50000;
    static String msg = "";
    static Stack<Integer> openPort;
    JpcapCaptor jpcap = null;
    JpcapSender jpsend = null;

    public FinScan(String ip, int head, int tail) {
        this.ip = ip;
        this.head = head;
        this.tail = tail;
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        JpcapSender jpsend = null;
        int caplen = 1512;
        boolean promiscCheck = true;//混杂模式
        int to_ms = 20;//超时时间

        try {
            this.jpsend = JpcapSender.openDevice(devices[6]);
            this.jpcap = JpcapCaptor.openDevice(devices[6], caplen, promiscCheck, to_ms);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stack<Integer> finScan() throws IOException, InterruptedException {
        openPort = new Stack<Integer>();
        int num = tail - head + 1;
        for(int i=head;i<=tail;i++){
            scan(ip,i);
        }
        while(finRec.count!=num) {
            Thread.sleep(100);
//            System.out.println(finRec.count+"  "+(tail-1));
        };
//        System.out.println(openPort.hashCode());
        return openPort;
    }

    //初始化网卡捕获  开启接收器  发送包
    public void scan(String hostname,int port) throws UnknownHostException {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        TCPPacket fin = new TCPPacket(myPort,port,0, 0, false, false, false,
                false, false, true, false, false, 1024, 0);
        fin.setIPv4Parameter(0, false, false, false, 0, false,
                false, false, 0, 61185, 64, IPPacket.IPPROTO_TCP,
                devices[6].addresses[5].address , InetAddress.getByName(hostname));
        fin.data = msg.getBytes();
        EthernetPacket ep = new EthernetPacket();
        ep.frametype = EthernetPacket.ETHERTYPE_IP;
        ep.src_mac = devices[6].mac_address;
//        ep.dst_mac = "58:69:6c:ec:e3:67".getBytes();
        ep.dst_mac = new byte[] { (byte) 0x58, (byte) 0x69, (byte) 0x6c, (byte) 0xec, (byte) 0xe3,
                (byte) 0x67 };
        //本机的锐捷网卡的多播地址


        fin.datalink = ep;
        finRec.openPort = openPort;
//        System.out.println(openPort.hashCode());
        new Thread(new finRec(hostname, port, jpcap)).start();

        jpsend.sendPacket(fin);

    }

}

class finRec implements Runnable{
    String hostname;
    int port;
    static Stack<Integer> openPort;
    static int count=0;
    JpcapCaptor jpcap;
    int timeout = 20;

    public finRec(String hostname, int port, JpcapCaptor jpcap) {
        this.hostname = hostname;
        this.port = port;
        this.jpcap = jpcap;
    }

    @Override
    public void run() {
//        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        JpcapCaptor jpcap = null;
//        int caplen = 1512;
//        boolean promiscCheck = true;//混杂模式
//        int to_ms = 20;//超时时间

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
                    if (!tcp.rst) openPort.push(tcp.src_port);
                    break;
                }
            }
        }
        synchronized (this){
            openPort.push(port);
//            System.out.println(openPort.hashCode());
            count++;
        }

    }
}
