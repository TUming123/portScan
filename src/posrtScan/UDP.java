package posrtScan;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Stack;


public class UDP{
    String ip;
    int head,tail;

    //初始化
    public UDP(String ip, int head, int tail) {
        this.ip = ip;
        this.head = head;
        this.tail = tail;
    }

    //开始udp扫描
    public Stack<Integer> udpScan() throws IOException, InterruptedException {
        Stack<Integer> openPort = new Stack<Integer>();

        for(int i=head;i<=tail;i++){
            if(testPort(ip,i)) openPort.push(i);
        }

        return openPort;
    }

    Boolean testPort(String hostname, int port) throws IOException, InterruptedException {
        //        创建DatagrampSocket实现数据发送和接受，此时也可以指定端口，作为客户端端口；如果不指定，系统会生成端口，绑定socket
        Boolean flag = false;
        DatagramSocket socket = new DatagramSocket();
        //        InetSocketAddress存放ip和端口
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        byte[] data = new byte[20];
        for (int i=0;i<data.length;i++) data[i]=53;
        //        创建数据报
        DatagramPacket packet = new DatagramPacket(data, data.length, address);
        //            发送数据
//        int j = 0;
//        while(j<10){
        socket.send(packet);
        if(getICMP.getICMP(hostname)==null){
            flag = true;
        }
        socket.close();
        return flag;
//            Thread.sleep(1000);
//            j++;
//        }
//        data = new byte[1024];
//        packet = new DatagramPacket(data, data.length);
//
//        socket.receive(packet);
//        String str = new String(packet.getData(), 0, packet.getLength());
//        System.out.println("我是服务器，收到：" + str);
//        System.out.println("address:" + packet.getAddress() + " port:" + packet.getPort());
//        socket = new DatagramSocket(8888);
//        关闭socket

    }
}


class getICMP{

    static int timeout = 50;

    public static String getICMP(String hostname) {
        /*--------------	第一步绑定网络设备       --------------*/
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        long startTime = System.currentTimeMillis();
        long endTime;
//        for (NetworkInterface n : devices) {
//            System.out.println(n.name + "     |     " + n.description);
//        }
//        System.out.println("-------------------------------------------");

        JpcapCaptor jpcap = null;
        int caplen = 1512;
        boolean promiscCheck = true;//混杂模式
        int to_ms = 20;//超时时间
        int j = 0;

//        for (NetworkInterface n : devices) {
//            System.out.println(n.name + "     |     " + n.description);
//        }
//        System.out.println("-------------------------------------------");
        try {
//            即将打开的设备名,从设备上一次读取的最大字节数,说明是否将设备设为混杂模式的Boolean值,和以后调用processPacket()方法要使用到的超时值.
            jpcap = JpcapCaptor.openDevice(devices[5], caplen, promiscCheck, to_ms);
            jpcap.setFilter("host " + hostname, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            endTime = System.currentTimeMillis();
            if(endTime - startTime >= timeout) break;
            boolean flag = false;
            Packet packet = jpcap.getPacket();
            if (packet instanceof IPPacket && ((IPPacket) packet).version == 4) {
                IPPacket ip = (IPPacket) packet;// 强转
                j++;
                String protocol = "";
                if ((int) ip.protocol == 1) {
                    protocol = "ICMP";
                    ICMPPacket icmp = (ICMPPacket) packet;
                    if (icmp.type == 3 && icmp.code == 3) return icmp.src_ip.toString();
                    break;
                }
//                System.out.println("版本：IPv4");
//                System.out.println("优先权：" + ip.priority);
//                System.out.println("区分服务：最大的吞吐量： " + ip.t_flag);
//                System.out.println("区分服务：最高的可靠性：" + ip.r_flag);
//                System.out.println("长度：" + ip.length);
//                System.out.println("标识：" + ip.ident);
//                System.out.println("DF:Don't Fragment: " + ip.dont_frag);
//                System.out.println("NF:Nore Fragment: " + ip.more_frag);
//                System.out.println("片偏移：" + ip.offset);
//                System.out.println("生存时间：" + ip.hop_limit);
//                System.out.println("协议：" + protocol);
//                System.out.println("源IP " + ip.src_ip.getHostAddress());
//                System.out.println("目的IP " + ip.dst_ip.getHostAddress());
//                System.out.println("源主机名： " + ip.src_ip);
//                System.out.println("目的主机名： " + ip.dst_ip);
//                System.out.println("----------------------------------------------");

            }
        }
        return null;
    }
}

