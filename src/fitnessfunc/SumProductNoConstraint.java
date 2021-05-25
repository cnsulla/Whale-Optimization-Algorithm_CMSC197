package fitnessfunc;

public class SumProductNoConstraint implements ObjectiveFunction{
@Override
  public double getFitness(int[][][] sudoku) {
    int size = sudoku.length;
    int[] rowSums = new int[size];
    int[] colSums = new int[size];
    int[] subSums = new int[size];
    int subGridW = (int)Math.sqrt(size);
    for(int i = 0; i < size; i++){
        for(int j = 0;  j < size; j++){
            rowSums[i] += sudoku[i][j][0];
            colSums[j] += sudoku[i][j][0];
            subSums[(i/subGridW)*subGridW + j/subGridW] += sudoku[i][j][0];
        }
    }   

    double correctSum = size * (size + 1) / 2.0;
    int rowSum = 0;
    for(int i = 0; i < rowSums.length; i++){
        rowSum+= Math.abs(rowSums[i] - correctSum);
    }

    int colSum = 0;
    for(int i = 0; i < colSums.length; i++){
        colSum += Math.abs(colSums[i] - correctSum);
    }

    int subSum = 0;
    for(int i = 0; i < subSums.length; i++){
        subSum+= Math.abs(subSums[i] - correctSum);
    }
    
    double fitness = 1.0 / (1 + Math.abs(rowSum) + Math.abs(colSum) + Math.abs(subSum));
    
    return fitness;
  }
}