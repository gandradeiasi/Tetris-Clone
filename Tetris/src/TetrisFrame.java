import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class TetrisFrame extends JFrame implements KeyListener, ActionListener{
	private int screenHeight;
	private int columns;
	private int height;
	private int rows;
	private int squareSize;
	private int width;
	private int points;
	private int speed;
	private int[][] matrix;
	private boolean gameOver;
	private JMenuBar menuBar;
	private JMenu menuGame;
	private JMenuItem restartGame;
	private JMenuItem controls;
	private ImageIcon icon;
	private TetrisFrame instance;
	private JPanel[][] squares;
	private Tetramino currentTetramino;
	private Tetramino nextTetramino;
	private Color[][] colorMatrix;
	public void startGame() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		
		//Initialize fields
		
		gameOver 			= false;
		menuBar 			= new JMenuBar();
		menuGame			= new JMenu("Game");
		restartGame 		= new JMenuItem("Restart Game");
		controls			= new JMenuItem("Controls");
		speed				= 1000;
		screenHeight		= gd.getDisplayMode().getHeight();
		points 				= 0;
		instance 			= this;
		columns				= 10;
		rows				= 20;
		squareSize 			= (screenHeight - 100) / rows;
		height 				= rows * squareSize + 20;
		width				= columns * squareSize;
		matrix 				= new int[rows][columns];
		squares 			= new JPanel[rows][columns];
		nextTetramino		= new Tetramino();
		currentTetramino 	= new Tetramino();
		icon 				= new ImageIcon("src\\t.png");
		colorMatrix			= new Color[rows][columns];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				matrix[i][j] = 0;
				squares[i][j] = new JPanel();
			}
		}
		
		//Add components
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				this.add(squares[i][j]);
			}
		}
		this.setJMenuBar(menuBar);
		menuBar.add(menuGame);
		menuGame.add(restartGame);
		menuGame.add(controls);
		
		//Add Action Listener
		restartGame.addActionListener(this);
		controls.addActionListener(this);
		
		//Configure Frame
		this.setSize(width,height+20);
		this.setLayout(new GridLayout(rows,columns));
		this.setTitle("Tetris");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.addKeyListener(this);
		this.setIconImage(icon.getImage());
				
		//Set Visibility
		this.setVisible(true);
		
		showControls();
		
		//Loop
		Thread loop = new Thread() {
			@Override
			public void run() {
				while (true) {
					//Refresh Title
					setTitle("Tetris | Points: " + points);
					
					//Clear Completed Rows
					int rowsCleared = 0;
					int pointsEarned = 0;
					
					for (int i = 0; i < rows; i++) {
						int counter = 0;
						for (int j = 0; j < columns; j++) {
							if (matrix[i][j] == 1) counter++;
							if (counter == columns) {
								for (int i2 = i; i2 >= 1; i2--) {
									for (int j2 = 0; j2 < columns; j2++) {
										matrix[i2][j2] = matrix[i2-1][j2];
										colorMatrix[i2][j2] = colorMatrix[i2-1][j2];
									}
								}
								rowsCleared++;
								pointsEarned += 100;
							}
						}
					}
					
					if (rowsCleared > 0) increaseSpeed();
					
					points += pointsEarned * rowsCleared; 
					
					//Refresh Output
					refreshOutput();
					
					//Refresh Tetramino Position
					for (int i = 0; i < currentTetramino.getSize(); i++) {
						for (int j = 0; j < currentTetramino.getSize(); j++) {
							if (currentTetramino.getRow() + i < rows || currentTetramino.getColumn() + j < columns) {
								if (currentTetramino.getMatrix()[i][j] == 1) {
									while(true) {
										try {
											squares[currentTetramino.getRow()+i][currentTetramino.getColumn()+j].setBackground(currentTetramino.getColor());
											break;
										}
										catch(Exception e) {
											if (currentTetramino.getColumn() >= 5) currentTetramino.goLeft();
											else currentTetramino.goRight();
										}
									}
								}
							}
						}
					}
					
					try {
						sleep(1000/60);
					} catch (InterruptedException e) {}
				}
			}
		};

		Thread decreasePoints = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						
					}
					if (points - 1 >= 0 && !gameOver) points--;
				}
			}
		};
		
		Thread fall = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						sleep(speed);
					} catch (InterruptedException e) {}

					if (currentTetramino.canGoDown(instance)) {
						currentTetramino.goDown();
					}
					else {
						if (currentTetramino.getMoved()) newTetramino();
						else {
							gameOver = true;
							decreasePoints.interrupt();
							JOptionPane.showMessageDialog(instance, "Points: " + points);
							restartGame();
						}
					}
				}
			}
		};
		
		loop.start();
		fall.start();
		decreasePoints.start();
	}
	
	public void newTetramino() {
		for (int i = 0; i < currentTetramino.getSize(); i++) {
			for (int j = 0; j < currentTetramino.getSize(); j++) {
				try {
					if (currentTetramino.getMatrix()[i][j] == 1) colorMatrix[i+currentTetramino.getRow()][j+currentTetramino.getColumn()] = currentTetramino.getColor();
				}
				catch(Exception e){}
				
				if (currentTetramino.getRow() + i < rows || currentTetramino.getColumn() + j < columns) {
					if (currentTetramino.getMatrix()[i][j] == 1) {
						matrix[currentTetramino.getRow()+i][currentTetramino.getColumn()+j] = 1;
					}
				}
			}
		}
		currentTetramino = nextTetramino;
		nextTetramino();
	}
	
	public void nextTetramino() {
		Tetramino localTetramino;
		while (true) {
			localTetramino = new Tetramino();
			if (currentTetramino.getId() != localTetramino.getId()) break;
		}
		nextTetramino = localTetramino;
	}	
	
	public void increaseSpeed() {
		speed = (int) (speed * 0.95);
	}
	
	public void restartGame() {
		points = 0;
		speed = 1000;
		currentTetramino = new Tetramino();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				matrix[i][j] = 0;
			}
		}
		gameOver = false;
	}
	
	public void refreshOutput() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (matrix[i][j] == 0) {
					squares[i][j].setBackground(new Color(255,255,255));
				}
				else if (matrix[i][j] == 1) {
					squares[i][j].setBackground(colorMatrix[i][j]);
				}
			}
		}
	}
	
	public void showControls() {
		JOptionPane.showMessageDialog(this,"Arrows Keys: Move\n"+
				   "Z: Rotate Left\n"+
				   "X or Up Arrow: Rotate Right\n"+
				   "C or Space: Drop\n"+
				   "R: Restart Game");
	}

	@Override
	public void keyPressed(KeyEvent e) {
		currentTetramino.refreshLimits();
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (currentTetramino.canGoRight(this)) {
				currentTetramino.goRight();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (currentTetramino.canGoLeft(this)) {
				currentTetramino.goLeft();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (currentTetramino.canGoDown(this)) {
				currentTetramino.goDown();
			}
			else newTetramino();
		}
		if (e.getKeyCode() == KeyEvent.VK_Z) {
			currentTetramino.rotateLeft(this);
		}
		if (e.getKeyCode() == KeyEvent.VK_X || e.getKeyCode() == KeyEvent.VK_UP) {
			currentTetramino.RotateRight(this);
		}
		if (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_SPACE) {
			while (currentTetramino.canGoDown(this)) {
				currentTetramino.goDown();
			}
			newTetramino();
		}
		if (e.getKeyCode() == KeyEvent.VK_R) {
			restartGame();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == restartGame) {
			restartGame();
		}
		else if (e.getSource() == controls) {
			showControls();
		}
	}
	

	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	public int[][] getMatrix() {
		return matrix;
	}
}
