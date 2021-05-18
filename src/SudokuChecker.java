import java.util.LinkedList;

public class SudokuChecker { //Sudoku Checker and various stuff galore
  public static int check(int[][][] sudoku, int percentage) {
    LinkedList<Integer> list = new LinkedList<Integer>();
    
    float p = percentage * 0.01f;
    int numfilled = 0;
    //check rows
    for (int i = 0; i < sudoku.length; ++i) {
      for (int j = 0; j < sudoku.length; ++j) {
        if (sudoku[i][j][0] != 0) {
          if (list.contains(sudoku[i][j][0])) return 1;
          list.add(sudoku[i][j][0]);
          numfilled++;
        }
      }
      list.clear();
    }
      list.clear();
    
    if (numfilled > (sudoku.length * sudoku.length) * p) return 2;
    
    //check columns
    for (int i = 0; i < sudoku.length; ++i) {
      for (int j = 0; j < sudoku.length; ++j) {
        if (sudoku[j][i][0] != 0) {
          if (list.contains(sudoku[j][i][0])) return 1;
          list.add(sudoku[j][i][0]);
        }
      }
      list.clear();
    }
      list.clear();
    
    int sx = 0;
    int sy = 0;
    int gridw = (int)Math.sqrt(sudoku.length);
    
    //check subgrids
    while (sx < sudoku.length) {
      sy = 0;
      while (sy < sudoku.length) {
        for (int i = sx; i < sx + gridw; ++i) {
          for (int j = sy; j < sy + gridw; ++j) {
            if (sudoku[i][j][0] != 0) {
              if (list.contains(sudoku[j][i][0])) {
                return 1;
              } 
              list.add(sudoku[i][j][0]);
            }
          }
        }
        list.clear();
        sy += gridw;
      }
      sx += gridw;
    }
    return 0;
  }
  
  public static int[][][] prepareForTheThingToHappen(int[][][] b) {
    for (int i = 0; i < b.length; ++i) {
      for (int j = 0; j < b.length; ++j) {
        if (b[i][j][0] != 0) b[i][j][1] = 0;
      }
    }
    
    return b;
  }
  
  public static int[][][] dotheThing(int[][][] ar, int percentage) {
    double p = percentage * 0.01;
    int lensq = ar.length * ar.length;
    int snum = (int)(p * lensq);
    
    int snumc = 0;
    for (int i = 0; i < ar.length; ++i) {
      for (int j = 0; j < ar.length; ++j) {
        if (ar[i][j][1] == 0) snumc++;
      }
    }
    
    int[] arr = new int[lensq];
    for (int i = 0; i < lensq; ++i) {
      arr[i] = i;
    }
    //shuffle the thing
    for (int i = 0; i < lensq; ++i) {
      int j = (int)(Math.random() * (lensq - i)) + i;
      int temp = arr[j];
      arr[j] = arr[i];
      arr[i] = temp;
    }
    
    //set first snum numbers as start squares;
    for (int i = 0; i < lensq; ++i) {
      int x = arr[i] % ar.length;
      int y = (int)(arr[i] / ar.length);
      if (snumc < snum) {
        ar[x][y][1] = 0;
        snumc++;
      }
      else if (ar[x][y][1] != 0) {
        ar[x][y][0] = 0;
				ar[x][y][1] = 1;
      }
    }
    
    return ar;
  }
}