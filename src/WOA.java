import java.util.ArrayList;
import java.util.Random;
import fitnessfunc.*;

public class WOA implements Runnable {
  public static final double B_CONSTANT = 0.2;
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
    bestSoln = subgridify(ArrayUtil.copy(sudoku), sudokuWidth, true);
    //change to positive infinity if minimize
    bestFit = Double.NEGATIVE_INFINITY; 
      
    SubGrid[][] pop = new SubGrid[population][sudokuWidth];
    //initial population initialization
    for (int i = 0; i < population; ++i) {
      pop[i] = subgridify(ArrayUtil.copy(sudoku), sudokuWidth, true);
      //note the subgrid is randomized at SubGrid initialization
    }
    
    //these variables remain constant and i dont like floating point division
    double aDelta = 2. / maximumIteration;
    double a2Delta = -1. / maximumIteration;
    
    //WOA main loop 
    while (iter < maximumIteration && bestFit < 1.) {
      iter++;
      
      double weightsTotal = 0.;
      for (int i = 0; i < population; ++i) {
        //pops on the same row of the array share a sudoku
        double fitness = fitnessFunc.getFitness(pop[i][0].getSudoku());
        //the order wont somehow get switched right?
        pop[i][0].setWeight(fitness);
        weightsTotal += fitness;
        
        //change to < if minimize
        if (fitness > bestFit) {
          //bestSoln = pop[i];
          bestSoln = subgridify(ArrayUtil.copy(pop[i][0].getSudoku()), 
                            sudokuWidth, false);
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
            //use random leader depending on ranking
              double rand = random.nextDouble() * weightsTotal;
              for (int k = 0; k < population; ++k) {
                if (pop[k][0].getWeight() < rand) {
                  lead = pop[k];
                  break;
                }
                rand -= pop[k][0].getWeight();
              }
            }
            
            int updateIX = pop[i][j].randomNonStartIndex();
            /*
             * updateIX should only be < 0 if all values on the SubGrid
             * are given, so there would be no need to update the value
             * if updateIX < 0
             */
            if (updateIX >= 0) {
              double leaderVal = lead[j].getValue(updateIX);
              double cpopVal = pop[i][j].getValue(updateIX);
              double D = Math.abs(C * leaderVal - cpopVal);
              //agents[i][j] = lead[j] - A * D
              //SudokuBee uses ceiling
              double newValue = leaderVal - A * D;
              /*
               * idk maybe this should be changed #######################
               * the WOA algorithm given enforces the bounds before
               * updating the value but then it also enforces the bounds
               * before checking the fitness so i dont think this will
               * change anything
               */
              if (newValue > upperBound) newValue = upperBound;
              if (newValue < lowerBound) newValue = lowerBound;
              pop[i][j].setValue(updateIX, newValue);
            }
          }
          //encircling behaviour
          else {
            int ix = pop[i][j].randomNonStartIndex();
            if (ix >= 0) {
              double leaderVal = bestSoln[j].getValue(ix);
              double cpopVal = pop[i][j].getValue(ix);
              double D2Lead = Math.abs(leaderVal - cpopVal);
              //eq 2.5
              double newValue = D2Lead * Math.exp(B_CONSTANT * l) * 
                                        Math.cos(2 * Math.PI * l) + leaderVal;
              if (newValue > upperBound) newValue = upperBound;
              if (newValue < lowerBound) newValue = lowerBound;
              pop[i][j].setValue(ix, newValue);
            }
          }
        }
      }
    }
  }
  
  public boolean isDone() {
    return iter >= maximumIteration || bestFit >= 1.;
  }
  
  public int[][][] getBestSolution() {
    return ArrayUtil.copy(bestSoln[0].getSudoku());
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
  
  private SubGrid[] subgridify(int[][][] sudoku, int w, boolean fill) {
    SubGrid[] output = new SubGrid[w];
    int gridW = (int)Math.sqrt(w);
    
    boolean constrain =  !(fitnessFunc instanceof SumProductNoConstraint);
    int count = 0;
    for (int i = 0; i < w; i += gridW) {
      for (int j = 0; j < w; j += gridW) {
        //x is j, y is i
        output[count++] = new SubGrid(sudoku, j, i, gridW, constrain, fill);
      }
    }
    return output;
  }
  
  private class SubGrid {
    private final int[][][] sudoku;
    private double[] doubleValues;
    public final int x;
    public final int y;
    public final int w;
    public final boolean constrained;
    private ArrayList<String> nonStarts;
    private double weight = 0.;
    
    //subgrid expects sudoku to be a valid sudoku array
    public SubGrid(int[][][] sudoku, int x, int y, int w, 
            boolean constrain, boolean fill) {
      this.sudoku = sudoku;
      this.constrained = constrain;
      this.x = x;
      this.y = y;
      this.w = w;
      nonStarts = new ArrayList<String>();
      for (int i = x; i < x + w; ++i) {
        for (int j = y; j < y + w; ++j) {
          if (sudoku[i][j][1] == 1) { 
            //add to nonStarts for picking random index later
            int ix = (i - x) + (j - y) * w;
            nonStarts.add(ix + "");
            if (!constrained && fill) {
              //if not constrained fill with random value
              sudoku[i][j][0] = (int)(Math.random() * w * w) + 1;
            }
          }
        }
      }
      
      if (constrained && fill) {
        fillConstrained();
      }
      
      buildDoubleValues();
    }
    
    public SubGrid(int[][][] sudoku, int x, int y, int w) {
      this(sudoku, x, y, w, true, true);
    }
    
    public void setWeight(double weight) {
      this.weight = weight;
    }
    
    public double getWeight() {
      return this.weight;
    }
    
    private void buildDoubleValues() {
      doubleValues = new double[size()];
      for (int i = 0; i < size(); ++i) {
        int xOff = i % w;
        int yOff = (int)(i / w);
        doubleValues[i] = (double)sudoku[this.x + xOff][this.y + yOff][0];
      }
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
    
    public ArrayList<String> nonStarts() {
      return nonStarts;
    }
    
    public void fillConstrained() {
      boolean[] filled = new boolean[w * w];
      for (int i = x; i < x + w; ++i) {
        for (int j = y; j < y + w; ++j) {
          if (sudoku[i][j][0] > 0 && sudoku[i][j][1] == 0) {
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
      
      if (toFill.isEmpty()) return;
      
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
    
    public double getValue(int ix) {
      //int xOff = ix % w;
      //int yOff = (int)(ix / w);
      //return sudoku[x + xOff][y + yOff][0];
      return doubleValues[ix];
    }
    
    public void setValue(int ix, double dval) {
      if (dval < 0 || dval > size()) return;
      
      doubleValues[ix] = dval;
      int val = (int)Math.ceil(dval);
      
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