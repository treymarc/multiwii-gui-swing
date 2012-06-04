package eu.kprod;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;

import eu.kprod.gui.DebugFrame;
import eu.kprod.gui.MwiChartFactory;
import eu.kprod.serial.SerialCom;
import eu.kprod.serial.SerialDevice;
import eu.kprod.serial.SerialException;
import eu.kprod.serial.SerialListener;
import eu.kprod.serial.SerialNotFoundException;
import gnu.io.CommPortIdentifier;

public class MwGuiFrame extends JFrame implements SerialListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MwGuiFrame.class);

    class SerialTimeOut extends TimerTask {

        public void run() {
            try {
                // requestMSP(MSP.ATTITUDE);
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

                MwGuiFrame frame = new MwGuiFrame();
                MwGuiFrame.serialListener = frame;

                frame.setVisible(true);
            }
        });

    }

    private JButton startButton;
    private JButton stopButton;

    private JComboBox serialPorts;
    private JComboBox serialRates;

    private static SerialCom com;
    private static SerialListener serialListener;

    private Timer timer;

    private static DebugFrame debugFrame;
    private ChartPanel chartTrendPanel;
    private JPanel overviewPanel;

    private JPanel getOverviewPanel() {

        if (overviewPanel == null) {
            chartTrendPanel = new ChartPanel(MwiChartFactory.createChart(MSP
                    .getModel().getDs()));
            chartTrendPanel.setPreferredSize(new java.awt.Dimension(500, 270));

            overviewPanel = new JPanel();
            overviewPanel.setLayout(new BorderLayout());
            overviewPanel.setBorder(new EmptyBorder(1, 1, 1, 1));

            overviewPanel.add(chartTrendPanel, BorderLayout.CENTER);
        }

        return overviewPanel;
    }

    public MwGuiFrame() {
        super();

        MSP.setModel(new DataMwiiModel());

        super.setTitle("MwGui - v0.0.1");

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
        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
        pane.setBorder(new EmptyBorder(1, 1, 1, 1));

        startButton = new JButton(("Start"));
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logger.trace("actionPerformed "
                        + e.getSource().getClass().getName());

                if (timer != null) {
                    timer.cancel();
                    // timer.purge();

                }
                timer = new Timer();

                timer.schedule(new SerialTimeOut(), 0, 80);

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
            }
        });

        pane.add(stopButton);
        pane.add(Box.createRigidArea(new Dimension(1, 0)));
        pane.add(startButton);

        getContentPane().add(pane, BorderLayout.NORTH);
        getContentPane().add(getOverviewPanel(), BorderLayout.CENTER);

        pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
        pane.setBorder(new EmptyBorder(1, 1, 1, 1));

        List<String> portNames = new ArrayList<String>();

        portNames.add("");
        for (@SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> enumeration = CommPortIdentifier
        .getPortIdentifiers(); enumeration.hasMoreElements();) {
            CommPortIdentifier commportidentifier = enumeration.nextElement();
            // System.out.println("Found communication port: " +
            // commportidentifier);
            if (commportidentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                // System.out.println("Adding port to serial port menu: " +
                // commportidentifier);
                String curr_port = commportidentifier.getName();
                portNames.add(curr_port);
            }
        }

        serialPorts = new JComboBox(portNames.toArray());
        serialPorts.setMaximumSize(serialPorts.getMinimumSize());
        serialPorts.setSelectedIndex(0);
        serialPorts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                logger.trace("actionPerformed "
                        + event.getSource().getClass().getName());

                if (com != null) {
                    com.closeSerialPort();
                }
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
        });

        serialRates = new JComboBox();

        for (Integer entry : SerialDevice.SerialRateStrings) {
            serialRates.addItem(entry);
        }

        serialRates.setSelectedIndex(10);
        serialRates.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                logger.trace("actionPerformed "
                        + event.getSource().getClass().getName());

                if (com != null) {
                    com.closeSerialPort();
                }
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

        pane.add(serialPorts);
        pane.add(Box.createRigidArea(new Dimension(1, 0)));
        pane.add(serialRates);

        getContentPane().add(pane, BorderLayout.SOUTH);

        pack();
    }

    public static SerialListener getInstance() {
        return serialListener;
    }

    public static JFrame getDebugFrame() {
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

        // JMenuItem openLog = new JMenuItem("Open");

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
        debug.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MwGuiFrame.showDebugFrame();
            }
        });

        /* clic sur le choix Fin du menu fichier */
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (com != null) {
                    com.closeSerialPort();
                }
                System.exit(0);
            }
        });

        return menubar;
    }

    // send string
    synchronized private void send(String s) {
        if (com != null) {
            com.send(s, 0 /* lineEndings.getSelectedIndex() */);
        }
    }

    /*
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

}
