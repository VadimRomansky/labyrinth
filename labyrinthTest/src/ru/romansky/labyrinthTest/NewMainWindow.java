package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;

public class NewMainWindow {
    private JFrame myFrame;
    private CardLayout myLayout;
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel startGamePanel;
    private JPanel optionsPanel;
    private JPanel generatorPanel;
    private JPanel simpleGamePanel;
    private JPanel generateMapPanel;
    private JButton generateButton;
    private JLabel widthLabel;
    private JComboBox widthComboBox;
    private JLabel heightLabel;
    private JComboBox heightComboBox;
    private JCheckBox generateCycleCheckBox;
    private JCheckBox generateStopAfterCheckBox;
    private JLabel generateMinRegionLabel;
    private JComboBox generateMinRegionSizeComboBox;
    private JLabel stoppingProbabilityLabel;
    private JTextField stoppingProbabilityTextField;
    private JLabel branchingProbabilityLabel;
    private JTextField branchingProbabilityTextField;
    private JLabel minotaususLabel;
    private JComboBox generateMinotaursComboBox;
    private JLabel portalsLabel;
    private JComboBox generatePortalsComboBox;
    private JLabel maxRegionLabel;
    private JComboBox maxRegionComboBox;
    private JButton newGameButton;
    private JButton generatorButton;
    private JButton optionsButton;
    private JButton exitButton;
    private JButton generateBackButton;
    private JLabel sizeLabel;
    private JComboBox sizeComboBox;
    private JLabel difficultyLabel;
    private JComboBox difficultyComboBox;
    private JLabel startMinotaursLabel;
    private JComboBox startMinotaursComboBox;
    private JLabel startPortalsLabel;
    private JComboBox startPortalsComboBox;
    private JButton startSimpleButton;
    private JButton startReturnButton;
    private JPanel simpleGameMapPanel;
    private JButton optionsBackButton;
    private JButton simpleGameReturnButton;
    private JButton simpleGameExitButton;
    private JPanel helpPanel;
    private JButton helpBackButton;
    private JButton helpButton;
    private JButton startClassicButton;
    private JPanel classicGamePanel;
    private JPanel classicGameMapPanel;
    private JButton classicGameReturnButton;
    private JButton classicGameExitButton;
    private JTextArea simpleGameTextArea;
    private JScrollPane simpleGameTextScrollPane;

