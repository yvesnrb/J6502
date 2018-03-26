package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import machine.Mpu;

public class MainWindow extends JFrame{

	private static final long serialVersionUID = -2542455248044922069L;

	public MainWindow(Mpu machine) {
        this.machine = machine;
        screenIndex = machine.getProgramCounter();
        memoryPanel.setEditable(false);
    }

    Mpu machine;
    int screenIndex;
    
    Font lowerLabelFont    = new Font(Font.MONOSPACED, Font.BOLD, 15);
    Font memoryMonitorFont = new Font(Font.MONOSPACED, Font.PLAIN, 17);
    
    BorderLayout mainWindowLayout  = new BorderLayout();
    GridLayout   lowerPanelLayout  = new GridLayout(1, 2);
    FlowLayout   lowerLeftLayout   = new FlowLayout(FlowLayout.LEFT);
    FlowLayout   lowerRightLayout  = new FlowLayout(FlowLayout.RIGHT);
    
    JEditorPane memoryPanel = new JEditorPane("text/html", "");
    JPanel lowerPanel       = new JPanel();
    JPanel lowerLeftPanel   = new JPanel();
    JPanel lowerRightPanel  = new JPanel();
    
    JMenuBar menubar = new JMenuBar();
    JMenuItem fileMenu = new JMenuItem("File");
    
    JButton stepButton           = new JButton("  Step  ");
    JButton rollbackButton       = new JButton("Rollback");
    JButton nextPageButton       = new JButton("    >   ");
    JButton previousPageButton   = new JButton("   <    ");
    JButton nextPageX5Button     = new JButton("    >>  ");
    JButton previousPageX5Button = new JButton("   <<   ");
    
    JLabel PCLabel    = new JLabel();
    JLabel ACCLabel   = new JLabel();
    JLabel XCCLabel   = new JLabel();
    JLabel YCCLabel   = new JLabel();
    JLabel FlagsLabel = new JLabel();
    
