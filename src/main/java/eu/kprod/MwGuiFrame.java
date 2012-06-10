package eu.kprod;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.ds.MwSensorClassIMU;
import eu.kprod.ds.MwSensorClassMotor;
import eu.kprod.ds.MwSensorClassServo;
import eu.kprod.gui.DebugFrame;
import eu.kprod.gui.LogViewerFrame;
import eu.kprod.gui.MwMainPanel;
import eu.kprod.gui.changepanel.MwBOXPanel;
import eu.kprod.gui.changepanel.MwPIDPanel;
import eu.kprod.gui.chart.MwChartFactory;
import eu.kprod.gui.chart.MwChartPanel;
import eu.kprod.gui.comboBox.MwJComboBox;
import eu.kprod.serial.SerialCom;
import eu.kprod.serial.SerialDevice;
import eu.kprod.serial.SerialException;
import eu.kprod.serial.SerialListener;
import eu.kprod.serial.SerialNotFoundException;

/**
 * Known issues
 * 
 * - when zooming the chart : news values are still recorded so due to the
 * dataSource maxItemcounts and AgeLimite , the chart gets emptied at the zoomed
 * date
 * 
 * @author treym
 * 
 */
public class MwGuiFrame extends JFrame implements SerialListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MwGuiFrame.class);

    class SerialTimeOut extends TimerTask {

        public void run() {
            try {
                // TODO do no send all requests at the same time

                // TODO attitude panel
                // requestMSP(MSP.ATTITUDE);

                if (motorFrame!=null && motorFrame.isVisible()) {
                    send(MSP.request(MSP.MOTOR));
                }
                if (servoFrame!=null && servoFrame.isVisible()) {
                    send(MSP.request(MSP.SERVO));
                }
                send(MSP.request(MSP.RAW_IMU));
            } catch (Exception e) {
                timer.cancel();
                // timer.purge();
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

                if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                }
                // Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);

                MwGuiFrame frame;

                frame = new MwGuiFrame();
                MwGuiFrame.serialListener = frame;

                frame.setVisible(true);

            }
        });

    }

    public static final List<Integer> SerialRefreshRateStrings = initializeMap();
    private static final Integer DEFAULT_BAUDRATE = 115200;

    private static List<Integer> initializeMap() {
        List<Integer> m = new ArrayList<Integer>();
        m.add(1);
        m.add(2);
        m.add(5);
        m.add(10);
        m.add(15);
        m.add(20);
        m.add(25);
//        m.add(30);
//        m.add(40);
//        m.add(50);

        return Collections.unmodifiableList(m);
    }


    private static SerialCom com;
    private static SerialListener serialListener;

    private Timer timer;

    private static DebugFrame debugFrame;

    private static LogViewerFrame motorFrame;
    private static LogViewerFrame servoFrame;
    private JPanel realTimePanel;
    private Properties appProps;
    private JMenu serialMenuPort;
    private ButtonGroup baudRateMenuGroup;
    private ButtonGroup portNameMenuGroup;
    private JPanel settingsPanel;
    private Integer defaultRefreshRate = 10;
    private JMenuItem rescanSerial;
    private JMenuItem disconnectSerial;
    private String frameTitle;

    private JPanel getRawImuChartPanel() {

        if (realTimePanel == null) {

            JButton stopButton = new JButton(("Stop"));
            stopButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    logger.trace("actionPerformed "
                            + e.getSource().getClass().getName());
                    stopTimer();
                }
            });

            
            final MwJComboBox serialRefreshRate = new MwJComboBox("Refresh rate (hz)",
                    (Integer[]) SerialRefreshRateStrings
                            .toArray(new Integer[SerialRefreshRateStrings.size()]));
            serialRefreshRate.setMaximumSize(serialRefreshRate.getMinimumSize());
            serialRefreshRate.setSelectedIndex(3);
            serialRefreshRate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (timer != null) {
                        restartTimer((Integer)serialRefreshRate.getSelectedItem());
                    }

                }
            });
            
            final MwChartPanel realTimeChart = MwChartFactory.createChart(MSP.getModel()
                    .getRealTimeData().getDataSet(MwSensorClassIMU.class));
            MSP.getModel()
                    .getRealTimeData()
                    .addListener(MwSensorClassIMU.class,
                            (MwDataSourceListener) realTimeChart);

            realTimeChart.setPreferredSize(new java.awt.Dimension(700, 400));

            realTimePanel = new JPanel();
            realTimePanel.setLayout(new BorderLayout());
            realTimePanel.add(realTimeChart, BorderLayout.CENTER);

            JButton startButton = new JButton(("Start")); 
            startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    logger.trace("actionPerformed "
                            + e.getSource().getClass().getName());

                    beginSerialCom();
                    restartTimer((Integer)serialRefreshRate.getSelectedItem());
                    realTimeChart.restoreAutoBounds();
                }
            });
            
            JPanel pane = new JPanel();
            pane.setLayout(new FlowLayout(FlowLayout.LEADING));
            pane.setBorder(new EmptyBorder(1, 1, 1, 1));

           
            pane.add(stopButton );
            pane.add(startButton);
            pane.add(serialRefreshRate);

            realTimePanel.add(pane, BorderLayout.SOUTH);
            realTimePanel.add(getUavPanel() ,BorderLayout.EAST);
        }

        return realTimePanel;
    }

    protected void beginSerialCom() {
        boolean openCom = false;
        try {
            if (!getCom().isOpen()) {
                openCom = true;
            }

        } catch (SerialException e1) {

            openCom = true;
        } finally {
            if (openCom) {
                openSerialPort();
            }
        }
        
    }

    private JPanel getUavPanel() {
        // TODO Auto-generated method stub
        return new JPanel();
    }

    public MwGuiFrame() {
        super();
        
        MSP.setModel(new MwDataModel());

        appProps = new Properties();

        try {
            URL url = ClassLoader.getSystemResource("app.properties");
            appProps.load(url.openStream());
        } catch (Exception e) {   
            throw new MwGuiRuntimeException("Failed to load app properties", e);
        }

        frameTitle = appProps.getProperty("mainframe.title");
        this.setTitle(null);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setJMenuBar(createMenuBar());

        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                logger.trace("windowClosing "
                        + e.getSource().getClass().getName());
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                }
                if (com != null) {
                    com.closeSerialPort();
                }
            }
        });

        getContentPane().setLayout(new BorderLayout());
