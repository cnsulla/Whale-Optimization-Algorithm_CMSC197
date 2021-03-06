import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Graphics;

public class Animation{
  private JPanel pane;
  private JLabel board;
  private int sudokuArray[][][];
  private int size;
  private int startX, startY;
  private int inc;
  private int btnX, btnY;
  private int ans;
  private int increment[]={56,31,20};
  protected JLabel btn[][][];
  private generalPanel gp=new generalPanel();
  ///
  BoardGraphics boardg;
  public Animation(int sudokuArray[][][], JPanel pane){
    this.pane=pane;
    this.sudokuArray=sudokuArray;
    pane.setVisible(true);
    setConstants();
  }
  
  private void setConstants(){
    size=sudokuArray.length;
    /** legacy code
		startX=size/6+6;
		startY=86;
    if (size == 9 ) inc=increment[0];
    else if (size == 16) inc=increment[1];
    else if (size == 25) inc=increment[2];
		//inc=increment[size/3-2];
		btn=new JLabel[size][size][size+1];
		for(int ctr=0, X=startX, Y=startY; ctr<size; ctr++, Y+=inc, X=startX){
			for(int count=0; count<size; count++, X+=inc){
				String img="normal";
				if(sudokuArray[ctr][count][1]==0)
					img="given";
				for(int counter=0; counter<size+1; counter++)
					btn[ctr][count][counter]=gp.addInvisibleLabel(pane, "img/box/common/"+img+"/"+counter+".png", X, Y, inc, inc);
				if(img.equals("given"))
					btn[ctr][count][sudokuArray[ctr][count][0]].setVisible(true);
				}
			}
		board=gp.addLabel(pane,"img/board/"+size+"x"+size+".png",0,84);
    **/
    boardg = new BoardGraphics(520, pane.getHeight());
    boardg.setBounds(0, 0, 520, pane.getHeight());
    pane.add(boardg);
  }
  
  protected int[][][] getSudokuArray(){
    return sudokuArray;
  }
  
  protected void changePic(int solution[][][]){
    //sorry no more cool animation
    /*
		for(int row=0, row2=size-1; row<size; row++, row2--){
			for(int col=row, col2=size-1; col<size; col++, col2--){
				if(sudokuArray[row][col][1]==1){
					btn[row][col][sudokuArray[row][col][0]].setVisible(false);
					btn[row][col][solution[row][col][0]].setVisible(true);
					}
				if(sudokuArray[col][row][1]==1){
					btn[col][row][sudokuArray[col][row][0]].setVisible(false);
					btn[col][row][solution[col][row][0]].setVisible(true);
					}
				if(sudokuArray[row2][col2][1]==1){
					btn[row2][col2][sudokuArray[row2][col2][0]].setVisible(false);
					btn[row2][col2][solution[row2][col2][0]].setVisible(true);
					}
				if(sudokuArray[col2][row2][1]==1){
					btn[col2][row2][sudokuArray[col2][row2][0]].setVisible(false);
					btn[col2][row2][solution[col2][row2][0]].setVisible(true);
					}
				}
			}
    */
    boardg.setBoard(solution);
    boardg.repaint();
    sudokuArray=solution;
  }
  
  protected void setSudoku(int solution[][][]){
    sudokuArray=solution;
  }
  
  protected void decompose(){
    pane.removeAll();
    board=null;
    sudokuArray=null;
    btn=null;
    gp=null;
    pane.setVisible(false);
  }
}