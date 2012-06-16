package eu.kprod.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.ds.MwSensorClassHUD;
import eu.kprod.ds.MwSensorClassIMU;
import eu.kprod.ds.MwSensorClassMotor;
import eu.kprod.ds.MwSensorClassServo;
import eu.kprod.gui.changepanel.MwBOXPanel;
import eu.kprod.gui.changepanel.MwPIDPanel;
import eu.kprod.gui.chart.MwChartFactory;
import eu.kprod.gui.chart.MwChartPanel;
import eu.kprod.gui.comp.MwJButton;
import eu.kprod.gui.comp.MwJComboBox;
import eu.kprod.gui.comp.MwJMenu;
import eu.kprod.gui.comp.MwJMenuBar;
import eu.kprod.gui.comp.MwJMenuItem;
import eu.kprod.gui.comp.MwJPanel;
import eu.kprod.gui.comp.MwJRadioButton;
import eu.kprod.gui.comp.MwJSplitPane;
import eu.kprod.gui.comp.StyleColor;
import eu.kprod.msp.MSP;
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
public class MwGuiFrame extends JFrame implements SerialListener,MwDataSourceListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MwGuiFrame.class);
    private static MwGuiFrame instance;

    class actionMspSender implements ActionListener {

        private int[] requests;

        public actionMspSender(int[] requests1) {
            this.requests = requests1;
        }

        public actionMspSender(int msp) {
            this.requests = new int[1];
            this.requests[0] = msp;
        }

        public void actionPerformed(ActionEvent e) {

            beginSerialCom();
            boolean restart = false;
            if (timer != null) {
                stopTimer();
                restart = true;
            }
            for (int i : requests) {
                try {
                    Thread.sleep(14);
                    send(MSP.request(i));
                    Thread.sleep(14);

                } catch (Exception p) {
                    p.printStackTrace();
                }
            }
            if (restart) {
                restartTimer(defaultRefreshRate);
            }

        }
    }



    public static MwGuiFrame getInstance() {
        if (instance == null) {
            instance = new MwGuiFrame();
        }
        return instance;
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
        // m.add(30);
        // m.add(40);
        // m.add(50);

        return Collections.unmodifiableList(m);
    }

    private static SerialCom com;

    private static Timer timer;

    private static DebugFrame debugFrame;
    private static LogViewerFrame motorFrame;
    private static LogViewerFrame servoFrame;

    private MwJPanel realTimePanel;

    private static JMenu serialMenuPort;
    private static ButtonGroup baudRateMenuGroup;
    private static ButtonGroup portNameMenuGroup;
    private MwJPanel settingsPanel;
    private static Integer defaultRefreshRate = 10;
    private static MwJMenuItem rescanSerial;
    private static MwJMenuItem disconnectSerial;
    private String frameTitle;
    private int sizeY = 400;
    private int sizeX = 700;
    private static MwChartPanel realTimeChart;
    private static MwHudPanel hudPanel;
    private static MwSensorCheckBoxJPanel realTimeCheckBoxPanel;

    private MwJPanel getRawImuChartPanel() {

        if (realTimePanel == null) {

            JButton stopButton = new MwJButton("Stop", "Stop monitoring");
            stopButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    logger.trace("actionPerformed "
                            + e.getSource().getClass().getName());
                    stopTimer();
                }
            });

            final MwJComboBox serialRefreshRate = new MwJComboBox(
                    "Refresh rate (hz)",
                    (Integer[]) SerialRefreshRateStrings
                            .toArray(new Integer[SerialRefreshRateStrings
                                    .size()]));
