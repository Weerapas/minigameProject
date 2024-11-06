import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;

public class LabEscape extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GamePanel gamePanel;

    URL imgBGUrl = getClass().getResource("bg1.jpg");
    Image imgBG = new ImageIcon(imgBGUrl).getImage();
    URL imgActorUrl = getClass().getResource("run.png");
    Image imgActor = new ImageIcon(imgActorUrl).getImage();
    URL imgTrapUrl = getClass().getResource("trap.png");
    Image imgTrap = new ImageIcon(imgTrapUrl).getImage();
    URL imgDoorUrl = getClass().getResource("door.png");
    Image imgDoor = new ImageIcon(imgDoorUrl).getImage();

    public LabEscape() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // สร้างหน้าเมนูพร้อมพื้นหลัง
        BackgroundPanel menuPanel = new BackgroundPanel(imgBG);
        menuPanel.setLayout(new GridBagLayout());
        
        JLabel gameTitle = new JLabel("LAB Escape");
        gameTitle.setFont(new Font("Arial", Font.BOLD, 36));
        gameTitle.setForeground(Color.BLUE);

        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(200, 60));
        JButton quitButton = new JButton("Quit");
        quitButton.setPreferredSize(new Dimension(200, 60));

        // ปรับขนาดภาพตัวละครในหน้าเมนู
        Image scaledActorImage = imgActor.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel actorLabel = new JLabel(new ImageIcon(scaledActorImage)); 

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "game");
                gamePanel.requestFocusInWindow();
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuPanel.add(gameTitle, gbc);

        gbc.gridy = 1;
        menuPanel.add(actorLabel, gbc);

        gbc.gridy = 2;
        menuPanel.add(startButton, gbc);

        gbc.gridy = 3;
        menuPanel.add(quitButton, gbc);

        // สร้างหน้าจอเกม
        gamePanel = new GamePanel(imgBG, imgActor, imgTrap, imgDoor);

        // เพิ่มหน้าเมนูและหน้าเกมใน CardLayout
        mainPanel.add(menuPanel, "menu");
        mainPanel.add(gamePanel, "game");

        add(mainPanel);
        cardLayout.show(mainPanel, "menu");
    }

    // Inner class สำหรับหน้าจอเกม
    static class GamePanel extends JPanel {
        Image imgBg;
        Image imgActor;
        Image imgTrap;
        Image imgDoor;
        int x = 20;
        int y = 500;
        int jumpHeight = 150;
        int groundLevel = 500;
        boolean isJumping = false;
        boolean goingUp = false;
        boolean gameOver = false;
        boolean gameWin = false;
        int deathCount = 0;
        int level = 0;
        final int MAX_LEVEL = 5;

        ArrayList<Point> traps = new ArrayList<>();
        int doorX = 1080;
        int doorY = 460;

        Timer t = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver && !gameWin) {
                    repaint();
                }
            }
        });

        public GamePanel(Image imgBg, Image imgActor, Image imgTrap, Image imgDoor) {
            this.imgBg = imgBg;
            this.imgActor = imgActor;
            this.imgTrap = imgTrap;
            this.imgDoor = imgDoor;
            setFocusable(true);
            t.start();

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        jump();
                    } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
                        resetGame();
                    }
                }
            });
        }

        public void jump() {
            if (!isJumping && !gameOver && !gameWin) {
                isJumping = true;
                goingUp = true;
            }
        }

        public void resetGame() {
            x = 20;
            y = groundLevel;
            isJumping = false;
            goingUp = false;
            gameOver = false;
            level = 0;
            traps.clear();
            t.start();
            repaint();
        }

        // ฟังก์ชันเพิ่มเลเวลและเพิ่มกับดักหนาม
        public void advanceLevel() {
            level++;

            if (level > MAX_LEVEL) { // ถ้าผ่านเลเวล 5 แล้ว
            gameWin = true;
            t.stop();
            return;
            }

            traps.clear(); // ล้างกับดักเดิม

    // เพิ่มกับดักตามเลเวล
    for (int i = 0; i < level; i++) {
        traps.add(new Point(200 + i * 200, 550)); // เพิ่มกับดักห่างกัน 200 พิกเซล
    }

    // รีเซ็ตตำแหน่งตัวละคร
    x = 20;
    y = groundLevel;
    isJumping = false;
    goingUp = false;
}

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (x > getWidth()) {
                x = 20;
            } else {
                x += 3;
            }

            if (isJumping) {
                if (goingUp) {
                    y -= 5;
                    if (y <= groundLevel - jumpHeight) {
                        goingUp = false;
                    }
                } else {
                    y += 5;
                    if (y >= groundLevel) {
                        y = groundLevel;
                        isJumping = false;
                    }
                }
            }

            for (Point trap : traps) {
                if (x + 60 >= trap.x && x <= trap.x + 50 && y + 60 >= trap.y) {
                    gameOver = true;
                    deathCount++;
                    t.stop();
                    break;
                }
            }

            if (x + 60 >= doorX && x <= doorX + 100 && y + 60 >= doorY && !gameOver && !gameWin) {
                advanceLevel();
            }

            g.drawImage(imgBg, 0, 0, getWidth(), getHeight(), this);
            g.drawImage(imgActor, x, y, 100, 100, this);
            for (Point trap : traps) {
                g.drawImage(imgTrap, trap.x, trap.y, 60, 60, this);
            }
            g.drawImage(imgDoor, doorX, doorY, 150, 150, this);

            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(Color.BLACK);
            g.drawString("Deaths: " + deathCount, 10, 30);
            g.drawString("Level: " + level, 10, 60);

            if (gameOver) {
                g.setFont(new Font("Arial", Font.BOLD, 48));
                g.setColor(Color.RED);
                g.drawString("Game Over", getWidth() / 2 - 150, getHeight() / 2);
                g.setFont(new Font("Arial", Font.PLAIN, 24));
                g.drawString("Press 'R' to Restart", getWidth() / 2 - 100, getHeight() / 2 + 50);
            }

            if (gameWin) {
                g.setFont(new Font("Arial", Font.BOLD, 48));
                g.setColor(Color.GREEN);
                g.drawString("You Win!", getWidth() / 2 - 100, getHeight() / 2);
            }
        }
    }

    // คลาสย่อยสำหรับแสดงพื้นหลังในหน้าเมนู
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(Image backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // วาดภาพพื้นหลัง
        }
    }

    public static void main(String[] args) {
        JFrame f = new LabEscape();
        f.setSize(1200, 700);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
