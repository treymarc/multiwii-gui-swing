package eu.kprod;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import eu.kprod.gui.DebugFrame;
import eu.kprod.gui.myChartFactory;
import eu.kprod.gui.myDataSource;
import eu.kprod.serial.SerialCom;
import eu.kprod.serial.SerialDevice;
import eu.kprod.serial.SerialException;
import eu.kprod.serial.SerialListener;
import eu.kprod.serial.SerialNotFoundException;
import gnu.io.CommPortIdentifier;


public class MwGuiFrame extends JFrame  implements SerialListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static DebugFrame debugFrame;


	class SerialTimeOut extends TimerTask {

		public void run() {
			try{
				//requestMSP(MSP.ATTITUDE);
				requestMSP(MSP.RAW_IMU);
			}catch (NullPointerException e) {
				this.cancel();
				timer.purge();
			}
		}

	}

	/**
	 * @param args
	 * @throws SerialException 
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				
				MwGuiFrame frame = new MwGuiFrame();
				MwGuiFrame.serialListener= frame;
				
				frame.setVisible(true);


			}
		});
		
	}

	private JTextArea textArea;
	private JScrollPane scrollPane;

	private JButton startButton;
	private JButton stopButton;
	//  private JCheckBox autoscrollBox;
	private JComboBox<Object> serialPorts;
	private JComboBox<Integer> serialRates;


	private static final Logger logger = Logger.getLogger(MwGuiFrame.class);

	private static SerialCom com;
	private static SerialListener serialListener;


	private Timer timer ;

	private myDataSource ds = new myDataSource();
	private ChartPanel chartTrendPanel;
	private JPanel overviewPanel;

	private DataMwiiConfImplv2 model;

	private JPanel getOverviewPanel() {

		if (overviewPanel==null){
			chartTrendPanel = new ChartPanel(myChartFactory.createChart(ds));
			chartTrendPanel.setPreferredSize(new java.awt.Dimension(500, 270));

			overviewPanel = new JPanel();
			overviewPanel.setLayout(new BorderLayout());
			overviewPanel.setBorder(new EmptyBorder(1, 1, 1, 1));

			overviewPanel.add(chartTrendPanel,BorderLayout.CENTER);
		}

		return overviewPanel;
	}


	public MwGuiFrame()  {
		super();

		model = new DataMwiiConfImplv2();

		super.setTitle("MwGui");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setJMenuBar(createMenuBar());

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				logger.trace("windowClosing "+ e.getSource().getClass().getName());
				if (timer!=null){
					timer.cancel();
					timer.purge();
				}  
				if(com!=null){
					com.closeSerialPort();
				}
			}
		});  

		getContentPane().setLayout(new BorderLayout());

		JPanel pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		pane.setBorder(new EmptyBorder(1, 1, 1, 1));

		startButton = new JButton(("Start"));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.trace("actionPerformed "+ e.getSource().getClass().getName());

				timer = new Timer();
				timer.scheduleAtFixedRate(new SerialTimeOut(),0, 50);

			}});

		stopButton = new JButton(("Stop"));
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.trace("actionPerformed "+ e.getSource().getClass().getName());
				if (timer!=null){
					timer.cancel();
					timer.purge();
				}    
			}});


		pane.add(stopButton);
		pane.add(Box.createRigidArea(new Dimension(1, 0)));
		pane.add(startButton);

		getContentPane().add(pane, BorderLayout.NORTH);    
		getContentPane().add(getOverviewPanel(), BorderLayout.CENTER);

		pane = new JPanel();
		pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
		pane.setBorder(new EmptyBorder(1, 1, 1, 1));

		//    autoscrollBox =new JCheckBox(("Autoscroll"), true);

		List<String> portNames = new ArrayList<String>();
		
		portNames.add("");
		for (@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> enumeration = CommPortIdentifier.getPortIdentifiers(); enumeration.hasMoreElements();)
		{
			CommPortIdentifier commportidentifier = enumeration.nextElement();
			//System.out.println("Found communication port: " + commportidentifier);
			if (commportidentifier.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				//System.out.println("Adding port to serial port menu: " + commportidentifier);
				String curr_port = commportidentifier.getName();
				portNames.add(curr_port);
			}
		}

		serialPorts = new JComboBox<Object>(portNames.toArray());
		//    serialPorts.setSelectedItem(com2.getDeviceName());
		serialPorts.setMaximumSize(serialPorts.getMinimumSize());	
		serialPorts.setSelectedIndex(0);		
		serialPorts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				logger.trace("actionPerformed "+ event.getSource().getClass().getName());

				if (com!=null){
					com.closeSerialPort();
				}
				try {
					if ( serialPorts.getSelectedItem() !=null && serialRates.getSelectedItem()!=null){
						com =  new SerialCom(
								serialPorts.getSelectedItem().toString(),
								(Integer)serialRates.getSelectedItem());

						com.openSerialPort();
						com.setListener(MwGuiFrame.getInstance());
					}
				} catch (SerialNotFoundException e) {
					
				}catch (SerialException e) {
					e.printStackTrace();
				}

			}});


		serialRates = new JComboBox<Integer>();

		for (Integer entry :  SerialDevice.serialRateStrings) {
			serialRates.addItem(entry);
		}

		serialRates.setSelectedIndex(10);
		serialRates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {

				logger.trace("actionPerformed "+ event.getSource().getClass().getName());

				if (com!=null){
					com.closeSerialPort();
				}
				try {
					if(com!=null){
						com.setSerialRate((Integer) serialRates.getSelectedItem());
						com.openSerialPort();
						com.setListener(MwGuiFrame.serialListener);
					}
				} catch (SerialException e) {
					e.printStackTrace();
				}

			}});

		serialRates.setMaximumSize(serialRates.getMinimumSize());

		//    pane.add(autoscrollBox);
		//    pane.add(Box.createHorizontalGlue());
		pane.add(serialPorts);
		pane.add(Box.createRigidArea(new Dimension(1, 0)));
		pane.add(serialRates);

		getContentPane().add(pane, BorderLayout.SOUTH);

		pack();

	}
	public static SerialListener getInstance() {
		// TODO Auto-generated method stub
		return serialListener;
	}


	public static JFrame getDebugFrame() {
		if (debugFrame ==null){
			debugFrame = new DebugFrame("Debug Panel");
		}
		return debugFrame;
	}
	protected static void showDebugFrame() {
		getDebugFrame().setVisible(true);
		getDebugFrame().repaint();
	}

	public static void closeDebugFrame() {
		if (debugFrame != null){
			debugFrame.dispose();
			MwGuiFrame.debugFrame = null;
		}
	}

	public static SerialCom getCom() {
		return com;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		/* différents menus */
		JMenu menu1 = new JMenu("File");
		JMenu menu2 = new JMenu("Edit");

		/* differents choix de chaque menu */
		JMenuItem debug = new JMenuItem("Debug");
		JMenuItem quit = new JMenuItem("Quit");
		JMenuItem annuler = new JMenuItem("Undo");
		JMenuItem copier = new JMenuItem("Copy");
		JMenuItem coller = new JMenuItem("Paste");


