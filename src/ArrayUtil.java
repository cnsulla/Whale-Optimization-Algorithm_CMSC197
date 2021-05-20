public class ArrayUtil {
  public static int[][][] copy(int[][][] ar) {
    int[][][] out = new int[ar.length][][];
    for (int i = 0; i < ar.length; ++i) {
      out[i] = new int[ar[i].length][];
      for (int j = 0;j < ar[i].length; ++j) {
        out[i][j] = new int[ar[i][j].length];
        for (int k = 0; k < ar[i][j].length; ++k) {
          out[i][j][k] = ar[i][j][k];
        }
      }
    }
    
    return out;
  }
  
  public static void printSudoku(int[][][] ar) {
    for (int i = 0; i < ar.length; ++i) {
      for (int j = 0; j < ar.length; ++j) {
        System.out.print(ar[j][i][0] + ", ");
      }
      System.out.println();
    }
  }
  
  
  public static void countNumbers(int[][][] sudoku) {
    int size = sudoku.length;
    int rows[][] = new int[size][size];
    int cols[][] = new int[size][size];
    
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        int val = sudoku[i][j][0] - 1;
        rows[j][val]++;
        cols[i][val]++;
      }
    }
    
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        System.out.println("row " + i + " has " + rows[i][j] + " intances of " + (j + 1));
        System.out.println("col " + i + " has " + cols[i][j] +  " intances of " + (j + 1));
      }
    }
  }
}