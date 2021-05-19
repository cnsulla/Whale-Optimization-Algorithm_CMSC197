import java.util.ArrayList;

public class WOA {
  
  private static class SubGrid {
    private final int[][][] sudoku;
    public final int x;
    public final int y;
    public final int w;
    public final boolean constrained;
    
    //subgrid expects sudoku to be a valid sudoku array
    public SubGrid(int[][][] sudoku, int x, int y, int w, boolean constrain) {
      this.sudoku = sudoku;
      this.constrained = constrain;
      this.x = x;
      this.y = y;
      this.w = w;
      if (constrain)
        fillConstrained();
      else {
        for (int i = x; i < x + w; ++i) {
          for (int j = 0; j < y + w; ++j) {
            if (sudoku[i][j][1] == 0) { //if not given assign random value
              sudoku[i][j][0] = (int)(Math.random() * w * w) + 1;
            }
          }
        }
      }
    }
    
    public SubGrid(int[][][] sudoku, int x, int y, int w) {
      this(sudoku, x, y, w, true);
    }
    
    public void fillConstrained() {
      boolean[] filled = new boolean[w * w];
      for (int i = x; i < x + w; ++i) {
        for (int j = y; j < y + w; ++j) {
          if (sudoku[i][j][0] > 0)
            filled[sudoku[i][j][0] - 1] = true;
        }
      }
      ArrayList<String> toFill = new ArrayList<>(); //String lul
      for (int i = 0; i < filled.length; ++i) {
        if (!filled[i])  {
          toFill.add((i + 1) + "");
        }
      }
      
      for (int i = x; i < x + w; ++i) {
        for (int j = y; j < y + w; ++j) {
          if (sudoku[i][j][1] == 0) { //if not given assign random value
            //get random value from toFill then remove it from toFill
            int randIX = (int)(Math.random() * toFill.size());
            try {
              sudoku[i][j][0] = Integer.parseInt(toFill.remove(randIX));
            } catch (NumberFormatException e) {
              //this shouldnt happen unless something very wrong happened
              e.printStackTrace();
            }
          }
        }
      }
    }
    
    public int getValue(int ix) {
      int xOff = ix % w;
      int yOff = (int)(ix / w);
      return sudoku[x + xOff][y + yOff][0];
    }
    
    public void setValue(int ix, int val) {
      if (val < 0 || val > size()) return;
      
      int xOff = ix % w;
      int yOff = (int)(ix / w);
      
      if (!constrained) { //if unconstrained no swapping is done
        sudoku[x + xOff][y + yOff][0] = val;
        return;
      }
      
      int currVal = sudoku[x + xOff][y + yOff][0];
      if (currVal == val) return; //no need to swap or do anything
      boolean isGiven = false;
      boolean found = false;
      for (int i = x; i < x + w; ++i) {
        for (int j = y; j < y + w; ++j) {
          if (sudoku[i][j][0] == val) { //swap
            found = true;
            isGiven = (sudoku[i][j][1] == 1);
            if (!isGiven) //only swap if the number is not a given
              sudoku[i][j][0] = currVal; //assign old value to swap target
            break;
          }
        }
        if (found) break; //break only breaks out of the inner loop
                          //this looks bad but idk how to change
      }
      
      if (!isGiven) //only swap if the number is not a given
        sudoku[x + xOff][y + yOff][0] = val; // assign new value
    }
    
    public int width() {
      return w;
    }
    
    public int size() {
      return w * w;
    }
  }
}