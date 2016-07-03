package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.FloodlightProvider;
import controller.json.JsonToController;
import controller.json.PolicyManager;
import controller.json.QoSManager;
import controller.json.StaticFlowPusher;
import controller.util.HexString;
import controller.util.MySQLHelper;
import controller.util.StringUtils;
import model.ActionOutput;
import model.Device;
import model.FlowEntry;
import model.IAction;
import model.Instruction;
import model.InstructionApplyActions;
import model.Match;
import model.Meter;
import model.OFPort;
import model.Policy;
import model.Queue;
import model.Switch;
import twaver.DataBoxSelectionAdapter;
import twaver.DataBoxSelectionEvent;
import twaver.DataBoxSelectionListener;
import twaver.Element;
import twaver.Link;
import twaver.Node;
import twaver.TDataBox;
import twaver.TWaverConst;
import twaver.chart.LineChart;
import twaver.network.TNetwork;
import twaver.network.background.ColorBackground;
import twaver.table.TTable;
import twaver.table.TTableAdapter;
import twaver.table.TTableColumn;
import twaver.table.TTableModel;
import view.util.DisplayMessage;
import view.util.SWTResourceManager;

public class MainUI {

	private Shell shell;
	private Display display;
	
	private TDataBox topologybox = new TDataBox("Network Topology");//DataBox
	private TNetwork network = new TNetwork(topologybox);//创建可视化视图组件
	private TDataBox chartbox = new TDataBox("Line Chart");
	private final LineChart lineChart = new LineChart(chartbox);
	private Element send = new Node();
	private Element receive = new Node();
	
	private static FloodlightProvider floodlightProvider = FloodlightProvider.getSingleton();
	private Label hostnameLab,healthLab,roleLab,uptimeLab,memoryLab,modulesLab,tablesLab;
	
	private Button qosbtn;
	
	private TTable blackTable;
	private Text polNametxt,polSwtxt,polPrioritytxt;
	private Composite addComposite;
	private Composite deleteComposite;
	private Text vipsrctxt,vipdsttxt;
	private Composite saddComposite;
	private Composite sdComposite;
	private Text vpathtxt;
	private Text siptxt,stxt;
	private Text sdiptxt,sdtxt,sdidtxt;
	private Composite baddComposite, bconComposite;
	private Composite bdComposite;
	private Text dnametxt,dipsrctxt,dipdsttxt,dporttxt;
	private Text bidtxt,bnametxt,ipsrctxt,ipdsttxt,porttxt,btxt;
	private Text maxtxt;
	private Combo mincom;
	private TTable busTable;
	private List<Switch> switchs;
	private List<Device> devices;
	private List<model.Link> links;
	private Map<String,Switch> switchsMap = new HashMap<String,Switch>();
	
	private String dpidPattern = "^[\\d|\\D][\\d|\\D]:[\\d|\\D][\\d|\\D]:" +
			"[\\d|\\D][\\d|\\D]:[\\d|\\D][\\d|\\D]:" +
			"[\\d|\\D][\\d|\\D]:[\\d|\\D][\\d|\\D]:" +
			"[\\d|\\D][\\d|\\D]:[\\d|\\D][\\d|\\D]$";
	
	private Timer timer;
	private String switchDpid = "00:00:00:00:00:00:00:01";
	private String portNo = "1";
	
	private Map<String, Switch> oldSwitchStats = new HashMap<String, Switch>();
	private Map<String, Double> portRXRateMap = new HashMap<String ,Double>();
	private Map<String, Double> portTXRateMap = new HashMap<String ,Double>();
	int chartX = 0;
	
	Map<Integer, String> blackTmp = new HashMap<>();
	Map<Integer, String> tasksTmp = new HashMap<>();
	
