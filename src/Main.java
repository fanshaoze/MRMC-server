import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main// 主类
{
	private static ServerThreadReceive str; // 用于接收邻居信息的服务器线程
	private static ServerThreadSendCommand stsc;// 用于发送控制命令的服务器线程

	public static ArrayList<NodeInfo> nodes;// 节点信息的列表
	public static Object nodesLock;// 列表的锁

	private static MainWindow mainWindow;

	public static void main(String[] args)// 程序的入口点
	{
		Connections.receiveList = new ArrayList<ConnectionThreadReceive>();// 初始化连接列表
		Connections.receiveListLock = new Object();
		Connections.sendCommandList = new ArrayList<ConnectionThreadSendCommand>();
		Connections.sendCommandListLock = new Object();

		nodes = new ArrayList<NodeInfo>();// 初始化节点信息列表
		nodesLock = new Object();// 初始化列表的锁

		str = new ServerThreadReceive();// 创建用于接收邻居信息的服务器线程
		str.start();// 启动线程

		stsc = new ServerThreadSendCommand();// 创建用于发送控制命令的服务器线程
		stsc.start();// 启动线程

		mainWindow = new MainWindow();// 创建主窗口

		Scanner scanner = new Scanner(System.in);
		while (true)
		{
			String command = scanner.nextLine();
			System.out.println(command);
			if (command.equals("send"))
			{
				System.out.println("Sending configurations to routers.");
				SendConfiguration();
			}
		}
	}

	public static void SendConfiguration()// 下发所有节点的配置
	{
		System.out.println("send here");
		SendToNode("04:F0:21:39:64:26", "SETLINK 04:F0:21:39:64:26#DISABLED 04:F0:21:39:64:25#36#link203");
		SendToNode("04:F0:21:39:64:20", "SETLINK 04:F0:21:39:64:20#6#link204 04:F0:21:39:64:1F#36#link203");
		SendToNode("04:F0:21:39:64:06", "SETLINK 04:F0:21:39:64:06#6#link204 04:F0:21:39:64:05#DISABLED");
	}

	private static void SendToNode(String nodeid, String command)// 向某个指定的节点发送一条命令
	{
		ConnectionThreadSendCommand ctsc = null;
		synchronized (Connections.sendCommandListLock)// 给发送命令连接列表加锁
		{
			for (ConnectionThreadSendCommand c : Connections.sendCommandList)// 在发送命令连接列表中，找到nodeid相同的连接
			{
				//if (c.nodeID.equals(nodeid))
				if (true)
				{
					ctsc = c;
					break;
				}
			}
		}
		if (ctsc == null)
		{
			return;
		}
		System.out.println("send here!");
		ctsc.sendCommand(command);// 发送命令
	}
}

class Connections// 连接的列表
{
	public static ArrayList<ConnectionThreadReceive> receiveList;// 用于接收邻居信息的连接列表
	public static Object receiveListLock;// 列表的锁

	public static ArrayList<ConnectionThreadSendCommand> sendCommandList;// 用于发送控制命令的连接列表
	public static Object sendCommandListLock;// 列表的锁
}

class ServerThreadReceive extends Thread // 此线程监听用于接收邻居信息的端口，并接受新连接
{
	@Override
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(10001, 10);
			while (true)
			{
				Socket socket = serverSocket.accept();
				ConnectionThreadReceive ctr = new ConnectionThreadReceive(socket);
				synchronized (Connections.receiveListLock)
				{
					Connections.receiveList.add(ctr);
				}
				ctr.start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("在接收邻居信息服务器线程中出现异常");
		}
	}
}

class ConnectionThreadReceive extends Thread
{
	private Socket connection; // 与客户端建立的连接

	ConnectionThreadReceive(Socket socket) // 构造方法接受一个与客户端的连接作为参数
	{
		connection = socket;
	}

