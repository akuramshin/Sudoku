import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.*;

public class Sudoku implements Runnable{

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private final JPanel MenuPane = new JPanel();
    private JFrame f = new JFrame();
    JLabel hintLabel;
    private int size = 9;
    private int numHint = size * 2 - (int) Math.sqrt(size);
    private int blockSize;
    private long startTime;
    private JPanel tileGrid;
    private Tile[][] tileSquares;
    private Tile highLightedTile;
    private Tile errorTile;
    private SudokuSolver data;
    private Random random;
    
    Sudoku() {
        initializeGui();
    }

    // Setups the GUI
    public final void initializeGui() {
    	// Create new Sudoku grid
    	random = new Random();
    	blockSize = (int) Math.sqrt(size);
		startTime = System.currentTimeMillis();
    	data = new SudokuSolver(size);
    	data.genNewSudoku();

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        
        JButton restartButton = new JButton("Restart");
        tools.add(restartButton); 
        restartButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				restart();
			}
        });
        
        JButton hintButton = new JButton(" Hint ");
        tools.add(hintButton); 
        hintButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				hint();
			}
        });
        JButton easyButton = new JButton("Easy");
        tools.add(easyButton); 
        easyButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				size = 4;
				restart();
			}
        });
        
        JButton mediumButton = new JButton("Medium");
        tools.add(mediumButton); 
        mediumButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				size = 9;
				restart();
			}
        });
        
        JButton hardButton = new JButton("Hard");
        tools.add(hardButton); 
        hardButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				size = 16;
				restart();
			}
        });

        tools.addSeparator();
        
        hintLabel = new JLabel();
        hintLabel.setText("Hints: " + numHint);
        hintLabel.setFont(new Font("Serif", Font.BOLD, 14));
        hintLabel.setForeground(Color.ORANGE);
        tools.add(hintLabel);
        
        createField();
        updateTileIcons();
        tileGrid.setBorder(new LineBorder(Color.BLACK));
        gui.add(tileGrid);
        
        MenuPane.setBorder(new EmptyBorder(4, 4, 4, 4));
        MenuPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		for (int i = 0; i <= size; i++){
			Tile num = new Tile(Integer.toString(i));
			num.value = i;
			MenuPane.add(num, gbc);
			num.setBorder(BorderFactory.createCompoundBorder(
					num.getBorder(), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
			num.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				reset();
    				setNewValue(num.value, highLightedTile);
    				updateTileIcons();
    			}
            });
			gbc.gridy++;
		}
    }
    
    // Resets the highlights of all the tiles
    public void reset(){
    	for (int i = 0; i < tileSquares.length; i++){
    		for (int j = 0; j < tileSquares[i].length; j++){
    			tileSquares[i][j].mode = 0;
    		}
    	}
    }
    
    // Generates and shows the hint
    public void hint(){
    	ArrayList<int[]> availableSpots = new ArrayList<int[]>();
    	if (numHint > 0){
			reset();
			for (int i = 0; i < size; i++){
				for (int j = 0; j < size; j++){
					if (data.sudoku[i][j] == 0){
						availableSpots.add(new int[] {i, j});
					}
				}
			}
			if (availableSpots.size() == 0){
				return;
			}
			int hintTarget = random.nextInt(availableSpots.size());
			setNewValue(data.solvedSudoku[availableSpots.get(hintTarget)[0]][availableSpots.get(hintTarget)[1]], 
					tileSquares[availableSpots.get(hintTarget)[0]][availableSpots.get(hintTarget)[1]]);
			tileSquares[availableSpots.get(hintTarget)[0]][availableSpots.get(hintTarget)[1]].mode = 3;
			updateTileIcons();
			numHint--;
			hintLabel.setText("Hints: " + numHint);
			return;
	     }
    }
   
    // Setting a new value to the grid, checking for errors
    public void setNewValue(int newVal, Tile tile){
    	if(!checkGUI(newVal, tile.row, tile.col) && newVal != 0){
    		errorTile.mode = 2;
    	}else{
    		data.sudoku[tile.row][tile.col] = newVal;
    	}
    }

    // Returns the current gui
    public final JComponent getGui() {
        return gui;
    }
    
    // Returns the current MenuPane
    public final JPanel getSidePanel(){
    	return MenuPane;
    }
    
    // Populates the gui with a tile (buttons) grid as the Sudoku grid
    void createField(){
    	tileSquares = new Tile[size][size];
    	tileGrid = new JPanel(new GridLayout(blockSize, blockSize));
    	JPanel subPanel = null;
    	
        for (int i = 0; i < size; i++){
        	subPanel = new JPanel(new GridLayout(blockSize, blockSize));
        	
        	subPanel.setBorder(new LineBorder(Color.BLACK, 2));
        	for (int j = 0; j < size; j++){
        		Tile b = new Tile((((int)(i / blockSize)) * blockSize) + ((int)(j / blockSize)), (i%blockSize) * blockSize + (j%blockSize));
        		b.setHorizontalAlignment(JTextField.CENTER);
        		b.setBorder(BorderFactory.createCompoundBorder(
        				b.getBorder(), BorderFactory.createEmptyBorder(5,5,5,5)));
        		b.setBackground(Color.WHITE);
        		if (data.sudoku[b.row][b.col] == 0){
	        		b.addActionListener(new ActionListener(){
	        			@Override
	        			public void actionPerformed(ActionEvent e) {
	        				reset();
	        				b.mode = 1;
	        				updateTileIcons();
	        			}
	                });
        		}
        		subPanel.add(b);
        		
                // Add button to tile grid
        		tileSquares[b.row][b.col] = b;
        		if (data.sudoku[b.row][b.col] > 0){
        			b.pressed = true;
        		}
        	}
        	tileGrid.add(subPanel);
        }
       }
    
    // Updates the frame to show the new highlights, values and checks for win
    void updateTileIcons(){
    	for (int i = 0; i < tileSquares.length; i++) {
            for (int j = 0; j < tileSquares[i].length; j++) {
            	if(!checkGUI(data.sudoku[i][j], i, j) && data.sudoku[i][j] != 0 && !tileSquares[i][j].isEnabled()){
            		errorTile.mode = 2;
            		tileSquares[i][j].mode = 2;
            	}
            }
    	}
    	
    	for (int ii = 0; ii < tileSquares.length; ii++) {
            for (int jj = 0; jj < tileSquares[ii].length; jj++) {
            		tileSquares[ii][jj].setFont(new Font("Arial", Font.PLAIN, 24 - (size - 9)));
            		if (data.sudoku[ii][jj] > 0){
            			tileSquares[ii][jj].setText(Integer.toString(data.sudoku[ii][jj]));
            		}else{
            			tileSquares[ii][jj].setText(" ");
            		}
            		switch(tileSquares[ii][jj].mode){
            			case 0: tileSquares[ii][jj].setBackground(Color.WHITE); break;
                		case 1: tileSquares[ii][jj].setBackground(Color.YELLOW); highLightedTile = tileSquares[ii][jj]; break;
                		case 2: tileSquares[ii][jj].setBackground(Color.RED); break;
                		case 3: tileSquares[ii][jj].setBackground(Color.GREEN); break;
            		}
                }
            }
    	for (int i = 0; i < data.sudoku.length; i ++){
    		for (int j = 0; j < data.sudoku.length; j ++){
    			if (data.sudoku[i][j] == 0){
    				return;
    			}
    		}
    	}
    	win();
    }
    
    // Checks if there are any Sudoku errors on the grid (same numbers in a row, coll, square)
    public boolean checkGUI(int number, int row, int coll) {	
		for (int i = 0; i < size; i++){
			if (data.sudoku[row][i] == number){
				errorTile = tileSquares[row][i];
				return false;
			}
			if (data.sudoku[i][coll] == number){
				errorTile = tileSquares[i][coll];
				return false;
			}
			int r = (row/blockSize) * blockSize;
			int p = (coll/blockSize) * blockSize;
							
			for(int j = 0; j < blockSize; j++){
				for (int k = 0; k < blockSize; k++){
					if(number == data.sudoku[r + j][p + k]){
						errorTile = tileSquares[r + j][p + k];
						return false;
					}
				}
			}			
		}		
		return true;		
	}
    
    public static void main(String[] args) {
    	(new Thread(new Sudoku())).start();
    }
    
    // Puts everything together
    @Override
    public void run() {
        f.getContentPane().add(this.getGui());
        f.add(this.getSidePanel(), BorderLayout.AFTER_LINE_ENDS);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //f.setLocationByPlatform(true);

        // ensures the frame is the minimum size it needs to be
        // in order display the components within it
        f.pack();
        // ensures the minimum size is enforced.
        f.setMinimumSize(f.getSize());
        f.setSize(f.getSize());
        f.setVisible(true);
    }
    
    // restarts
    void restart(){
    	f.dispose();
    	numHint = size * 2;
    	MenuPane.removeAll();
		gui.remove(tileGrid);
		gui.remove(gui.getComponent(0));
		f = new JFrame();
		tileSquares = null;
		initializeGui();
		run();
    }
    
    // Shows win frame
    void win(){
    	// Set all tiles to "pressed", so you can't use the board anymore
    	for (int ii = 0; ii < tileSquares.length; ii++) {
            for (int jj = 0; jj < tileSquares[ii].length; jj++) {
            	tileSquares[ii][jj].pressed = true;
            }
    	}
    	
    	// Creating the WIN frame
    	JFrame winFrame = new JFrame();
    	winFrame.setLayout(new BorderLayout());
    	winFrame.setBackground(Color.WHITE);
    	winFrame.setSize(300, 300);
        winFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JLabel youWin = new JLabel("You Win!");
        youWin.setFont(new Font("Impact", Font.BOLD, 24));
        youWin.setForeground(Color.GREEN);
        youWin.setHorizontalAlignment(JLabel.CENTER);
        youWin.setVerticalAlignment(JLabel.CENTER);
        
        JLabel Timer = new JLabel("Time: " + Long.toString((System.currentTimeMillis() - startTime) / 1000) + " seconds");
        Timer.setFont(new Font("Arial", Font.BOLD, 16));
        Timer.setForeground(Color.RED);
        Timer.setHorizontalAlignment(JLabel.CENTER);
        Timer.setVerticalAlignment(JLabel.CENTER);
        
    	winFrame.add(youWin, BorderLayout.NORTH);
    	winFrame.add(Timer, BorderLayout.SOUTH);
        winFrame.setVisible(true);
    }
}