	private static Logger log = LoggerFactory.getLogger(MainUI.class);
	public MainUI(){
		open();
	}
	
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// If the window is closed, stop the entire application.
		display.dispose();
		System.exit(0);
	}
	//展示控制器信息
	private void displayControllerInfo(){	
		try {
			 if (JsonToController.getControllerInfo()) {
				 if (floodlightProvider.getController().getIP()!=null) {
						hostnameLab.setText(floodlightProvider.getController().getIP()+":"+floodlightProvider.getController().getOpenFlowPort());
						healthLab.setText(floodlightProvider.getController().getHealth());
						memoryLab.setText(floodlightProvider.getController().getMemory().getFreeOfTotal());
						uptimeLab.setText(String.valueOf(floodlightProvider.getController().getUptime())+" msec");
				        roleLab.setText(floodlightProvider.getController().getRole());
				        
						log.info("Controller hostname: {}:{}",floodlightProvider.getController().getIP(),floodlightProvider.getController().getOpenFlowPort());
						log.info("Controller health: {}", floodlightProvider.getController().getHealth());
						log.info("Controller role: {}", floodlightProvider.getController().getRole());
						log.info("Controller memory: {}", floodlightProvider.getController().getMemory());
						log.info("Controller uptime: {}", floodlightProvider.getController().getUptime());
				        StringBuilder str1 = new StringBuilder();
				        Iterator<String> i = floodlightProvider.getController().getLoadedModules().iterator();
				        while (i.hasNext()) {
							String s = (String) i.next();
							str1.append(s);
							str1.append("\n");
						}
						modulesLab.setText(str1.toString());
						
						StringBuilder str2 = new StringBuilder();			
						i = floodlightProvider.getController().getTables().iterator();
						while (i.hasNext()) {
							String s = (String) i.next();
							str2.append(s);
							str2.append("\n");
						}
						tablesLab.setText(str2.toString());
					} 
			}
			 else {
				log.error("Failed to get Controller Infomation");
				DisplayMessage.displayError(shell, "获取Controller信息出错！");
			}
		} catch (IOException e) {
			log.error("Failed to get Controller information: {}",e.getMessage());
			//e.printStackTrace();
		}
	}
	
	//展示黑名单
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void displayBlackList(){
		TTableModel model = blackTable.getTableModel();
		model.clearRawData();
		blackTmp.clear();
		int id = 1;
		for(String key : floodlightProvider.blackList.keySet()) {
			Vector row = new Vector();
			blackTmp.put(id, key);
			row.addElement(id++);
			row.addElement(floodlightProvider.blackList.get(key)[0]);
			row.addElement(floodlightProvider.blackList.get(key)[1]);
			model.addRow(row);
		}
	}
	
	//展示黑名单
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void displayTasks(){
		TTableModel model = busTable.getTableModel();
		model.clearRawData();
		tasksTmp.clear();
		int id = 1;
		for(String key : floodlightProvider.tasks.keySet()) {
			Vector row = new Vector();
			tasksTmp.put(id, key);
			row.addElement(id++);
			row.addElement(key);
			row.addElement(floodlightProvider.tasks.get(key)[0]);
			row.addElement(floodlightProvider.tasks.get(key)[1]);
			row.addElement(floodlightProvider.tasks.get(key)[2]);
			row.addElement(floodlightProvider.tasks.get(key)[3]);
			row.addElement(floodlightProvider.tasks.get(key)[4]);
			row.addElement(floodlightProvider.tasks.get(key)[5]);
			model.addRow(row);
		}
		boolean enbale = false;
		try {
			enbale = QoSManager.getStatusInfo();
		} catch (IOException e2) {
			log.error("Failed to get status of QoS module: {}", e2.getMessage());
			//e2.printStackTrace();
		}
		if (!floodlightProvider.getController().getQosStatus().equalsIgnoreCase("Yes") && !enbale) {
		qosbtn.setVisible(true);
		qosbtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					if (QoSManager.enaleQoS()) {
						qosbtn.setVisible(false);
					} else {
						DisplayMessage.displayError(shell, "用户业务管理启动失败！");
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	}
	
	private void createContents(){
		shell = new Shell(SWT.MIN);
		shell.setSize(1200, 800);
		shell.setText("Floodlight 控制器管理平台");
		shell.setBackground(new org.eclipse.swt.graphics.Color(Display.getCurrent(), 255, 255, 255));
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);  //设置父控件的背景模式，即所有的子控件采用父控件的背景色
		try {
			ImageData scratimgdt = new ImageData(getClass().getResourceAsStream("/logo1.png"));
			Image scratimg = new Image(display,scratimgdt);
			shell.setImage(scratimg);
			
			ImageData logoimgdt = new ImageData(getClass().getResourceAsStream("/logo9.png"));
			Image logoimg = new Image(display,logoimgdt);
			Label logolab = new Label(shell, SWT.NONE);
			logolab.setBounds(30, 15, 250, 50);
			logolab.setImage(logoimg);
			
			ImageData floodlightimgdt = new ImageData(getClass().getResourceAsStream("/logo.jpg"));
			Image floodlightimg = new Image(display,floodlightimgdt);
			Label floodlightlab = new Label(shell, SWT.NONE);
			floodlightlab.setBounds(1050, 10, 150, 60);
			floodlightlab.setImage(floodlightimg);
			
//			Label lab = new Label(shell, SWT.CENTER);
//			lab.setFont(SWTResourceManager.getFont("华文新魏", 28, SWT.NORMAL));
//			lab.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
//			lab.setBounds(80,20,1000,50);
//			//lab.setText("Floodlight 控制器管理平台");
		} catch (Exception e) {
			log.error("Failed to load images: {}",e.getMessage());
			//e.printStackTrace();
		}
		
		Label sptrlab = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		sptrlab.setBounds(10, 70, 1160, 5);
		
		final TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(10, 80, 1160, 660);
		tabFolder.setFont(SWTResourceManager.getFont("华文新魏", 20, SWT.NORMAL));
		//**********************控制器******************************************
		TabItem conTabItem = new TabItem(tabFolder, SWT.NONE);
		conTabItem.setText("  控制器  ");
		ImageData conimgdt = new ImageData(getClass().getResourceAsStream("/c0.png"));
		Image conimg = new Image(display,conimgdt);
		conTabItem.setImage(conimg);
		Composite conComposite = new Composite(tabFolder, SWT.NONE);
		conTabItem.setControl(conComposite);
		
		Label conlab = new Label(conComposite, SWT.NONE);
		conlab.setBounds(30, 20, 200, 30);
		conlab.setFont(SWTResourceManager.getFont("华文新魏", 15, SWT.NORMAL));
		conlab.setText("Controller Status");
		
		Label consptrlab = new Label(conComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		consptrlab.setBounds(20, 50, 1100, 5);
		
		Label hnLabel = new Label(conComposite, SWT.None);
		hnLabel.setBounds(40,90,150,30);
		hnLabel.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		hnLabel.setText("Hostname: ");
		
		hostnameLab = new Label(conComposite, SWT.NONE);
		hostnameLab.setBounds(200,90,200,30);
		hostnameLab.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		//hostnameLab.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		
		Label hrLabel = new Label(conComposite, SWT.None);
		hrLabel.setBounds(40,130,150,30);
		hrLabel.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		hrLabel.setText("Role: ");
		
		roleLab = new Label(conComposite, SWT.NONE);
		roleLab.setBounds(200,130,200,30);
		roleLab.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		//healthLab.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		
		Label hlLabel = new Label(conComposite, SWT.None);
		hlLabel.setBounds(40,170,150,30);
		hlLabel.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		hlLabel.setText("Healthy: ");
		
		healthLab = new Label(conComposite, SWT.NONE);
		healthLab.setBounds(200,170,200,30);
		healthLab.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		//healthLab.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		
		Label utLabel = new Label(conComposite, SWT.None);
		utLabel.setBounds(40,210,150,30);
		utLabel.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		utLabel.setText("Uptime: ");
		
		uptimeLab = new Label(conComposite, SWT.NONE);
		uptimeLab.setBounds(200,210,200,30);
		uptimeLab.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		//uptimeLab.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		
		Label mfLabel = new Label(conComposite, SWT.None);
		mfLabel.setBounds(40,250,150,30);
		mfLabel.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		mfLabel.setText("JVM memory bloat: ");
		
		memoryLab = new Label(conComposite, SWT.NONE);
		memoryLab.setBounds(200, 250, 400, 30);
		memoryLab.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		//memoryLab.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		
		Label mlLabel = new Label(conComposite, SWT.NONE);
		mlLabel.setBounds(600,90,150,30);
		mlLabel.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		mlLabel.setText("Modules loaded: ");
		
		modulesLab = new Label(conComposite, SWT.NONE);
		modulesLab.setBounds(600, 120, 500, 500);
		modulesLab.setFont(SWTResourceManager.getFont("华文新魏", 10, SWT.NORMAL));
		//modulesLab.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		
		Label tbLabel = new Label(conComposite, SWT.NONE);
		tbLabel.setBounds(40,290,150,30);
		tbLabel.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		tbLabel.setText("Tables int Storage: ");
		
		tablesLab = new Label(conComposite, SWT.NONE);
		tablesLab.setBounds(200, 290, 400, 400);
		tablesLab.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		///tablesLab.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		
		displayControllerInfo();
		//***************************************************************************
		
		//*************************网络拓扑*****************************************
		TabItem topTabItem = new TabItem(tabFolder, SWT.NONE);
		topTabItem.setText("  网络拓扑  ");
		ImageData topoimgdt = new ImageData(getClass().getResourceAsStream("/bus1.png"));
		Image topoimg = new Image(display,topoimgdt);
		topTabItem.setImage(topoimg);
		final Composite topComposite = new Composite(tabFolder, SWT.NONE);
		topTabItem.setControl(topComposite);
		
		Label topolab = new Label(topComposite, SWT.NONE);
		topolab.setBounds(30, 20, 200, 30);
		topolab.setFont(SWTResourceManager.getFont("华文新魏", 15, SWT.NORMAL));
		topolab.setText("Network Topology");
		
		Label toposptrlab = new Label(topComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		toposptrlab.setBounds(20, 50, 1100, 5);
		
		final Group leftgroup = new Group(topComposite, SWT.EMBEDDED);
		leftgroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		leftgroup.setLayout(new GridLayout(1, false));
		leftgroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		leftgroup.setBounds(30,60,550,550);
		leftgroup.setText("Topology");
		Composite leftComposite = new Composite(leftgroup, SWT.EMBEDDED);
		leftComposite.setBounds(20, 30, 510, 500);
		Frame leftframe = SWT_AWT.new_Frame(leftComposite);
		leftframe.setLayout(new BorderLayout());
		JPanel networkPanel = new JPanel(new BorderLayout());
		leftframe.add(networkPanel,BorderLayout.CENTER);
		
		network.doLayout(TWaverConst.LAYOUT_TREE);
		network.setBackground(new ColorBackground(Color.white));
		network.setToolbarByName("");
		//network.setAlpha(0.5f);
		networkPanel.add(network,BorderLayout.CENTER);
		
		//向topology中添加交换机
		switchs = floodlightProvider.getSwitches(true);
		final Map<String,Node> switchmap = new HashMap<String, Node>();
		for (int i = 0; i < switchs.size(); i++) {
			Node sw = new Node(switchs.get(i).getDpid());
			sw.setName("S"+HexString.toLong(switchs.get(i).getDpid()));
			//sw.setName(switchs.get(i).getDpid());
			
			String html = "<html>"+"DPID: "+switchs.get(i).getDpid()+"<br>"
					+"Manufacturer: "+switchs.get(i).getMfr_desc()+"<br>"
					+"Hardware: "+switchs.get(i).getHw_desc()+"<br>"
					+"Software: OVS"+switchs.get(i).getSw_desc()+"<br>"
					+"Table: "+switchs.get(i).getTables()+"</html>";
			sw.setToolTipText(html);
			
			sw.setImage("/switch.png");
//			double x = 30+Math.random()*400;
//			double y = 30+Math.random()*400;
//			sw.setLocation(x, y);
			switchmap.put(switchs.get(i).getDpid(), sw);
			switchsMap.put(sw.getName(), switchs.get(i));
			topologybox.addElement(sw);
		}
		
		//向topology中添加设备
		devices = floodlightProvider.getDevices(true);
		final Map<String,Node> hostmap = new HashMap<String, Node>();
		for(int i = 0; i < devices.size(); i++){
			Node host = new Node(devices.get(i).getMac_addr()); 
			
			String ipv4Addr = devices.get(i).getIpv4_addr();
			int pos = ipv4Addr.lastIndexOf('.');
			int hostNumber = Integer.valueOf(ipv4Addr.substring(pos+1));
			host.setName("H"+hostNumber);
            
			if(devices.get(i).getAttachmentPoint() != null ){
				String html = "<html>" + "IP: " + devices.get(i).getIpv4_addr() + "<br>"+
										 "MAC: " + devices.get(i).getMac_addr() + "<br>" +
										 "Attach: " +devices.get(i).getAttachmentPoint().getPort() + "<br>"
										 +"</html>";
				host.setToolTipText(html);
			}
			if (devices.get(i).getMac_addr().equals("00:00:00:00:00:20")) {
				host.setName("Server");
				host.setImage("/server.png");
			} else
				host.setImage("/host2.png");
			
//			double x = 30+Math.random()*400;
//			double y = 30+Math.random()*400;
//			host.setLocation(x, y);
			hostmap.put(devices.get(i).getMac_addr(), host);
			Link link = new Link();
			link.setFrom(host);
			link.setTo(switchmap.get(devices.get(i).getAttachmentPoint().getSwitchDPID()));
			
			String html1 = "<html>"+"0->"+devices.get(i).getAttachmentPoint().getPort()+"<br>"+"</html>";
			link.setToolTipText(html1);

			topologybox.addElement(host);
			topologybox.addElement(link);
		}
		
		//向topology中添加交换机间链路
		links = floodlightProvider.getLinks(true);
		for (int i = 0; i < links.size(); i++) {
			Link link = new Link();
			link.setFrom(switchmap.get(links.get(i).getSrcSwitch()));
			link.setTo(switchmap.get(links.get(i).getDstSwtich()));
			//link.setName(links.get(i).getDirection());
			
//			try {
//				long rx = StatisticsCollector.getSwitchPortBandwidth(links.get(i).getSrcSwitch(), OFPort.of(links.get(i).getSrcPort())).getBitsPerSecondRx();
//				long tx = StatisticsCollector.getSwitchPortBandwidth(links.get(i).getSrcSwitch(), OFPort.of(links.get(i).getSrcPort())).getBitsPerSecondTx();
//			} catch (IOException e1) {
//				log.error("Failed to get Switch Port Bandwidth: {}", e1.getMessage());
//				//e1.printStackTrace();
//			}
			String html = "<html>"+links.get(i).getSrcPort()+"&#60;->"+links.get(i).getDstPort()+"<br>"
								  +links.get(i).getDirection()+"</html>";
			link.setToolTipText(html);
			
			topologybox.addElement(link);
		}
		network.doLayout(TWaverConst.LAYOUT_SYMMETRIC, false);
		
		Group rightgroup = new Group(topComposite, SWT.NONE);
		rightgroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		rightgroup.setLayout(new GridLayout(1, false));
		rightgroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		rightgroup.setBounds(600,60,520,550);
		rightgroup.setText("Monitor");
		
		Composite rightCompositeTop = new Composite(rightgroup, SWT.EMBEDDED);
		rightCompositeTop.setBounds(20, 30, 470, 260);
		
		Frame rightframeTop = SWT_AWT.new_Frame(rightCompositeTop);
		rightframeTop.setLayout(new BorderLayout());
		JPanel chartPanel = new JPanel(new BorderLayout());
		rightframeTop.add(chartPanel,BorderLayout.CENTER);
		
		lineChart.setYAxisVisible(true);
		lineChart.setYScaleTextVisible(true);
		lineChart.setXAxisVisible(true);
		lineChart.setXScaleTextVisible(true);
		//lineChart.setStartIndex(0);
		//lineChart.setEndIndex(30);
		lineChart.setInflexionVisible(true);
		lineChart.setValueTextVisible(true);
		//lineChart.setXScaleTextSpanCount(1);
		//lineChart.setYScaleValueGap(10);
		lineChart.setInflexionVisible(true);
		lineChart.setLineType(TWaverConst.LINE_TYPE_AREA);
		chartPanel.add(lineChart,BorderLayout.CENTER);
		
		send.setName("发送流量");
		send.putChartColor(Color.GREEN);
		send.putChartInflexionStyle(TWaverConst.INFLEXION_STYLE_TRIANGLE);
		chartbox.addElement(send);
		 
		receive.setName("接受流量");
		receive.putChartColor(Color.RED);
		receive.putChartInflexionStyle(TWaverConst.INFLEXION_STYLE_DIAMOND);
		chartbox.addElement(receive);
		
		Composite rightCompositeBot = new Composite(rightgroup, SWT.EMBEDDED);
		rightCompositeBot.setBounds(20, 330, 470, 210);
		rightCompositeBot.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		Frame rightframeBot = SWT_AWT.new_Frame(rightCompositeBot);
		rightframeBot.setLayout(new BorderLayout());
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(Color.RED);
		rightframeBot.add(tablePanel,BorderLayout.CENTER);
		
		final TTable table = new TTable();
		table.addColumn(new TTableColumn("端口号"));
		table.addColumn(new TTableColumn("连接状态"));
		table.addColumn(new TTableColumn("发送流量(KB)"));
		table.addColumn(new TTableColumn("接受流量(KB)"));
		table.addColumn(new TTableColumn("丢包数"));
		table.addColumn(new TTableColumn("错误"));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tablePanel.add(new JScrollPane(table),BorderLayout.CENTER);
	
		DataBoxSelectionListener dataBoxListener = new DataBoxSelectionAdapter(){
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void selectionChanged(DataBoxSelectionEvent e) {
				Element element = e.getBoxSelectionModel().lastElement();
				if (element != null) {
					TTableModel model = table.getTableModel();
					model.clearRawData();
					if (element.getName() != null && (element.getID().toString().matches(dpidPattern)) && switchsMap.get(element.getID()) != null) {
						List<model.Port> ports = (switchsMap.get(element.getID())).getPorts();
						switchDpid = switchsMap.get(element.getID()).getDpid();
						lineChart.setTitle("交换机["+switchDpid+":"+portNo+"]");
						for (int i = 0; i < ports.size(); i++) {
							Vector row = new Vector();
							row.addElement(ports.get(i).getPortNo());
							if (ports.get(i).getState() == 1) {
								row.addElement("Down");	
							} else {
								row.addElement("Up");
							}
							row.addElement(ports.get(i).getTXBytes()/1000);
							row.addElement(ports.get(i).getRXBytes()/1000);
							row.addElement(ports.get(i).getTXDropped()+ports.get(i).getRXDropped());
							row.addElement(ports.get(i).getRXCRCErr()+ports.get(i).getRXErrors()+ports.get(i).getRXOverErr()+ports.get(i).getRXFrameErr());
							model.addRow(row);
						}
					}
				}
			}
		};
		topologybox.getSelectionModel().addDataBoxSelectionListener(dataBoxListener);

		table.getTableModel().addTableListener(new TTableAdapter() {
			
			@SuppressWarnings("rawtypes")
			@Override
			public void rowClicked(int arg0, Vector arg1, int arg2) {
				portNo = String.valueOf(arg1.get(2));
			}
		});
		//***************************************************************************
		
		//**************************访问控制**************************************
		TabItem typeTabItem = new TabItem(tabFolder, SWT.NONE);
		typeTabItem.setText("  访问控制  ");
		ImageData aclimgdt = new ImageData(getClass().getResourceAsStream("/acl.png"));
		Image aclimg = new Image(display,aclimgdt);
		typeTabItem.setImage(aclimg);
		Composite typeComposite = new Composite(tabFolder, SWT.NONE);
		typeTabItem.setControl(typeComposite);
		
		Label acllab = new Label(typeComposite, SWT.NONE);
		acllab.setBounds(30, 20, 200, 30);
		acllab.setFont(SWTResourceManager.getFont("华文新魏", 15, SWT.NORMAL));
		acllab.setText("用户访问控制");
		
		Label aclsptrlab = new Label(typeComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		aclsptrlab.setBounds(20, 50, 1100, 5);
		
		Group slistgroup = new Group(typeComposite, SWT.NONE);
		slistgroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		slistgroup.setLayout(new GridLayout(1, false));
		slistgroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		slistgroup.setBounds(600,90,400,500);
		slistgroup.setText("黑名单");
		
		Composite slistComposite = new Composite(slistgroup, SWT.EMBEDDED);
		slistComposite.setBounds(20, 30, 360, 450);
		slistComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		Frame slistframe = SWT_AWT.new_Frame(slistComposite);
		slistframe.setLayout(new BorderLayout());
		JPanel slistPanel = new JPanel(new BorderLayout());
		slistPanel.setBackground(Color.RED);
		slistframe.add(slistPanel,BorderLayout.CENTER);
		
		blackTable = new TTable();
		blackTable.addColumn(new TTableColumn("编号"));
		blackTable.addColumn(new TTableColumn("IP地址"));
		blackTable.addColumn(new TTableColumn("备注"));
		blackTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		slistPanel.add(new JScrollPane(blackTable),BorderLayout.CENTER);
		
		Group saddgroup = new Group(typeComposite, SWT.NONE);
		saddgroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		saddgroup.setLayout(new GridLayout(1, false));
		saddgroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		saddgroup.setBounds(100,90,400,210);
		saddgroup.setText("添加黑名单");
		
		saddComposite = new Composite(saddgroup, SWT.NONE);
		saddComposite.setBounds(20, 30, 360, 120);
		//saddComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		
		Label sname = new Label(saddComposite, SWT.NONE);
		sname.setBounds(50,15,50,30);
		sname.setText("IP地址:");
		
		siptxt = new Text(saddComposite, SWT.BORDER);
		siptxt.setBounds(100, 10, 200, 30);
		
		Label stos = new Label(saddComposite, SWT.NONE);
		stos.setBounds(50, 65, 50, 30);
		stos.setText("备注:");
		
		stxt = new Text(saddComposite, SWT.BORDER);
		stxt.setBounds(100,60,200,30);
		
		Button srestbtn = new Button(saddgroup, SWT.NONE);
		srestbtn.setBounds(100, 150, 80, 30);
		srestbtn.setText("重置");
		srestbtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				siptxt.setText("");
				stxt.setText("");
			}
		});
		
		Button saddbtn = new Button(saddgroup, SWT.NONE);
		saddbtn.setBounds(200, 150, 80, 30);
		saddbtn.setText("添加");
		saddbtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(siptxt.getText() != null && siptxt.getText() != ""){
					FlowEntry flowEntry = new FlowEntry();
					//flowEntry.setName("rule" + FloodlightProvider.ruleNum);
					flowEntry.setName("rule" + siptxt.getText());
					flowEntry.setSw("00:00:00:00:00:00:00:02");
					//flowEntry.setPriority(32767);
					Match match = new Match();
					match.setEthType((short)(0x0800));
					match.setIpv4Src(siptxt.getText());
					flowEntry.setMatch(match);
					flowEntry.setActive(true);
					List<Instruction> instructions = new ArrayList<>();
					List<IAction> actions = new ArrayList<>();
					Instruction instruction = new InstructionApplyActions();
					ActionOutput actionOutput = new ActionOutput();
					actionOutput.setPort(OFPort.IN_PORT);
					actions.add(actionOutput);
					instruction.setActions(actions);
					instructions.add(instruction);
					flowEntry.setInstructions(instructions);
					if (flowEntry.getName() != null && flowEntry.getName() != "") {
						try {
							log.info("Add flow entry: {}",flowEntry);
							if (StaticFlowPusher.addFlow(flowEntry)) {
								String sql = "select * from blacklist where ip = \'" + flowEntry.getMatch().getIpv4Src() + "\'";
								if (!MySQLHelper.isExist(sql)) {
									sql = "insert into blacklist(name,ip,note) values('"
											+flowEntry.getName()+"','"+flowEntry.getMatch().getIpv4Src()+"','"+stxt.getText()+"')";
									int result = MySQLHelper.executeNonQuery(sql);
									if (result <= 0) {
										StaticFlowPusher.deleteFlow(flowEntry.getName());
										log.error("Failed to access DataBase to add blacklist");
										DisplayMessage.displayError(shell, "添加黑名单失败!");
									}
									else {
										String [] tmp = new String[2];
										tmp[0] = flowEntry.getMatch().getIpv4Src();
										tmp[1] = stxt.getText();
										floodlightProvider.blackList.put(flowEntry.getName(), tmp);
										siptxt.setText("");
										stxt.setText("");
										displayBlackList();
									}
								}
								else{
									log.info("It has been existed in DataBase: {}",flowEntry.getMatch().getIpv4Src());
									DisplayMessage.displayError(shell, "已禁止该用户!");
								}
							}	else {
								log.error("Add flow entry failed: {}",flowEntry);
								DisplayMessage.displayError(shell, "添加黑名单失败!");
							}
						} catch (Exception e2) {
							log.error("Failed to add blacklist: {}",e2.getMessage());
							//e2.printStackTrace();
						}
					}
				}
			}
		});
		
		Group sdeletegroup = new Group(typeComposite, SWT.NONE);
		sdeletegroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		sdeletegroup.setLayout(new GridLayout(1, false));
		sdeletegroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		sdeletegroup.setBounds(100,320,400,270);
		sdeletegroup.setText("删除黑名单");
		
		sdComposite = new Composite(sdeletegroup, SWT.NONE);
		sdComposite.setBounds(20, 30, 360, 150);
		
		Label sdid = new Label(sdComposite, SWT.NONE);
		sdid.setBounds(50,15,50,30);
		sdid.setText("编号:");
		
		sdidtxt = new Text(sdComposite, SWT.BORDER);
		sdidtxt.setBounds(100, 10, 200, 30);
		
		Label sdname = new Label(sdComposite, SWT.NONE);
		sdname.setBounds(50,65,50,30);
		sdname.setText("IP地址:");
		
		sdiptxt = new Text(sdComposite, SWT.BORDER);
		sdiptxt.setBounds(100, 60, 200, 30);
		sdiptxt.setEnabled(false);
		
		Label sdtos = new Label(sdComposite, SWT.NONE);
		sdtos.setBounds(50, 115, 50, 30);
		sdtos.setText("备注:");
		
		sdtxt = new Text(sdComposite, SWT.BORDER);
		sdtxt.setBounds(100,120,200,30);
		sdtxt.setEnabled(false);
		
		sdidtxt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				String txt = sdidtxt.getText();
				if(txt != null && txt != "")
				{
					if(blackTmp.containsKey(Integer.parseInt(txt)))
					{
						if(floodlightProvider.blackList.containsKey(blackTmp.get(Integer.parseInt(txt)))){
						sdiptxt.setText(floodlightProvider.blackList.get(blackTmp.get(Integer.parseInt(txt)))[0]);
						sdtxt.setText(floodlightProvider.blackList.get(blackTmp.get(Integer.parseInt(txt)))[1]);
						}
					}
				}
			}
		});
		
		Button sdrestbtn = new Button(sdeletegroup, SWT.NONE);
		sdrestbtn.setBounds(100, 210, 80, 30);
		sdrestbtn.setText("重置");
		sdrestbtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sdidtxt.setText("");
				sdiptxt.setText("");
				sdtxt.setText("");
			}
		});
		
		Button sdbtn = new Button(sdeletegroup, SWT.NONE);
		sdbtn.setBounds(200, 210, 80, 30);
		sdbtn.setText("删除");
		sdbtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(sdidtxt.getText() != null && sdidtxt.getText() != "")
				{
					if(blackTmp.containsKey(Integer.parseInt(sdidtxt.getText())))
					{
						try {
							String sql = "select * from blacklist where name = '" + blackTmp.get(Integer.parseInt(sdidtxt.getText())) +"'";
							if (MySQLHelper.isExist(sql)) {
								sql = "delete from blacklist where name = '"+blackTmp.get(Integer.parseInt(sdidtxt.getText()))+"'";
								int result = MySQLHelper.executeNonQuery(sql);
								if (result <= 0){
									log.error("Failed to access DataBase to delete blacklist");
									DisplayMessage.displayError(shell, "删除黑名单失败!");
								}
								else
								{
									if (StaticFlowPusher.deleteFlow(blackTmp.get(Integer.parseInt(sdidtxt.getText())))) {
										floodlightProvider.blackList.remove(blackTmp.get(Integer.parseInt(sdidtxt.getText())));
										sdidtxt.setText("");
										sdiptxt.setText("");
										sdtxt.setText("");
										displayBlackList();
									}	else {
										log.error("Failed to delete flow entry: {}",blackTmp.get(Integer.parseInt(sdidtxt.getText())));
										DisplayMessage.displayError(shell, "删除黑名单失败!");
									}
								}
							}
							else{
								log.info("It is not in the DataBase: {}",blackTmp.get(Integer.parseInt(sdidtxt.getText())));
								DisplayMessage.displayError(shell, "该用户不存在!");
							}
						} catch (NumberFormatException e1) {
							log.error("Failed to add blacklist: {}",e1.getMessage());
							//e1.printStackTrace();
						} catch (IOException e1) {
							log.error("Failed to add blacklist: {}",e1.getMessage());
							//e1.printStackTrace();
						}
					}
				}
			}
		});
		//getBlackListFromDB();
		//displayBlackList();
		//FloodlightProvider.ruleNum = floodlightProvider.blackList.size();
		//***************************************************************************
		
		//****************************路由控制************************************
		TabItem polTabItem = new TabItem(tabFolder, SWT.NONE);
		polTabItem.setText("  路由控制  ");
		ImageData routeimgdt = new ImageData(getClass().getResourceAsStream("/route.png"));
		Image routeimg = new Image(display,routeimgdt);
		polTabItem.setImage(routeimg);
		final Composite polComposite = new Composite(tabFolder, SWT.NONE);
		polTabItem.setControl(polComposite);
		
		Label routelab = new Label(polComposite, SWT.NONE);
		routelab.setBounds(30, 20, 200, 30);
		routelab.setFont(SWTResourceManager.getFont("华文新魏", 15, SWT.NORMAL));
		routelab.setText("简单路由控制");
		
		Label routesptrlab = new Label(polComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		routesptrlab.setBounds(20, 50, 1100, 5);
		
		Group addgroup = new Group(polComposite, SWT.NONE);
		addgroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		addgroup.setLayout(new GridLayout(1, false));
		addgroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		addgroup.setBounds(600,60,510,265);
		addgroup.setText("Route");
		
		
		Button addbtn = new Button(addgroup, SWT.NONE);
		addbtn.setBounds(410, 80, 80, 30);
		addbtn.setText("添加");
		addbtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String scrIp = polNametxt.getText();
				String dstIp = polPrioritytxt.getText();
				String[] switchNameList;
				String sws = polSwtxt.getText();
				switchNameList = sws.split(" ");
				List<String> switchDpids = new ArrayList<>();
				for(String name : switchNameList){
					for(Map.Entry<String, Node> entry : switchmap.entrySet()){
						if(entry.getValue().getName().toLowerCase().equals(name.toLowerCase())){
							switchDpids.add(entry.getKey());
							break;
						}
					}
				}
				floodlightProvider.setPath(scrIp, dstIp, switchDpids);
				StringBuilder path = new StringBuilder();
				path.append(scrIp+"-->");
				for(String s : switchNameList){
					path.append(s+"-->");
				}
				path.append(dstIp);
				stxt.append(path.toString().toUpperCase()+"\n");
			}
		});
		
		addComposite = new Composite(addgroup, SWT.NONE);
		addComposite.setBounds(20, 30, 480, 230);
		//addComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
				
		Label polNamelab = new Label(addComposite, SWT.NONE);
		polNamelab.setBounds(0, 20, 80, 20);
		polNamelab.setText("源IP地址:");
				
		polNametxt = new Text(addComposite, SWT.BORDER);
		polNametxt.setBounds(0, 50, 75, 30);
		
		Label markLab1 = new Label(addComposite, SWT.NONE);
		markLab1.setBounds(81, 55, 12, 30);
		markLab1.setText("→");
		
		Label polSwlab = new Label(addComposite, SWT.BOLD);
		polSwlab.setBounds(100, 20, 150, 20);
		polSwlab.setText("路径（交换机序列）:");
				
		polSwtxt = new Text(addComposite, SWT.BORDER);
		polSwtxt.setBounds(100, 50, 175, 30);
		
		Label markLab2 = new Label(addComposite, SWT.BOLD);
		markLab2.setBounds(281, 55, 12, 30);
		markLab2.setText("→");
		
		Label polPrioritylab = new Label(addComposite, SWT.NONE);
		polPrioritylab.setBounds(300, 20, 80, 20);
		polPrioritylab.setText("目的IP地址:");
				
		polPrioritytxt = new Text(addComposite, SWT.BORDER);
		polPrioritytxt.setBounds(300, 50, 75, 30);
		
		Label slab = new Label(addComposite, SWT.NONE);
		slab.setBounds(0, 90, 50, 20);
		slab.setText("传输路径:");
		
		stxt = new Text(addComposite, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL);
		stxt.setBounds(0, 110, 470, 100);
		//stxt.setEnabled(false);				
			
		Group deletegroup = new Group(polComposite, SWT.NONE);
		deletegroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		deletegroup.setLayout(new GridLayout(1, false));
		deletegroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		deletegroup.setBounds(600,330,510,280);
		deletegroup.setText("Verify");
		
		Button vgetbtn = new Button(deletegroup, SWT.NONE);
		vgetbtn.setBounds(410, 80, 80, 30);
		vgetbtn.setText("显示");
		vgetbtn.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Element> elements = topologybox.getElementsByType(Node.class);
				for (int i = 0; i < elements.size(); i++) {
					elements.get(i).putLabelColor(Color.black);
					elements.get(i).putStateOutlineColor(null);;
				}
				List<Link> links = topologybox.getElementsByType(Link.class);
				for (int i = 0; i < links.size(); i++) {
					links.get(i).putLinkFlowing(false);
				}
				String pathStr = "";
				List<String> path = floodlightProvider.getPath(vipsrctxt.getText(), vipdsttxt.getText());
				List<Node> nodes = new ArrayList<>();
				if(path != null && path.size() > 0){
					if(hostmap.size() > 0){
						if(hostmap.containsKey(path.get(0))){
							pathStr += hostmap.get(path.get(0)).getName() + "-->";
							hostmap.get(path.get(0)).putLabelColor(Color.red);
							//hostmap.get(path.get(0)).putRenderColor(Color.red);
							//hostmap.get(path.get(0)).putBorderColor(Color.red);
							hostmap.get(path.get(0)).putStateOutlineColor(Color.red);
							nodes.add(hostmap.get(path.get(0)));
						}
						for (int i = 1; i < path.size() - 1; i++) {
							if(switchmap.containsKey(path.get(i))){
								pathStr += switchmap.get(path.get(i)).getName() + "-->";
								switchmap.get(path.get(i)).putLabelColor(Color.red);
								//switchmap.get(path.get(i)).putRenderColor(Color.red);
								switchmap.get(path.get(i)).putStateOutlineColor(Color.red);
								nodes.add(switchmap.get(path.get(i)));
							}
						}
						if(hostmap.containsKey(path.get(path.size() - 1)));{
							pathStr += hostmap.get(path.get(path.size() - 1)).getName();
							hostmap.get(path.get(path.size() - 1)).putLabelColor(Color.red);
							//hostmap.get(path.get(path.size() - 1)).putRenderColor(Color.red);
							hostmap.get(path.get(path.size() - 1)).putStateOutlineColor(Color.red);
							nodes.add(hostmap.get(path.get(path.size() - 1)));
						}
						if(!nodes.isEmpty()){
							//List<Link> links = topologybox.getElementsByType(Link.class);
							for (int i = 1; i < nodes.size(); i++) {
								for (int j = 0; j < links.size(); j++) {
									if ((nodes.get(i-1) == links.get(j).getFrom() && nodes.get(i) == links.get(j).getTo()) || (nodes.get(i-1) == links.get(j).getTo() && nodes.get(i) == links.get(j).getFrom())) {
										links.get(j).putLinkFlowing(true);
										links.get(j).putLinkFlowingColor(Color.red);
										//links.get(j).putLinkOutlineColor(Color.black);
										//links.get(j).putLinkColor(Color.white);
										break;
									}
								}
							}
						}
					}
				}
				vpathtxt.append(pathStr+"\n");
				vipsrctxt.setText("");
				vipdsttxt.setText("");
			}
		});
				
		deleteComposite = new Composite(deletegroup, SWT.NONE);
		deleteComposite.setBounds(20, 30, 480, 240);
				
		Label pdIDlab = new Label(deleteComposite, SWT.NONE);
		pdIDlab.setBounds(0, 20, 80, 30);
		pdIDlab.setText("源IP地址:");
				
		vipsrctxt = new Text(deleteComposite, SWT.BORDER);
		vipsrctxt.setBounds(0, 50, 120, 30);
		
		Label markLab = new Label(deleteComposite, SWT.NONE);
		markLab.setBounds(150, 55, 100, 30);
		markLab.setText("------------>");
		
		Label pdNamelab = new Label(deleteComposite, SWT.NONE);
		pdNamelab.setBounds(255, 20, 80, 30);
		pdNamelab.setText("目的IP地址:");
		
		vipdsttxt = new Text(deleteComposite, SWT.BORDER);
		vipdsttxt.setBounds(255, 50, 120, 30);
				
		Label pdSwlab = new Label(deleteComposite, SWT.NONE);
		pdSwlab.setBounds(0, 100, 50, 20);
		pdSwlab.setText("传输路径:");
		
		vpathtxt = new Text(deleteComposite, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		vpathtxt.setBounds(0, 120, 470, 100);
		//vpathtxt.setEnabled(false);
		//addComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		//***************************************************************************		
		
		//**************************业务管理**************************************
		TabItem busTabItem = new TabItem(tabFolder, SWT.NONE);
		busTabItem.setText("  业务管理  ");
		ImageData busimgdt = new ImageData(getClass().getResourceAsStream("/topo0.png"));
		Image busimg = new Image(display,busimgdt);
		busTabItem.setImage(busimg);
		Composite busComposite = new Composite(tabFolder, SWT.NONE);
		busTabItem.setControl(busComposite);
				
		Label buslab = new Label(busComposite, SWT.NONE);
		buslab.setBounds(30, 20, 150, 30);
		buslab.setFont(SWTResourceManager.getFont("华文新魏", 15, SWT.NORMAL));
		buslab.setText("用户业务管理");
				
		Label bussptrlab = new Label(busComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		bussptrlab.setBounds(20, 50, 1100, 5);
		
//		Label qosLabel = new Label(busComposite, SWT.None);
//		qosLabel.setBounds(40,170,150,30);
//		qosLabel.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
//		qosLabel.setText("QoS Status: ");
//		
//		qosLab = new Label(busComposite, SWT.NONE);
//		qosLab.setBounds(200,170,50,30);
//		qosLab.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
//		//healthLab.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		qosbtn = new Button(busComposite,SWT.NONE);
		qosbtn.setText("启动");
		qosbtn.setBounds(180, 15, 80, 30);
		qosbtn.setVisible(false);
				
		Group blistgroup = new Group(busComposite, SWT.NONE);
		blistgroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		blistgroup.setLayout(new GridLayout(1, false));
		blistgroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		blistgroup.setBounds(560,70,530,280);
		blistgroup.setText("业务名单");
				
		Composite blistComposite = new Composite(blistgroup, SWT.EMBEDDED);
		blistComposite.setBounds(20, 20, 490, 240);
		blistComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		Frame blistframe = SWT_AWT.new_Frame(blistComposite);
		blistframe.setLayout(new BorderLayout());
		JPanel blistPanel = new JPanel(new BorderLayout());
		blistPanel.setBackground(Color.RED);
		blistframe.add(blistPanel,BorderLayout.CENTER);
		
		busTable = new TTable();
		busTable.addColumn(new TTableColumn("编号"));
		busTable.addColumn(new TTableColumn("业务名"));
		busTable.addColumn(new TTableColumn("IP源地址"));
		busTable.addColumn(new TTableColumn("IP目的地址"));
		busTable.addColumn(new TTableColumn("端口号"));
		busTable.addColumn(new TTableColumn("限制速率"));
		busTable.addColumn(new TTableColumn("保障速率"));
		busTable.addColumn(new TTableColumn("备注"));
		busTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		blistPanel.add(new JScrollPane(busTable),BorderLayout.CENTER);
				
		Group baddgroup = new Group(busComposite, SWT.NONE);
		baddgroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		baddgroup.setLayout(new GridLayout(1, false));
		baddgroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		baddgroup.setBounds(60,70,400,530);
		baddgroup.setText("添加业务");
				
		baddComposite = new Composite(baddgroup, SWT.NONE);
		baddComposite.setBounds(20, 20, 360, 450);
		//baddComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
				
		Label bname = new Label(baddComposite, SWT.NONE);
		bname.setBounds(30,20,60,30);
		bname.setText("业务名:");
				
		bnametxt = new Text(baddComposite, SWT.BORDER);
		bnametxt.setBounds(110, 15, 200, 30);
				
		Label ipsrc = new Label(baddComposite, SWT.NONE);
		ipsrc.setBounds(30,65,60,30);
		ipsrc.setText("IP源地址:");
				
		ipsrctxt = new Text(baddComposite, SWT.BORDER);
		ipsrctxt.setBounds(110, 60, 200, 30);
				
		Label ipdst = new Label(baddComposite, SWT.NONE);
		ipdst.setBounds(30,110,70,30);
		ipdst.setText("IP目的地址:");
				
		ipdsttxt = new Text(baddComposite, SWT.BORDER);
		ipdsttxt.setBounds(110, 105, 200, 30);
				
		Label port = new Label(baddComposite, SWT.NONE);
		port.setBounds(30,155,60,30);
		port.setText("端口号:");
				
		porttxt = new Text(baddComposite, SWT.BORDER);
		porttxt.setBounds(110, 150, 200, 30);		
		
		
		Group bcongroup = new Group(baddComposite, SWT.NONE);
		bcongroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		bcongroup.setLayout(new GridLayout(1, false));
		bcongroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		bcongroup.setBounds(15,190,295,200);
		bcongroup.setText("速率控制");
				
		bconComposite = new Composite(bcongroup, SWT.NONE);
		bconComposite.setBounds(10, 20, 280, 175);
		//bconComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		
		Label maxflow = new Label(bconComposite, SWT.NONE);
		maxflow.setBounds(0,15,220,30);
		maxflow.setText("白天(6:00am-24:00pm)限制速率:");
		
		maxtxt = new Text(bconComposite, SWT.BORDER);
		maxtxt.setBounds(15, 45, 240, 30);
				
		Label minflow = new Label(bconComposite, SWT.NONE);
		minflow.setBounds(0,95,220,30);
		minflow.setText("夜晚(0:00pm-6:00am)保障速率:");
				
//		mintxt = new Text(bconComposite, SWT.BORDER);
//		mintxt.setBounds(15, 125, 240, 30);
		
		mincom = new Combo(bconComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		mincom.setBounds(15, 125, 240, 30);
		mincom.setItems(new String[] {"300kbps","500kbps","800kbps","1000kbps"});
		
		
		Label btos = new Label(baddComposite, SWT.NONE);
		btos.setBounds(30, 410, 60, 30);
		btos.setText("备注:");
		
		btxt = new Text(baddComposite, SWT.BORDER);
		btxt.setBounds(110,405,200,30);
				
		Button brestbtn = new Button(baddgroup, SWT.NONE);
		brestbtn.setBounds(90, 480, 80, 30);
		brestbtn.setText("重置");
		brestbtn.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			bnametxt.setText("");
			ipsrctxt.setText("");
			ipdsttxt.setText("");
			porttxt.setText("");
			btxt.setText("");
			maxtxt.setText("");
			//mintxt.setText("");
			}
		});
				
		Button baddbtn = new Button(baddgroup, SWT.NONE);
		baddbtn.setBounds(200, 480, 80, 30);
		baddbtn.setText("添加");
		baddbtn.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (bnametxt.getText().length() > 0 && ipsrctxt.getText().length() > 0 && ipdsttxt.getText().length() > 0 && maxtxt.getText().length() > 0 && mincom.getSelectionIndex() >= 0) {
				Policy policy1 = new Policy();
				policy1.setName(bnametxt.getText()+"1");
				policy1.setSw("00:00:00:00:00:00:00:01");
				policy1.setPriority((short)32767);
				Meter meter = new Meter();
				meter.setName("meter");
				meter.setRate(Long.parseLong(maxtxt.getText()));
				meter.setBurst(0);
				meter.setMaxrate(Long.parseLong(maxtxt.getText())+100);
				policy1.setPolicyType(meter);
				policy1.setEthtype((short)(0x0800));
				policy1.setIpsrc(ipsrctxt.getText());
				policy1.setIpdst(ipdsttxt.getText());
				if (porttxt.getText() != "") {
					policy1.setDstport(Short.parseShort(porttxt.getText()));
				}
				policy1.setEnable(false);
				log.debug(policy1.toString());
				
				Policy policy2 = new Policy();
				policy2.setName(bnametxt.getText()+"2");
				policy2.setSw("00:00:00:00:00:00:00:01");
				policy2.setPriority((short)32767);
				Queue queue = new Queue();
				queue.setName("queue");
				queue.setPort((short)1);
				switch(mincom.getSelectionIndex()) {
				case 0:
					queue.setQueue((short)1);
					break;
				case 1:
					queue.setQueue((short)2);
					break;
				case 2:
					queue.setQueue((short)3);
					break;
				case 3:
					queue.setQueue((short)4);
					break;
				case 4:
					queue.setQueue((short)5);
					break;
				case 5:
					queue.setQueue((short)6);
					break;
				case 6:
					queue.setQueue((short)7);
					break;
				case 7:
					queue.setQueue((short)8);
					break;
				default:
					log.error("You don't select the limited rate or the selection is error");
					break;
				}
				policy2.setPolicyType(queue);
				policy2.setEthtype((short)(0x0800));
				policy2.setIpsrc(ipsrctxt.getText());
				policy2.setIpdst(ipdsttxt.getText());
				if (porttxt.getText() != "") {
					policy2.setDstport(Short.parseShort(porttxt.getText()));
				}
				policy2.setEnable(false);
				log.debug(policy2.toString());
				
				if (policy1.getName() != null && policy1.getName() != "" && policy2.getName() != null && policy2.getName() != "") {
					try {
						if (PolicyManager.addPathPolicy(policy1) && PolicyManager.addPathPolicy(policy2)) {
							
							String sql = "select * from tasks where name = \'" + bnametxt.getText() + "\'";
							if (!MySQLHelper.isExist(sql)) {
								int port = 0;
								int minrate = Integer.parseInt(StringUtils.stripEnd(mincom.getItem(mincom.getSelectionIndex()), "kbps"));
								log.debug("{}",minrate);
								if(porttxt.getText() != "" && porttxt.getText().length() != 0)
									port = Integer.parseInt(porttxt.getText());
								sql = "insert into tasks(name,ipsrc,ipdst,port,limitedv,guaranteedv,note) values('"
										+bnametxt.getText()+"','"+policy1.getIpsrc()+"','"+policy1.getIpdst()+ "','"
										+port+"','"+maxtxt.getText()+"','"+minrate+"','"+btxt.getText()+"')";
								log.debug(sql);
								int result = MySQLHelper.executeNonQuery(sql);
								if (result <= 0) {
									//StaticFlowPusher.deleteFlow(flowEntry.getName());
									log.error("Failed to access DataBase to add tasks");
									DisplayMessage.displayError(shell, "添加业务失败!");
								}
								else {
									String [] tmp = new String[6];
									tmp[0] = policy1.getIpsrc();
									tmp[1] = policy1.getIpdst();
									tmp[2] = porttxt.getText();
									tmp[3] = maxtxt.getText();
									tmp[4] = String.valueOf(minrate);
									tmp[5] = btxt.getText();
									floodlightProvider.tasks.put(bnametxt.getText(), tmp);
									bnametxt.setText("");
									ipsrctxt.setText("");
									ipdsttxt.setText("");
									porttxt.setText("");
									btxt.setText("");
									maxtxt.setText("");
									//mintxt.setText("");
									displayTasks();
								}
							}
							else{
								log.info("It has been existed in DataBase: {}",bnametxt.getText());
								DisplayMessage.displayError(shell, "该业务名称已经存在!");
							}
						} else {
							DisplayMessage.displayError(shell, "添加业务失败!");
						}
					} catch (IOException e1) {
						log.error("Add Path's Policy error: {}", e1.getMessage());
						//e1.printStackTrace();
					}
				}
			}
			}
		});
				
		Group bdeletegroup = new Group(busComposite, SWT.NONE);
		bdeletegroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		bdeletegroup.setLayout(new GridLayout(1, false));
		bdeletegroup.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		bdeletegroup.setBounds(560,360,530,240);
		bdeletegroup.setText("删除业务");
				
		bdComposite = new Composite(bdeletegroup, SWT.NONE);
		bdComposite.setBounds(70, 20, 360, 180);
		
		Label did = new Label(bdComposite, SWT.NONE);
		did.setBounds(30,10,60,30);
		did.setText("编号:");
				
		bidtxt = new Text(bdComposite, SWT.BORDER);
		bidtxt.setBounds(110, 5, 200, 30);
		
		Label dname = new Label(bdComposite, SWT.NONE);
		dname.setBounds(30,50,60,30);
		dname.setText("业务名:");
				
		dnametxt = new Text(bdComposite, SWT.BORDER);
		dnametxt.setBounds(110, 45, 200, 30);
		dnametxt.setEnabled(false);
				
		Label dipsrc = new Label(bdComposite, SWT.NONE);
		dipsrc.setBounds(30,100,60,30);
		dipsrc.setText("IP源地址:");
				
		dipsrctxt = new Text(bdComposite, SWT.BORDER);
		dipsrctxt.setBounds(110, 90, 200, 30);
		dipsrctxt.setEnabled(false);
				
		Label dipdst = new Label(bdComposite, SWT.NONE);
		dipdst.setBounds(30,150,60,30);
		dipdst.setText("IP目的地址:");
				
		dipdsttxt = new Text(bdComposite, SWT.BORDER);
		dipdsttxt.setBounds(110, 135, 200, 30);
		dipdsttxt.setEnabled(false);
				
		Label dport = new Label(bdComposite, SWT.NONE);
		dport.setBounds(30,200,60,30);
		dport.setText("端口号:");
				
		dporttxt = new Text(bdComposite, SWT.BORDER);
		dporttxt.setBounds(110, 180, 200, 30);
		dporttxt.setEnabled(false);
				
