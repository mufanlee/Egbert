package view;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.FloodlightProvider;
import view.util.SWTResourceManager;

public class StartupUI {

	protected static Shell shell;
	protected static Display display;
	protected static Text iptxt;
	
	private static Logger log = LoggerFactory.getLogger(StartupUI.class);
	
	public StartupUI(){
		init();
	}
	
	public void init(){
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		shell.dispose();
		System.exit(0);
	}
	
	private void createContents(){
		shell = new Shell(SWT.MIN);
		shell.setSize(470,400);
		shell.setText("Floodlight 控制器管理平台");
		shell.setLayout(null);
		shell.setBackground( new Color(Display.getCurrent(),255,255,255));
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);  //设置父控件的背景模式，即所有的子控件采用父控件的背景色
		
		try {
			ImageData scratimgdt = new ImageData(getClass().getResourceAsStream("/logo7.png"));
			Image scratimg = new Image(display,scratimgdt);
			Label scratlab = new Label(shell,SWT.NONE);
			scratlab.setBounds(20,0,470,278);
			scratlab.setImage(scratimg);
			
			ImageData imgdt = new ImageData(getClass().getResourceAsStream("/logo1.png"));
			Image img = new Image(display,imgdt);
			shell.setImage(img);
			
//			ImageData floodlightimgdt = new ImageData(getClass().getResourceAsStream("/floodlight.png"));
//			Image floodlightimg = new Image(display, floodlightimgdt);
//			Label floodlightlab = new Label(shell, SWT.NONE);
//			floodlightlab.setBounds(30,260,100,80);
//			floodlightlab.setImage(floodlightimg);
		} catch (Exception e) {
			//System.out.println("Error,Image has error!");
			log.error("Failed to load images: {}", e.getMessage());
			//e.printStackTrace();
		}
		
		Label lab = new Label(shell, SWT.NONE);
		lab.setBounds(30, 279, 344, 17);
		lab.setFont(SWTResourceManager.getFont("华文新魏", 12, SWT.NORMAL));
		lab.setText("请输入floodlight控制器的IP地址：");
		
		Label iplab = new Label(shell, SWT.NONE);
		iplab.setBounds(40,308,17,27);
		iplab.setFont(SWTResourceManager.getFont("华文新魏", 10, SWT.NORMAL));
		iplab.setText("IP:");
		
		iptxt = new Text(shell, SWT.BORDER);
		iptxt.setBounds(69, 302, 220, 27);
		iptxt.addTraverseListener(new TraverseListener() {
			
			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN && !iptxt.getText().isEmpty()) {
					connect();
				}
			}
		});
		
		Button launchbtn = new Button(shell, SWT.NONE);
		launchbtn.setBounds(310, 302, 91, 29);
		launchbtn.setText("启动");
		launchbtn.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!iptxt.getText().isEmpty()) {
					connect();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
	}
	
	public static void connect(){
		int timeOut = 5000;
		try {
			if (InetAddress.getByName(iptxt.getText()).isReachable(timeOut)) {
				shell.setVisible(false);
				FloodlightProvider.getSingleton().getController().setIP(iptxt.getText());
				FloodlightProvider.getSingleton().getController().setOpenFlowPort(8080);
				new MainUI();
			} else
			{
				MessageBox mb = new MessageBox(shell,SWT.ICON_ERROR | SWT.OK);
				mb.setText("Error!");
				mb.setMessage("IP地址不可达，请确认填写了正确的IP地址！");
				mb.open();
			}
		} catch (UnknownHostException e) {
			MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setText("Error!");
			mb.setMessage("主机IP地址解析出错，请确认填写了正确的IP地址！");
			mb.open();
		} catch (IOException e) {
			MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			mb.setText("Error!");
			mb.setMessage("IP地址不可达，请确认填写了正确的IP地址！");
			mb.open();
		}
	}
}
