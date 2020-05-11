package posrtScan;

import java.io.IOException;

import jpcap.*;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;


public class Jpcap {

    public static void main(String[] args) {
        /*--------------	��һ���������豸       --------------*/
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();

        for (NetworkInterface n : devices) {
            System.out.println(n.name + "     |     " + n.description);
        }
        System.out.println("-------------------------------------------");

        JpcapCaptor jpcap = null;
        int caplen = 1512;
        boolean promiscCheck = true;

        try {
            jpcap = JpcapCaptor.openDevice(devices[5], caplen, promiscCheck, 50);
            //0 �� 1
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*----------�ڶ���ץ��-----------------*/
        int i = 0;
        while (i < 10) {
            Packet packet = jpcap.getPacket();
            if (packet instanceof IPPacket && ((IPPacket) packet).version == 4) {
//                i++;
                IPPacket ip = (IPPacket) packet;// ǿת

                System.out.println("�汾��IPv4");
                System.out.println("����Ȩ��" + ip.priority);
                System.out.println("���ַ��������������� " + ip.t_flag);
                System.out.println("���ַ�����ߵĿɿ��ԣ�" + ip.r_flag);
                System.out.println("���ȣ�" + ip.length);
                System.out.println("��ʶ��" + ip.ident);
                System.out.println("DF:Don't Fragment: " + ip.dont_frag);
                System.out.println("NF:Nore Fragment: " + ip.more_frag);
                System.out.println("Ƭƫ�ƣ�" + ip.offset);
                System.out.println("����ʱ�䣺" + ip.hop_limit);

                String protocol = "";
                switch ((int) ip.protocol) {
                    case 1:
                        protocol = "ICMP";
                        break;
                    case 2:
                        protocol = "IGMP";
                        break;
                    case 6:
                        protocol = "TCP";
                        break;
                    case 8:
                        protocol = "EGP";
                        break;
                    case 9:
                        protocol = "IGP";
                        break;
                    case 17:
                        protocol = "UDP";
                        break;
                    case 41:
                        protocol = "IPv6";
                        break;
                    case 89:
                        protocol = "OSPF";
                        break;
                    default:
                        break;
                }
                System.out.println("Э�飺" + protocol);
                System.out.println("ԴIP " + ip.src_ip.getHostAddress());
                System.out.println("Ŀ��IP " + ip.dst_ip.getHostAddress());
                System.out.println("Դ�������� " + ip.src_ip);
                System.out.println("Ŀ���������� " + ip.dst_ip);
                System.out.println("----------------------------------------------");
            }else if (packet instanceof IPPacket && ((IPPacket) packet).version == 6){

                IPPacket ip = (IPPacket) packet;// ǿת

                System.out.println("�汾��IPv6");
                System.out.println("����Ȩ��" + ip.priority);
                System.out.println("���ַ��������������� " + ip.t_flag);
                System.out.println("���ַ�����ߵĿɿ��ԣ�" + ip.r_flag);
                System.out.println("���ȣ�" + ip.length);
                System.out.println("��ʶ��" + ip.ident);
                System.out.println("DF:Don't Fragment: " + ip.dont_frag);
                System.out.println("NF:Nore Fragment: " + ip.more_frag);
                System.out.println("Ƭƫ�ƣ�" + ip.offset);
                System.out.println("����ʱ�䣺" + ip.hop_limit);

                String protocol = "";
                switch ((int) ip.protocol) {
                    case 58:
                        protocol = "ICMP";
                        break;
                    case 2:
                        protocol = "IGMP";
                        break;
                    case 6:
                        protocol = "TCP";
                        break;
                    case 8:
                        protocol = "EGP";
                        break;
                    case 9:
                        protocol = "IGP";
                        break;
                    case 17:
                        protocol = "UDP";
                        break;
                    case 41:
                        protocol = "IPv6";
                        break;
                    case 89:
                        protocol = "OSPF";
                        break;
                    default:
                        break;
                }
                System.out.println("Э�飺" + protocol);
                System.out.println("ԴIP " + ip.src_ip.getHostAddress());
                System.out.println("Ŀ��IP " + ip.dst_ip.getHostAddress());
                System.out.println("Դ�������� " + ip.src_ip);
                System.out.println("Ŀ���������� " + ip.dst_ip);
                System.out.println("----------------------------------------------");
            }
        }

    }

}
