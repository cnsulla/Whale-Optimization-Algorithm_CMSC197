package fitnessfunc;

public class SumProductNoConstraint implements ObjectiveFunction{
@Override
  public double getFitness(int[][][] sudoku) {
    int size = sudoku.length;
    int[] rowSums = new int[size];
    int[] colSums = new int[size];
    int[] subSums = new int[size];
    for(int i = 0; i < size; i++){
        for(int j = 0;  j < size; j++){
            rowSums[i] += sudoku[i][j][0];
            colSums[j] += sudoku[i][j][0];
            subSums[(i/size)*size + j/size] += sudoku[i][j][0];
        }
    }   

    int rowSum = 0;
    for(int i = 0; i < rowSums.length; i++){
        rowSum+= rowSums[i];
    }

    int colSum = 0;
    for(int i = 0; i < colSums.length; i++){
        colSum+= colSums[i];
    }

    int subSum = 0;
    for(int i = 0; i < subSums.length; i++){
        subSum+= subSums[i];
    }
    
    double correctSum = size * (size + 1) / 2.0;
    double fitness = 1.0 / (1 + Math.abs(rowSum - correctSum) + Math.abs(colSum - correctSum) + Math.abs(subSum - correctSum));
    
    return fitness;
  }
}