import java.util.Random;
class GenerateSudoku{
  private int[][][] sudoku;
  private Random rand=new Random();
  public GenerateSudoku(int[][][] sudoku, int percent){
    this.sudoku=sudoku;
    double p = percent * 0.01;
    int lensq = sudoku.length * sudoku.length;
    int snum = (int)(p * lensq);
    
    int[] ar = new int[lensq];
    for (int i = 0; i < lensq; ++i) {ar[i] = i;}
    //shuffle the thing
    for (int i = 0; i < lensq; ++i) {
      int j = (int)(Math.random() * (lensq - i)) + i;
      int temp = ar[j];
      ar[j] = ar[i];
      ar[i] = temp;
    }
    
    //set first snum numbers as start squares;
    for (int i = 0; i < lensq; ++i) {
      int x = ar[i] % sudoku.length;
      int y = (int)(ar[i] / sudoku.length);
      if (i < snum) {
        this.sudoku[x][y][1] = 0;
      }
      else {
        this.sudoku[x][y][0] = 0;
				this.sudoku[x][y][1] = 1;
      }
    }
    /*
		for(int ctr=0; ctr<sudoku.length; ctr++){
			for(int ct=ctr; ct<sudoku.length; ct++){
				double first=rand.nextDouble(), second=rand.nextDouble();
				if(first>1-second){
					this.sudoku[ct][ctr][0]=0;
					this.sudoku[ct][ctr][1]=1;
					}
				else{
					this.sudoku[ct][ctr][1]=0;
					}
				if(ct!=ctr && first>1-second){
					this.sudoku[ctr][ct][0]=0;
					this.sudoku[ctr][ct][1]=1;
					}
				else if(ct!=ctr){
					this.sudoku[ctr][ct][1]=0;
					}
				}
			}
     */
  }
  
  protected int[][][] getSudoku(){
    return sudoku;
  }
  
  private void sop(Object obj){
    System.out.println(obj+"");
  }
}