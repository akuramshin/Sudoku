import java.util.Random;


public class SudokuSolver {

	public int[][] sudoku;
	public int[][] solvedSudoku;
	Random random;
	int size;
	int blockSize;
	
	
	public SudokuSolver(int size){
		sudoku = new int[size][size];
		solvedSudoku = new int[size][size];
		this.size = size;
		blockSize = (int) Math.sqrt(size);
	}
	
	public void genNewSudoku(){
		random = new Random();
    	int[][] copySudoku = new int[size][size];
    	genNewPuzzle();
		for (int i = 0; i < sudoku.length; i++) {
		    System.arraycopy(sudoku[i], 0, copySudoku[i], 0, sudoku[0].length);
		}		

		while (!Solve(sudoku)){
			sudoku = new int[size][size];
			genNewPuzzle();
			for (int i = 0; i < sudoku.length; i++) {
			    System.arraycopy(sudoku[i], 0, copySudoku[i], 0, sudoku[0].length);
			}		
		}
    	for (int i = 0; i < sudoku.length; i++) {
		    System.arraycopy(sudoku[i], 0, solvedSudoku[i], 0, sudoku[0].length);
		}	
		sudoku = copySudoku; 
	}
	
	
	public boolean Solve(int[][] sudoku){
		int[] lowest = {21, 0, 0};
		// TODO: optimize the solving algorithm through this candidate checker,
		// Instead of checking every possibility, every time, use the last move and adjust candidates along row, column and square
		OUTER: for(int i = 0; i < sudoku.length; i++){
			for (int j = 0; j < sudoku.length; j ++){
				if (sudoku[i][j] == 0){
					int candidates = 0;
					for(int n = 1; n <= sudoku.length; n++){
						if(check(n, i, j)){
							candidates++;
						}
					}
					if (candidates < lowest[0]){
						lowest[0] = candidates;
						lowest[1] = i;
						lowest[2] = j;	
					}
					if (candidates == 1){
						break OUTER;
					}
				}
			}
		}
		if (lowest[0] == 21){
			return true;
		}
		

		for(int n = 1; n <= sudoku.length; n++){
				if(check(n, lowest[1], lowest[2])){
					sudoku[lowest[1]][lowest[2]] = n;
					if(Solve(sudoku)){
						return true;
					}
			}
		}
			sudoku[lowest[1]][lowest[2]] = 0;
			return false;
	}
	
	
	public boolean check(int number, int row, int coll) {	
		for (int i = 0; i < size; i++){
			if (sudoku[row][i] == number){
				return false;
			}
			if (sudoku[i][coll] == number){
				return false;
			}
			int r = (row/blockSize) * blockSize;
			int p = (coll/blockSize) * blockSize;
							
			for(int j = 0; j < blockSize; j++){
				for (int k = 0; k < blockSize; k++){
					if(number == sudoku[r + j][p + k]){
						return false;
					}
				}
			}			
		}		
		return true;		
	}
	
	public void genNewPuzzle(){
		int count = 0;
		int randX;
		int randY;
		while(true){
			randX = random.nextInt(size);
			randY = random.nextInt(size);
			if (sudoku[randX][randY] == 0){
				int rand = random.nextInt(size) +  1;
				while (!check(rand, randX, randY)){
					rand = random.nextInt(size) +  1;
				}
				sudoku[randX][randY] = rand;
				count++;
				if ((float)(count) / (float)(size * size) >= 0.23){
					return;
				}
			}
		}
	}
	
	
	public void printSudoku(int[][] sudoku){
		for(int row=0;row<size;row++){
			for(int column=0;column<size;column++){
				if ((column+1) % Math.sqrt(size) == 0){
					System.out.print((sudoku[row][column]==0)? " |":sudoku[row][column] + "|");
				}else{
					System.out.print((sudoku[row][column]==0)?"  ":sudoku[row][column] + " ");
				}
			}
		if((row+1) % Math.sqrt(size) == 0){
			System.out.println();
			System.out.print("-----------------");
		}
		System.out.println();
		}
	}
}