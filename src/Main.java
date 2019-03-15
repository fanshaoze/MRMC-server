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

public class Main// 主类
{
	private static ServerThreadReceive str; // 用于接收邻居信息的服务器线程
	private static ServerThreadSendCommand stsc;// 用于发送控制命令的服务器线程
	
	public static ArrayList<NodeInfo> nodes;// 节点信息的列表
	public static Object nodesLock;// 列表的锁

	private static MainWindow mainWindow;
	
	public static String rootMacaddr;
	public static double[][][][] NeiInforArrary;
	public static int channel5G = 36;
	public static int channel2G = 6;
	
	public static void main(String[] args)// 程序的入口点
	{
		

		Connections.receiveList = new ArrayList<ConnectionThreadReceive>();// 初始化连接列表
		Connections.receiveListLock = new Object();
		Connections.sendCommandList = new ArrayList<ConnectionThreadSendCommand>();
		Connections.sendCommandListLock = new Object();

		rootMacaddr = "04:F0:21:39:64:26";
		
		nodes = new ArrayList<NodeInfo>();// 初始化节点信息列表
		nodesLock = new Object();// 初始化列表的锁
		//BFSofMRMC();

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
		//SendToNode("04:F0:21:39:64:26", "SETLINK 04:F0:21:39:64:26#DISABLED 04:F0:21:39:64:25#36#link203");
		//SendToNode("04:F0:21:39:64:20", "SETLINK 04:F0:21:39:64:20#6#link204 04:F0:21:39:64:1F#36#link203");
		//SendToNode("04:F0:21:39:64:06", "SETLINK 04:F0:21:39:64:06#6#link204 04:F0:21:39:64:05#DISABLED");
		
		BFSofMRMC();
		
		
		//SendToNode("04:F0:21:39:64:06", "SETLINK 04:F0:21:39:64:06#6#link204 04:F0:21:39:64:05#DISABLED");
		//SendToNode("04:F0:21:39:64:26", "SETLINK 04:F0:21:39:64:26#6#link204 04:F0:21:39:64:25#DISABLED");
		//SendToNode("04:F0:21:39:64:1C", "SETLINK 04:F0:21:39:64:1B#36#link204 04:F0:21:39:64:1F#36#link203");
	}
	public static void BFSofMRMC(){// MRMC的初步算法：广度优先搜索
		//读文件的方式
		//BFStestinit("");
		
		int i,j,k= 0;
		int front = 0;
		int back = 0;
		int elem = 0;
		int a,b,c,d,e;
		int flag,flag1,flag2 = 0;
		int visitednum = 0;
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
		System.out.print(radioNum+" "+nodeNum);
		
		for(NodeInfo ni: Main.nodes){
			
			if(ni.nodeID.equalsIgnoreCase(rootMacaddr)){
				//ni_temp = (NodeInfo)
			}
			//i++;
		}
		
		compose();
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				for(d = 0;d<nodeNum;d++){
					for(e = 0;e<radioNum;e++){
						System.out.print(NeiInforArrary[a][d][b][e]+" ");
					}
					System.out.print("&");
				}
				System.out.println(b+"b");
			}
			System.out.println(a+"a");
		}
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
		//队列，元素是nodeID，每次找的射频是 t%node-number，找这个neighbor下的
		//System.out.println("课程：" + c3.name + "第一次出现的索引位置为：" + coursesToSelect.indexOf(c3));
		while(front != back){
			flag1 = 0;
			flag2 = 0;
			visitednum = 0;
			String radioMac = null;
			int channel = 0;
			String ssid;
			elem = que[front];
			front += 1;
			niTemp = nodes.get(elem);
			for (i = 0;i<radioNum;i++){
				if (visited[elem][i] == 0 && niTemp.radioInfo.get(i).neighborList.size()>0) {
					//邻居判断
					for(a = 0;a<nodeNum;a++){
						for (b = 0;b<radioNum;b++){
							if(NeiInforArrary[elem][a][i][b] != 0){
								visitednum = 0;
								for(j = 0;j<radioNum;j++){
									if(visited[a][j] == 0){
										visitednum++;
									}
								}
							}
							if(visitednum == radioNum){
								flag2 = 1;
								break;
							}
						}
						if(flag2 == 1) break;
					}
					if(flag2 == 1){
						visited[elem][i] = 1;
						radioMac = niTemp.radioInfo.get(i).radioNumber;
						if(i == 0) channel = channel2G;
						else channel = channel5G;
						flag1 = 1;
						break;
					}
					else{
						visited[elem][i] = 1;
						continue;
					}
				}
				else if (visited[elem][i] == 0 && niTemp.radioInfo.get(i).neighborList.size()== 0) {
					//邻居判断
					visited[elem][i] = 1;
					continue;
				}
			}
			if (flag1 == 0){
				continue;
			}
			flag = 0;
			String[] submac = radioMac.split(":");
			ssid = "link" + submac[submac.length-1];
			System.out.println(ssid);
			radioTemp = niTemp.radioInfo.get(i);
			for(a = 0;a<nodeNum;a++){
				for(b = 0;b<radioNum;b++){
					for(d = 0;d<nodeNum;d++){
						for(e = 0;e<radioNum;e++){
							System.out.print(NeiInforArrary[a][d][b][e]+" ");
						}
						System.out.print("&");
					}
					System.out.println(b+"b");
				}
				System.out.println(a+"a");
			}
			for(NeighborInfo nei : radioTemp.neighborList){
				for(j = 0;j<nodeNum;j++){
					niTemp1 = nodes.get(j);
					for(k = 0;k<radioNum;k++){
						radioTemp1 = niTemp1.radioInfo.get(k);
						System.out.println(radioTemp1.radioNumber+" "+nei.neighborMac);
						if(radioTemp1.radioNumber.equalsIgnoreCase(nei.neighborMac)){
							if(visited[j][k] == 0){
								flag = 1;
								//应当增加关于信噪比的判断
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
				
				//获取nei里面，关于node的信息
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
				for(d = 0;d<nodeNum;d++){
					for(e = 0;e<radioNum;e++){
						System.out.print(NeiInforArrary[a][d][b][e]+" ");
					}
					System.out.print("&");
				}
				System.out.println(b+"b");
			}
			System.out.println(a+"a");
		}
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				if(signaled[a][b] ==1){
					for(c = 0;c<nodeNum;c++){
						for(d = 0;d<radioNum;d++){
							if(NeiInforArrary[c][a][d][b]>-70.0 && NeiInforArrary[a][c][b][d]>-70.0 && 
									NeiInforArrary[c][a][d][b]<0.0 && NeiInforArrary[a][c][b][d]<0.0 &&
									nodes.get(c).radioInfo.get(d).direction == "down"){
								System.out.println(NeiInforArrary[c][a][d][b]+" %% "+NeiInforArrary[a][c][b][d]);
								nodes.get(a).radioInfo.get(b).assignedssid = nodes.get(c).radioInfo.get(d).assignedssid;
								nodes.get(a).radioInfo.get(b).assignedChannel = nodes.get(c).radioInfo.get(d).assignedChannel;
								System.out.println(" && ");
								System.out.println(nodes.get(a).radioInfo.get(b).radioNumber+" "+nodes.get(c).radioInfo.get(d).radioNumber);
								//nodes.get(a).radioInfo.get(b).direction = "up";
								//还不是最优的，

							}
						}
				    }
			    }
			}
		}
		for(a = 0;a<nodeNum;a++){
			results[a] = "SETLINK ";
			for(b = 0;b<radioNum;b++){
				//String subRadioMac = nodes.get(elem).radioInfo.get(i).assignedssid.substring(0, 4);
				if (resultTemp[a][b] == 1){
					radioTemp1 =  nodes.get(a).radioInfo.get(b);
					results[a] += radioTemp1.radioNumber+"#"+radioTemp1.assignedChannel+"#"+radioTemp1.assignedssid+" ";
					System.out.println(results[a]);
				}
				else{
					radioTemp1 =  nodes.get(a).radioInfo.get(b);
					results[a] += radioTemp1.radioNumber+"#"+"DISABLED"+" ";
					System.out.println(results[a]);
				}
			}
			results[a] = results[a].substring(0, results[a].length()-1);
			System.out.println(results[a]);
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		for(a = 0;a<nodeNum;a++){
			System.out.println(results[a]);
		}
		for(a = 0;a<nodeNum;a++){
			SendToNode(nodes.get(a).nodeID, results[a]);
		}
	}
		//组合形成字符串。ssid，channel
		//String 
	public static boolean queEmpty(int que[])// 组合形成
	{
		int i = 0; 
		for(i = 0;i<que.length;i++){
			if(que[i] != 0){
				return false;
			}
		}
		return true;
	}
	
	
	public static void compose()// 组合形成
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
			System.out.println(ni.nodeID);
			for(b = 0;b<ni.radioInfo.size();b++){
				ra = ni.radioInfo.get(b);
				neiNum = ra.neighborList.size(); 
				for(c = 0;c<neiNum;c++){
					NeighborInfo nei = ra.neighborList.get(c);
					for(d = 0;d<nodes.size();d++){
						nis = nodes.get(d);
						for(e = 0;e<nis.radioInfo.size();e++){
							ras = nis.radioInfo.get(e);
							System.out.println(nei.neighborMac+" "+ras.radioNumber);
							System.out.println(nei.neighborMac.equalsIgnoreCase(ras.radioNumber));
							if(nei.neighborMac.equalsIgnoreCase(ras.radioNumber)){
								NeiInforArrary[a][d][b][e] = nei.signal;
							}
						}	
					}
				}
			}
		}
	}
	
	private static void SendToNode(String nodeid, String command)// 向某个指定的节点发送一条命令
	{
		ConnectionThreadSendCommand ctsc = null;
		synchronized (Connections.sendCommandListLock)// 给发送命令连接列表加锁
		{
			for (ConnectionThreadSendCommand c : Connections.sendCommandList)// 在发送命令连接列表中，找到nodeid相同的连接
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
		ctsc.sendCommand(command);// 发送命令
	}
	public static void BFStestinit(String neighborinform)  // 线程运行时，执行此函数
	{
		int j = 0;
		String lines[] = new String[8];
		/*
		lines[0] = "NEIGHBOR 04:F0:21:39:64:12 04:F0:21:39:64:11 36 04:f0:21:39:64:1B#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2 04:f0:21:39:64:17#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[1] = "NEIGHBOR 04:F0:21:39:64:1C 04:F0:21:39:64:1B 36 04:f0:21:39:64:11#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2 04:f0:21:39:64:17#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[2] = "NEIGHBOR 04:F0:21:39:64:1C 04:F0:21:39:64:1C 11 04:f0:21:39:64:14#-71#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2 04:f0:21:39:64:16#-28#-95#781.000000#VHT-MCS$9$80MHz$VHT-NSS$2#867.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[3] = "NEIGHBOR 04:F0:21:39:64:18 04:F0:21:39:64:17 36 04:f0:21:39:64:11#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[4] = "NEIGHBOR 04:F0:21:39:64:18 04:F0:21:39:64:18 11 04:f0:21:39:64:20#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2 04:f0:21:39:64:14#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[5] = "NEIGHBOR 04:F0:21:39:64:14 04:F0:21:39:64:14 11 04:f0:21:39:64:1C#-71#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2 04:f0:21:39:64:18#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[6] = "NEIGHBOR 04:F0:21:39:64:16 04:F0:21:39:64:16 11 04:f0:21:39:64:1C#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[7] = "NEIGHBOR 04:F0:21:39:64:20 04:F0:21:39:64:20 11 04:f0:21:39:64:18#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		*/
		String str1 = "temp.txt"; 
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader br = new BufferedReader(fr);   
			try {
				neighborinform = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		
		
			InputStream is;
			Scanner scanner = null;
			String line = neighborinform;
			//neighborinform = lines[0];
		//is = neighborinform;
		//is = connection.getInputStream(); // 获取TCP连接的输入流
		//System.out.print("aaaa\n");
		//System.out.print(is);
		//scanner = new Scanner(is); // 用Scanner包装输入流
		//while (true) // 一直循环，每次读取一行
			while (neighborinform != null) {
			
				line = neighborinform;
				System.out.println(line);
				
				//System.out.print(line);
				line.replace("\r", ""); // 去除行尾的换行符
				line.replace("\n", "");
				//line = "NEIGHBOR 04:F0:21:39:64:1C 04:F0:21:39:64:1B 36 04:f0:21:39:64:1f"
				//		+ "#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
				String[] parts = line.split(" "); // 以空格为分隔符，对接收到的一行进行分割
				String command = parts[0];
				System.out.println("command : "+command);
				if (command.equals("NEIGHBOR")) // 如果这一行是邻居信息
				{

					boolean found = false;
					NodeInfo foundni = null;
					System.out.println(Main.nodes.size());
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
						if(s.equals("NONEIGHBOR")){
							break;
						}
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
				j++;
				try {
					neighborinform = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
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

						printer.println(command + "\r\n");// 发送此命令
						//os.flush();
						printer.flush();
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
	public String direction;
	public String assignedssid;
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