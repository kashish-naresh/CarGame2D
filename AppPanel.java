import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AppPanel extends JPanel {
    
    int crx, cry;
    int car_x, car_y;
    int speedX, speedY;
    int nOpponent;
    String imageLoc[];
    int lx[], ly[];
    int score;
    int highScore;
    int speedOpponent[];
    boolean isFinished;
    boolean isUp, isDown, isRight, isLeft;
    private static final String HIGH_SCORE_FILE = "highscore.txt";  

    public AppPanel() {
        resetGame();
        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {
                stopCar(e);
            }
            public void keyPressed(KeyEvent e) {
                moveCar(e);
            }
        });
        
        setFocusable(true);
        highScore = loadHighScore();
        repaint();
    }

    public int loadHighScore() {
        try {
            File file = new File(HIGH_SCORE_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                int score = Integer.parseInt(reader.readLine());
                return score;
            }
        } catch (IOException e) {
            System.out.println("Error reading high score: " + e.getMessage());
        }
        return 0;
    }
    
    public void saveHighScore(int score) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE));
            writer.write(String.valueOf(score));
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving high score: " + e.getMessage());
        }
    }

    public void updateScore(int newScore) {
        score = newScore;
        if (score > highScore) {
            highScore = score;
            saveHighScore(highScore);
        }
    }

    private void resetGame() {
        crx = cry = -999;
        car_x = car_y = 300;
        speedX = speedY = 0;
        nOpponent = 0;
        lx = new int[20];
        ly = new int[20];
        imageLoc = new String[20];
        speedOpponent = new int[20];
        isFinished = false;
        score = 0;
        highScore = loadHighScore();
        isUp = isDown = isLeft = isRight = false;
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D obj = (Graphics2D) g;
        obj.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
        try {
            obj.drawImage(getToolkit().getImage("./Resource/st_road.png"), 0, 0, this);
            if (cry >= -499 && crx >= -499)
                obj.drawImage(getToolkit().getImage("./Resource/cross_road.png"), crx, cry, this);
    
            obj.drawImage(getToolkit().getImage("./Resource/car_self.png"), car_x, car_y, this);
    
            // if (isFinished) {
            //     obj.drawImage(getToolkit().getImage("/Resource/boom.png"), car_x - 30, car_y - 30, this);
            // }
    
            if (this.nOpponent > 0) {
                for (int i = 0; i < this.nOpponent; i++) {
                    obj.drawImage(getToolkit().getImage(this.imageLoc[i]), this.lx[i], this.ly[i], this);
                }
            }
    
            obj.setColor(Color.WHITE);
            obj.setFont(new Font("Arial", Font.BOLD, 16));
            obj.drawString("Score: " + score, 10, 20);
            obj.drawString("High Score: " + highScore, 10, 40);
    
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    void moveRoad(int count) {
        if (crx == -999 && cry == -999) { 
            if (count % 10 == 0) {
                crx = 499;
                cry = 0;
            }
        } else {
            crx--;
        }
    
        car_x += speedX;
        car_y += speedY;
    
        if (car_x < 0) car_x = 0;
        if (car_x + 93 >= 500) car_x = 500 - 93;
        if (car_y <= 124) car_y = 124;
        if (car_y >= 364 - 50) car_y = 364 - 50;
    
        for (int i = 0; i < this.nOpponent; i++) {
            this.lx[i] -= speedOpponent[i];
        }
    
        int index[] = new int[nOpponent];
        for (int i = 0; i < nOpponent; i++) {
            if (lx[i] >= -127) {
                index[i] = 1;
            }
        }
    
        int c = 0;
        for (int i = 0; i < nOpponent; i++) {
            if (index[i] == 1) {
                imageLoc[c] = imageLoc[i];
                lx[c] = lx[i];
                ly[c] = ly[i];
                speedOpponent[c] = speedOpponent[i];
                c++;
            }
        }
    
        score += nOpponent - c;
    
        if (score > highScore) {
            highScore = score;
            saveHighScore(highScore);
            repaint();
        }
        
        nOpponent = c;
    
        int diff = 0;
        for (int i = 0; i < nOpponent; i++) {
            diff = car_y - ly[i];
            if ((ly[i] >= car_y && ly[i] <= car_y + 46) || (ly[i] + 46 >= car_y && ly[i] + 46 <= car_y + 46)) {
                if (car_x + 87 >= lx[i] && !(car_x >= lx[i] + 87)) {
                    finish();
                }
            }
        }
    }
    
    void finish() {
        String str = "";
        isFinished = true;
        this.repaint();

        if (score == highScore && score != 0) {
            str = "\nCongratulations!!! It's a high score!";
        }

        int option = JOptionPane.showConfirmDialog(this, 
            "Game Over!!!\nYour Score : " + score + "\nHigh Score : " + highScore + str, 
            "Game Over", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    public void moveCar(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            isRight = true;
            speedX = 1;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            isLeft = true;
            speedX = -2;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            isUp = true;
            speedY = -1;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            isDown = true;
            speedY = 1;
        }
    }
    
    public void stopCar(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            isRight = false;
            speedX = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            isLeft = false;
            speedX = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            isUp = false;
            speedY = 0;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            isDown = false;
            speedY = 0;
        }
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("Car Racing Game");
        AppPanel game = new AppPanel();
        frame.add(game);
        frame.setSize(500,500);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        int count = 1, c = 1;
        while (true) {
            game.moveRoad(count);
            while (c <= 1) {
                game.repaint();
                try {
                    Thread.sleep(5);
                } catch (Exception e) {
                    System.out.println(e);
                }
                c++;
            }

            c = 1;
            count++;
            if (game.nOpponent < 4 && count % 200 == 0) {
                game.imageLoc[game.nOpponent] = "./Resource/car_left_" + ((int)((Math.random() * 100) % 3) + 1) + ".png";
                game.lx[game.nOpponent] = 499;
                int p = (int)(Math.random() * 100) % 4;
                if (p == 0) {
                    p = 250;
                } else if (p == 1) {
                    p = 300;
                } else if (p == 2) {
                    p = 185;
                } else {         
                    p = 130;
                }
                game.ly[game.nOpponent] = p;
                game.speedOpponent[game.nOpponent] = (int)(Math.random() * 100) % 2 + 2;
                game.nOpponent++;
            }
        }
    }
}