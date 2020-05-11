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

    //��ʼ��
    public UDP(String ip, int head, int tail) {
        this.ip = ip;
        this.head = head;
        this.tail = tail;
    }

    //��ʼudpɨ��
    public Stack<Integer> udpScan() throws IOException, InterruptedException {
        Stack<Integer> openPort = new Stack<Integer>();

        for(int i=head;i<=tail;i++){
            if(testPort(ip,i)) openPort.push(i);
        }

        return openPort;
    }

    Boolean testPort(String hostname, int port) throws IOException, InterruptedException {
        //        ����DatagrampSocketʵ�����ݷ��ͺͽ��ܣ���ʱҲ����ָ���˿ڣ���Ϊ�ͻ��˶˿ڣ������ָ����ϵͳ�����ɶ˿ڣ���socket
        Boolean flag = false;
        DatagramSocket socket = new DatagramSocket();
        //        InetSocketAddress���ip�Ͷ˿�
        InetSocketAddress address = new InetSocketAddress(hostname, port);
        byte[] data = new byte[20];
        for (int i=0;i<data.length;i++) data[i]=53;
        //        �������ݱ�
        DatagramPacket packet = new DatagramPacket(data, data.length, address);
        //            ��������
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
//        System.out.println("���Ƿ��������յ���" + str);
//        System.out.println("address:" + packet.getAddress() + " port:" + packet.getPort());
//        socket = new DatagramSocket(8888);
//        �ر�socket

    }
}


class getICMP{

    static int timeout = 50;

    public static String getICMP(String hostname) {
        /*--------------	��һ���������豸       --------------*/
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        long startTime = System.currentTimeMillis();
        long endTime;
//        for (NetworkInterface n : devices) {
//            System.out.println(n.name + "     |     " + n.description);
//        }
//        System.out.println("-------------------------------------------");

        JpcapCaptor jpcap = null;
        int caplen = 1512;
        boolean promiscCheck = true;//����ģʽ
        int to_ms = 20;//��ʱʱ��
        int j = 0;

//        for (NetworkInterface n : devices) {
//            System.out.println(n.name + "     |     " + n.description);
//        }
//        System.out.println("-------------------------------------------");
        try {
//            �����򿪵��豸��,���豸��һ�ζ�ȡ������ֽ���,˵���Ƿ��豸��Ϊ����ģʽ��Booleanֵ,���Ժ����processPacket()����Ҫʹ�õ��ĳ�ʱֵ.
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
                IPPacket ip = (IPPacket) packet;// ǿת
                j++;
                String protocol = "";
                if ((int) ip.protocol == 1) {
                    protocol = "ICMP";
                    ICMPPacket icmp = (ICMPPacket) packet;
                    if (icmp.type == 3 && icmp.code == 3) return icmp.src_ip.toString();
                    break;
                }
//                System.out.println("�汾��IPv4");
//                System.out.println("����Ȩ��" + ip.priority);
//                System.out.println("���ַ��������������� " + ip.t_flag);
//                System.out.println("���ַ�����ߵĿɿ��ԣ�" + ip.r_flag);
//                System.out.println("���ȣ�" + ip.length);
//                System.out.println("��ʶ��" + ip.ident);
//                System.out.println("DF:Don't Fragment: " + ip.dont_frag);
//                System.out.println("NF:Nore Fragment: " + ip.more_frag);
//                System.out.println("Ƭƫ�ƣ�" + ip.offset);
//                System.out.println("����ʱ�䣺" + ip.hop_limit);
//                System.out.println("Э�飺" + protocol);
//                System.out.println("ԴIP " + ip.src_ip.getHostAddress());
//                System.out.println("Ŀ��IP " + ip.dst_ip.getHostAddress());
//                System.out.println("Դ�������� " + ip.src_ip);
//                System.out.println("Ŀ���������� " + ip.dst_ip);
//                System.out.println("----------------------------------------------");

            }
        }
        return null;
    }
}

