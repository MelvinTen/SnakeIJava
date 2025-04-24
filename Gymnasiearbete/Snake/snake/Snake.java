package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Snake extends JPanel implements KeyListener, ActionListener {

	final int width = 400;
	final int height = 400;
	final int unitSize = 20;
	final int allUnits = (width * height) / (unitSize * unitSize);
	int delay = 180;
	Image bg;

	final int x[] = new int[allUnits];			
	final int y[] = new int[allUnits];

	int bodyParts = 3;
	int applesEaten;
	int appleX;
	int appleY;
	int rng = (int)((Math.random() * 16)); // 0 - 15

	char direction = 'R';
	boolean running = false;
	Timer timer;

	public Snake() {
		initGame();
	}

	private void initGame() {
		addKeyListener(new TAdapter());
		setFocusable(true);

		x[0] = width / 2;  // Mitt på spelplanen horisontellt
		y[0] = height / 2; // Mitt på spelplanen vertikalt
		for (int i = 1; i < bodyParts; i++) {
			x[i] = x[0] - i * unitSize; // Placera kroppsdelarna till vänster om huvudet
			y[i] = y[0]; // Alla kroppsdelar är på samma rad
		}

		setPreferredSize(new Dimension(width, height));

		bg =  Toolkit.getDefaultToolkit().getImage(getClass().getResource("/snakeBg.png"));

		startGame();
	}

	private void startGame() {
		placeApple();
		running = true;
		timer = new Timer(delay, this);
		timer.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bg, 0, 0, 400, 400, this);
		draw(g);
		scoreCounter(g);
	}

	private void draw(Graphics g) {
		if (running) {
			switch(rng) {
			case 0:
				String effect = "Slow down";
				displayEffect(g, effect);
				g.setColor(Color.WHITE);
				g.fillOval(appleX, appleY, unitSize, unitSize);
				break;
			case 1:
				effect = "-1 point";
				displayEffect(g, effect);
				g.setColor(Color.YELLOW);
				g.fillOval(appleX, appleY, unitSize, unitSize);
				break;
			case 2:
				effect = "Speedboost";
				displayEffect(g, effect);
				g.setColor(Color.GREEN);
				g.fillOval(appleX, appleY, unitSize, unitSize);
				break;
			case 3:
				effect = "+1 point";
				displayEffect(g, effect);
				g.setColor(Color.PINK);
				g.fillOval(appleX, appleY, unitSize, unitSize);
				break;
			case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: case 12: case 13: case 14:
			case 15:
				g.setColor(Color.RED);
				g.fillOval(appleX, appleY, unitSize, unitSize);
				break;
			}


			for (int i = 0; i < bodyParts; i++) {
				if (i == 0) {
					g.setColor(Color.GREEN);
				} else {
					g.setColor(new Color(45, 180, 0));
				}
				g.fillRect(x[i], y[i], unitSize, unitSize);
			}

			Toolkit.getDefaultToolkit().sync();
		} else {
			gameOver(g);
		}
	}

	private void placeApple() {
		boolean validPosition = false;
		do {
			System.out.println(rng);
			appleX = (int) (Math.random() * (width / unitSize)) * unitSize;;
			appleY = (int) (Math.random() * (width / unitSize)) * unitSize;;
			
			for(int i = bodyParts; i >= 0; i--) {	//kollar om äpplets position = position på ormens kroppsdelar, isåfall kör om positionerna
				if((x[i] == appleX) && (y[i] == appleY)) {
					validPosition = false;
					System.out.println(appleX + " + " + appleY);
					System.out.println(x[i] + " + " + y[i]);
					break;
				}else {
					validPosition = true;
				}
			}
		} while (!validPosition);	//kör om metoden tills äpplet är på en godkänd ruta
	}

	private void move() {
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		switch (direction) {
		case 'U':
			y[0] -= unitSize;
			break;
		case 'D':
			y[0] += unitSize;
			break;
		case 'L':
			x[0] -= unitSize;
			break;
		case 'R':
			x[0] += unitSize;
			break;
		}
	}

	private void checkCollision() {
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}

		if (x[0] < 0 || x[0] >= width || y[0] < 0 || y[0] >= height) {
			running = false;
		}

		if (!running) {
			timer.stop();
		}
	}

	private void checkApple() {
		if ((x[0] == appleX) && (y[0] == appleY)) {
			placeApple();
			rng = (int)((Math.random() * 16)); // 0 - 15
			bodyParts++;
			applesEaten++;
			timer.stop();
			timer = new Timer(delay, this);
			timer.start();
			switch (rng) {
			case 0:
				System.out.println("Slow down");
				timer.stop();
				timer = new Timer(240, this);
				timer.start();
				break;
			case 1:
				System.out.println("-1 point");
				applesEaten -= 2; 
				bodyParts -= 2;
				break;
			case 2: 
				System.out.println("Speedboost");
				timer.stop();
				timer = new Timer(105, this);
				timer.start();
				break;
			case 3: 
				System.out.println("+1 point");
				applesEaten++;
				bodyParts++;
				break;
			case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: case 12: case 13: case 14:
			case 15:
				break;
			}	
		}
	}

	private void scoreCounter(Graphics g) {			//skriver ut antal poäng man har
		String scoreCount = "Score: " + applesEaten;
		Font small = new Font("Helvetica", Font.BOLD, 14);

		g.setColor(Color.GREEN);
		g.setFont(small);
		g.drawString(scoreCount, 25, 25);
	}

	private void gameOver(Graphics g) {					//skriver ut antal poäng man fick när man dör
		String msg = "Game Over";
		String scoreMsg = "Score: " + applesEaten;
		Font small = new Font("Helvetica", Font.BOLD, 14);
		FontMetrics metr = getFontMetrics(small);

		g.setColor(Color.RED);
		g.setFont(small);
		g.drawString(msg, (width - metr.stringWidth(msg)) / 2, height / 2);
		g.drawString(scoreMsg, (width - metr.stringWidth(scoreMsg)) / 2, height / 2 + 20);
	}

	private void displayEffect(Graphics g, String effect) {		//skriver ut vilken effekt man får
		String display = effect;
		Font small = new Font("Helvetica", Font.BOLD, 14);

		g.setColor(Color.GREEN);
		g.setFont(small);
		g.drawString(display, 150, 25);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			move();
			checkCollision();
			checkApple();
		}
		repaint();
	}

	private class TAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();

			if ((key == KeyEvent.VK_LEFT) && (direction != 'R')) {
				direction = 'L';
			}

			if ((key == KeyEvent.VK_RIGHT) && (direction != 'L')) {
				direction = 'R';
			}

			if ((key == KeyEvent.VK_UP) && (direction != 'D')) {
				direction = 'U';
			}

			if ((key == KeyEvent.VK_DOWN) && (direction != 'U')) {
				direction = 'D';
			}
			if ((key == KeyEvent.VK_ESCAPE)) {
				System.exit(0);
			}
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Snake Game");
		Snake game = new Snake();
		frame.add(game);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);


	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}