//		JMenuItem openLog = new JMenuItem("Open");

		/* Ajouter les choix au menu */
		menu1.add(debug);
		menu1.add(quit);
		menu2.add(annuler);
		menu2.add(copier);
		menu2.add(coller);

		/* Ajouter les menu sur la bar de menu */
		menubar.add(menu1);
		menubar.add(menu2);

		/* clic sur le choix Démarrer du menu fichier */
		debug.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				MwGuiFrame.showDebugFrame();
			}
		});

		/* clic sur le choix Fin du menu fichier */
		quit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(com!=null){
					com.closeSerialPort();
				}
				System.exit(0);
			}
		});

		return menubar;
	}



	// send string 
	private void send(String s) {
		if (com!=null){
			com.send(s, 0 /*lineEndings.getSelectedIndex()*/);
		}
	}


	//send msp without payload 
	private void requestMSP(int msp) {
		requestMSP( msp, null);
	}

	//send multiple msp without payload 
	private void requestMSP(int[] msps) {
		StringBuffer bf = new StringBuffer();
		for (int m : msps) {
			bf.append(MSP.OUT).append((char)(m));
		}
		send(bf.toString());
	}

	//send msp with payload 
	private void requestMSP(int msp, Character[] payload) {
		if(msp < 0) {
			return; 
		}
		StringBuffer bf = new StringBuffer().append(MSP.OUT);

		if (payload != null){
			bf.append((char)(payload.length)).append((char)(msp)); 
			byte checksum=0;
			for (char c :payload){
				bf.append(c);
				checksum ^= (int)(c);
			}
			bf.append((char) ((int)(checksum)) );
		}else{
			bf.append((char)(msp));
		}

		send(bf.toString());  
	}


	private static int p;
	private int read32() {return (inBuf[p++]&0xff) + ((inBuf[p++]&0xff)<<8) + ((inBuf[p++]&0xff)<<16) + ((inBuf[p++]&0xff)<<24); }
	private int read16() {return (inBuf[p++]&0xff) + ((inBuf[p++])<<8); }
	private int read8()  {return inBuf[p++]&0xff;}

	private static byte checksum=0;
	private static int stateMSP=0,offset=0,dataSize=0;
	private static byte[] inBuf   = new byte[128];


	/*
	 * (non-Javadoc)
	 * @see net.fd.gui.AbstractSerialMonitor#message(java.lang.String)
	 */
	public void readSerialByte(final byte input) {
		//System.currentTimeMillis();
		if (debugFrame != null){
			
			debugFrame.readSerialByte(input);
					        
			
		}

		char c = (char)input;

		if (stateMSP > 99) {
			if (offset <= dataSize) {
				if (offset < dataSize) checksum ^= c;
				inBuf[offset++] = (byte)(c);
			} else {
				if ( checksum == inBuf[dataSize] ) {
					decode(stateMSP);
				}
				stateMSP = 0;
			}
		}

		if (stateMSP <5) {

			if (stateMSP == 4) {
				if (c > 99) {
					stateMSP = c;
					offset = 0;checksum = 0;p=0;
				} else {
					stateMSP = 0;
				} 
			}
			// with/without payload ?
			if (stateMSP == 3) {
				if (c<100) {
					stateMSP++;
					dataSize = c;
					if (dataSize>63) dataSize=63;
				} else {
					stateMSP = (int)c;
				}
			}

			//header detection $M>
			switch(c) {
			case '$':                                         
				if (stateMSP == 0) stateMSP++;break;
			case 'M':
				if (stateMSP == 1) stateMSP++;break;
			case '>':
				if (stateMSP == 2) stateMSP++;break;
			}

		}    

	}

	private void decode(int stateMSP2) {

		switch(stateMSP2) {
		case MSP.IDENT:
			this.getModel().setVersion( read8() );
			this.getModel().setMultiType( read8() );
			break;
		case MSP.STATUS:
			
			//        cycleTime = read16();
			//        i2cError = read16();
			//        present = read16();
			//        mode = read16();
			//        if ((present&1) >0) {buttonAcc.setColorBackground(green_);} else {buttonAcc.setColorBackground(red_);tACC_ROLL.setState(false); tACC_PITCH.setState(false); tACC_Z.setState(false);}
			//        if ((present&2) >0) {buttonBaro.setColorBackground(green_);} else {buttonBaro.setColorBackground(red_); tBARO.setState(false); }
			//        if ((present&4) >0) {buttonMag.setColorBackground(green_);} else {buttonMag.setColorBackground(red_); tMAGX.setState(false); tMAGY.setState(false); tMAGZ.setState(false); }
			//        if ((present&8) >0) {buttonGPS.setColorBackground(green_);} else {buttonGPS.setColorBackground(red_); tHEAD.setState(false);}
			//        if ((present&16)>0) {buttonSonar.setColorBackground(green_);} else {buttonSonar.setColorBackground(red_);}
			//        for(i=0;i<CHECKBOXITEMS;i++) {
			//          if ((mode&(1<<i))>0) buttonCheckbox[i].setColorBackground(green_); else buttonCheckbox[i].setColorBackground(red_);
			//        } 
			break;
		case MSP.RAW_IMU:	
			Date d = new Date();
			ds.put(d,"ax", Double.valueOf(read16() ));
			ds.put(d,"ay", Double.valueOf(read16() ));
			ds.put(d,"az", Double.valueOf(read16() ));

			ds.put(d,"gx", Double.valueOf(read16()/8 ));
			ds.put(d,"gy", Double.valueOf(read16()/8 ));
			ds.put(d,"gz", Double.valueOf(read16()/8 ));

			ds.put(d,"magx", Double.valueOf(read16()/3 ));
			ds.put(d,"magy", Double.valueOf(read16()/3 ));
			ds.put(d,"magz", Double.valueOf(read16()/3 ));

			break;
		case MSP.SERVO:		
			//        for(i=0;i<8;i++) servo[i] = read16(); 
			break;
		case MSP.MOTOR:		
			//        for(i=0;i<8;i++) mot[i] = read16(); 
			break;
		case MSP.RC:		
			//        rcRoll = read16();rcPitch = read16();rcYaw = read16();rcThrottle = read16();    
			//        rcAUX1 = read16();rcAUX2 = read16();rcAUX3 = read16();rcAUX4 = read16(); 
			break;
		case MSP.RAW_GPS:		
			//        GPS_fix = read8();
			//        GPS_numSat = read8();
			//        GPS_latitude = read32();
			//        GPS_longitude = read32();
			//        GPS_altitude = read16();
			//        GPS_speed = read16(); 
			break;
		case MSP.COMP_GPS:			
			//        GPS_distanceToHome = read16();
			//        GPS_directionToHome = read16();
			//        GPS_update = read8(); 
			break;
		case MSP.ATTITUDE:			
			//        angx = read16()/10;angy = read16()/10;
			//        head = read16(); 
			break;
		case MSP.ALTITUDE:			
			//        alt = read32(); 
			break;
		case MSP.BAT:			
			//        bytevbat = read8();
			//        pMeterSum = read16(); 
			break;
		case MSP.RC_TUNING:	
			this.getModel().setRC_RATE((int)(read8()/100.0));
			this.getModel().setRC_EXPO((int)(read8()/100.0));
			this.getModel().setRollPitchRate((int)(read8()/100.0));
			this.getModel().setYawRate((int)(read8()/100.0));
			this.getModel().setDynThrPID((int)(read8()/100.0));
			this.getModel().setThrottleMID((int)(read8()/100.0));
			this.getModel().setThrottleEXPO((int)(read8()/100.0));
			break;
		case MSP.ACC_CALIBRATION:
			break;
		case MSP.MAG_CALIBRATION:	 
			break;
		case MSP.PID:
			
			//        for(i=0;i<PIDITEMS;i++) {
			//          byteP[i] = read8();byteI[i] = read8();byteD[i] = read8();
			//          switch (i) {
			//           case 0: 
			//confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
			//break;
			//           case 1:
			//confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
			//break;
			//           case 2:
			//confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
			//break;
			//           case 3:
			//confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
			//break;
			//           case 7:
			//confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
			//break;
			//           case 8:
			//              confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/1000.0);confD[i].setValue(byteD[i]);
			//              break;
			//           //Different rates fot POS-4 POSR-5 NAVR-6
			//           case 4:
			//              confP[i].setValue(byteP[i]/100.0);confI[i].setValue(byteI[i]/100.0);confD[i].setValue(byteD[i]/1000.0);
			//              break;
			//           case 5:
			//              confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/100.0);confD[i].setValue(byteD[i]/1000.0);
			//              break;                   
			//           case 6:
			//              confP[i].setValue(byteP[i]/10.0);confI[i].setValue(byteI[i]/100.0);confD[i].setValue(byteD[i]/1000.0);
			//              break;                   
			//          }
			//          confP[i].setColorBackground(green_);
			//          confI[i].setColorBackground(green_);
			//          confD[i].setColorBackground(green_);
			//        } 
			//        
			break;
		case MSP.BOX:
			//        for( i=0;i<CHECKBOXITEMS;i++) {
			//          activation[i] = read16();
			//          for( aa=0;aa<12;aa++) {
			//            if ((activation[i]&(1<<aa))>0) checkbox[i].activate(aa); else checkbox[i].deactivate(aa);
			//          }
			//        }
			break;
		case MSP.MISC:
			//        intPowerTrigger = read16();
			break;
		case MSP.MOTOR_PINS:			
			//        for( i=0;i<8;i++) {
			//          byteMP[i] = read8();
			//        } 
			break;
		case MSP.DEBUG:			
			//        debug1 = read16();debug2 = read16();debug3 = read16();debug4 = read16();
			break;
		}

	}


	private DataMwiiConfImplv2 getModel() {
		return  this.model;
	}



}
