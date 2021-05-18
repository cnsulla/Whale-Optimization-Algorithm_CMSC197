public class SampleSudoku{
  Subgrid[] subgrids;
  int subgridWidth;
  public SampleSudoku(int dimension){
    subgrids = new Subgrid[dimension];
    subgridWidth =(int)Math.sqrt(dimension);
    for(int i = 0; i < dimension; i++){
      subgrids[i] = new Subgrid();
    }
  }
  public void setValue(int column, int row, int value){
    subgrids[subgridIndex(column, row)].setValue(
            column % subgridWidth, row % subgridWidth, value);
  }
  
  public int getValue(int column, int row){
    return subgrids[subgridIndex(column, row)].getValue(column % subgridWidth, row % subgridWidth);
  }
  
  private int subgridIndex(int column, int row){
    return row / subgridWidth * subgridWidth + column/subgridWidth;
  }
}