//		Label bdtos = new Label(bdComposite, SWT.NONE);
//		bdtos.setBounds(30, 145, 60, 30);
//		bdtos.setText("备注:");
//				
//		dtxt = new Text(bdComposite, SWT.BORDER);
//		dtxt.setBounds(110,140,200,30);
//		dtxt.setEnabled(false);
				
		bidtxt.addModifyListener(new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent arg0) {
			String txt = bidtxt.getText();
			if(txt != null && txt != "")
			{
				if(tasksTmp.containsKey(Integer.parseInt(txt)))
				{
					if(floodlightProvider.tasks.containsKey(tasksTmp.get(Integer.parseInt(txt)))){
					dnametxt.setText(tasksTmp.get(Integer.parseInt(txt)));
					dipsrctxt.setText(floodlightProvider.tasks.get(tasksTmp.get(Integer.parseInt(txt)))[0]);
					dipdsttxt.setText(floodlightProvider.tasks.get(tasksTmp.get(Integer.parseInt(txt)))[1]);
					dporttxt.setText(floodlightProvider.tasks.get(tasksTmp.get(Integer.parseInt(txt)))[2]);
					//dporttxt.setText(floodlightProvider.tasks.get(tasksTmp.get(Integer.parseInt(txt)))[4]);
					//dtxt.setText(floodlightProvider.tasks.get(tasksTmp.get(Integer.parseInt(txt)))[5]);
					}
				}
			}
			}
		});
				
		Button bdrestbtn = new Button(bdeletegroup, SWT.NONE);
		bdrestbtn.setBounds(140, 200, 80, 30);
		bdrestbtn.setText("重置");
		bdrestbtn.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			dnametxt.setText("");
			dipsrctxt.setText("");
			dipdsttxt.setText("");
			dporttxt.setText("");
			//dtxt.setText("");
			}
		});
				
		Button bdbtn = new Button(bdeletegroup, SWT.NONE);
		bdbtn.setBounds(250, 200, 80, 30);
		bdbtn.setText("删除");
		bdbtn.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			}
		});
		
		//getTasksListFromDB();
		//displayTasks();
		//****************************************************************************
		//标签切换
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e){
				if (tabFolder.getSelectionIndex() == 1) {
					leftgroup.setParent(topComposite);
					List<Element> elements = topologybox.getElementsByType(Node.class);
					for (int i = 0; i < elements.size(); i++) {
						elements.get(i).putLabelColor(Color.black);
						elements.get(i).putStateOutlineColor(null);;
					}
					vpathtxt.setText("");
					List<Link> links = topologybox.getElementsByType(Link.class);
					for (int i = 0; i < links.size(); i++) {
						links.get(i).putLinkFlowing(false);
					}
				}
				else if (tabFolder.getSelectionIndex() == 3) {
					leftgroup.setParent(polComposite);
				}
			}
		});
		
		//定时器
		ActionListener timerAListener = new ActionListener() {		
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void actionPerformed(ActionEvent e) {
				switchs = floodlightProvider.getSwitches(true);
				switchsMap.clear();
				for (int i = 0; i < switchs.size(); i++) {
					switchsMap.put(switchs.get(i).getDpid(), switchs.get(i));
				}
				
				if (switchDpid!=null) {
					TTableModel model = table.getTableModel();
					model.clearRawData();
					List<model.Port> ports = (switchsMap.get(switchDpid)).getPorts();
					lineChart.setTitle("交换机["+switchDpid+":"+portNo+"]");
					for (int i = 0; i < ports.size(); i++) {
						Vector row = new Vector();
						row.addElement(ports.get(i).getPortNo());
						if (ports.get(i).getState() == 0) {
							row.addElement("Up");	
						} else {
							row.addElement("Down");
						}
						row.addElement(ports.get(i).getTXBytes()/1000);
						row.addElement(ports.get(i).getRXBytes()/1000);
						row.addElement(ports.get(i).getTXDropped()+ports.get(i).getRXDropped());
						row.addElement(ports.get(i).getRXCRCErr()+ports.get(i).getRXErrors()+ports.get(i).getRXOverErr()+ports.get(i).getRXFrameErr());
						model.addRow(row);
					}
				}
				
				List<Switch> switchs = floodlightProvider.getSwitches(true);
				for (int i = 0; i < switchs.size(); i++) {
					Switch sw = switchs.get(i);
					if (oldSwitchStats.containsKey(sw.getDpid())) {
						for (int j = 0; j < sw.getPorts().size(); j++) {
							double rxrate = (double)(sw.getPorts().get(j).getRXBytes() - oldSwitchStats.get(sw.getDpid()).getPorts().get(j).getRXBytes())*8/1024;
							//System.out.println("RXRate:"+rxrate);	
							double time = sw.getPorts().get(j).getDurationSec()+sw.getPorts().get(j).getDurationNsec()*0.0000000001-oldSwitchStats.get(sw.getDpid()).getPorts().get(j).getDurationSec()-oldSwitchStats.get(sw.getDpid()).getPorts().get(j).getDurationNsec()*0.0000000001;
							//System.out.println(sw.getPorts().get(j).getDurationSec()+sw.getPorts().get(j).getDurationNsec()*0.0000000001-oldSwitchStats.get(sw.getDpid()).getPorts().get(j).getDurationSec()-oldSwitchStats.get(sw.getDpid()).getPorts().get(j).getDurationNsec()*0.0000000001);	
							portRXRateMap.put(sw.getDpid()+"&"+sw.getPorts().get(j).getPortNo(), rxrate/time);
							double txrate = (double)(sw.getPorts().get(j).getTXBytes() - oldSwitchStats.get(sw.getDpid()).getPorts().get(j).getTXBytes())*8/1024;
							//System.out.println("TXRate:"+txrate);
							portTXRateMap.put(sw.getDpid()+"&"+sw.getPorts().get(j).getPortNo(), txrate/time);
							//System.out.println("Current Speed:"+sw.getPorts().get(j).getCurrSpeed());
							//System.out.println("Max Speed:"+sw.getPorts().get(j).getMaxSpeed());
						}
						oldSwitchStats.remove(sw.getDpid());
					}
					oldSwitchStats.put(sw.getDpid(), sw);
				}
				
				lineChart.setTitle("交换机["+switchDpid+":"+portNo+"]");
				if (lineChart.valueCount()>10) {
					lineChart.removeHead(1);
				}
				
				//switchDpid
				if (send!=null&&receive!=null&&lineChart!=null&&lineChart.isShowing()) {
					Set<String> keys = portRXRateMap.keySet();
					for (String key : keys) {
						String [] str = key.split("&");
						if (switchDpid!=null&&portNo!=null) {
							if (str[0].equalsIgnoreCase(switchDpid)&&str[1].equalsIgnoreCase(portNo)) {
								if (portRXRateMap.containsKey(key)) {
									lineChart.addXScaleText(String.valueOf((chartX++)*5));
									send.addChartValue(portRXRateMap.get(key));
									receive.addChartValue(portTXRateMap.get(key));
								}
							}
						}
					}
				}
			}
		};
		timer = new Timer(5000,timerAListener);
		timer.setInitialDelay(5000);
		timer.start();
	}
	
	//从数据库获取黑名单
	private void getBlackListFromDB(){
		String sql = "select * from blacklist";
		ResultSet resultSet = MySQLHelper.executeQuery(sql);
		try {
			while (resultSet.next()) {
				String [] tmp = new String[2];
				tmp[0] = resultSet.getString(3);
				tmp[1] = resultSet.getString(4);
				floodlightProvider.blackList.put(resultSet.getString(2), tmp);		
			}
		} catch (SQLException e) {
			log.error("Select data from blacklist Database error");
			e.printStackTrace();
		}
		MySQLHelper.free(resultSet);
	}
	
	//从数据库获取任务
	private void getTasksListFromDB(){
		String sql = "select * from tasks";
		ResultSet resultSet = MySQLHelper.executeQuery(sql);
		try {
			while (resultSet.next()) {
				String [] tmp = new String[6];
				tmp[0] = resultSet.getString(3);
				tmp[1] = resultSet.getString(4);
				tmp[2] = String.valueOf(resultSet.getInt(5));
				tmp[3] = String.valueOf(resultSet.getInt(6));
				tmp[4] = String.valueOf(resultSet.getInt(7));
				tmp[5] = resultSet.getString(8);
				floodlightProvider.tasks.put(resultSet.getString(2), tmp);
			}
		} catch (SQLException e) {
			log.error("Select data from tasks Database error");
			e.printStackTrace();
		}
		MySQLHelper.free(resultSet);
	}
	
	public static byte[] intToBytes2(int n){  
	    byte[] b = new byte[4];  
	    for(int i = 0;i < 4;i++){  
	        b[i] = (byte)(n >> (24 - i * 8));   
	    }  
	    return b;  
	}
}
