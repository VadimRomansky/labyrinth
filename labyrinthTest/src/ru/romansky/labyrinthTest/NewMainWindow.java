package ru.romansky.labyrinthTest;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

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
    //private JButton startSimpleButton;
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
    private JScrollPane miniMapScrollPane;
    private JPanel miniMapPanel;
    private JPanel textHelpPanel;
    private JLabel helpIntroLabel;
    private JTextArea helpIntroTextArea;
    private JPanel helpLabyrinthPanel;
    private JLabel helpControlLabel;
    private JTextArea helpControlsTextArea;
    private JLabel helpObjectsLabel;
    private JPanel helpObjectsPanel;
    private JLabel helpCollectMapLabel;
    private JTextArea helpCollectMapTextArea;
    private JPanel helpCollectMapPanel;
    private JLabel helpMainScreenLabel;
    private JTextArea helpMainScreenTextArea;
    private JPanel helpMainScreenPanel;
    private JButton helpExitButton;

    LinkedBlockingQueue<GameEvent> fromClientToServer;
    LinkedBlockingQueue<ServerEvent> fromServerToClient;
    private MainWindowMode myMode;
    Thread engineThread;

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

        fromServerToClient = new LinkedBlockingQueue<>();
        fromClientToServer = new LinkedBlockingQueue<>();
        simpleGameMapPanel = new SimpleGamePanel(myFrame, simpleGamePanel);
        classicGameMapPanel = new ClassicGamePanel(myFrame, classicGamePanel, fromClientToServer, fromServerToClient);
        generateMapPanel = new MapPanel(myFrame, generateMapPanel);



        classicGameMapPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    if(myMode == MainWindowMode.CLASSICGAME) {
                        ((ClassicGamePanel) classicGameMapPanel).stopDragMiniMap();
                        return;
                    }
                }
                if(SwingUtilities.isLeftMouseButton(e)) {
                    if(myMode == MainWindowMode.CLASSICGAME){
                        ClassicGamePanel classicGamePanel = (ClassicGamePanel) classicGameMapPanel;
                        if(classicGamePanel.isDragMiniMap()){
                            Point point = e.getPoint();
                            classicGamePanel.tryAddMiniMap(point);
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                mouseClicked(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


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

        URL labyrinthFile = getClass().getResource("/labyrinth.png");
        try {
            final Image image = ImageIO.read(labyrinthFile);
            helpLabyrinthPanel = new JPanel(){
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(image, 0, 0, null);
                }
            };
            helpLabyrinthPanel.setPreferredSize(new Dimension(-1, image.getHeight(null) + 20));
        } catch (IOException e) {
            e.printStackTrace();
            helpLabyrinthPanel = new JPanel();
        }

        URL mainScreenFile = getClass().getResource("/main_screen.png");
        try {
            final Image image = ImageIO.read(mainScreenFile);
            helpMainScreenPanel = new JPanel(){
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(image, 0, 0, null);
                }
            };
            helpMainScreenPanel.setPreferredSize(new Dimension(-1, image.getHeight(null) + 20));
        } catch (IOException e) {
            e.printStackTrace();
            helpMainScreenPanel = new JPanel();
        }


        URL minotaurFile = getClass().getResource("/minotaur.png");
        try {
            final Image image = ImageIO.read(minotaurFile);
            helpObjectsPanel = new JPanel(){
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(image, 0, 0, null);
                    int imageWidth = image.getWidth(null);
                    int imageHeight = image.getHeight(null);
                    g.setColor(Color.BLACK);
                    Graphics2D g2d = (Graphics2D) g;
                    FontMetrics fm = g2d.getFontMetrics();
                    String text = "minotaurs are guards of labyrinth, they will kill you if you will step on them. After that you will lose all your equipment and respawn into the hospital";
                    g2d.drawString(text, 3*imageWidth/2, fm.getAscent());

                    g.setColor(Color.BLUE);
                    text = "A";
                    g2d.drawString(text, imageWidth/2, imageHeight + fm.getAscent());
                    g.setColor(Color.BLACK);
                    text = "you can take new bullets in the arsenal. It is guaranted, that you can find a way from hospital to arsenal without minotaurs";
                    g2d.drawString(text, 3*imageWidth/2, imageHeight + fm.getAscent());
                    g.setColor(Color.RED);
                    int hlength = imageWidth*3/4;
                    int hwidth = imageWidth*1/4;
                    g2d.fillRect(imageWidth/2 - hlength/2, 5*imageHeight/2 - hwidth/2 , hlength, hwidth);
                    g2d.fillRect(imageWidth/2 - hwidth/2, 5*imageHeight/2 - hlength/2 , hwidth, hlength);
                    g.setColor(Color.BLACK);
                    text = "you will respawn in the hospital after every accident";
                    g2d.drawString(text, 3*imageWidth/2, 2*imageHeight + fm.getAscent());

                    int radius = imageWidth/3;
                    g2d.fill(new Ellipse2D.Double(imageWidth/2 - radius, 7*imageHeight/2 - radius, 2*radius, 2*radius));
                    text = "portal will teleport you to another determined portal somwhere in the labyrinth. All portals are connected into the cycle";
                    g2d.drawString(text, 3*imageWidth/2, 3*imageHeight + fm.getAscent());
                }
            };
            helpObjectsPanel.setPreferredSize(new Dimension(-1, image.getHeight(null)*4 + 20));
        } catch (IOException e) {
            e.printStackTrace();
            helpObjectsPanel = new JPanel();
        }

        URL pickFile = getClass().getResource("/pick_mini_map.png");
        URL putFile = getClass().getResource("/put_mini_map.png");
        try {
            final Image pickImage = ImageIO.read(pickFile);
            final Image putImage = ImageIO.read(putFile);
            helpCollectMapPanel = new JPanel(){
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(pickImage, 0, 0, null);
                    g.drawImage(putImage, this.getWidth()/2, 0, null);
                }
            };
            helpCollectMapPanel.setPreferredSize(new Dimension(-1, pickImage.getHeight(null) + 20));
        } catch (IOException e) {
            e.printStackTrace();
            helpCollectMapPanel = new JPanel();
        }
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


        miniMapScrollPane.setPreferredSize(new Dimension(200,1000));
        miniMapScrollPane.setBackground(Color.WHITE);

        myFrame.setContentPane(mainPanel);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.pack();
        Locale.setDefault(Locale.UK);
        //todo exit
        /*Timer timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                myFrame.repaint();
            }
        });*/
        myFrame.setVisible(true);
        //timer.start();

    }

    public void repaint(){
        myFrame.repaint();
        if(myMode == MainWindowMode.MENU){
            menuPanel.repaint();
        } else if (myMode == MainWindowMode.CLASSICGAME){
            classicGamePanel.repaint();
        } else if(myMode == MainWindowMode.GENERATOR){
            generatorPanel.repaint();
        } else if(myMode == MainWindowMode.HELP){
            helpPanel.repaint();
        } else if(myMode == MainWindowMode.OPTIONS){
            optionsPanel.repaint();
        } else if(myMode == MainWindowMode.STARTGAME){
            startGamePanel.repaint();
        } else if(myMode == MainWindowMode.SIMPLEGAME){
            simpleGamePanel.repaint();
        }
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
        miniMapPanel.setLayout(new BoxLayout(miniMapPanel, BoxLayout.Y_AXIS));

        classicGameTextScrollPane.setPreferredSize(new Dimension(600, 50));
        ((ClassicGamePanel)classicGameMapPanel).setTextArea(classicGameTextArea);
        ((ClassicGamePanel)classicGameMapPanel).setMiniMapPanel(miniMapPanel);

        classicGameReturnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel, "startgame");
                myMode = MainWindowMode.STARTGAME;
                engineThread.interrupt();
            }
        });

        classicGameExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                engineThread.interrupt();
                myFrame.dispose();
                System.exit(0);
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
                        MapGenerator mapGenerator = new MapGenerator(width, height, minSize, stopP, branchP, allowCycle, stopAfterCycle, minotaurs, portals, maxRegions, 1, (MapPanelBase) generateMapPanel);
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
        helpIntroTextArea.setText("Labyrinth (or Terra Incognita) is a logical paper-and-pencil game. You start game on the random point in the randomly generated labyrinth, like shown on the picture below. Your goal is to find exit from labyrinth, represented with the red line, by revealing the it's design. You should move step-by-step, opening the map, kill the minotaurs and combine parts of the map. You can create different labyrinths using map generator. For more detail information see https://en.wikipedia.org/wiki/Labyrinth_(paper-and-pencil_game)");
        helpMainScreenTextArea.setText("In the center of main screen you can see the game field, where you can move your character. In the upper-left there is current information about portals, minotaurs and your amount of bullets. In the bottom of the sreen there is a log of all your steps. And on the rigth side there is panel for opened parts of map.");
        helpControlsTextArea.setText("You can move throught the labyrinth using WASD on the keyboard. If there is not wall on your course, you will go through. Otherwise wall will appear on your map. In the beginning of every move, you can shoot in every direction to kill minotaurs, using arrows on keyboard. But you have only limited amount of bullets.");
        helpCollectMapTextArea.setText("To open the whole labyrinth, you should combine available map parts. They are represented on the mini map panel, on the right part of main screen. You can pick one of them with the mouse and drag it to the main map. If they will match, lines will be highlited with green colour, and you can combine them with mouse left-click. Click right buton if you want to stop dragging. Also you can delete unnecessary mini maps and split them, using buttons on them.");
        helpBackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myLayout.show(mainPanel, "menu");
                myMode = MainWindowMode.MENU;
            }
        });
        helpExitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.dispose();
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
        for(int i = 6; i <= 12; ++i){
            sizeComboBox.addItem(new Integer(i));
        }
        sizeComboBox.setSelectedIndex(2);

        difficultyComboBox.addItem("low");
        difficultyComboBox.addItem("middle");
        difficultyComboBox.addItem("high");
        difficultyComboBox.addItem("very high");

        startMinotaursComboBox.addItem("zero");
        startMinotaursComboBox.addItem("low");
        startMinotaursComboBox.addItem("middle");
        startMinotaursComboBox.addItem("high");
        startMinotaursComboBox.addItem("they are everywhere!");

        startPortalsComboBox.addItem("zero");
        startPortalsComboBox.addItem("low");
        startPortalsComboBox.addItem("middle");
        startPortalsComboBox.addItem("high");

        /*startSimpleButton.addActionListener(new ActionListener() {
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
        });*/

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
                    LabEngine engine = new LabEngine(map, fromClientToServer, fromServerToClient, (ClassicGamePanel) classicGameMapPanel);
                    myLayout.show(mainPanel, "classicgame");
                    myMode = MainWindowMode.CLASSICGAME;
                    //myFrame.requestFocus();
                    engineThread = new Thread(engine);
                    engineThread.start();
                    //map.print();
                    classicGameMapPanel.repaint();

                } catch (NumberFormatException ne){

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
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
                info.fakeKeys = 0;
                break;
            case 2:
                info.stopP = 0.1;
                info.branchP = 0.1;
                info.minSize = info.width * info.height / 5;
                info.maxRegions = (info.width * 2) / 3;
                info.allowCycle = true;
                info.stopAfterCycle = false;
                info.fakeKeys = 1;
                break;
            case 3:
                info.stopP = 0.05;
                info.branchP = 0.2;
                info.minSize = info.width * info.height / 5;
                info.maxRegions = info.width / 2;
                info.allowCycle = true;
                info.stopAfterCycle = false;
                info.fakeKeys = 2;
                break;
            default:
                info.stopP = 0.2;
                info.branchP = 0.01;
                info.minSize = info.width;
                info.maxRegions = info.width;
                info.allowCycle = false;
                info.stopAfterCycle = false;
                info.fakeKeys = 3;
                break;
        }


        int portalsType = startPortalsComboBox.getSelectedIndex();
        switch (portalsType) {
            case 1:
                info.portals = info.width / 2 + 1;
            case 2:
                info.portals = info.width / 2 + info.width * info.height / 20;
                break;
            case 3:
                info.portals = info.width / 2 + info.width * info.height / 10;
                break;
            default:
                info.portals = 0;
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
                System.exit(0);
            }
        });
    }
}
