package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

public class MainWindow {
    private JFrame myFrame;
    private JPanel mainPanel;
    private JPanel mapPanel;
    private JComboBox widthComboBox;
    private JComboBox heightComboBox;
    private JLabel widthLabel;
    private JLabel heightLabel;
    private JButton generateButton;
    private JCheckBox allowCycleButton;
    private JCheckBox stopAfterCycleButton;
    private JTextField stopProbabilityText;
    private JLabel stopProbabilityLabel;
    private JLabel branchingProbabilityLabel;
    private JTextField branchingProbabilityText;
    private JLabel mregionSizeLabel;
    private JComboBox regionSizeComboBox;
    private JLabel minotaurLabel;
    private JComboBox minotaurCombobox;
    private JLabel portalLabel;
    private JComboBox portalsComboBox;

    public MainWindow(){

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        JFrame frame = new JFrame("labyrinth");
        myFrame = frame;
        mainPanel = new JPanel();
        mapPanel = new MapPanel(myFrame, mainPanel);
    }

    public void show(){
        myFrame.setMinimumSize(new Dimension(600,600));
        myFrame.setBounds(400,300,600,600);
        mainPanel.setSize(new Dimension(600, 700));
        mapPanel.setSize(600, 600);
        for(int i = 6; i < 16; ++i){
            heightComboBox.addItem(new Integer(i));
            widthComboBox.addItem(new Integer(i));
            portalsComboBox.addItem(new Integer(i));
        }
        widthComboBox.setSelectedIndex(4);
        heightComboBox.setSelectedIndex(4);
        portalsComboBox.setSelectedIndex(4);
        for(int i = 2; i < 5; ++i){
            regionSizeComboBox.addItem(new Integer(i));
        }
        minotaurCombobox.addItem("zero");
        minotaurCombobox.addItem("low");
        minotaurCombobox.addItem("middle");
        minotaurCombobox.addItem("high");
        minotaurCombobox.addItem("they are everywhere!");
        minotaurCombobox.setSelectedIndex(2);
        stopProbabilityText.setText("0.1");
        branchingProbabilityText.setText("0.1");

        myFrame.setContentPane(mainPanel);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.pack();
        Locale.setDefault(Locale.UK);
        myFrame.setVisible(true);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int width = (Integer) widthComboBox.getItemAt(widthComboBox.getSelectedIndex());
                int height = (Integer) heightComboBox.getItemAt(heightComboBox.getSelectedIndex());
                int minSize = (Integer) regionSizeComboBox.getItemAt(regionSizeComboBox.getSelectedIndex());
                int portals = (Integer) portalsComboBox.getItemAt(portalsComboBox.getSelectedIndex());
                int minotaursType = minotaurCombobox.getSelectedIndex();
                int minotaurs = 0;
                switch (minotaursType){
                    case 1: minotaurs = width*height/12;
                    break;
                    case 2: minotaurs = width*height/9;
                    break;
                    case 3: minotaurs = width*height/6;
                    break;
                    case 4: minotaurs = width*height/3;
                    break;
                    default: minotaurs = 0;
                    break;
                }
                boolean allowCycle = allowCycleButton.isSelected();
                boolean stopAfterCycle = stopAfterCycleButton.isSelected();
                try {
                    String stopString = stopProbabilityText.getText();
                    double stopP = Double.parseDouble(stopString);
                    String branchString = branchingProbabilityText.getText();
                    double branchP = Double.parseDouble(branchString);

                    if(stopP < 0.0 || stopP > 1.0){

                    } else if(branchP < 0.0 || branchP > 1.0){

                    } else {
                        MapGenerator mapGenerator = new MapGenerator(width, height, minSize, stopP, branchP, allowCycle, stopAfterCycle, minotaurs, portals, (MapPanel) mapPanel);
                        final LabyrinthMap map = mapGenerator.generateEmptyMap();
                        ((MapPanel)mapPanel).resetMap(map);
                        mapGenerator.generateMap(map);
                        //map.print();
                        mapPanel.repaint();
                    }
                } catch (NumberFormatException ne){

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}