//            serialRefreshRate
//                    .setMaximumSize(serialRefreshRate.getMinimumSize());
//            serialRefreshRate
//            .setMinimumSize(serialRefreshRate.getMinimumSize());
            serialRefreshRate.setSelectedIndex(3);
            serialRefreshRate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (timer != null) {
                        restartTimer((Integer) serialRefreshRate
                                .getSelectedItem());
                    }
                }
            });

            setRealTimeChart(MwChartFactory.createChart(MSP
                    .getRealTimeData().getDataSet(MwSensorClassIMU.class)));
            MSP.getRealTimeData().addListener(MwSensorClassIMU.class,
                    (MwDataSourceListener) getRealTimeChart());

            getRealTimeChart()
                    .setPreferredSize(new java.awt.Dimension(sizeX, sizeY));

            // Create a split pane with the two scroll panes in it.
            JSplitPane splitPane = new MwJSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT, getHudPanel(), getRealTimeChart());
            splitPane.setOneTouchExpandable(true);
            splitPane.setDividerLocation(0.8);

            realTimePanel = new MwJPanel();

            realTimePanel.setLayout(new BorderLayout());
            realTimePanel.add(splitPane, BorderLayout.CENTER);

            JButton startButton = new MwJButton("Start", "Start monitoring");
            startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    logger.trace("actionPerformed "
                            + e.getSource().getClass().getName());

                    beginSerialCom();
                    restartTimer((Integer) serialRefreshRate.getSelectedItem());
                    getRealTimeChart().restoreAutoBounds();
                }
            });

            MwJPanel pane = new MwJPanel();
            pane.setLayout(new FlowLayout(FlowLayout.LEADING));
            pane.setBorder(new EmptyBorder(1, 1, 1, 1));

            pane.add(stopButton);
            pane.add(startButton);
            pane.add(serialRefreshRate);

            realTimePanel.add(pane, BorderLayout.SOUTH);
            // realTimePanel.add(getHudPanel() ,BorderLayout.EAST);
            realTimePanel.add(getRealTimeCheckBowPanel(), BorderLayout.EAST);
        }
        return realTimePanel;
    }

    private static MwSensorCheckBoxJPanel getRealTimeCheckBowPanel() {
        if (realTimeCheckBoxPanel == null) {
            realTimeCheckBoxPanel = new MwSensorCheckBoxJPanel();
        }
        return realTimeCheckBoxPanel;
    }

    protected static void beginSerialCom() {
        boolean openCom = false;
        try {
            if (!getCom().isOpen()) {
                openCom = true;
            }
        } catch (SerialException e1) {
            openCom = true;
        } finally {
            if (openCom) {
                try {
                    openSerialPort();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static MwHudPanel getHudPanel() {

        if (hudPanel == null) {
            hudPanel = new MwHudPanel(StyleColor.backGround);
            MSP.getRealTimeData().addListener(MwSensorClassHUD.class,
                    (MwDataSourceListener) hudPanel);
        }
        return hudPanel;
    }

    private MwGuiFrame() {
        super();
        MSP.getRealTimeData().addListener(MwSensorClassIMU.class,
                (MwDataSourceListener) this);
       
        {
            try {
                URL url = ClassLoader.getSystemResource("app.properties");
                final Properties appProps = new Properties();
                appProps.load(url.openStream());
                frameTitle = appProps.getProperty("mainframe.title");
            } catch (Exception e) {
                throw new MwGuiRuntimeException(
                        "Failed to load app properties", e);
            }
        }

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

        setBackground(StyleColor.backGround);
        getContentPane().setLayout(new BorderLayout());
        // getContentPane().add(new MwJPanel(), BorderLayout.SOUTH);
        getContentPane().add(
                new MwMainPanel(getRawImuChartPanel(), getSettingsPanel()),
                BorderLayout.CENTER);

        pack();
    }

    private MwJPanel getSettingsPanel() {

        if (settingsPanel == null) {
            settingsPanel = new MwJPanel();
            settingsPanel.setLayout(new BorderLayout());

            JButton writeToEepromButton = new MwJButton("Write",
                    "Write to eeprom");
            writeToEepromButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    logger.trace("actionPerformed "
                            + e.getSource().getClass().getName());
                    // TODO
                }
            });

            JButton readFromEepromButton = new MwJButton("Read", "Read eeprom");
            int[] req = { MSP.BOXNAMES, MSP.PIDNAMES, MSP.RC_TUNING, MSP.PID,
                    MSP.BOX, MSP.MISC };
            readFromEepromButton.addActionListener(new actionMspSender(req));

            JButton calibGyrButton = new MwJButton("Gyro", "Gyro calibration");
            JButton calibAccButton = new MwJButton("Acc", "Acc calibration");
            JButton calibMagButton = new MwJButton("Mag", "Mag calibration");

            calibAccButton.addActionListener(new actionMspSender(
                    MSP.ACC_CALIBRATION));
            calibMagButton.addActionListener(new actionMspSender(
                    MSP.MAG_CALIBRATION));
            // calibGyrButton.addActionListener(new
            // actionMspSender(MSP.MAG_CALIBRATION));

            MwJPanel pane = new MwJPanel();
            pane.setLayout(new FlowLayout(FlowLayout.LEADING));
            pane.setBorder(new EmptyBorder(1, 1, 1, 1));

            MwJPanel pidPane = new MwPIDPanel();
            MSP.setPidChangeListener((ChangeListener) pidPane);
            pane.add(pidPane);

            MwJPanel boxPane = new MwBOXPanel();
            MSP.setBoxChangeListener((ChangeListener) boxPane);
            pane.add(boxPane);

            settingsPanel.add(pane, BorderLayout.CENTER);

            pane = new MwJPanel();
            pane.setLayout(new FlowLayout(FlowLayout.LEADING));
            pane.setBorder(new EmptyBorder(1, 1, 1, 1));

            pane.add(readFromEepromButton);
            pane.add(writeToEepromButton);
            pane.add(calibGyrButton);
            pane.add(calibAccButton);
            pane.add(calibMagButton);

            settingsPanel.add(pane, BorderLayout.SOUTH);

        }
        return settingsPanel;
    }

    protected static void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timer = null;

    }

    protected static void openSerialPort() {
        closeSerialPort();
        getSerialPortAsMenuItem();
        if (portNameMenuGroup.getSelection() == null) {
            List<String> list = SerialDevice.getPortNameList();
            if (list == null || list.size() == 0) {
                list.add("");
            }
            Object[] array = list.toArray(new String[list.size()]);
            String name = (String) JOptionPane.showInputDialog(
                    MwGuiFrame.getInstance(), "Select a Serial Port", "port",
                    JOptionPane.INFORMATION_MESSAGE, null, array, array[0]);

            Enumeration<AbstractButton> els = portNameMenuGroup.getElements();
            ButtonModel model = null;
            while (els.hasMoreElements()) {
                AbstractButton abstractButton = (AbstractButton) els
                        .nextElement();
                try {
                    if (abstractButton.getActionCommand().equals(name)) {
                        model = abstractButton.getModel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (model != null) {
                portNameMenuGroup.setSelected(model, true);
            } else {

                JOptionPane.showMessageDialog(MwGuiFrame.getInstance(),
                        "Error while getting serial port name");
                return;
            }
        }
        try {
            String portname = (String) (portNameMenuGroup.getSelection()
                    .getActionCommand());
            if (portname == null) {
                return; // this should not happen, unless a bug
            }
            com = new SerialCom(portname,
                    (Integer) Integer.valueOf(baudRateMenuGroup.getSelection()
                            .getActionCommand()));
            com.openSerialPort();
            com.setListener(MwGuiFrame.getInstance());

            MwGuiFrame.getInstance().setTitle(
                    new StringBuffer()
                            .append(portname)
                            .append("@")
                            .append(baudRateMenuGroup.getSelection()
                                    .getActionCommand()).toString());
        } catch (SerialNotFoundException e) {

        } catch (SerialException e) {
            e.printStackTrace();
        }
    }

    public void setTitle(String s) {
        StringBuffer title = new StringBuffer().append(frameTitle);
        if (s != null && s.length() > 0) {
            title.append(" - ").append(s);
        }
        super.setTitle(title.toString());
    }

    protected static void restartTimer(Integer rate) {
        final class SerialTimeOut extends TimerTask {

            public void run() {
                try {
                    // TODO do no send all requests at the same time

                    send(MSP.request(MSP.ATTITUDE));
                    send(MSP.request(MSP.ALTITUDE));

                    if (motorFrame != null && motorFrame.isVisible()) {
                        send(MSP.request(MSP.MOTOR));
                    }
                    if (servoFrame != null && servoFrame.isVisible()) {
                        send(MSP.request(MSP.SERVO));
                    }
                    send(MSP.request(MSP.RAW_IMU));
                } catch (Exception e) {
                    timer.cancel();
                    // timer.purge();
                }
            }

        }
        if (timer != null) {
            timer.cancel();
            timer.purge();

        }
        timer = new Timer();
        timer.schedule(new SerialTimeOut(), 10, 1000 / rate);
        defaultRefreshRate = rate;
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
            openSerialPort();
            if (com == null){
                throw new SerialException("No Serial Com");
            }
        }
        return com;
    }

    private JMenuBar createMenuBar() {

        JMenuBar menubar = new MwJMenuBar();
        /* différents menus */
        JMenu menu1 = new MwJMenu("File");
        JMenu menu2 = new MwJMenu("Edit");
        JMenu menu3 = new MwJMenu("View");
        JMenu menu4 = new MwJMenu("Serial");

        /* differents choix de chaque menu */
        MwJMenuItem motor = new MwJMenuItem("Motor");
        MwJMenuItem servo = new MwJMenuItem("Servo");
        MwJMenuItem consoleSerial = new MwJMenuItem("Console");

        MwJMenuItem quit = new MwJMenuItem("Quit");
        MwJMenuItem annuler = new MwJMenuItem("Undo");
        MwJMenuItem copier = new MwJMenuItem("Copy");
        MwJMenuItem coller = new MwJMenuItem("Paste");

        // MwJMenuItem openLog = new MwJMenuItem("Open");

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

        /* Ajouter les menus */
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
                    servoFrame = new LogViewerFrame("Servo", MSP
                            .getRealTimeData(), MwSensorClassServo.class);
                } else {
                    servoFrame.setVisible(true);
                }
            }
        });

        motor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (motorFrame == null) {
                    motorFrame = new LogViewerFrame("Motor", MSP
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
        JMenu m = new MwJMenu("Baud");
        baudRateMenuGroup = new ButtonGroup();
        for (Integer p : SerialDevice.SERIAL_BAUD_RATE) {
            JRadioButton sm = new JRadioButton(p.toString());
            sm.setActionCommand(p.toString());
            sm.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {

                    logger.trace("actionPerformed "
                            + event.getSource().getClass().getName());

                    closeSerialPort();
                    try {

                        Object pp = event.getSource();
                        if (pp instanceof JRadioButtonMenuItem) {
                            JRadioButtonMenuItem va = (JRadioButtonMenuItem) pp;
                            if (com != null) {
                                com.setSerialRate(Integer.valueOf(va.getText()));
                                com.openSerialPort();
                                com.setListener(MwGuiFrame.getInstance());
                            }
                        }

                    } catch (SerialException e) {
                        e.printStackTrace();
                    }

                }
            });
            m.add(sm);
            baudRateMenuGroup.add(sm);
            if (DEFAULT_BAUDRATE.equals(p)) {
                sm.setSelected(true);
            }
        }
        return m;
    }

    private static JMenu getSerialPortAsMenuItem() {
        if (serialMenuPort == null) {
            JMenu m = new MwJMenu("Port");
            serialMenuPort = m;
        } else {
            serialMenuPort.removeAll();
        }

        portNameMenuGroup = new ButtonGroup();
        for (String p : SerialDevice.getPortNameList()) {
            JRadioButton sm = new MwJRadioButton(p);
            sm.setActionCommand(p);
            serialMenuPort.add(sm);
            portNameMenuGroup.add(sm);
        }
        serialMenuPort.addSeparator();
        serialMenuPort.add(getRescanSerialMenuIten());
        serialMenuPort.add(getDisconnectSerialMenuIten());
        return serialMenuPort;
    }

    private static MwJMenuItem getDisconnectSerialMenuIten() {
        if (disconnectSerial == null) {
            disconnectSerial = new MwJMenuItem("Close");

            disconnectSerial.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    closeSerialPort();
                    portNameMenuGroup.clearSelection();
                }
            });
        }
        return disconnectSerial;
    }

    private static MwJMenuItem getRescanSerialMenuIten() {
        if (rescanSerial == null) {
            rescanSerial = new MwJMenuItem("Rescan");
            rescanSerial.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    closeSerialPort();
                    getSerialPortAsMenuItem();
                }
            });
        }
        return rescanSerial;
    }

    /**
     * send a string to the serial com
     * 
     * @param s
     * @throws SerialException
     */
    synchronized private static void send(List<Byte> msp)
            throws SerialException {
        if (com != null) {
            byte[] arr = new byte[msp.size()];
            int i = 0;
            for (byte b : msp) {
                arr[i++] = b;
            }

            com.send(arr);
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

    static void closeSerialPort() {
        if (com != null) {
            com.closeSerialPort();
        }
        stopTimer();
        com = null;
        MwGuiFrame.getInstance().setTitle(null);
    }

    @Override
    public void reportSerial(Throwable e) {
        // we have an error

        stopTimer();
        closeSerialPort();

    }

    public static void AddSensorCheckBox(String sensorName) {

            getRealTimeCheckBowPanel().addSensorBox(sensorName);

    }

    @Override
    public void readNewValue(String name, Double value) {
        // TODO Auto-generated method stub
        MwGuiFrame.AddSensorCheckBox(name);
    }

    public static MwChartPanel getRealTimeChart() {
        return realTimeChart;
    }

    public void setRealTimeChart(MwChartPanel realTimeChart1) {
        realTimeChart = realTimeChart1;
    }



}