	@Override
	public void run() // 线程运行时，执行此函数
	{
		InputStream is;
		Scanner scanner = null;
		try
		{
			is = connection.getInputStream(); // 获取TCP连接的输入流
			System.out.print("aaaa\n");
			//System.out.print(is);
			scanner = new Scanner(is); // 用Scanner包装输入流
			while (true) // 一直循环，每次读取一行
			{
				String line = scanner.nextLine();
				System.out.print(line);
				line.replace("\r", ""); // 去除行尾的换行符
				line.replace("\n", "");
				//line = "NEIGHBOR 04:F0:21:39:64:1C 04:F0:21:39:64:1B 36 04:f0:21:39:64:1f"
				//		+ "#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
				String[] parts = line.split(" "); // 以空格为分隔符，对接收到的一行进行分割
				String command = parts[0];
				System.out.println("command : "+command);
				if (command.equals("NEIGHBOR")) // 如果这一行是邻居信息
				{
					synchronized (Main.nodesLock)
					{
						boolean found = false;
						NodeInfo foundni = null;
						//打印信息
						for (NodeInfo ni : Main.nodes)// 在节点列表中查找这个节点
						{
							if (ni.nodeID.equals(parts[1]))// 如果找到，则跳出循环
							{
								found = true;
								foundni = ni;
								break;
							}
						}
						if (found == false) // 如果没有找到，则创建一个，并添加到节点列表中
						{
							foundni = new NodeInfo();
							foundni.nodeID = parts[1];
							System.out.println("foundni.nodeID : "+foundni.nodeID);
							foundni.radioInfo = new ArrayList<RadioInfo>();
							Main.nodes.add(foundni);
						}

						found = false;
						RadioInfo foundri = null;
						for (RadioInfo ri : foundni.radioInfo)// 查找某个节点的某个radio
						{
							if ((ri.radioNumber.equals(parts[2]))
									&& (ri.assignedChannel == Integer.decode(parts[3]).intValue()))// 如果找到（需要radio的mac地址和信道号都匹配）
							{
								found = true;
								foundri = ri;
							}
						}
						if (found == false)// 如果没有找到，则创建一个，并添加到radio列表中
						{
							foundri = new RadioInfo();
							foundri.radioNumber = parts[2];
							System.out.println("foundri.radioNumber : "+foundri.radioNumber);
							foundri.assignedChannel = Integer.decode(parts[3]).intValue();
							foundri.neighborList = new ArrayList<NeighborInfo>();
							foundni.radioInfo.add(foundri);
							//打印信息
						}

						for (int i = 4; i < parts.length; i++)// 处理邻居信息的剩余部分
						{
							String s = parts[i];
							//打印信息
							String[] parts2 = s.split("#");// 将邻居信息以井号为分隔符分开
							found = false;
							NeighborInfo foundni2 = null;
							//打印信息
							for (NeighborInfo ni : foundri.neighborList)// 在现有的邻居列表中查找这个邻居
							{
								if (ni.neighborMac.equals(parts2[0]))// 如果找到，则跳出循环
								{
									found = true;
									foundni2 = ni;
									break;
								}
							}
							if (found == false)// 如果没有找到，则新创建一个邻居信息项目
							{
								foundni2 = new NeighborInfo();
								foundni2.neighborMac = parts2[0];
								System.out.println("parts2[0] : "+parts2[0]);
								foundri.neighborList.add(foundni2);
							}
							for(int t = 0;t<7;t++){
								System.out.println("t:"+t+" "+parts2[t]);
							}
							
							
							foundni2.signal = Double.parseDouble(parts2[1]);
							foundni2.noise = Double.parseDouble(parts2[2]);
							foundni2.tx_rate = Double.parseDouble(parts2[3]);
							foundni2.tx_QAM = parts2[4];
							foundni2.rx_rate = Double.parseDouble(parts2[5]);
							foundni2.rx_QAM = parts2[6];
							foundni2.rate = 0.5*foundni2.tx_rate;// 存储邻居的信息
							System.out.println("foundni2 : r:"+foundni2.rate+" s:"+foundni2.signal +" n:"+foundni2.noise +" t:"+
							foundni2.tx_rate +" tq:"+foundni2.tx_QAM +" r:"+foundni2.rx_rate +" rq:"+foundni2.rx_QAM );
							//打印信息
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			try
			{
				scanner.close();
			}
			catch (Exception e1)
			{

			}
		}
		synchronized (Connections.receiveListLock)// 对接收邻居信息连接列表加锁
		{
			for (int i = 0; i < Connections.receiveList.size(); i++)// 遍历列表
			{
				if (Connections.receiveList.get(i) == this)// 当连接关闭时，将当前的连接从列表中除去
				{
					Connections.receiveList.remove(i);
					i--;
				}
			}
		}
	}
}

class ServerThreadSendCommand extends Thread // 此线程监听用于下发节点配置的端口，并接受新连接
{
	@Override
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(10002, 10);// 监听10002端口
			System.out.println("start connect");
			while (true)// 一直循环
			{
				Socket socket = serverSocket.accept();// 接受连接
				System.out.println("accept");
				ConnectionThreadSendCommand ctsc = new ConnectionThreadSendCommand(socket);
				synchronized (Connections.sendCommandListLock)// 对发送命令连接列表加锁
				{
					System.out.println("synchronized");
					Connections.sendCommandList.add(ctsc);// 将当前连接加入到列表中
				}
				ctsc.start();// 启动线程
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("在发送命令服务器线程中发生异常");
		}
	}
}

class ConnectionThreadSendCommand extends Thread// 用于发送命令的连接线程
{
	private Socket connection; // 与客户端建立的连接
	public String nodeID;// 节点的ID
	private ArrayList<String> commandToSend;// 要发送的命令
	private Object queueLock;// 发送命令队列的锁

	public ConnectionThreadSendCommand(Socket socket) // 构造方法接受一个与客户端的连接作为参数
	{
		connection = socket;
		commandToSend = new ArrayList<String>();
		queueLock = new Object();
	}

	@Override
	public void run() // 线程运行时，执行此函数
	{
		System.out.println("ConnectionThreadSendCommand ");
		InputStream is = null;
		Scanner scanner = null;
		OutputStream os = null;
		PrintWriter printer = null;
		try
		{
			is = connection.getInputStream(); // 获取TCP连接的输入流
			scanner = new Scanner(is); // 用Scanner包装输入流

			os = connection.getOutputStream();// 获取TCP连接的输出流
			printer = new PrintWriter(os);// 用PrintWriter包装输出流

			String line = scanner.nextLine();// 读取一行
			System.out.println(line);
			line.replace("\r", ""); // 去除行尾的换行符
			line.replace("\n", "");

			nodeID = line;

			while (true) // 一直循环
			{
				synchronized (queueLock)// 对命令队列加锁
				{
					while (!commandToSend.isEmpty())// 若命令队列非空
					{
						String command = commandToSend.get(0);// 取出队列的头部元素
						commandToSend.remove(0);

						printer.print(command + "\r\n");// 发送此命令
						System.out.println("printer: "+ command);
					}
				}
				Thread.sleep(100);
			}
		}
		catch (Exception e)// 若出现异常
		{
			try
			{
				scanner.close();// 释放资源
				printer.close();
			}
			catch (Exception e1)
			{

			}
		}
		synchronized (Connections.sendCommandListLock)// 对发送命令连接列表加锁
		{
			for (int i = 0; i < Connections.sendCommandList.size(); i++)// 遍历列表
			{
				if (Connections.sendCommandList.get(i) == this)// 当连接关闭时，将当前的连接从列表中除去
				{
					Connections.sendCommandList.remove(i);
					i--;
				}
			}
		}
	}

	public void sendCommand(String command)// 使用当前的连接发送一条命令
	{
		System.out.println("sendCommand, "+command);
		
		synchronized (queueLock)// 对命令队列加锁
		{
			commandToSend.add(command);// 将命令放入发送队列中
		}
	}
}

class NodeInfo // 节点信息
{
	public String nodeID;
	public ArrayList<RadioInfo> radioInfo;
}

class RadioInfo // radio信息
{
	public String radioNumber;
	public int assignedChannel;
	public ArrayList<NeighborInfo> neighborList;
}

class NeighborInfo // 邻居信息
{
	public String neighborMac;
	public double rate;
	public double signal;
	public double noise;
	public double tx_rate;
	public String tx_QAM;
	public double rx_rate;
	public String rx_QAM;
}