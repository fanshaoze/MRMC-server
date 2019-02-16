import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main// ����
{
	private static ServerThreadReceive str; // ���ڽ����ھ���Ϣ�ķ������߳�
	private static ServerThreadSendCommand stsc;// ���ڷ��Ϳ�������ķ������߳�

	public static ArrayList<NodeInfo> nodes;// �ڵ���Ϣ���б�
	public static Object nodesLock;// �б����

	private static MainWindow mainWindow;

	public static void main(String[] args)// �������ڵ�
	{
		Connections.receiveList = new ArrayList<ConnectionThreadReceive>();// ��ʼ�������б�
		Connections.receiveListLock = new Object();
		Connections.sendCommandList = new ArrayList<ConnectionThreadSendCommand>();
		Connections.sendCommandListLock = new Object();

		nodes = new ArrayList<NodeInfo>();// ��ʼ���ڵ���Ϣ�б�
		nodesLock = new Object();// ��ʼ���б����

		str = new ServerThreadReceive();// �������ڽ����ھ���Ϣ�ķ������߳�
		str.start();// �����߳�

		stsc = new ServerThreadSendCommand();// �������ڷ��Ϳ�������ķ������߳�
		stsc.start();// �����߳�

		mainWindow = new MainWindow();// ����������

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

	public static void SendConfiguration()// �·����нڵ������
	{
		System.out.println("send here");
		SendToNode("04:F0:21:39:64:26", "SETLINK 04:F0:21:39:64:26#DISABLED 04:F0:21:39:64:25#36#link203");
		SendToNode("04:F0:21:39:64:20", "SETLINK 04:F0:21:39:64:20#6#link204 04:F0:21:39:64:1F#36#link203");
		SendToNode("04:F0:21:39:64:06", "SETLINK 04:F0:21:39:64:06#6#link204 04:F0:21:39:64:05#DISABLED");
	}

	private static void SendToNode(String nodeid, String command)// ��ĳ��ָ���Ľڵ㷢��һ������
	{
		ConnectionThreadSendCommand ctsc = null;
		synchronized (Connections.sendCommandListLock)// ���������������б����
		{
			for (ConnectionThreadSendCommand c : Connections.sendCommandList)// �ڷ������������б��У��ҵ�nodeid��ͬ������
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
		ctsc.sendCommand(command);// ��������
	}
}

class Connections// ���ӵ��б�
{
	public static ArrayList<ConnectionThreadReceive> receiveList;// ���ڽ����ھ���Ϣ�������б�
	public static Object receiveListLock;// �б����

	public static ArrayList<ConnectionThreadSendCommand> sendCommandList;// ���ڷ��Ϳ�������������б�
	public static Object sendCommandListLock;// �б����
}

class ServerThreadReceive extends Thread // ���̼߳������ڽ����ھ���Ϣ�Ķ˿ڣ�������������
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
			System.out.println("�ڽ����ھ���Ϣ�������߳��г����쳣");
		}
	}
}

class ConnectionThreadReceive extends Thread
{
	private Socket connection; // ��ͻ��˽���������

	ConnectionThreadReceive(Socket socket) // ���췽������һ����ͻ��˵�������Ϊ����
	{
		connection = socket;
	}

