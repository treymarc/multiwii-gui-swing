package eu.kprod;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.ds.MwSensorClassIMU;
import eu.kprod.ds.MwSensorClassMotor;
import eu.kprod.ds.MwSensorClassServo;
import eu.kprod.gui.DebugFrame;
import eu.kprod.gui.LogViewerFrame;
import eu.kprod.gui.MwChartFactory;
import eu.kprod.gui.comboBox.MwJComboBox;
import eu.kprod.serial.SerialCom;
import eu.kprod.serial.SerialDevice;
import eu.kprod.serial.SerialException;
import eu.kprod.serial.SerialListener;
import eu.kprod.serial.SerialNotFoundException;
import gnu.io.CommPortIdentifier;

/**
 * Know issues
 * 
 * - when zooming the chart : news values are still recorded 
 *  so due to the dataSource maxItemcounts and AgeLimite , 
 *  the chart gets emptied at the zoomed date
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
                
                if (showMotor){
                    send(MSP.request(MSP.MOTOR));
                }
                if (showServo){
                    send(MSP.request(MSP.SERVO));
                }
                send(MSP.request(MSP.RAW_IMU));
            } catch (NullPointerException e) {
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

    private static List<Integer> initializeMap() {
        List<Integer> m = new ArrayList<Integer>();
        m.add(1);
        m.add(2);
        m.add(5);
        m.add(10);
        m.add(15);
        m.add(20);
        m.add(25);
        m.add(30);
        m.add(40);
        m.add(50);

        return Collections.unmodifiableList(m);
    }
    
    private JButton startButton;
    private JButton stopButton;

    private static MwJComboBox<String> serialPorts;
    private static MwJComboBox<Integer> serialRates;
    private static MwJComboBox<Integer> serialRefreshRate;
    
    private static SerialCom com;
    private static SerialListener serialListener;

    private Timer timer;

    private static DebugFrame debugFrame;
    private static boolean showServo;
    private static boolean showMotor;
    private static LogViewerFrame motorFrame;
    private static LogViewerFrame servoFrame;
    private ChartPanel chartTrendPanel;
    private JPanel overviewPanel;
    private Properties props;

    private JPanel getMainChartPanel() {

        if (overviewPanel == null) {
            chartTrendPanel = MwChartFactory.createChart(MSP.getModel().getRealTimeData().getDataSet(MwSensorClassIMU.class));
            MSP.getModel().getRealTimeData().addListener(MwSensorClassIMU.class, (MwDataSourceListener)chartTrendPanel);
            
            chartTrendPanel.setPreferredSize(new java.awt.Dimension(500, 270));

            overviewPanel = new JPanel();
            overviewPanel.setLayout(new BorderLayout());
            overviewPanel.setBorder(new EmptyBorder(1, 1, 1, 1));

            overviewPanel.add(chartTrendPanel, BorderLayout.CENTER);
        }

        return overviewPanel;
    }

    public MwGuiFrame()   {
        super();

        
        MSP.setModel(new MwDataModel());

        props = new Properties();
        
        try {
            URL url = ClassLoader.getSystemResource("app.properties");
            props.load(url.openStream());
        } catch (Exception e) {
            // TODO Auto-generated catch block
           throw new MwGuiRuntimeException("Failed to load app properties", e);
        }
        
        super.setTitle(props.getProperty("mainframe.title"));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setJMenuBar(createMenuBar());

        addWindowListener(new WindowAdapter() {

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

        JPanel pane = new JPanel();
//        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
//        pane.setBorder(new EmptyBorder(1, 1, 1, 1));

        startButton = new JButton(("Start"));  
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.trace("actionPerformed "
                        + e.getSource().getClass().getName());

                boolean openCom = false;
                try {
                   if (! getCom().isOpen()){
                       openCom = true;
                   }
                        
                } catch (SerialException e1) {

                    openCom = true;
                }finally{
                    if ( openCom ){
                        openSerialPort();
                    }
                }
               restartTimer();
               chartTrendPanel.restoreAutoBounds();
            }
        });

        stopButton = new JButton(("Stop"));
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.trace("actionPerformed "
                        + e.getSource().getClass().getName());
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                }
                timer =null;
            }
        });

//        pane.add(stopButton);
//        pane.add(Box.createRigidArea(new Dimension(1, 0)));
//        pane.add(startButton);
//
//        getContentPane().add(pane, BorderLayout.NORTH);
        getContentPane().add(getMainChartPanel(), BorderLayout.CENTER);

//        pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
          pane.setBorder(new EmptyBorder(1, 1, 1, 1));

        List<String> portNames = new ArrayList<String>();

        
        for (@SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> enumeration = CommPortIdentifier
        .getPortIdentifiers(); enumeration.hasMoreElements();) {
            CommPortIdentifier commportidentifier = enumeration.nextElement();

            if (commportidentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                String curr_port = commportidentifier.getName();
                portNames.add(curr_port);
            }
        }

        if (portNames.size()==0) {
            portNames.add("");
        }
            
        serialPorts = new MwJComboBox<String>("Serial Port",portNames.toArray(new String[portNames.size()]));
        serialPorts.setMaximumSize(serialPorts.getMinimumSize());
        serialPorts.setSelectedIndex(0);
        
        serialPorts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
//                logger.trace("actionPerformed "
//                        + event.getSource().getClass().getName());
               openSerialPort();

            }
        });

        
        serialRefreshRate = new MwJComboBox<Integer>("Refresh rate (hz)",(Integer[])SerialRefreshRateStrings.toArray(new Integer[SerialRefreshRateStrings.size()]));
        serialRefreshRate.setMaximumSize(serialRefreshRate.getMinimumSize());
        serialRefreshRate.setSelectedIndex(3);
        serialRefreshRate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if( timer != null){
                    restartTimer();
                }

            }
        });
        
        
        serialRates = new MwJComboBox<Integer>("baud rate", (Integer[])SerialDevice.SerialRateStrings.toArray(new Integer[SerialDevice.SerialRateStrings.size()]));
        serialRates.setSelectedIndex(10);
        serialRates.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                logger.trace("actionPerformed "
                        + event.getSource().getClass().getName());

                closeSerialPort();
                try {
                    if (com != null) {
                        com.setSerialRate((Integer) serialRates
                                .getSelectedItem());
                        com.openSerialPort();
                        com.setListener(MwGuiFrame.serialListener);
                    }
                } catch (SerialException e) {
                    e.printStackTrace();
                }

            }
        });

        serialRates.setMaximumSize(serialRates.getMinimumSize());

        
        pane.add(stopButton,  BorderLayout.WEST);
        pane.add(Box.createRigidArea(new Dimension(1, 0)), BorderLayout.WEST);
        pane.add(startButton, BorderLayout.WEST);
        pane.add(Box.createRigidArea(new Dimension(1, 0)), BorderLayout.WEST);
        pane.add(serialPorts,  BorderLayout.WEST);
        pane.add(Box.createRigidArea(new Dimension(1, 0)), BorderLayout.WEST);
        pane.add(serialRates, BorderLayout.WEST);
        pane.add(Box.createRigidArea(new Dimension(1, 0)), BorderLayout.WEST);
        pane.add(serialRefreshRate,  BorderLayout.WEST);
        
        getContentPane().add(pane, BorderLayout.SOUTH);

        pack();
    }

    protected void openSerialPort() {

        closeSerialPort();
        try {
            if (serialPorts.getSelectedItem() != null
                    && serialRates.getSelectedItem() != null) {
                com = new SerialCom(serialPorts.getSelectedItem()
                        .toString(), (Integer) serialRates
                        .getSelectedItem());

                com.openSerialPort();
                com.setListener(MwGuiFrame.getInstance());
            }
        } catch (SerialNotFoundException e) {

        } catch (SerialException e) {
            e.printStackTrace();
        }
        
    }

    protected void restartTimer() {
        if (timer != null) {
            timer.cancel();
             timer.purge();

        }
        timer = new Timer();

        timer.schedule(new SerialTimeOut(), 10, 1000/(Integer)serialRefreshRate.getSelectedItem());

        
    }

    public static SerialListener getInstance() {
        return serialListener;
    }

    public static DebugFrame getDebugFrame() {
        if (debugFrame == null) {
            debugFrame = new DebugFrame("Debug serial",(Integer)serialRates.getSelectedItem());
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
        if (com == null){
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
        
        /* differents choix de chaque menu */
        JMenuItem motor = new JMenuItem("Motor");
        JMenuItem servo = new JMenuItem("Servo");
        JMenuItem debug = new JMenuItem("Debug");
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
        menu3.add(debug);
        menu3.add(servo);
        menu3.add(motor);
        
        /* Ajouter les menu sur la bar de menu */
        menubar.add(menu1);
        menubar.add(menu2);
        menubar.add(menu3);
        
        /* clic sur le choix Démarrer du menu fichier */
        debug.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MwGuiFrame.showDebugFrame();
            }
        });
        
       
        servo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                MwGuiFrame.showServo();
                
            }
        });
        
        motor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                MwGuiFrame.showMotor();
            
            }
        });

        /* clic sur le choix Fin du menu fichier */
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeSerialPort();
                System.exit(0);
            }
        });

        // TODO about multiwii
        return menubar;
    }

    protected static void showServo() {
        // TODO Auto-generated method stub
        showServo =true;
        if (servoFrame==null){
            servoFrame =   new LogViewerFrame("Servo", MSP.getModel().getRealTimeData(), MwSensorClassServo.class);
        }else{
            servoFrame.setVisible(true);
        }
    }

    protected static void showMotor() {
        // TODO Auto-generated method stub
        showMotor =true;
        if (motorFrame==null){
            motorFrame =  new LogViewerFrame("Motor", MSP.getModel().getRealTimeData() ,MwSensorClassMotor.class);

        }else{
            motorFrame.setVisible(true);
        }

    }

    // send string
    synchronized private void send(String s) {
        if (com != null) {
            try {
                com.send(s, 0 /* lineEndings.getSelectedIndex() */);
            } catch (SerialException e) {
                // TODO add error msg to logFrame
                e.printStackTrace();
                // TODO POPUp when unrecoverable error
            }
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

    public static void setSerialRate(Integer selectedItem) throws SerialException {
        serialRates.setSelectedItem(selectedItem);
        getCom().setSerialRate(selectedItem);
    }

    public static void closeSerialPort() {
        if (com !=null){
            com.closeSerialPort();
        }
    }

}
