package fitnessfunc;

public class MissingValues implements ObjectiveFunction {
  @Override
  public double getFitness(int[][][] sudoku) {
    int size = sudoku.length;
    int rows[][] = new int[size][size];
    int cols[][] = new int[size][size];
    
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        int val = sudoku[i][j][0] - 1;
        rows[i][val]++;
        cols[j][val]++;
      }
    }
    
    int missingNums = 0;
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (rows[i][j] == 0) missingNums++;
        if (cols[i][j] == 0) missingNums++;
      }
    }
    
    return 1. / (1 + missingNums);
  }
}