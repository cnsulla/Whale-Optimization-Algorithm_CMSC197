import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Cursor;
public class UIBoard{
	private JPanel pane;
	private JLabel board;
	private int sudokuArray[][][];
	private int size, startX, startY, inc, btnX, btnY, ans;
	private int increment[]={56,31,20};
	protected JButton btn[][];
	private generalPanel gp=new generalPanel();
  private BoardGraphics boardg;
  
	UIBoard(){}
	UIBoard(int sudokuArray[][][], JPanel pane){
		this.pane=pane;
		this.sudokuArray=sudokuArray;
		setConstants(false);
		}
	UIBoard(int sudokuArray[][][],boolean isNull, JPanel pane){
		this.pane=pane;
		this.sudokuArray=sudokuArray;
		ans=0;
    
		if(isNull)
			fill();
		setConstants(true);
		}
	private void fill(){
		size=sudokuArray.length;
		for(int ctr=0; ctr<size;ctr++){
			for(int count=0; count<size;count++){
				sudokuArray[ctr][count][0]=0;
				sudokuArray[ctr][count][1]=1;
				//sudokuArray[ctr][count][1]=1;
				}
			}
		}
    
  public BoardGraphics getBoardGraphics() {
    return boardg;
  }
	private void setConstants(boolean setCursor){
		size=sudokuArray.length;
    /*
		startX=size/6+6;
		startY=86;
    if (size == 9 ) inc=increment[0];
    else if (size == 16) inc=increment[1];
    else if (size == 25) inc=increment[2];
		btn=new JButton[size][size];
		for(int ctr=0, X=startX, Y=startY; ctr<size; ctr++, Y+=inc, X=startX){
			for(int count=0; count<size; count++, X+=inc){
				String img="normal";
				if(sudokuArray[ctr][count][1]==0)
					img="given";
				btn[ctr][count]=gp.gameButton(pane, "img/box/common/"+img+"/"+sudokuArray[ctr][count][0]+".png", X, Y, inc, inc);
				if(setCursor && img.equals("normal"))
					btn[ctr][count].setCursor(new Cursor(12));
				else
					btn[ctr][count].setCursor(new Cursor(0));
				if(sudokuArray[ctr][count][0]!=0)
					ans++;
				}
			}
		board=gp.addLabel(pane,"img/board/"+size+"x"+size+".png",0,84);
    */
    boardg = new BoardGraphics(520, pane.getHeight());
		boardg.setBounds(0, 0, 520, pane.getHeight());
    boardg.setBoard(sudokuArray);
    boardg.repaint();
    pane.add(boardg);
		}
	protected JButton getButton(){
		return btn[btnX][btnY];
		}
	protected int getStatus(int x, int y){
		return sudokuArray[x][y][1];
		}
	protected int[][][] getSudokuArray(){
		return sudokuArray;
		}
	protected void changeCursor(){
		for(int row=0; row<size; row++){
			for(int col=0; col<size; col++){
				btn[row][col].setCursor(new Cursor(0));
				btn[col][row].setCursor(new Cursor(0));
				}
			}
		}
	protected void changePic(){
    /*
		for(int row=0; row<size; row++){
			for(int col=row; col<size; col++){
				if(sudokuArray[row][col][1]==1)
					sudokuArray[row][col][0]=0;
				if(sudokuArray[col][row][1]==1)
					sudokuArray[col][row][0]=0;
				}
			}
    */
    boardg.setBoard(sudokuArray);
    boardg.repaint();
		}
    
	protected void setSudoku(int solution[][][]){
		sudokuArray=solution;
    
    boardg.setBoard(sudokuArray);
    boardg.repaint();
		}
	protected void setSudokuArray(int value, boolean isAns, int x, int y){
		if(sudokuArray[x][y][0]==0 && value!=0)
			ans++;
		if(sudokuArray[x][y][0]!=0 && value==0)
			ans--;
		sudokuArray[x][y][0]=value;
		int num=1;
		if(!isAns && value!=0)
			num=0;
		sudokuArray[x][y][1]=num;
		sudokuArray[x][y][0]=value;
    
    boardg.setBoard(sudokuArray);
    boardg.repaint();
		}
  
	protected int getValue(int x, int y){
		return sudokuArray[x][y][0];
		}
	protected int getSize(){
		return size;
		}
	protected int getAns(){
		return ans;
		}
	protected void decompose(){
		pane.removeAll();
		board=null;
		sudokuArray=null;
		btn=null;
		gp=null;
		}
	}