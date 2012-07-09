/**
 * Copyright (C) 2012 @author treym (Trey Marc)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.kprod.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import eu.kprod.ds.MwDataSourceListener;
import eu.kprod.ds.MwSensorClass;
import eu.kprod.ds.MwSensorClassCompas;
import eu.kprod.ds.MwSensorClassHUD;
import eu.kprod.ds.MwSensorClassIMU;
import eu.kprod.ds.MwSensorClassMotor;
import eu.kprod.ds.MwSensorClassRC;
import eu.kprod.ds.MwSensorClassServo;
import eu.kprod.gui.chart.MwChartFactory;
import eu.kprod.gui.chart.MwChartPanel;
import eu.kprod.gui.comp.MwColor;
import eu.kprod.gui.comp.MwJButton;
import eu.kprod.gui.comp.MwJComboBox;
import eu.kprod.gui.comp.MwJMenu;
import eu.kprod.gui.comp.MwJMenuBar;
import eu.kprod.gui.comp.MwJMenuItem;
import eu.kprod.gui.comp.MwJPanel;
import eu.kprod.gui.comp.MwJRadioButton;
import eu.kprod.gui.instrument.MwCompasPanel;
import eu.kprod.gui.instrument.MwHudPanel;
import eu.kprod.gui.instrument.MwInstrumentJPanel;
import eu.kprod.gui.instrument.MwRCDataPanel;
import eu.kprod.gui.instrument.MwUAVPanel;
import eu.kprod.gui.setting.MwBOXPanel;
import eu.kprod.gui.setting.MwPIDPanel;
import eu.kprod.msg.I18n;
import eu.kprod.msp.MSP;
import eu.kprod.serial.SerialCom;
import eu.kprod.serial.SerialDevice;
import eu.kprod.serial.SerialException;
import eu.kprod.serial.SerialListener;
import eu.kprod.serial.SerialNotFoundException;

/**
 * Known issues
 * - when zooming the chart : news values are still recorded so due to the
 * dataSource maxItemcounts and AgeLimite , the chart gets emptied at the zoomed
 * date
 * 
 * @author treym
 */
