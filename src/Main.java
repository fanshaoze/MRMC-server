import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	
	public static String rootMacaddr;
	public static double[][][][] NeiInforArrary;
	public static int channel5G = 36;
	public static int channel2G = 6;
	
	public static void main(String[] args)// �������ڵ�
	{
		Connections.receiveList = new ArrayList<ConnectionThreadReceive>();// ��ʼ�������б�
		Connections.receiveListLock = new Object();
		Connections.sendCommandList = new ArrayList<ConnectionThreadSendCommand>();
		Connections.sendCommandListLock = new Object();
		
		rootMacaddr = "04:F0:21:39:64:26";
		
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
		
		//SendToNode("04:F0:21:39:64:06", "SETLINK 04:F0:21:39:64:06#6#link204 04:F0:21:39:64:05#DISABLED");
		//SendToNode("04:F0:21:39:64:26", "SETLINK 04:F0:21:39:64:26#6#link204 04:F0:21:39:64:25#DISABLED");
		//SendToNode("04:F0:21:39:64:1C", "SETLINK 04:F0:21:39:64:1B#36#link204 04:F0:21:39:64:1F#36#link203");
	}
	public static void BFSofMRMC(){// MRMC�ĳ����㷨�������������
		//���ļ��ķ�ʽ
		BFStestinit("");
		
		int i,j,k= 0;
		int front = 0;
		int back = 0;
		int elem = 0;
		int a,b,c,d;
		int flag = 0;
		NodeInfo niTemp;
		NodeInfo niTemp1;
		int radioNo = 0;
		int nodeNum = 0;
		int radioNum = 0;
		RadioInfo radioTemp;
		RadioInfo radioTemp1;
		nodeNum = nodes.size();
		NodeInfo nis = nodes.get(0);
		radioNum = nis.radioInfo.size();
		int [][] visited = new int [nodeNum][radioNum];
		int [][] signaled = new int [nodeNum][radioNum];
		double[][] resultTemp = new double[nodeNum][radioNum];
		String[] results = new String[nodeNum];
		
		
		for(NodeInfo ni: Main.nodes){
			
			if(ni.nodeID.equals(rootMacaddr)){
				//ni_temp = (NodeInfo)
			}
			//i++;
		}
		
		compose();
		int que[] = new int[1000];
		for(i = 0;i<1000;i++){
			que[i] = -1;
		}
		
		
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				resultTemp[a][b] = 0.0;

			}
		}
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				visited[a][b] = 0;	
			}
		}
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				signaled[a][b] = 0;	
			}
		}
		que[0] = 0;
		back+=1;
		//���У�Ԫ����nodeID��ÿ���ҵ���Ƶ�� t%node-number�������neighbor�µ�
		//System.out.println("�γ̣�" + c3.name + "��һ�γ��ֵ�����λ��Ϊ��" + coursesToSelect.indexOf(c3));
		while(front != back){
			String radioMac = null;
			int channel = 0;
			String ssid;
			elem = que[front];
			front += 1;
			niTemp = nodes.get(elem);
			for (i = 0;i<radioNum;i++){
				if (visited[elem][i] == 0) {
					visited[elem][i] = 1;
					radioMac = niTemp.radioInfo.get(i).radioNumber;
					if(i == 0) channel = channel2G;
					else channel = channel5G;
					break;
				}
			}
			flag = 0;
			String[] submac = radioMac.split(":");
			ssid = "link" + submac[submac.length-1];
			radioTemp = niTemp.radioInfo.get(i);
			for(NeighborInfo nei : radioTemp.neighborList){
				for(j = 0;j<nodeNum;j++){
					niTemp1 = nodes.get(j);
					for(k = 0;k<radioNo;k++){
						radioTemp1 = niTemp1.radioInfo.get(k);
						if(radioTemp1.radioNumber.equals(nei.neighborMac)){
							if(visited[j][k] == 0){
								flag = 1;
								//Ӧ�����ӹ�������ȵ��ж�
								nodes.get(j).radioInfo.get(k).assignedssid = ssid;
								nodes.get(j).radioInfo.get(k).assignedChannel = channel;
								nodes.get(j).radioInfo.get(k).direction = "up";
								if (NeiInforArrary[elem][j][i][k]<=-70.0 || NeiInforArrary[j][elem][k][i]<=-70.0){
									for( a= 0;a<nodeNum;a++){
										for (b = 0;b<radioNum;b++){
											if(a != elem && b != i){
												if(NeiInforArrary[a][j][b][k]>-70){
													signaled[j][k] = 1;
												}
											}
										}
									}
								}
								resultTemp[j][k] = 1;
								
								visited[j][k] = 1;
								que[back] = j;
								back+=1;
								
							}
						}
					}
				}
				
				//��ȡnei���棬����node����Ϣ
			}
			if(flag == 1){
				nodes.get(elem).radioInfo.get(i).assignedssid = ssid;
				nodes.get(elem).radioInfo.get(i).assignedChannel = channel;
				nodes.get(elem).radioInfo.get(i).direction = "down";
				resultTemp[elem][i] = 1;
			}
		}
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				if(signaled[a][b] ==1){
					for(c = 0;c<nodeNum;c++){
						for(d = 0;d<radioNum;d++){
							if(NeiInforArrary[c][a][d][b]>-70 && NeiInforArrary[a][c][b][d]>-70 && 
									nodes.get(c).radioInfo.get(b).direction == "down"){
								nodes.get(a).radioInfo.get(b).assignedssid = nodes.get(c).radioInfo.get(d).assignedssid;
								nodes.get(a).radioInfo.get(b).assignedChannel = nodes.get(c).radioInfo.get(d).assignedChannel;
								//nodes.get(a).radioInfo.get(b).direction = "up";
								//���������ŵģ�

							}
						}
				    }
			    }
			}
		}
		for(a = 0;a<nodeNum;a++){
			results[a] = "SETLINK ";
			for(b = 0;b<radioNum;b++){
				String subRadioMac = nodes.get(elem).radioInfo.get(i).assignedssid.substring(0, 4);
				if (resultTemp[a][b] == 1){
					radioTemp1 =  nodes.get(elem).radioInfo.get(i);
					results[a] += radioTemp1.radioNumber+"#"+radioTemp1.assignedChannel+"#"+radioTemp1.assignedssid+" ";
				}
			}
			results[a] = results[a].substring(0, results[a].length()-2);
		}
		for(a = 0;a<nodeNum;a++){
			SendToNode(nodes.get(a).nodeID, results[a]);
		}
	}
		//����γ��ַ�����ssid��channel
		//String 
	public static boolean queEmpty(int que[])// ����γ�
	{
		int i = 0; 
		for(i = 0;i<que.length;i++){
			if(que[i] != 0){
				return false;
			}
		}
		return true;
	}
	
	
	public static void compose()// ����γ�
	{
		int a,b,c,d,e;
		int neiNum;
		RadioInfo ra;
		RadioInfo ras;
		System.out.println("send here");
		NodeInfo ni = nodes.get(0);
		NodeInfo nis;
		NeiInforArrary = new double[nodes.size()][nodes.size()][ni.radioInfo.size()][ni.radioInfo.size()];
		for(a = 0;a<nodes.size();a++){
			for(b = 0;b<ni.radioInfo.size();b++){
				for(d = 0;d<nodes.size();d++){
					for(e = 0;e<ni.radioInfo.size();e++){
						NeiInforArrary[a][d][b][e] = 0.0;
					}
				}	
			}
		}
		for(a = 0;a<nodes.size();a++){
			ni = nodes.get(a);
			for(b = 0;b<ni.radioInfo.size();b++){
				ra = ni.radioInfo.get(b);
				neiNum = ra.neighborList.size(); 
				for(c = 0;c<neiNum;c++){
					NeighborInfo nei = ra.neighborList.get(c);
					for(d = 0;d<nodes.size();d++){
						nis = nodes.get(d);
						for(e = 0;e<nis.radioInfo.size();e++){
							ras = nis.radioInfo.get(e);
							if(nei.neighborMac.equals(ras.radioNumber)){
								NeiInforArrary[a][d][b][e] = nei.signal;
							}
						}	
					}
				}
			}
		}
	}
	
	private static void SendToNode(String nodeid, String command)// ��ĳ��ָ���Ľڵ㷢��һ������
	{
		ConnectionThreadSendCommand ctsc = null;
		synchronized (Connections.sendCommandListLock)// ���������������б����
		{
			for (ConnectionThreadSendCommand c : Connections.sendCommandList)// �ڷ������������б��У��ҵ�nodeid��ͬ������
			{
				System.out.println("nodeID : "+c.nodeID);
				System.out.println("nodeid : "+nodeid);
				if (c.nodeID.equals(nodeid))
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
	public static void BFStestinit(String neighborinform) throws IOException // �߳�����ʱ��ִ�д˺���
	{
		String str1 = "temp.txt"; 
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader br = new BufferedReader(fr);   
			neighborinform = br.readLine();   
		
		
			InputStream is;
			Scanner scanner = null;
		//is = neighborinform;
		//is = connection.getInputStream(); // ��ȡTCP���ӵ�������
		//System.out.print("aaaa\n");
		//System.out.print(is);
		//scanner = new Scanner(is); // ��Scanner��װ������
		//while (true) // һֱѭ����ÿ�ζ�ȡһ��
			while (neighborinform != null) {
			{
				String line = neighborinform;
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
				neighborinform = br.readLine();  
			}
		}
	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		e.printStackTrace();
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

						printer.println(command + "\r\n");// ���ʹ�����
						//os.flush();
						printer.flush();
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
	public String direction;
	public String assignedssid;
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