//      getContentPane().add(new JPanel(), BorderLayout.SOUTH);
        getContentPane().add(new MwMainPanel(getRawImuChartPanel(),getSettingsPanel()), BorderLayout.CENTER);

        pack();
    }

    private JPanel getSettingsPanel() {
        if (settingsPanel == null) {
            settingsPanel = new JPanel();
            settingsPanel.setLayout(new BorderLayout());
  
            JButton writeToEepromButton = new JButton(("Write"));
            writeToEepromButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    logger.trace("actionPerformed "
                            + e.getSource().getClass().getName());
                    //TODO
                }
            });
            
            JButton readFromEepromButton = new JButton(("Read"));
            readFromEepromButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                     try {
                         beginSerialCom();
                         boolean restart = false;
                         if (timer!=null){
                         stopTimer();
                         restart=true;
                         }
                         int[] requests = {MSP.BOXNAMES, MSP.PIDNAMES, MSP.RC_TUNING, MSP.PID, MSP.BOX, MSP.MISC };
                         for (int i : requests) {
                             send(MSP.request(i));
                             try {
                                Thread.sleep(14);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        } 
                        if (restart) {
                            restartTimer(defaultRefreshRate);
                        }
                    } catch (SerialException e1) {
                        e1.printStackTrace();
                    } 
                }
            });
            
            JPanel pane = new JPanel();
            pane.setLayout(new FlowLayout(FlowLayout.LEADING));
            pane.setBorder(new EmptyBorder(1, 1, 1, 1));

            JPanel pidPane =      new MwPIDPanel();
            MSP.getModel().setPidChangeListener((ChangeListener) pidPane);
            pane.add(pidPane);
            
            JPanel boxPane =  new MwBOXPanel();
            MSP.getModel().setBoxChangeListener((ChangeListener) boxPane);
            pane.add(boxPane);
            
            settingsPanel.add(pane, BorderLayout.CENTER);
            
            pane = new JPanel();
            pane.setLayout(new FlowLayout(FlowLayout.LEADING));
            pane.setBorder(new EmptyBorder(1, 1, 1, 1));

            
             pane.add(readFromEepromButton );
             pane.add(writeToEepromButton );

            settingsPanel.add(pane, BorderLayout.SOUTH);

            
        }
        return settingsPanel;
    }

    protected void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timer = null;

    }

 

    protected void openSerialPort() {

        closeSerialPort();
        if (portNameMenuGroup.getSelection() == null) {
            JOptionPane.showMessageDialog(this, "No serial port selected");
            return;
        }   
        try {
            String portname = (String) (portNameMenuGroup.getSelection().getActionCommand());
            if (portname == null ) {
                return; // this should not happen, unless a bug
            }
            com = new SerialCom(portname,
                    (Integer) Integer.valueOf(baudRateMenuGroup.getSelection().getActionCommand()));
            com.openSerialPort();
            com.setListener(MwGuiFrame.getInstance());
            
            this.setTitle(new StringBuffer().append(portname).append("@").
                    append(baudRateMenuGroup.getSelection().getActionCommand()).toString() );
        } catch (SerialNotFoundException e) {

        } catch (SerialException e) {
            e.printStackTrace();
        }

    }

    public void setTitle(String s){
        StringBuffer title = new StringBuffer().append(frameTitle);
        if (s != null && s.length()>0){
            title.append(" - ").append(s);
        }
        super.setTitle(title.toString());
    }
    
    protected void restartTimer(Integer rate) {
        if (timer != null) {
            timer.cancel();
            timer.purge();

        }
        timer = new Timer();
        timer.schedule(new SerialTimeOut(), 10, 1000 / rate);
        defaultRefreshRate  = rate;
    }

    public static SerialListener getInstance() {
        return serialListener;
    }

    public static DebugFrame getDebugFrame() {
        if (debugFrame == null) {
            debugFrame = new DebugFrame("Debug serial");
        }
        return debugFrame;
    }

    protected static void showDebugFrame() {
        getDebugFrame().setVisible(true);
        getDebugFrame().repaint();

    }

    public static void closeDebugFrame() {
        if (debugFrame != null) {
            getDebugFrame().setVisible(false);
        }
    }

    public static SerialCom getCom() throws SerialException {
        if (com == null) {
            throw new SerialException("Serial Com is nul");
        }
        return com;
    }

    private JMenuBar createMenuBar() {
        
        JMenuBar menubar = new JMenuBar();
        /* différents menus */
        JMenu menu1 = new JMenu("File");
        JMenu menu2 = new JMenu("Edit");
        JMenu menu3 = new JMenu("View");
        JMenu menu4 = new JMenu("Serial");

        /* differents choix de chaque menu */
        JMenuItem motor = new JMenuItem("Motor");
        JMenuItem servo = new JMenuItem("Servo");
        JMenuItem consoleSerial = new JMenuItem("Console");

        JMenuItem quit = new JMenuItem("Quit");
        JMenuItem annuler = new JMenuItem("Undo");
        JMenuItem copier = new JMenuItem("Copy");
        JMenuItem coller = new JMenuItem("Paste");

        // JMenuItem openLog = new JMenuItem("Open");

        /* Ajouter les choix au menu */
        menu1.add(quit);
        
        menu2.add(annuler);
        menu2.add(copier);
        menu2.add(coller);
        
        menu3.add(servo);
        menu3.add(motor);

        menu4.add(getSerialPortAsMenuItem());
        menu4.add(getSerialBaudAsMenuItem());
        menu4.addSeparator();
      
        menu4.add(consoleSerial);
        
        /* Ajouter les menus  */
        menubar.add(menu1);
        menubar.add(menu2);
        menubar.add(menu3);
        menubar.add(menu4);
        

        consoleSerial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MwGuiFrame.showDebugFrame();
            }
        });

        servo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (servoFrame == null) {
                    servoFrame = new LogViewerFrame("Servo", MSP.getModel()
                            .getRealTimeData(), MwSensorClassServo.class);
                } else {
                    servoFrame.setVisible(true);
                }
            }
        });

        motor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (motorFrame == null) {
                    motorFrame = new LogViewerFrame("Motor", MSP.getModel()
                            .getRealTimeData(), MwSensorClassMotor.class);

                } else {
                    motorFrame.setVisible(true);
                }

            }
        });

      
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeSerialPort();
                System.exit(0);
            }
        });

        // TODO about multiwii
        return menubar;
    }

    private JMenuItem getSerialBaudAsMenuItem() {
        JMenu m = new JMenu("Baud");
        baudRateMenuGroup = new ButtonGroup(  );
        for (Integer p :  SerialDevice.SERIAL_BAUD_RATE){
            JMenuItem sm = new JRadioButtonMenuItem(p.toString());
            sm.setActionCommand(p.toString());
            sm.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {

                    logger.trace("actionPerformed "
                            + event.getSource().getClass().getName());

                    closeSerialPort();
                    try {
               
                            Object pp = event.getSource();
                            if (pp instanceof JRadioButtonMenuItem){
                                JRadioButtonMenuItem va = (JRadioButtonMenuItem) pp;
                               if (com != null){
                                com.setSerialRate(Integer.valueOf(va.getText()));
                                com.openSerialPort();
                                com.setListener(MwGuiFrame.serialListener);
                               }
                            } 
                        
                    } catch (SerialException e) {
                        e.printStackTrace();
                    }

                }
            });
            m.add(sm);
            baudRateMenuGroup.add(sm);
            if (DEFAULT_BAUDRATE.equals( p)){
                sm.setSelected(true);
            }
        }
        return m;
    }

    private JMenu getSerialPortAsMenuItem() {
        if (serialMenuPort == null){
            JMenu m = new JMenu("Port");
            serialMenuPort =m;
        }else{
            serialMenuPort.removeAll();
        }
        
        portNameMenuGroup = new ButtonGroup( );
        for (String p : SerialDevice.getPortNameList()){
            JMenuItem sm = new JRadioButtonMenuItem(p);
            sm.setActionCommand(p);
            serialMenuPort.add(sm);
            portNameMenuGroup.add(sm);
        }
        serialMenuPort.addSeparator();
        


        
        serialMenuPort.add(getRescanSerialMenuIten());
        serialMenuPort.add(getDisconnectSerialMenuIten());
        return serialMenuPort;
    }



    private JMenuItem getDisconnectSerialMenuIten() {
        if (disconnectSerial==null){
       disconnectSerial = new JMenuItem("Close");

        
        disconnectSerial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeSerialPort();
                portNameMenuGroup.clearSelection();
            }
        });
        }
        return disconnectSerial;
    }

    private JMenuItem getRescanSerialMenuIten() {
        if (rescanSerial==null){
rescanSerial = new JMenuItem("Rescan");
        rescanSerial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeSerialPort();
                getSerialPortAsMenuItem();
            }
        });}
        return rescanSerial;
    }

    /**
     * send a string to the serial com
     * @param s
     * @throws SerialException
     */
    synchronized private void send(String s) throws SerialException {
        if (com != null) {
            com.send(s);
        }
    }

    /**
     * (non-Javadoc)
     * 
     * @see net.fd.gui.AbstractSerialMonitor#message(java.lang.String)
     */
    synchronized public void readSerialByte(final byte input) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                MSP.decode(input);

                if (getDebugFrame().isVisible()) {
                    debugFrame.readSerialByte(input);
                }
            }
        });
    }

    void closeSerialPort() {
        if (com != null) {
            com.closeSerialPort();
        }
        stopTimer();
        com = null;
        this.setTitle(null);
    }

    @Override
    public void reportSerial(Throwable e) {
        // we have an error
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                stopTimer();
                closeSerialPort();

            }
        });
    }

}