public final class MwGuiFrame extends JFrame implements SerialListener,
        MwDataSourceListener, ChangeListener {

    class ActionMspSender implements ActionListener {

        private static final long SERIALDELAY = 14;
        private final int[] requests;

        public ActionMspSender(int msp) {
            this.requests = new int[1];
            this.requests[0] = msp;
        }

        public ActionMspSender(int[] requests1) {
            this.requests = requests1.clone();
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            beginSerialCom();
            boolean restart = false;
            if (timer != null) {
                stopTimer();
                restart = true;
            }
            for (int i : requests) {
                try {
                    Thread.sleep(SERIALDELAY);
                    send(MSP.request(i));
                    Thread.sleep(SERIALDELAY);

                } catch (Exception p) {
                    LOGGER.error(p.getMessage() + "\n");
                }
            }
            if (restart) {
                restartTimer(refreshRate);
            }

        }
    }

    private static final Logger LOGGER = Logger.getLogger(MwGuiFrame.class);

    private static final Integer DEFAULT_BAUDRATE = 115200;
    private static final Integer DEAULT_RATE = 10;

    private Integer refreshRate = DEAULT_RATE;

    public static final List<Integer> SERIAL_REFRESHRATES = initializeMap();

    private static ButtonGroup baudRateMenuGroup;
    private static MwSensorCheckBoxJPanel chartCheckBoxsPanel;

    private static SerialCom com;

    private MwJPanel instrumentPanel;
    private MwInstrumentJPanel hudPanel;
    private MwInstrumentJPanel compasPanel;
    private MwInstrumentJPanel rcDataPanel;

    private static DebugFrame debugFrame;

    private static MwJMenuItem disconnectSerial;
    private static MwJMenuItem rescanSerial;
    private static JMenu serialMenuPort;

    private boolean inited = false;

    private MwGuiFrame instance;

    private static JMenuBar menuBar;
    private static ButtonGroup portNameMenuGroup;

    private static MwChartPanel realTimeChart;

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String TEXT_ABOUT = "MwGui A Java Swing frontend for multiwii\n\n"
            + "This program comes with ABSOLUTELY NO WARRANTY.\n"
            + "This is free software, and you are welcome to redistribute it\n"
            + "under certain conditions";
    // private static LogViewerFrame motorFrame;
    // private static LogViewerFrame servoFrame;
    private static Timer timer;
    private static MwUAVPanel uavPanel;
    private static MwConfiguration conf;

    public static void addSensorCheckBox(final String sensorName) {
        getChartCheckBoxPanel().addSensorBox(sensorName);
    }

    protected void beginSerialCom() {
        boolean openCom = false;
        try {
            if (!getCom().isOpen()) {
                openCom = true;
            }
        } catch (final SerialException e1) {
            openCom = true;
        } finally {
            if (openCom) {
                try {
                    openSerialPort();
                } catch (final Exception e) {
                    LOGGER.error(e.getMessage() + "\n");
                }
            }
        }
    }

    public void closeDebugFrame() {
        if (debugFrame != null) {
            getDebugFrame().setVisible(false);
        }
    }

    void closeSerialPort() {
        resetAllValues();
        if (com != null) {
            com.closeSerialPort();
        }
        stopTimer();
        com = null;

    }

    public static MwSensorCheckBoxJPanel getChartCheckBoxPanel() {
        if (chartCheckBoxsPanel == null) {
            chartCheckBoxsPanel = new MwSensorCheckBoxJPanel(conf);
        }
        return chartCheckBoxsPanel;
    }

    public static MwChartPanel getChartPanel() {
        return realTimeChart;
    }

    public SerialCom getCom() throws SerialException {
        if (com == null) {
            openSerialPort();
            if (com == null) {
                throw new SerialException("No Serial Com");
            }
        }
        return com;
    }

    public DebugFrame getDebugFrame() {
        if (debugFrame == null) {
            debugFrame = new DebugFrame("Debug serial", instance);
        }
        return debugFrame;
    }

    private MwJMenuItem getDisconnectSerialMenuIten() {
        if (disconnectSerial == null) {
            disconnectSerial = new MwJMenuItem("Close");
            disconnectSerial.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    closeSerialPort();
                    portNameMenuGroup.clearSelection();
                }
            });
        }
        return disconnectSerial;
    }

    //
    // public MwGuiFrame getInstance() {
    // if (instance == null) {
    // instance = new MwGuiFrame();
    // MSP.setUavChangeListener(instance);
    // }
    // return instance;
    // }

    public MwJPanel getInstrumentPanel() {
        if (instrumentPanel == null) {

            final MwJPanel pane = new MwJPanel(conf);
            pane.setLayout(new GridLayout(1, 4));

            pane.add(hudPanel = new MwHudPanel(conf));
            MSP.getRealTimeData().addListener(MwSensorClassHUD.class, hudPanel);

            pane.add(compasPanel = new MwCompasPanel(conf));
            MSP.getRealTimeData().addListener(MwSensorClassCompas.class,
                    compasPanel);

            pane.add(uavPanel = new MwUAVPanel(conf));
            MSP.getRealTimeData().addListener(MwSensorClassMotor.class,
                    uavPanel);
            MSP.getRealTimeData().addListener(MwSensorClassServo.class,
                    uavPanel);

            pane.add(rcDataPanel = new MwRCDataPanel(conf));
            MSP.getRealTimeData().addListener(MwSensorClassRC.class,
                    rcDataPanel);
            pane.setMinimumSize(new Dimension(770, 200));
            pane.setMaximumSize(new Dimension(770, 200));

            instrumentPanel = new MwJPanel(conf);
            instrumentPanel.add(Box.createHorizontalGlue());
            instrumentPanel.setLayout(new BoxLayout(instrumentPanel,
                    BoxLayout.LINE_AXIS));
            instrumentPanel.add(pane);
            instrumentPanel.add(Box.createHorizontalGlue());

        }
        return instrumentPanel;
    }

    private MwJMenuItem getRescanSerialMenuIten() {
        if (rescanSerial == null) {
            rescanSerial = new MwJMenuItem("Rescan");
            rescanSerial.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    stopTimer();

                    closeSerialPort();
                    getSerialPortAsMenuItem();
                    SwingUtilities.updateComponentTreeUI(menuBar);
                }
            });
        }
        return rescanSerial;
    }

    private JMenu getSerialPortAsMenuItem() {
        if (serialMenuPort == null) {
            final JMenu m = new MwJMenu("Port");
            serialMenuPort = m;
        } else {
            serialMenuPort.removeAll();
        }

        portNameMenuGroup = new ButtonGroup();
        for (final String p : SerialDevice.getPortNameList()) {
            final JRadioButton sm = new MwJRadioButton(p);
            sm.setActionCommand(p);
            serialMenuPort.add(sm);
            portNameMenuGroup.add(sm);
        }
        serialMenuPort.addSeparator();
        serialMenuPort.add(getRescanSerialMenuIten());
        serialMenuPort.add(getDisconnectSerialMenuIten());
        return serialMenuPort;
    }

    private static List<Integer> initializeMap() {
        final List<Integer> m = new ArrayList<Integer>();
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

    protected void openSerialPort() {
        closeSerialPort();
        getSerialPortAsMenuItem();
        if (portNameMenuGroup.getSelection() == null) {
            final List<String> list = SerialDevice.getPortNameList();
            if (list == null || list.size() == 0) {
                list.add("");
            }
            final Object[] array = list.toArray(new String[list.size()]);
            final String name = (String) JOptionPane.showInputDialog(this,
                    "Select a Serial Port", "port",
                    JOptionPane.INFORMATION_MESSAGE, null, array, array[0]);

            final Enumeration<AbstractButton> els = portNameMenuGroup
                    .getElements();
            ButtonModel model = null;
            while (els.hasMoreElements()) {
                final AbstractButton abstractButton = els.nextElement();
                try {
                    if (abstractButton.getActionCommand().equals(name)) {
                        model = abstractButton.getModel();
                    }
                } catch (final Exception e) {
                    LOGGER.error(e.getMessage() + "\n");
                }
            }
            if (model != null) {
                portNameMenuGroup.setSelected(model, true);
            } else {

                JOptionPane.showMessageDialog(this,
                        "Error while getting serial port name");
                return;
            }
        }
        try {
            final String portname = portNameMenuGroup.getSelection()
                    .getActionCommand();

            if (portname == null) {
                return; // this should not happen, unless a bug
            }
            com = new SerialCom(portname, Integer.valueOf(baudRateMenuGroup
                    .getSelection().getActionCommand()));

            com.openSerialPort();
            com.setListener(this);
            MSP.setUavChangeListener(instance);
            this.setTitle(new StringBuffer()
                    .append(portname)
                    .append("@")
                    .append(baudRateMenuGroup.getSelection().getActionCommand())
                    .toString());
        } catch (final SerialNotFoundException e) {
            LOGGER.error(e.getMessage() + "\n");
        } catch (final SerialException e) {
            LOGGER.error(e.getMessage() + "\n");
        }
    }

    protected void restartTimer(final Integer rate) {
        final class SerialTimeOut extends TimerTask {

            @Override
            public void run() {
                try {
                    // TODO do not send all requests at the same time
                    send(MSP.request(MSP.ATTITUDE));
                    send(MSP.request(MSP.ALTITUDE));

                    // if (motorFrame != null && motorFrame.isVisible()) {
                    send(MSP.request(MSP.MOTOR));
                    // }
                    // if (servoFrame != null && servoFrame.isVisible()) {
                    send(MSP.request(MSP.SERVO));
                    // }
                    send(MSP.request(MSP.RAW_IMU));
                    send(MSP.request(MSP.DEBUG));
                    send(MSP.request(MSP.RC));
                } catch (SerialException e) {
                    LOGGER.error("Error while sending command");
                } catch (final Exception e) {
                    timer.cancel();

                }
            }
        }

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timer = new Timer();
        timer.schedule(new SerialTimeOut(), 10, 1000 / rate);
        refreshRate = rate;
    }

    /**
     * send a string to the serial com
     * 
     * @param command
     *            is the packet to send
     * @throws SerialException
     */
    private synchronized void send(final ByteArrayOutputStream cmd)
            throws SerialException {
        if (com != null) {
            if (!inited) {
                com.send(MSP.request(MSP.IDENT));
            }
            com.send(cmd);
        }
    }

    protected void showDebugFrame() {
        getDebugFrame().setVisible(true);
        getDebugFrame().repaint();
    }

    protected static void stopTimer() {
        if (timer != null) {
            // if there is a serial io in jni , the job can not be canceled ?
            //
            timer.cancel();
            timer.purge();
        }
        timer = null;
    }

    private MwJPanel centerChartPanel;

    private String frameTitle;

    private MwJPanel realTimePanel;

    private MwJPanel settingsPanel;

    private final int sizeX = 700;

    private final int sizeY = 400;

    public MwGuiFrame(MwConfiguration mwConfiguration) {
        super();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                stopTimer();
                closeSerialPort();
                System.exit(0);
            }
        });
        instance = this;
        conf = mwConfiguration;
        MSP.getRealTimeData().addListener(MwSensorClassIMU.class, this);

        {
            try {
                URL url = ClassLoader.getSystemResource("app.properties");
                Properties appProps = new Properties();
                appProps.load(url.openStream());
                frameTitle = appProps.getProperty("mainframe.title");
            } catch (final Exception e) {
                throw new MwGuiRuntimeException(
                        "INIT Failed to load app properties", e);
            }
        }

        this.setTitle(null);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setJMenuBar(menuBar = createMenuBar());

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                LOGGER.trace("windowClosing "
                        + e.getSource().getClass().getName() + "\n");
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                }
                if (com != null) {
                    com.closeSerialPort();
                }
            }
        });

        setBackground(conf.color.getColor(MwColor.BACKGROUND_COLOR));
        getContentPane().setLayout(new BorderLayout());
        // getContentPane().add(new MwJPanel(), BorderLayout.SOUTH);
        getContentPane().add(
                new MwMainPanel(getRealTimePanel(), getSettingsPanel()),
                BorderLayout.CENTER);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JMenuBar createMenuBar() {

        final JMenuBar menubar = new MwJMenuBar();
        /* différents menus */
        final JMenu menu1 = new MwJMenu("File");
        final JMenu menu3 = new MwJMenu("Upload");
        final JMenu menu4 = new MwJMenu("Serial");
        final JMenu menu5 = new MwJMenu("Help");

        /* differents choix de chaque menu */
        // MwJMenuItem motor = new MwJMenuItem("Motor");
        // final MwJMenuItem servo = new MwJMenuItem("Servo");
        final MwJMenuItem consoleSerial = new MwJMenuItem("Console");

        final MwJMenuItem quit = new MwJMenuItem("Quit");

        final MwJMenuItem preferences = new MwJMenuItem("Preferences");

        final MwJMenuItem helpContent = new MwJMenuItem("Help Contents");
        final MwJMenuItem about = new MwJMenuItem("About MwGui");

        // MwJMenuItem openLog = new MwJMenuItem("Open");

        /* Ajouter les choix au menu */
        menu1.add(preferences);
        menu1.addSeparator();
        menu1.add(quit);

        // menu3.add(servo);
        // menu3.add(motor);

        menu4.add(getSerialPortAsMenuItem());
        menu4.add(getSerialBaudAsMenuItem());
        menu4.addSeparator();
        menu4.add(consoleSerial);

        menu5.add(helpContent);
        menu5.add(about);

        /* Ajouter les menus */
        menubar.add(menu1);
        menubar.add(menu3);
        menubar.add(menu4);
        menubar.add(menu5);

        preferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instance.showPreferencesFrame();
            }
        });

        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea textArea = new JTextArea(10, 50);
                textArea.setText(TEXT_ABOUT);
                textArea.setEditable(false);
                textArea.setFocusable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setBorder(BorderFactory
                        .createTitledBorder((String) null));
                JOptionPane.showMessageDialog(instance, scrollPane,
                        "About MwGui", JOptionPane.PLAIN_MESSAGE);
            }
        });

        helpContent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(instance,
                        "https://github.com/treymarc/mwi-swing/wiki",
                        "MwGui Help Contents", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        consoleSerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instance.showDebugFrame();
            }
        });

        // servo.addActionListener(new ActionListener() {
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // if (servoFrame == null) {
        // servoFrame = new LogViewerFrame("Servo", MSP
        // .getRealTimeData(), MwSensorClassServo.class);
        // } else {
        // servoFrame.setVisible(true);
        // }
        // }
        // });

        // motor.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // if (motorFrame == null) {
        // motorFrame = new LogViewerFrame("Motor", MSP
        // .getRealTimeData(), MwSensorClassMotor.class);
        //
        // } else {
        // motorFrame.setVisible(true);
        // }
        // }
        // });

        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopTimer();

                closeSerialPort();
                System.exit(0);
            }
        });

        return menubar;
    }

    protected void showPreferencesFrame() {
        // TODO Auto-generated method stub

        // conf =new MwConfiguration;
        // {
        // MwColor
        // MwResources
        // }

    }

    private MwJPanel getRealTimePanel() {

        if (realTimePanel == null) {

            JButton stopButton = new MwJButton("Stop", "Stop monitoring");
            stopButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    LOGGER.trace("actionPerformed "
                            + e.getSource().getClass().getName() + "\n");
                    stopTimer();
                }
            });

            final MwJComboBox serialRefreshRate = new MwJComboBox(
                    "Refresh rate (hz)",
                    SERIAL_REFRESHRATES.toArray(new Integer[SERIAL_REFRESHRATES
                            .size()]));
            // serialRefreshRate
            // .setMaximumSize(serialRefreshRate.getMinimumSize());
            // serialRefreshRate
            // .setMinimumSize(serialRefreshRate.getMinimumSize());
            serialRefreshRate.setSelectedIndex(3);
            serialRefreshRate.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    if (timer != null) {
                        restartTimer((Integer) serialRefreshRate
                                .getSelectedItem());
                    }
                }
            });

            setRealTimeChart(MwChartFactory.createChart(conf, MSP
                    .getRealTimeData().getDataSet(MwSensorClassIMU.class)));

            MSP.getRealTimeData().addListener(MwSensorClassIMU.class,
                    getChartPanel());

            getChartPanel().setPreferredSize(
                    new java.awt.Dimension(sizeX, sizeY));

            centerChartPanel = new MwJPanel(new BorderLayout());

            centerChartPanel.add(getChartPanel(), BorderLayout.CENTER);
            centerChartPanel.add(getChartCheckBoxPanel(), BorderLayout.EAST);
            centerChartPanel.add(getInstrumentPanel(), BorderLayout.SOUTH);

            realTimePanel = new MwJPanel();
            realTimePanel.setLayout(new BorderLayout());
            realTimePanel.add(centerChartPanel, BorderLayout.CENTER);

            JButton startButton = new MwJButton("Start", "Start monitoring");

            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    LOGGER.trace("actionPerformed "
                            + e.getSource().getClass().getName() + "\n");

                    beginSerialCom();
                    restartTimer((Integer) serialRefreshRate.getSelectedItem());
                    getChartPanel().restoreAutoBounds();
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
        }
        return realTimePanel;
    }

    private JMenuItem getSerialBaudAsMenuItem() {
        JMenu m = new MwJMenu("Baud");
        baudRateMenuGroup = new ButtonGroup();
        for (Integer p : SerialDevice.SERIAL_BAUD_RATE) {
            JRadioButton sm = new JRadioButton(p.toString());
            sm.setActionCommand(p.toString());
            sm.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    // LOGGER.trace("actionPerformed "+
                    // event.getSource().getClass().getName()+"\n");
                    closeSerialPort();
                    try {
                        Object pp = event.getSource();
                        if (pp instanceof JRadioButtonMenuItem) {
                            JRadioButtonMenuItem va = (JRadioButtonMenuItem) pp;
                            if (com != null) {
                                com.setSerialRate(Integer.valueOf(va.getText()));
                                com.openSerialPort();
                                com.setListener(instance);
                            }
                        }
                    } catch (SerialException e) {

                        LOGGER.error(e.getMessage() + "\n");
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

    private MwJPanel getSettingsPanel() {

        if (settingsPanel == null) {
            settingsPanel = new MwJPanel();
            settingsPanel.setLayout(new BorderLayout());

            JButton writeToEepromButton = new MwJButton("Write",
                    "Write to eeprom");

            writeToEepromButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    LOGGER.trace("actionPerformed "
                            + e.getSource().getClass().getName() + "\n");
                    // TODO Write to eeprom
                }
            });

            JButton readFromEepromButton = new MwJButton("Read", "Read eeprom");

            final int[] req = { MSP.BOXNAMES, MSP.PIDNAMES, MSP.RC_TUNING,
                    MSP.PID, MSP.BOX, MSP.MISC };

            readFromEepromButton.addActionListener(new ActionMspSender(req));

            JButton calibGyrButton = new MwJButton("Gyro", "Gyro calibration");
            JButton calibAccButton = new MwJButton("Acc", "Acc calibration");
            JButton calibMagButton = new MwJButton("Mag", "Mag calibration");

            calibAccButton.addActionListener(new ActionMspSender(
                    MSP.ACC_CALIBRATION));
            calibMagButton.addActionListener(new ActionMspSender(
                    MSP.MAG_CALIBRATION));
            // calibGyrButton.addActionListener(new
            // actionMspSender(MSP.MAG_CALIBRATION));

            MwJPanel pane = new MwJPanel();
            pane.setLayout(new FlowLayout(FlowLayout.LEADING));
            pane.setBorder(new EmptyBorder(1, 1, 1, 1));

            MwJPanel pidPane = new MwPIDPanel("PID");
            MSP.setPidChangeListener((ChangeListener) pidPane);
            pane.add(pidPane);

            MwJPanel boxPane = new MwBOXPanel("AUX Box");
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

    @Override
    public void readNewValue(Integer string, int i) {
        switch (string) {
            case MSP.UAVVERSION_KEY:

                inited = true;
                break;

            case MSP.UAVTYPE_KEY:
                uavPanel.setUavType(i);
                break;
            default:
                break;
        }
    }

    @Override
    public void readNewValue(Class<? extends MwSensorClass> sensorClass,
            String name, Double value) {
        MwGuiFrame.addSensorCheckBox(name);
    }

    /**
     * (non-Javadoc)
     * 
     * @see net.fd.gui.AbstractSerialMonitor#message(java.lang.String)
     */
    @Override
    public synchronized void readSerialByte(final int input) {
        MSP.decode(input);
        if (getDebugFrame().isVisible()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    debugFrame.readSerialByte(input);
                }
            });
        }
    }

    @Override
    public void reportSerial(Throwable e) {
        LOGGER.error(I18n.format("error SerialDevice : {0}\n", e.getMessage()));

        stopTimer();
        closeSerialPort();
    }

    @Override
    public void resetAllValues() {
        realTimeChart.resetAllValues();
        hudPanel.resetAllValues();
        compasPanel.resetAllValues();

    }

    public void setRealTimeChart(MwChartPanel realTimeChart1) {
        realTimeChart = realTimeChart1;
    }

    @Override
    public void setTitle(String name) {
        StringBuffer title = new StringBuffer().append(frameTitle);
        if (name != null && name.length() > 0) {
            title.append(" - ").append(name);
        }
        super.setTitle(title.toString());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // TODO Auto-generated method stub

    }

    public static void setConf(MwConfiguration conf1) {
        conf = conf1;

    }

    public static void setColorGraph(int index, Color color) {
        // TODO Auto-generated method stub
        conf.setColorGraph(index, color);
    }
}
