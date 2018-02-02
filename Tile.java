import javax.swing.JButton;

public class Tile extends JButton{

    public boolean pressed = false;
    public int row;
    public int col;
    public int mode = 0;
    public int value;

    public Tile(int row, int col){
        this.row = row;
        this.col = col;
    }

    public Tile(String value){
        super.setText(value);
    }
}