    public void display() {
        this.setLayouts();
        this.setFonts();
        this.addActionListeners();
        this.addItens();
        this.populateLowerLabels();
        this.populateMemoryMonitor();
        this.setSize(1055, 525);
        this.setTitle("J6502 v0.0");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    
    private void addActionListeners() {
    	this.stepButton.addActionListener(e -> this.stepButtonListener());
        this.nextPageButton.addActionListener(e -> this.nextPageButtonListener());
        this.nextPageX5Button.addActionListener(e -> this.nextPageButton5XListener());
        this.previousPageButton.addActionListener(e -> this.previousPageButtonListener());
        this.previousPageX5Button.addActionListener(e -> this.previousPageButton5XListener());
    }
    
    private void setFonts() {
    	PCLabel.setFont(lowerLabelFont);
        ACCLabel.setFont(lowerLabelFont);
        XCCLabel.setFont(lowerLabelFont);
        YCCLabel.setFont(lowerLabelFont);
        FlagsLabel.setFont(lowerLabelFont);
        
        memoryPanel.setFont(memoryMonitorFont);
        memoryPanel.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    }
    
    private void setLayouts() {
    	this.setLayout(mainWindowLayout);
        lowerPanel.setLayout(lowerPanelLayout);
        lowerLeftPanel.setLayout(lowerLeftLayout);
        lowerRightPanel.setLayout(lowerRightLayout);
    }
    
    private void addItens() {
        this.add(memoryPanel, BorderLayout.CENTER);
        this.add(lowerPanel, BorderLayout.SOUTH);
        this.setJMenuBar(menubar);
        
        menubar.add(fileMenu);
          
        lowerPanel.add(lowerLeftPanel);
        lowerPanel.add(lowerRightPanel);
        
        lowerRightPanel.add(PCLabel);
        lowerRightPanel.add(ACCLabel);
        lowerRightPanel.add(XCCLabel);
        lowerRightPanel.add(YCCLabel);
        lowerRightPanel.add(FlagsLabel);
        
        lowerLeftPanel.add(previousPageX5Button);
        lowerLeftPanel.add(previousPageButton);
        lowerLeftPanel.add(stepButton);
        lowerLeftPanel.add(rollbackButton);
        lowerLeftPanel.add(nextPageButton);
        lowerLeftPanel.add(nextPageX5Button);
        
    }
    
    private void populateLowerLabels() {
        PCLabel.setText("PC: " + String.format("0x%04X", machine.getProgramCounter()));
        ACCLabel.setText("ACC: " + String.format("0x%02X", machine.getAcc()));
        XCCLabel.setText("XCC: " + String.format("0x%02X", machine.getXcc()));
        YCCLabel.setText("YCC: " + String.format("0x%02X", machine.getYcc()));
        this.assembleFlagsLabel();
    }
    
    private void assembleFlagsLabel() {
        String labelText = "<HTML>FLAGS: ";
        if(machine.getFlags(machine.NEGATIVE_FLAG)) {
            labelText += "<font color='red'>N</font>";
        } else {
            labelText += "N";
        }
        if(machine.getFlags(machine.OVERFLOW_FLAG)) {
            labelText += "<font color='red'>V</font>";
        } else {
            labelText += "V";
        }
        if(machine.getFlags(machine.BREAK_FLAG)) {
            labelText += "-<font color='red'>B</font>";
        } else {
            labelText += "-B";
        }
        if(machine.getFlags(machine.DECIMAL_FLAG)) {
            labelText += "<font color='red'>D</font>";
        } else {
            labelText += "D";
        }
        if(machine.getFlags(machine.INTERUPT_FLAG)) {
            labelText += "<font color='red'>I</font>";
        } else {
            labelText += "I";
        }
        if(machine.getFlags(machine.ZERO_FLAG)) {
            labelText += "<font color='red'>Z</font>";
        } else {
            labelText += "Z";
        }
        if(machine.getFlags(machine.CARRY_FLAG)) {
            labelText += "<font color='red'>C</font>";
        } else {
            labelText += "C";
        }
        
        FlagsLabel.setText(labelText);
    }
    
    private void populateMemoryMonitor() {
        String labelText = "<HTML>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp 00 &nbsp&nbsp 01 &nbsp&nbsp 02 &nbsp&nbsp 03 &nbsp&nbsp 04 &nbsp&nbsp 05 &nbsp&nbsp 06 &nbsp&nbsp 07 &nbsp&nbsp 08 &nbsp&nbsp 09 &nbsp&nbsp 0A &nbsp&nbsp 0B &nbsp&nbsp 0C &nbsp&nbsp 0D &nbsp&nbsp 0E &nbsp&nbsp 0F<br/>";
        int addressColumn = this.screenIndex;
        int[] lineDump;
        for (int i = 0; i <= 15; i++) {
            String line = "";
            line += String.format("0x%04X", addressColumn) + ": ";
            lineDump = machine.getMemoryInterval(addressColumn, addressColumn + 0x0F);
            for (int j = 0; j < 16; j++) {
                if (addressColumn + j == machine.getProgramCounter()) {
                    line += "<font color='red'>" + String.format("%02X", lineDump[j]) + "</font>" +" &nbsp&nbsp ";
                    continue;
                }
                line += String.format("%02X", lineDump[j]) + " &nbsp&nbsp ";
            }
            labelText += line + "<br/>";
            addressColumn += 0x10;
        }
        
        memoryPanel.setText(labelText);
    }
    
    private void stepButtonListener() {
        machine.step();
        populateLowerLabels();
        populateMemoryMonitor();
    }
    
    
    private void nextPageButtonListener() {
        if (screenIndex >= 0xFF00) {
            screenIndex = 0x0000;
        } else {
            screenIndex += 0x0100;
        }
        populateMemoryMonitor();
    }
        
    private void nextPageButton5XListener() {
        if (screenIndex >= 0xFB00) {
            screenIndex = 0x0000;
        } else {
            screenIndex += 0x0500;
        }
        populateMemoryMonitor();
    }
        
    private void previousPageButtonListener() {
        if (screenIndex == 0x0000) {
            screenIndex = 0xFF00;
        } else {
            screenIndex -= 0x100;
        }
        populateMemoryMonitor();
    }
       
    private void previousPageButton5XListener() {
        if (screenIndex <= 0x0500) {
            screenIndex = 0x0000;
        } else {
            screenIndex -= 0x500;
        }
        populateMemoryMonitor();
    }  
}
