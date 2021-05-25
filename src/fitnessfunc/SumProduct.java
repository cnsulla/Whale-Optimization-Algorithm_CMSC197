package fitnessfunc;

public class SumProduct implements ObjectiveFunction{
@Override
  public double getFitness(int[][][] sudoku) {
    int size = sudoku.length;
    int[] rowSums = new int[size];
    int[] colSums = new int[size];
    for(int i = 0; i < size; i++){
        for(int j = 0;  j < size; j++){
            rowSums[i] += sudoku[i][j][0];
            colSums[j] += sudoku[i][j][0];
        }
    }   

    double correctSum = size * (size + 1) / 2.0;
    int rowSum = 0;
    for(int i = 0; i < rowSums.length; i++){
        rowSum+= Math.abs(rowSums[i] - correctSum);
    }

    int colSum = 0;
    for(int i = 0; i < colSums.length; i++){
        colSum+= Math.abs(colSums[i] - correctSum);
    }
    
    double fitness = 1.0 / (1 + Math.abs(rowSum) + Math.abs(colSum));
    
    return fitness;
  }
}