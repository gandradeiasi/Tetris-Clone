import java.awt.Color;
import java.awt.Point;
import java.util.Random;

public class Tetramino{
	private int		size;
	private int		bottom;
	private int 	left;
	private int 	right;
	private int[][] matrix;
	private boolean moved;
	private Point 	position;
	private Color	color;

	public Tetramino() {
		moved = false;
		
		Random random = new Random();
		
		int minimumColor = 0;
		int maximumColor = 150;
		int colorNumber  = random.nextInt(maximumColor-minimumColor)+minimumColor;
		
		color = new Color(colorNumber,colorNumber,colorNumber);
		
		switch(random.nextInt(7)) {
		case 0:
			size = 4;
			position = new Point(3,0);
			matrix = new int[][] {{0,1,0,0},
							      {0,1,0,0},
							      {0,1,0,0},
								  {0,1,0,0}};
			break;
		case 1:
			size = 3;
			position = new Point(3,0);
			matrix = new int[][] {{0,1,0},
							      {1,1,1},
							      {0,0,0}};
			break;
		case 2:
			size = 3;
			position = new Point(3,0);
			matrix = new int[][] {{0,1,1},
							      {1,1,0},
							      {0,0,0}};
			break;
		case 3:
			size = 3;
			position = new Point(3,0);
			matrix = new int[][] {{1,1,0},
							      {0,1,1},
							      {0,0,0}};
			break;
		case 4:
			size = 3;
			position = new Point(3,0);
			matrix = new int[][] {{0,1,0},
							      {0,1,0},
							      {0,1,1}};
			break;
		case 5:
			size = 3;
			position = new Point(3,0);
			matrix = new int[][] {{0,1,0},
							      {0,1,0},
							      {1,1,0}};
			break;
		case 6:
			size = 2;
			position = new Point(4,0);
			matrix = new int[][] {{1,1},
							      {1,1}};
			break;
		}
		
		
		
		
		refreshLimits();
	}
	
	public void refreshLimits() {
		//Set Bottom
		bottom = getRow()+size;
		boolean continueSearch = true;
		for (int i = size-1; i >= 0 && continueSearch; i--) {
			for (int j = 0; j < size; j++) {
				if (matrix[i][j] == 1) {
					continueSearch = false;
					break;
				}
				else if (matrix[i][j] == 0 && j == size-1) {
					bottom--;
				}
			}
		}
		//Set Left
		left = getColumn() + 1;
		continueSearch = true;
		for (int j = 0; j < size && continueSearch; j++) {
			for (int i = 0; i < size; i++) {
				if (matrix[i][j] == 1) {
					continueSearch = false;
					break;
				}
				else if (matrix[i][j] == 0 && i == size-1) {
					left++;
				}
			}
		}
		//Set Right
		right = getColumn() + size;
		continueSearch = true;
		for (int j = size-1; j >= 0 && continueSearch; j--) {
			for (int i = 0; i < size; i++) {
				if (matrix[i][j] == 1) {
					continueSearch = false;
					break;
				}
				else if (matrix[i][j] == 0 && i == size-1) {
					right--;
				}
			}
		}
		relocate();
	}
	
	public void rotateLeft(TetrisFrame tetris) {
		int [][] matrix = new int[size][size];
		
		int i2 = 0;
		int j2 = 0;
		
		for (int j = 0; j < size; j++) {
			for (int i = size-1; i >= 0; i--) {
				matrix[i][j] = this.matrix[i2][j2];
				j2++;
			}
			j2 = 0;
			i2++;
		}
		
		boolean canRotate = true;
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				try {
					if (matrix[i][j] == 1 && tetris.getMatrix()[getRow()+i][getColumn()+j] == 1) {
						canRotate = false;
					}
				}
				catch(Exception e) {}
			}
		}
		
		if (canRotate) this.matrix = matrix;
		
		this.refreshLimits();
	}
	
	public void RotateRight(TetrisFrame tetris) {
		int [][] matrix = new int[size][size];
		
		int i2 = 0;
		int j2 = 0;
		
		for (int j = size-1; j >= 0; j--) {
			for (int i = 0; i < size; i++) {
				matrix[i][j] = this.matrix[i2][j2];
				j2++;
			}
			j2 = 0;
			i2++;
		}
		
		boolean canRotate = true;
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				try {
					if (matrix[i][j] == 1 && tetris.getMatrix()[getRow()+i][getColumn()+j] == 1) {
						canRotate = false;
					}
				}
				catch(Exception e) {}
			}
		}
		
		if (canRotate) this.matrix = matrix;
		
		refreshLimits();
	}
	
	public void relocate() {
		while(left<=0) {
			goRight();
		}
		
		while(right>10) {
			goLeft();
		}
	}
	
	public void goDown() {
		position.setLocation(position.getX(), position.getY()+1);
		bottom++;
		moved = true;
	}
	
	public void goLeft() {
		position.setLocation(position.getX()-1, position.getY());
		left--;
		right--;
	}
	
	public void goRight() {
		position.setLocation(position.getX()+1, position.getY());
		right++;
		left++;
	}
	
	public boolean canGoDown(TetrisFrame tetrisFrame) {
		if(bottom+1 > tetrisFrame.getRows()) {	
			return false;
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int[][] matrix = tetrisFrame.getMatrix();
				if (this.matrix[i][j] == 1) {
					if(matrix[getRow()+i+1][getColumn()+j] == 1) {
						return false;
					}
				}
			}
		}
		return true;
	} 
	
	public boolean canGoLeft(TetrisFrame tetrisFrame) {
		if (left-1 <= 0) return false;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int[][] matrix = tetrisFrame.getMatrix();
				if (this.matrix[i][j] == 1) {
					if(matrix[getRow()+i][getColumn()+j-1] == 1) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public boolean canGoRight(TetrisFrame tetrisFrame) {
		if (right+1 > tetrisFrame.getColumns()) return false;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int[][] matrix = tetrisFrame.getMatrix();
				if (this.matrix[i][j] == 1) {
					if(matrix[getRow()+i][getColumn()+j+1] == 1) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public int getRow() {
		return position.y;
	}
	
	public int getColumn() {
		return position.x;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getBottom() {
		return bottom;
	}
	
	public int getLeft() {
		return left;
	}
	
	public int getRight() {
		return right;
	}
	
	public int[][] getMatrix() {
		return matrix;
	}
	
	public boolean getMoved() {
		return moved;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setMoved(boolean b) {
		moved = b;
	}
}