    public NewMainWindow(){
        mainPanel.removeAll();
        mainPanel.add(menuPanel,"menu");
        mainPanel.add(startGamePanel,"startgame");
        mainPanel.add(optionsPanel,"options");
        mainPanel.add(helpPanel,"help");
        mainPanel.add(generatorPanel,"generate");
        mainPanel.add(simpleGamePanel,"simplegame");
        mainPanel.add(classicGamePanel,"classicgame");
        myLayout = (CardLayout) mainPanel.getLayout();

        ////menu
        setupMenuPanel();
        ////start game
        setupStartGamePanel();
        ///options
        setupOptionsPanel();
        //help
        setupHelpPanel();
        ////simple game
        setupSimpleGamePanel();
        ////classic game
        setupClassicGamePanel();

        ///generator
        setupGeneratorPanel();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        JFrame frame = new JFrame("labyrinth");
        myFrame = frame;
        //myLayout = new CardLayout();
        mainPanel = new JPanel(new CardLayout());


        menuPanel = new JPanel();
        startGamePanel = new JPanel();
        optionsPanel = new JPanel();
        helpPanel = new JPanel();
        generatorPanel = new JPanel();
        simpleGamePanel = new JPanel();
        classicGamePanel = new JPanel();


        //mainPanel.add(menuPanel,"menu");
        //mainPanel.add(startGamePanel,"startgame");
        //mainPanel.add(optionsPanel,"options");
        //mainPanel.add(generatorPanel,"generate");
        //mainPanel.add(simpleGamePanel,"game");

        //simpleGameMapPanel = new MapPanel(myFrame, mainPanel);
        simpleGameMapPanel = new SimpleGamePanel(myFrame, simpleGamePanel);
        generateMapPanel = new MapPanel(myFrame, generateMapPanel);
        //simpleGameMapPanel.setFocusable(true);
        frame.setFocusable( true );
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println(e.getKeyCode());
                ((MapPanelBase) simpleGameMapPanel).keyPressed(e);
            }
        });
    }

    public void show(){
        myFrame.setMinimumSize(new Dimension(600,600));
        myFrame.setBounds(400,300,600,600);
        mainPanel.setSize(new Dimension(600, 600));




        myFrame.setContentPane(mainPanel);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.pack();
        Locale.setDefault(Locale.UK);
        myFrame.setVisible(true);


    }

    private void setupSimpleGamePanel() {
        //simpleGamePanel.setSize(600, 500);
        simpleGameTextScrollPane.setSize(600,200);
        //simpleGameTextArea.setSize(600,100);
        ((SimpleGamePanel)simpleGameMapPanel).setTextArea(simpleGameTextArea);

        simpleGameReturnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel, "startgame");
            }
        });

        simpleGameExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.dispose();
            }
        });
    }

    private void setupClassicGamePanel() {
        //classicGamePanel.setSize(600, 500);

        classicGameReturnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel, "startgame");
            }
        });

        classicGameExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.dispose();
            }
        });
    }

    private void setupGeneratorPanel() {
        generateMapPanel.setSize(600, 500);
        for(int i = 6; i < 16; ++i){
            heightComboBox.addItem(new Integer(i));
            widthComboBox.addItem(new Integer(i));
            generatePortalsComboBox.addItem(new Integer(i));
        }
        widthComboBox.setSelectedIndex(4);
        heightComboBox.setSelectedIndex(4);
        generatePortalsComboBox.setSelectedIndex(4);
        for(int i = 2; i < 6; ++i){
            generateMinRegionSizeComboBox.addItem(new Integer(i));
        }
        generateMinRegionSizeComboBox.setSelectedIndex(3);
        for(int i = 1; i < 11; ++i){
            maxRegionComboBox.addItem(new Integer(i));
        }
        maxRegionComboBox.setSelectedIndex(5);
        generateMinotaursComboBox.addItem("zero");
        generateMinotaursComboBox.addItem("low");
        generateMinotaursComboBox.addItem("middle");
        generateMinotaursComboBox.addItem("high");
        generateMinotaursComboBox.addItem("they are everywhere!");
        generateMinotaursComboBox.setSelectedIndex(2);
        stoppingProbabilityTextField.setText("0.1");
        branchingProbabilityTextField.setText("0.1");
        generateCycleCheckBox.setSelected(true);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.requestFocus();
                int width = (Integer) widthComboBox.getItemAt(widthComboBox.getSelectedIndex());
                int height = (Integer) heightComboBox.getItemAt(heightComboBox.getSelectedIndex());
                int minSize = (Integer) generateMinRegionSizeComboBox.getItemAt(generateMinRegionSizeComboBox.getSelectedIndex());
                int portals = (Integer) generatePortalsComboBox.getItemAt(generatePortalsComboBox.getSelectedIndex());
                int maxRegions = (Integer) maxRegionComboBox.getItemAt(maxRegionComboBox.getSelectedIndex());
                int minotaursType = generateMinotaursComboBox.getSelectedIndex();
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
                boolean allowCycle = generateCycleCheckBox.isSelected();
                boolean stopAfterCycle = generateStopAfterCheckBox.isSelected();
                try {
                    String stopString = stoppingProbabilityTextField.getText();
                    double stopP = Double.parseDouble(stopString);
                    String branchString = branchingProbabilityTextField.getText();
                    double branchP = Double.parseDouble(branchString);

                    if(stopP < 0.0 || stopP > 1.0){

                    } else if(branchP < 0.0 || branchP > 1.0){

                    } else {
                        MapGenerator mapGenerator = new MapGenerator(width, height, minSize, stopP, branchP, allowCycle, stopAfterCycle, minotaurs, portals, maxRegions, (MapPanelBase) generateMapPanel);
                        final LabyrinthMap map = mapGenerator.generateEmptyMap();
                        mapGenerator.generateMap(map);
                        ((MapPanelBase) generateMapPanel).resetMap(map);

                        //map.print();
                        generateMapPanel.repaint();
                    }
                } catch (NumberFormatException ne){

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });

        generateBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel, "menu");
            }
        });
    }

    private void setupHelpPanel() {
        helpBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel, "menu");
            }
        });
    }

    private void setupOptionsPanel() {
        optionsBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"menu");
            }
        });
    }

    private void setupStartGamePanel() {
        simpleGameMapPanel.setSize(600, 500);
        for(int i = 6; i < 16; ++i){
            sizeComboBox.addItem(new Integer(i));
        }
        sizeComboBox.setSelectedIndex(4);

        difficultyComboBox.addItem("low");
        difficultyComboBox.addItem("middle");
        difficultyComboBox.addItem("high");
        difficultyComboBox.addItem("very high");

        startMinotaursComboBox.addItem("zero");
        startMinotaursComboBox.addItem("low");
        startMinotaursComboBox.addItem("middle");
        startMinotaursComboBox.addItem("high");
        startMinotaursComboBox.addItem("they are everywhere!");

        startPortalsComboBox.addItem("low");
        startPortalsComboBox.addItem("middle");
        startPortalsComboBox.addItem("high");

        startSimpleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.requestFocus();
                int width = (Integer) sizeComboBox.getItemAt(sizeComboBox.getSelectedIndex());
                int height = width;
                int difficulty = difficultyComboBox.getSelectedIndex();

                double stopP = 0;
                double branchP = 0;
                int minSize = 2;
                int maxRegions = 1;
                boolean allowCycle = false;
                boolean stopAfterCycle = false;

                switch (difficulty){
                    case 1:
                        stopP = 0.1;
                        branchP = 0.05;
                        minSize = 2*width;
                        maxRegions = (width*2)/3;
                        allowCycle = true;
                        stopAfterCycle = true;
                        break;
                    case 2:
                        stopP = 0.1;
                        branchP = 0.1;
                        minSize = width*height/5;
                        maxRegions = (width*2)/3;
                        allowCycle = true;
                        stopAfterCycle = false;
                        break;
                    case 3:
                        stopP = 0.05;
                        branchP = 0.2;
                        minSize = width*height/5;
                        maxRegions = width/2;
                        allowCycle = true;
                        stopAfterCycle = false;
                        break;
                    default:
                        stopP = 0.2;
                        branchP = 0.01;
                        minSize = width;
                        maxRegions = width;
                        allowCycle = false;
                        stopAfterCycle = false;
                        break;
                }

                int portals = 0;
                int portalsType = startPortalsComboBox.getSelectedIndex();
                switch (portalsType){
                    case 1:
                        portals = width/2 + width*height/20;
                        break;
                    case 2:
                        portals = width/2 + width*height/10;
                        break;
                    default:
                        portals = width/2 + 1;
                        break;
                }

                int minotaurs = 0;

                int minotaursType = startMinotaursComboBox.getSelectedIndex();
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

                try {

                    if(stopP < 0.0 || stopP > 1.0){

                    } else if(branchP < 0.0 || branchP > 1.0){

                    } else {
                        MapGenerator mapGenerator = new MapGenerator(width, height, minSize, stopP, branchP, allowCycle, stopAfterCycle, minotaurs, portals, maxRegions, (MapPanelBase) simpleGameMapPanel);
                        final LabyrinthMap map = mapGenerator.generateEmptyMap();
                        mapGenerator.generateMap(map);
                        ((MapPanelBase) simpleGameMapPanel).resetMap(map);
                        ((SimpleGamePanel) simpleGameMapPanel).restart();

                        //map.print();
                        simpleGameMapPanel.repaint();
                    }
                } catch (NumberFormatException ne){

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }


                myLayout.show(mainPanel,"simplegame");
                myFrame.requestFocus();
            }
        });

        startClassicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel, "classicgame");
                myFrame.requestFocus();
            }
        });

        startReturnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"menu");
            }
        });
    }

    private void setupMenuPanel() {
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"startgame");
            }
        });

        generatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"generate");
            }
        });

        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"options");
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"help");
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.dispose();
            }
        });
    }
}
