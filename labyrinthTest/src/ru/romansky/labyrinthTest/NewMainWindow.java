package ru.romansky.labyrinthTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;

enum MainWindowMode {MENU, OPTIONS, HELP, GENERATOR, STARTGAME, SIMPLEGAME, CLASSICGAME};

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
    private JTextArea classicGameTextArea;
    private JScrollPane classicGameTextScrollPane;
    private JList miniMapList;

    private MainWindowMode myMode;

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

        myMode = MainWindowMode.MENU;

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
        classicGameMapPanel = new ClassicGamePanel(myFrame, classicGamePanel);
        generateMapPanel = new MapPanel(myFrame, generateMapPanel);

        miniMapList = new JList<LabyrinthMap>();
        miniMapList.setFixedCellHeight(-1);
        ListCellRenderer<LabyrinthMap> listRender = new ListCellRenderer<LabyrinthMap>() {

            @Override
            public Component getListCellRendererComponent(
                    JList<? extends LabyrinthMap> list, LabyrinthMap value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JPanel panel = new MiniMapPanel(value);
                panel.setBackground(Color.WHITE);
                int hight = (value.height + 2)*(MiniMapPanel.cellWidth + MiniMapPanel.borderWidth);
                panel.setBounds(0, 0, miniMapList.getWidth() - 20, hight);
                panel.setPreferredSize(new Dimension(miniMapList.getWidth() -20, hight));
                return panel;
            }
        };
        DefaultListModel<LabyrinthMap> listMod = new DefaultListModel<LabyrinthMap>();
        miniMapList = new JList<LabyrinthMap>(listMod);
        miniMapList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        miniMapList.setCellRenderer(listRender);

        frame.setFocusable( true );
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println(e.getKeyCode());
                if(myMode == MainWindowMode.SIMPLEGAME) {
                    ((MapPanelBase) simpleGameMapPanel).keyPressed(e);
                } else if(myMode == MainWindowMode.CLASSICGAME){
                    ((MapPanelBase) classicGameMapPanel).keyPressed(e);
                }
            }
        });
    }

    public void show(){
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(myFrame.getGraphicsConfiguration());
        int taskBarSize = scnMax.bottom;
        myFrame.setMinimumSize(new Dimension(600,600));
        myFrame.setSize(screenSize.width, screenSize.height - taskBarSize);
        myFrame.setPreferredSize(new Dimension(screenSize.width,screenSize.height - taskBarSize));
        myFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        //mainPanel.setSize(new Dimension(600, 600));




        myFrame.setContentPane(mainPanel);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.pack();
        Locale.setDefault(Locale.UK);
        myFrame.setVisible(true);


    }

    private void setupSimpleGamePanel() {
        //simpleGamePanel.setSize(600, 500);
        simpleGameTextScrollPane.setPreferredSize(new Dimension(600, 50));
        //simpleGameTextScrollPane.revalidate();
        //simpleGameTextArea.setSize(600,100);
        ((SimpleGamePanel)simpleGameMapPanel).setTextArea(simpleGameTextArea);

        simpleGameReturnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel, "startgame");
                myMode = MainWindowMode.STARTGAME;
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

        classicGameTextScrollPane.setPreferredSize(new Dimension(600, 50));
        ((ClassicGamePanel)classicGameMapPanel).setTextArea(classicGameTextArea);
        ((ClassicGamePanel)classicGameMapPanel).setMiniMapList(miniMapList, (DefaultListModel<LabyrinthMap>) miniMapList.getModel());

        classicGameReturnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel, "startgame");
                myMode = MainWindowMode.STARTGAME;
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
                myMode = MainWindowMode.MENU;
            }
        });
    }

    private void setupHelpPanel() {
        helpBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel, "menu");
                myMode = MainWindowMode.MENU;
            }
        });
    }

    private void setupOptionsPanel() {
        optionsBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"menu");
                myMode = MainWindowMode.MENU;
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
                MapGeneratorInfo mapGeneratorInfo = collectGeneratorInfo();
                try {
                        MapGenerator mapGenerator = new MapGenerator(mapGeneratorInfo, (MapPanelBase) simpleGameMapPanel);
                        final LabyrinthMap map = mapGenerator.generateEmptyMap();
                        mapGenerator.generateMap(map);
                        ((MapPanelBase) simpleGameMapPanel).resetMap(map);
                        ((SimpleGamePanel) simpleGameMapPanel).restart();

                        //map.print();
                        simpleGameMapPanel.repaint();

                } catch (NumberFormatException ne){

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }


                myLayout.show(mainPanel,"simplegame");
                myMode = MainWindowMode.SIMPLEGAME;
                myFrame.requestFocus();
            }
        });

        startClassicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.requestFocus();
                MapGeneratorInfo mapGeneratorInfo = collectGeneratorInfo();
                try {
                    MapGenerator mapGenerator = new MapGenerator(mapGeneratorInfo, (MapPanelBase) classicGameMapPanel);
                    final LabyrinthMap map = mapGenerator.generateEmptyMap();
                    mapGenerator.generateMap(map);
                    ((MapPanelBase) classicGameMapPanel).resetMap(map);
                    ((ClassicGamePanel) classicGameMapPanel).restart();

                    //map.print();
                    classicGameMapPanel.repaint();

                } catch (NumberFormatException ne){

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                myLayout.show(mainPanel, "classicgame");
                myMode = MainWindowMode.CLASSICGAME;
                myFrame.requestFocus();
            }
        });

        startReturnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"menu");
                myMode = MainWindowMode.MENU;
            }
        });
    }

    public MapGeneratorInfo collectGeneratorInfo() {
        MapGeneratorInfo info = new MapGeneratorInfo();
        info.width = (Integer) sizeComboBox.getItemAt(sizeComboBox.getSelectedIndex());
        info.height = info.width;


        int difficulty = difficultyComboBox.getSelectedIndex();
        switch (difficulty) {
            case 1:
                info.stopP = 0.1;
                info.branchP = 0.05;
                info.minSize = 2 * info.width;
                info.maxRegions = (info.width * 2) / 3;
                info.allowCycle = true;
                info.stopAfterCycle = true;
                break;
            case 2:
                info.stopP = 0.1;
                info.branchP = 0.1;
                info.minSize = info.width * info.height / 5;
                info.maxRegions = (info.width * 2) / 3;
                info.allowCycle = true;
                info.stopAfterCycle = false;
                break;
            case 3:
                info.stopP = 0.05;
                info.branchP = 0.2;
                info.minSize = info.width * info.height / 5;
                info.maxRegions = info.width / 2;
                info.allowCycle = true;
                info.stopAfterCycle = false;
                break;
            default:
                info.stopP = 0.2;
                info.branchP = 0.01;
                info.minSize = info.width;
                info.maxRegions = info.width;
                info.allowCycle = false;
                info.stopAfterCycle = false;
                break;
        }


        int portalsType = startPortalsComboBox.getSelectedIndex();
        switch (portalsType) {
            case 1:
                info.portals = info.width / 2 + info.width * info.height / 20;
                break;
            case 2:
                info.portals = info.width / 2 + info.width * info.height / 10;
                break;
            default:
                info.portals = info.width / 2 + 1;
                break;
        }


        int minotaursType = startMinotaursComboBox.getSelectedIndex();
        switch (minotaursType) {
            case 1:
                info.minotaurs = info.width * info.height / 12;
                break;
            case 2:
                info.minotaurs = info.width * info.height / 9;
                break;
            case 3:
                info.minotaurs = info.width * info.height / 6;
                break;
            case 4:
                info.minotaurs = info.width * info.height / 3;
                break;
            default:
                info.minotaurs = 0;
                break;
        }
        return info;
    }

    private void setupMenuPanel() {
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"startgame");
                myMode = MainWindowMode.STARTGAME;
            }
        });

        generatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"generate");
                myMode = MainWindowMode.GENERATOR;
            }
        });

        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"options");
                myMode = MainWindowMode.OPTIONS;
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel,"help");
                myMode = MainWindowMode.HELP;
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
