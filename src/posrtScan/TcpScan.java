package posrtScan;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TcpScan {
	String ip;
	int head,tail;
	int threadNum = 1000;

	public TcpScan(String ip, int head, int tail) {
		this.ip = ip;
		this.head = head;
		this.tail = tail;
	}

	public Stack<Integer> startScan() throws InterruptedException {
		Stack<Integer> port = new Stack<Integer>();
		ExecutorService myEs = Executors.newFixedThreadPool(threadNum);
		int i=0;

		for (i = head; i <= tail; i++) {
			port.push(i);
		}

		while (!port.isEmpty()) {
			i = port.pop();
			myEs.execute(new myTcpScan("local" + ":" + i, ip, i));
		}
		myEs.shutdown();
		myEs.awaitTermination(1, TimeUnit.HOURS);
		return myTcpScan.openPort;
	}

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		long startTime = System.currentTimeMillis();
//		int head = 0;
//		int tail = 10000;
//		int threadNum = 10000;
//		int i = 0;
//		String ad = "127.0.0.1";
//		Stack<Integer> port = new Stack<Integer>();
//		ExecutorService myEs = Executors.newFixedThreadPool(threadNum);//固定线程个数
//
//		for (i = head; i <= tail; i++) {
//			port.push(i);
//		}
//
//		while (!port.isEmpty()) {
//			i = port.pop();
//			myEs.execute(new myTcpScan("local" + ":" + i, ad, i));
//		}
//		myEs.shutdown();
//		try {
//			myEs.awaitTermination(1, TimeUnit.HOURS);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("扫描到" + myTcpScan.openPort.size() + "个端口");
//		myTcpScan.showOpenPort();
//		long endTime = System.currentTimeMillis();
//		System.out.println("Cost Time:" + (endTime - startTime));
//	}
}

class myTcpScan implements Runnable {
	private Thread t;
	private String threadName;
	static Stack<Integer> openPort = new Stack<Integer>();
	String ip;// 目标地址
	Socket s;
	private int no;

	synchronized void addPort(int no) {
		openPort.push(no);
//		System.out.println(System.identityHashCode(openPort));
//		System.out.println(openPort.size());

	}

	myTcpScan(String name, String ip, int no) {
		this.ip = ip;
		this.no = no;
		threadName = name;
//		System.out.println("Creating " + threadName);
	}

//	myTcpScan(String name, String ip, int no, CyclicBarrier barrier) {
//		port = new Stack<Integer>();
//		openPort = new Stack<Integer>();
//		this.ip = ip;
//		this.no = no;
//		threadName = name;
//		this.barrier = barrier;
////		System.out.println("Creating " + threadName);
//	}
//利用socket进行扫描
	public void run() {
//		String currentName = Thread.currentThread().getName();
//		System.out.println("Running " + currentName);
		try {
			new Socket().connect(new InetSocketAddress(ip,no), 100);// 通过这样的方法来判断端口是否开启 , 如果没有连接上会抛出异常
//			System.out.println("扫描到端口:" + no);
			addPort(no);

//			System.out.println(openPort.size());
//			System.out.println("扫描到端口:" + no);
// 解决的问题是每扫描完一个建立连接非常消耗资源 所以一定要关闭
			if(s != null) s.close();
//			System.out.println(maxnum);
		} catch (IOException e) {
//			System.out.println("Thread " + currentName + " interrupted.");
//			e.printStackTrace();
		}
	}

	public static void showOpenPort() {// 输出开放端口
		Stack<Integer> p = (Stack<Integer>) openPort.clone();
		while (!p.isEmpty()) {
			System.out.println(p.pop());
		}
	}

//	public void start() throws CloneNotSupportedException {
////		System.out.println("Starting " + threadName);
//		if(maxnum>0) {
//			while (!port.isEmpty()) {
//				myTcpScan t1 = new myTcpScan(threadName, ip, no, barrier);
//				t1.setNo(port.pop());
//				t = new Thread(t1, threadName + " NO:" + t1.no);// 多线程运行
//				t.start();
//			}
//		}
//	}
}
