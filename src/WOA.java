import java.util.ArrayList;
import java.util.Random;
import fitnessfunc.ObjectiveFunction;

public class WOA implements Runnable {
  public static final double B_CONSTANT = 1.;
  public final int population;
  public final int maximumIteration;
  public final int dimensions;
  public final int lowerBound;
  public final int upperBound;
  public final int sudokuWidth;
  private int[][][] sudoku;
  private SubGrid[] bestSoln;
  private double bestFit;
  private int iter;
  private ObjectiveFunction fitnessFunc;
  /*
   *  sudokuWidth should be equal to x if the 
   *  sudoku grid is x * x
   *  sudokuWidth also dictates the dimensions of the WOA
   *  since each subgrid is used as a dimension
   */
  public WOA(int[][][] sudoku, ObjectiveFunction fitnessFunc,
          int sudokuWidth, int population, int maximumIteration) {
    this.dimensions = sudokuWidth;
    this.population = population;
    this.maximumIteration = maximumIteration;
    this.lowerBound = 1;
    this.upperBound = sudokuWidth;
    this.sudokuWidth = sudokuWidth;
    this.sudoku = sudoku;
    this.fitnessFunc = fitnessFunc;
  }
  
  @Override
  public void run() {
    iter = 0;
    Random random = new Random(System.currentTimeMillis());
    ArrayUtil.printSudoku(sudoku);
    bestSoln = subgridify(ArrayUtil.copy(sudoku), sudokuWidth);
    System.out.println("filled");
    ArrayUtil.printSudoku(bestSoln[0].getSudoku());
    //change to positive infinity if minimize
    bestFit = Double.NEGATIVE_INFINITY; 
      
    SubGrid[][] pop = new SubGrid[population][sudokuWidth];
    //initial population initialization
    for (int i = 0; i < population; ++i) {
      pop[i] = subgridify(ArrayUtil.copy(sudoku), sudokuWidth);
      //note the subgrid is randomized at SubGrid initialization
    }
    
    //these variables remain constant and i dont like floating point division
    double aDelta = 2. / maximumIteration;
    double a2Delta = -1. / maximumIteration;
    
    //WOA main loop 
    while (iter < maximumIteration) {
      iter++;
      
      for (int i = 0; i < population; ++i) {
        //pops on the same row of the array share a sudoku
        double fitness = fitnessFunc.getFitness(pop[i][0].getSudoku());
        //change to < if minimize
        if (fitness > bestFit) {
          bestSoln = pop[i];
          bestFit = fitness;
        }
      }
      
      //eq 2.3 a linearly decreases from 2 to 0
      double a = 2. - iter * aDelta;
      //i dont get this. the reading(eq 2.5) just says l is random from [-1, 1]
      double a2 = -1. + iter * a2Delta;
      
      for (int i = 0; i < population; ++i) {
        double r1 = random.nextDouble();
        double r2 = random.nextDouble();
        
        double A = 2. * a * r1 - a; //eq 2.3
        double C = 2. * r2; //eq 2.4
        
        //parameter for encircling behaviour
        double l = (a2 - 1) * random.nextDouble() + 1;
        
        double p = random.nextDouble();
        for (int j = 0; j < dimensions; ++j) {
          //eq 2.6
          if (p < 0.5) {
            SubGrid[] lead = bestSoln;
            //use random leader if A >= 1
            if (Math.abs(A) >= 1.) {
              int leadRandIX = random.nextInt(population);
              lead = pop[leadRandIX];
            }
            int updateIX = pop[i][j].randomNonStartIndex();
            /*
             * updateIX should only be < 0 if all values on the SubGrid
             * are given, so there would be no need to update the value
             * if updateIX < 0
             */
            if (updateIX >= 0) {
              int leaderVal = lead[j].getValue(updateIX);
              int cpopVal = pop[i][j].getValue(updateIX);
              double D = Math.abs(C * leaderVal - cpopVal);
              //agents[i][j] = lead[j] - A * D
              //SudokuBee uses ceiling
              int newValue = (int)Math.ceil(leaderVal - A * D);
              /*
               * idk maybe this should be changed #######################
               * the WOA algorithm given enforces the bounds before
               * updating the value but then it also enforces the bounds
               * before checking the fitness so i dont think this will
               * change anything
               */
              if (newValue < lowerBound) newValue = lowerBound;
              if (newValue > upperBound) newValue = upperBound;
              pop[i][j].setValue(updateIX, newValue);
            }
          }
          //encircling behaviour
          else {
            int ix = pop[i][j].randomNonStartIndex();
            if (ix >= 0) {
              int leaderVal = bestSoln[j].getValue(ix);
              int cpopVal = pop[i][j].getValue(ix);
              double D2Lead = Math.abs(leaderVal - cpopVal);
              //eq 2.5
              int newValue = (int)Math.ceil(
                D2Lead * Math.exp(B_CONSTANT * l) * 
                Math.cos(2 * Math.PI * l) + leaderVal
              );
              pop[i][j].setValue(ix, newValue);
            }
          }
        }
      }
    }
  }
  
