package posrtScan;

import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
//		TcpScan try = new TcpScan("local", "127.0.0.1",0);

        Scanner sc = new Scanner(System.in);
        int choice = 0;
        System.out.println("Port Scan:");
        System.out.println("1.TCP All-connected:");
        System.out.println("2.SYN:");
        System.out.println("3.FIN:");
        System.out.println("4.UDP:");
        choice = sc.nextInt();
        try {
            switch (choice) {
                case 1:
                    tcpScan();
                    break;
                case 2:
                    synScan();
                    break;
                case 3:
                    finScan();
                    break;
                case 4:
                    udpScan();
                    break;
                default:
                    System.out.println("Select nothing, exit soon");
            }
        } catch (InterruptedException | IOException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

    public static void tcpScan() throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        String ip;
        int head, tail;

        System.out.println("ip:");
        ip = sc.nextLine();
        System.out.println("start port:");
        head = sc.nextInt();
        System.out.println("end port:");
        tail = sc.nextInt();
//        采用多线程
        long startTime = System.currentTimeMillis();
        showPort(new TcpScan(ip, head, tail).startScan());
        long endTime = System.currentTimeMillis();
        System.out.println("Cost Time:" + (endTime - startTime));
    }

    public static void synScan() throws InterruptedException, IOException {
        Scanner sc = new Scanner(System.in);
        String ip;
        int head, tail;

        System.out.println("ip:");
        ip = sc.nextLine();
        System.out.println("start port:");
        head = sc.nextInt();
        System.out.println("end port:");
        tail = sc.nextInt();

        long startTime = System.currentTimeMillis();
        showPort(new SynScan(ip, head, tail).synScan());
        long endTime = System.currentTimeMillis();
        System.out.println("Cost Time:" + (endTime - startTime));
    }

    public static void finScan() throws InterruptedException, IOException {
        Scanner sc = new Scanner(System.in);
        String ip;
        int head, tail;

        System.out.println("ip:");
        ip = sc.nextLine();
        System.out.println("start port:");
        head = sc.nextInt();
        System.out.println("end port:");
        tail = sc.nextInt();

        long startTime = System.currentTimeMillis();
        showPort(new FinScan(ip,head,tail).finScan());
        long endTime = System.currentTimeMillis();
        System.out.println("Cost Time:" + (endTime - startTime));
    }

    public static void udpScan() throws InterruptedException, IOException {
        Scanner sc = new Scanner(System.in);
        String ip;
        int head, tail;

        System.out.println("ip:");
        ip = sc.nextLine();
        System.out.println("start port:");
        head = sc.nextInt();
        System.out.println("end port:");
        tail = sc.nextInt();

        long startTime = System.currentTimeMillis();
        showPort(new UDP(ip, head, tail).udpScan());
        long endTime = System.currentTimeMillis();
        System.out.println("Cost Time:" + (endTime - startTime));
    }

    public static void showPort(Stack<Integer> port) {
        System.out.println("Size:" + port.size() + "\tOutcome:");
        Stack<Integer> p = (Stack<Integer>) port.clone();
        while (!p.isEmpty()) {
            System.out.println(p.pop());
        }
    }
}


/*
182.92.156.231
1
100
 */


/*
不足之处
未设置超时时间的全局设置
多线程仍然存在问题
 */