	@Override
	public void run() // �߳�����ʱ��ִ�д˺���
	{
		InputStream is;
		Scanner scanner = null;
		try
		{
			is = connection.getInputStream(); // ��ȡTCP���ӵ�������
			System.out.print("aaaa\n");
			//System.out.print(is);
			scanner = new Scanner(is); // ��Scanner��װ������
			while (true) // һֱѭ����ÿ�ζ�ȡһ��
			{
				String line = scanner.nextLine();
				System.out.print(line);
				line.replace("\r", ""); // ȥ����β�Ļ��з�
				line.replace("\n", "");
				//line = "NEIGHBOR 04:F0:21:39:64:1C 04:F0:21:39:64:1B 36 04:f0:21:39:64:1f"
				//		+ "#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
				String[] parts = line.split(" "); // �Կո�Ϊ�ָ������Խ��յ���һ�н��зָ�
				String command = parts[0];
				System.out.println("command : "+command);
				if (command.equals("NEIGHBOR")) // �����һ�����ھ���Ϣ
				{
					synchronized (Main.nodesLock)
					{
						boolean found = false;
						NodeInfo foundni = null;
						//��ӡ��Ϣ
						for (NodeInfo ni : Main.nodes)// �ڽڵ��б��в�������ڵ�
						{
							if (ni.nodeID.equals(parts[1]))// ����ҵ���������ѭ��
							{
								found = true;
								foundni = ni;
								break;
							}
						}
						if (found == false) // ���û���ҵ����򴴽�һ��������ӵ��ڵ��б���
						{
							foundni = new NodeInfo();
							foundni.nodeID = parts[1];
							System.out.println("foundni.nodeID : "+foundni.nodeID);
							foundni.radioInfo = new ArrayList<RadioInfo>();
							Main.nodes.add(foundni);
						}

						found = false;
						RadioInfo foundri = null;
						for (RadioInfo ri : foundni.radioInfo)// ����ĳ���ڵ��ĳ��radio
						{
							if ((ri.radioNumber.equals(parts[2]))
									&& (ri.assignedChannel == Integer.decode(parts[3]).intValue()))// ����ҵ�����Ҫradio��mac��ַ���ŵ��Ŷ�ƥ�䣩
							{
								found = true;
								foundri = ri;
							}
						}
						if (found == false)// ���û���ҵ����򴴽�һ��������ӵ�radio�б���
						{
							foundri = new RadioInfo();
							foundri.radioNumber = parts[2];
							System.out.println("foundri.radioNumber : "+foundri.radioNumber);
							foundri.assignedChannel = Integer.decode(parts[3]).intValue();
							foundri.neighborList = new ArrayList<NeighborInfo>();
							foundni.radioInfo.add(foundri);
							//��ӡ��Ϣ
						}

						for (int i = 4; i < parts.length; i++)// �����ھ���Ϣ��ʣ�ಿ��
						{
							String s = parts[i];
							//��ӡ��Ϣ
							String[] parts2 = s.split("#");// ���ھ���Ϣ�Ծ���Ϊ�ָ����ֿ�
							found = false;
							NeighborInfo foundni2 = null;
							//��ӡ��Ϣ
							for (NeighborInfo ni : foundri.neighborList)// �����е��ھ��б��в�������ھ�
							{
								if (ni.neighborMac.equals(parts2[0]))// ����ҵ���������ѭ��
								{
									found = true;
									foundni2 = ni;
									break;
								}
							}
							if (found == false)// ���û���ҵ������´���һ���ھ���Ϣ��Ŀ
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
							foundni2.rate = 0.5*foundni2.tx_rate;// �洢�ھӵ���Ϣ
							System.out.println("foundni2 : r:"+foundni2.rate+" s:"+foundni2.signal +" n:"+foundni2.noise +" t:"+
							foundni2.tx_rate +" tq:"+foundni2.tx_QAM +" r:"+foundni2.rx_rate +" rq:"+foundni2.rx_QAM );
							//��ӡ��Ϣ
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
		synchronized (Connections.receiveListLock)// �Խ����ھ���Ϣ�����б����
		{
			for (int i = 0; i < Connections.receiveList.size(); i++)// �����б�
			{
				if (Connections.receiveList.get(i) == this)// �����ӹر�ʱ������ǰ�����Ӵ��б��г�ȥ
				{
					Connections.receiveList.remove(i);
					i--;
				}
			}
		}
	}
}

class ServerThreadSendCommand extends Thread // ���̼߳��������·��ڵ����õĶ˿ڣ�������������
{
	@Override
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(10002, 10);// ����10002�˿�
			System.out.println("start connect");
			while (true)// һֱѭ��
			{
				Socket socket = serverSocket.accept();// ��������
				System.out.println("accept");
				ConnectionThreadSendCommand ctsc = new ConnectionThreadSendCommand(socket);
				synchronized (Connections.sendCommandListLock)// �Է������������б����
				{
					System.out.println("synchronized");
					Connections.sendCommandList.add(ctsc);// ����ǰ���Ӽ��뵽�б���
				}
				ctsc.start();// �����߳�
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("�ڷ�������������߳��з����쳣");
		}
	}
}

class ConnectionThreadSendCommand extends Thread// ���ڷ�������������߳�
{
	private Socket connection; // ��ͻ��˽���������
	public String nodeID;// �ڵ��ID
	private ArrayList<String> commandToSend;// Ҫ���͵�����
	private Object queueLock;// ����������е���

	public ConnectionThreadSendCommand(Socket socket) // ���췽������һ����ͻ��˵�������Ϊ����
	{
		connection = socket;
		commandToSend = new ArrayList<String>();
		queueLock = new Object();
	}

	@Override
	public void run() // �߳�����ʱ��ִ�д˺���
	{
		System.out.println("ConnectionThreadSendCommand ");
		InputStream is = null;
		Scanner scanner = null;
		OutputStream os = null;
		PrintWriter printer = null;
		try
		{
			is = connection.getInputStream(); // ��ȡTCP���ӵ�������
			scanner = new Scanner(is); // ��Scanner��װ������

			os = connection.getOutputStream();// ��ȡTCP���ӵ������
			printer = new PrintWriter(os);// ��PrintWriter��װ�����

			String line = scanner.nextLine();// ��ȡһ��
			System.out.println(line);
			line.replace("\r", ""); // ȥ����β�Ļ��з�
			line.replace("\n", "");

			nodeID = line;

			while (true) // һֱѭ��
			{
				synchronized (queueLock)// ��������м���
				{
					while (!commandToSend.isEmpty())// ��������зǿ�
					{
						String command = commandToSend.get(0);// ȡ�����е�ͷ��Ԫ��
						commandToSend.remove(0);

						printer.print(command + "\r\n");// ���ʹ�����
						System.out.println("printer: "+ command);
					}
				}
				Thread.sleep(100);
			}
		}
		catch (Exception e)// �������쳣
		{
			try
			{
				scanner.close();// �ͷ���Դ
				printer.close();
			}
			catch (Exception e1)
			{

			}
		}
		synchronized (Connections.sendCommandListLock)// �Է������������б����
		{
			for (int i = 0; i < Connections.sendCommandList.size(); i++)// �����б�
			{
				if (Connections.sendCommandList.get(i) == this)// �����ӹر�ʱ������ǰ�����Ӵ��б��г�ȥ
				{
					Connections.sendCommandList.remove(i);
					i--;
				}
			}
		}
	}

	public void sendCommand(String command)// ʹ�õ�ǰ�����ӷ���һ������
	{
		System.out.println("sendCommand, "+command);
		
		synchronized (queueLock)// ��������м���
		{
			commandToSend.add(command);// ��������뷢�Ͷ�����
		}
	}
}

class NodeInfo // �ڵ���Ϣ
{
	public String nodeID;
	public ArrayList<RadioInfo> radioInfo;
}

class RadioInfo // radio��Ϣ
{
	public String radioNumber;
	public int assignedChannel;
	public ArrayList<NeighborInfo> neighborList;
}

class NeighborInfo // �ھ���Ϣ
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