  public boolean isDone() {
    return iter >= maximumIteration;
  }
  
  public int[][][] getBestSolution() {
    return bestSoln[0].getSudoku();
  }
  
  public String getCycles() {
    return iter + "";
  }
  
  public double getFitness() {
    return bestFit;
  }
  
  public void decompose() {
    //let the garbage collector handle it
  }
  
  private SubGrid[] subgridify(int[][][] sudoku, int w) {
    SubGrid[] output = new SubGrid[w];
    int gridW = (int)Math.sqrt(w);
    
    int count = 0;
    for (int i = 0; i < w; i += gridW) {
      for (int j = 0; j < w; j += gridW) {
        //x is j, y is i
        output[count++] = new SubGrid(sudoku, j, i, gridW);
      }
    }
    return output;
  }
  
  private class SubGrid {
    private final int[][][] sudoku;
    public final int x;
    public final int y;
    public final int w;
    public final boolean constrained;
    private ArrayList<String> nonStarts;
    
    //subgrid expects sudoku to be a valid sudoku array
    public SubGrid(int[][][] sudoku, int x, int y, int w, boolean constrain) {
      this.sudoku = sudoku;
      this.constrained = constrain;
      this.x = x;
      this.y = y;
      this.w = w;
      nonStarts = new ArrayList<String>();
      for (int i = x; i < x + w; ++i) {
        for (int j = 0; j < y + w; ++j) {
          if (sudoku[i][j][1] == 1) { 
            //add to nonStarts for picking random index later
            int ix = (i - x) + (j - y) * w;
            nonStarts.add(ix + "");
            if (!constrained) {
              //if not constrained fill with random value
              sudoku[i][j][0] = (int)(Math.random() * w * w) + 1;
            }
          }
        }
      }
      
      if (constrained) {
        fillConstrained();
      }
    }
    
    public SubGrid(int[][][] sudoku, int x, int y, int w) {
      this(sudoku, x, y, w, true);
    }
    
    public int randomNonStartIndex() {
      if (nonStarts.size() == 0) return -1;
      int ix = (int)(Math.random() * nonStarts.size());
      try {
        return Integer.parseInt(nonStarts.get(ix));
      } catch (Exception e) {
        return -1;
      }
    }
    
    public int[][][] getSudoku() {
      return sudoku;
    }
    
    public void fillConstrained() {
      boolean[] filled = new boolean[w * w];
      for (int i = x; i < x + w; ++i) {
        for (int j = y; j < y + w; ++j) {
          if (sudoku[i][j][0] > 0) {
            filled[sudoku[i][j][0] - 1] = true;
          }
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
          if (sudoku[i][j][1] == 1) { //if not given assign random value
            //get random value from toFill then remove it from toFill
            int randIX = (int)(Math.random() * toFill.size());
            try {
              int val = Integer.parseInt(toFill.remove(randIX));
              sudoku[i][j][0] = val;
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
      
      for (int i = x; i < x + w; ++i) {
        for (int j = y; j < y + w; ++j) {
          if (sudoku[i][j][0] == val) { //swap
            isGiven = (sudoku[i][j][1] == 0);
            if (!isGiven) { //only swap if the number is not a given
              sudoku[i][j][0] = currVal; //assign old value to swap target
              sudoku[x + xOff][y + yOff][0] = val; // assign new value
            }
            //only search the first instance of the number
            return;
          }
        }        
      }
      
    }
    
    public int width() {
      return w;
    }
    
    public int size() {
      return w * w;
    }
  }
}