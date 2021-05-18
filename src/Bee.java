import java.util.Random;
import java.math.BigInteger;
class Bee{
  private int[][][] solution;
  private double fitness;
  private Subgrid[] subgrid;
  private Random rand=new Random();
  private BigInteger lenFactorial;
  private BigInteger summation;
  private int subgridW = 0;
  private int penaltyType = 0;
  private static final BigInteger NINE_FACTORIAL = new BigInteger("362880");
  private static final BigInteger SIXTEEN_FACTORIAL = new BigInteger("20922789888000");
  private static final BigInteger TWENTYFIVE_FACTORIAL = new BigInteger("15511210043330985984000000");
  private static final BigInteger NINE_SUMMATION = new BigInteger("36");
  private static final BigInteger SIXTEEN_SUMMATION = new BigInteger("136");
  private static final BigInteger TWENTYFIVE_SUMMATION = new BigInteger("325");
  
  public static final int MISSINGNO_PENALTY = 0;
  public static final int SUMPROD_PENALTY = 1;
  public static final int SUMPROD_PENALTY_NO_SUBGRID = 2;
  
  public Bee(Subgrid[] subgrid, int penaltyType){
    this.subgrid=subgrid;
    this.penaltyType = penaltyType;
  }
  
  public Bee(int[][][] prob, Subgrid[] subgrid, int penaltyType){
    this(subgrid, penaltyType);
    
    solution=prob;
    for(int ctr=0; ctr<subgrid.length; ctr++){
      int[] needed=neededNumbers(subgrid[ctr]);
      for(int y=subgrid[ctr].getStartY(), indexRand=needed.length, limY=y+subgrid[ctr].getDimY(); y<limY; y++){
        for(int x=subgrid[ctr].getStartX(), limX=x+subgrid[ctr].getDimX(); x<limX; x++){
          if(solution[y][x][1]==1){
            int tmp=rand.nextInt(indexRand);
            solution[y][x][0]=needed[tmp];
            needed[tmp]=needed[indexRand-1];
            needed[indexRand-1]=solution[y][x][0];
            indexRand=indexRand-1;
          }
        }
      }
    }
    doSizeThingIDK(prob);
  }
  
  private void doSizeThingIDK(int[][][] prob) {
    if (prob.length == 25) {
      lenFactorial = TWENTYFIVE_FACTORIAL;
      summation = TWENTYFIVE_SUMMATION;
    }
    else if (prob.length == 16) {
      lenFactorial = SIXTEEN_FACTORIAL;
      summation = SIXTEEN_SUMMATION;
    }
    else if (prob.length == 9) {
      lenFactorial = NINE_FACTORIAL;
      summation = SIXTEEN_SUMMATION;
    }
    else {
      lenFactorial = BigInteger.ONE;
      summation = BigInteger.ZERO;
      for (int i = prob.length; i > 0; i--) {
        lenFactorial = lenFactorial.multiply(BigInteger.valueOf(i));
        summation = summation.add(BigInteger.valueOf(i));
      }
    }
  }
  
  protected void copyProblem(int[][][] prob){
    solution=prob;
    doSizeThingIDK(prob);
  }
  
  protected void printResult(){
    for(int ctr=0; ctr<solution.length; ctr++){
      for(int ctr1=0; ctr1<solution[ctr].length; ctr1++){
        System.out.print(solution[ctr][ctr1][0]+"");
      }
      System.out.println("");
    }
  }
  
  protected int getPenaltyValue(){
    switch(penaltyType) {
      case MISSINGNO_PENALTY:
       return missigNoPenalty();
      case SUMPROD_PENALTY:
       BigInteger bi = sumProductPenalty();
       int val;
       try {
         val = bi.intValueExact();
       } catch (java.lang.ArithmeticException e) {
         val = Integer.MAX_VALUE;
       }
       return val;
      case SUMPROD_PENALTY_NO_SUBGRID:
       BigInteger sps = sumProdNoSubgrid();
       int spsVal;
       try {
         spsVal = sps.intValueExact();
       } catch (java.lang.ArithmeticException e) {
         spsVal = Integer.MAX_VALUE;
       }
       return spsVal;
    }
    
    return 1;
  }
  
  protected int[][][] getSolution(){
    return solution;
  }
  
  protected void setFitness(double fit){
    fitness=fit;
  }
  
  protected double getFitness(){
    return fitness;
  }
  
  protected int getElement(int j){
    int row=j/solution.length, column=j%solution.length;
    if(solution[row][column][1]==0)
      return 0;
    
    return solution[row][column][0];
  }
  
  protected int[] neededNumbers(Subgrid grid){
    int[] needed=new int[solution.length];
    int removed=0;
    for(int ctr=1; ctr<=solution.length; ctr++)
      needed[ctr-1]=ctr;
    for(int y=grid.getStartY(), limY=y+grid.getDimY(); y<limY; y++){
      for(int x=grid.getStartX(), limX=x+grid.getDimX(); x<limX; x++){
        if(solution[y][x][1]==0){
          needed[solution[y][x][0]-1]=0;
          removed=removed+1;
        }
      }
    }
    int[] neededNum=new int[solution.length-removed];
    
    for(int ctr=0, ctr2=0; ctr<solution.length; ctr++){
      if(needed[ctr]>0){
        if (penaltyType != SUMPROD_PENALTY_NO_SUBGRID)
          neededNum[ctr2]=needed[ctr];
        else
          neededNum[ctr2] = (int)(Math.random() * 9) + 1;
        ctr2=ctr2+1;
      }
    }
  return neededNum;
  }
  
  protected int[][][] getCopy(){
    int[][][] copy=new int[solution.length][solution.length][2];
    for(int ctr=0; ctr<copy.length; ctr++){
      for(int ct=0; ct<copy.length; ct++){
        copy[ctr][ct][0]=solution[ctr][ct][0];
        copy[ct][ctr][0]=solution[ct][ctr][0];
        copy[ctr][ct][1]=solution[ctr][ct][1];
        copy[ct][ctr][1]=solution[ct][ctr][1];
      }
    }
    return copy;
  }
  
  protected int[][][] swap(int[][][] solution, int subgridNum, 
        int row, int column, int xij, int vij){

    this.solution=solution;
    
    for (int y = subgrid[subgridNum].getStartY(), 
            limY = y + subgrid[subgridNum].getDimY(); 
            y<limY; y++) {
      for (int x=subgrid[subgridNum].getStartX(), 
              limX=x+subgrid[subgridNum].getDimX(); 
              x<limX; x++) {
        if(solution[y][x][0]==vij){
          solution[y][x][0]=xij;
          solution[row][column][0]=vij;
          return solution;
        }
      }
    }
    return null;
  }
  
  private int missigNoPenalty() {
    int penalty=0;
    customSet hor=new customSet();
    customSet ver=new customSet();
    for(int ctr=0; ctr<solution.length; ctr++){
      hor.clear();
      ver.clear();
      for(int ct=0; ct<solution.length; ct++){
        if(hor.contains(solution[ctr][ct][0]))
          penalty++;
        else
          hor.add(new Integer(solution[ctr][ct][0]));
        if (ver.contains(solution[ct][ctr][0]))
          penalty++;
        else
          ver.add(new Integer(solution[ct][ctr][0]));
      }
    }
    return penalty;
  }
  
  private BigInteger sumProductPenalty() {
    BigInteger rowSumTotal = BigInteger.ZERO;
    BigInteger rowProdTotal = BigInteger.ZERO;
    for (int i = 0; i < solution.length; ++i) {
      BigInteger rowSum = BigInteger.ZERO;
      BigInteger rowProd = BigInteger.ONE;
      for (int j = 0; j < solution.length; ++j) {
        rowSum = rowSum.add(BigInteger.valueOf(solution[j][i][0]));
        rowProd = rowProd.multiply(BigInteger.valueOf(solution[j][i][0]));
      }
      rowSumTotal = rowSumTotal.add(rowSum.subtract(summation));
      rowProdTotal = rowProdTotal.add(rowProd.subtract(lenFactorial));
    }
    
    BigInteger colSumTotal = BigInteger.ZERO;
    BigInteger colProdTotal = BigInteger.ZERO;
    for (int i = 0; i < solution.length; ++i) {
      BigInteger colSum = BigInteger.ZERO;
      BigInteger colProd = BigInteger.ONE;
      for (int j = 0; j < solution.length; ++j) {
        colSum = colSum.add(BigInteger.valueOf(solution[i][j][0]));
        colProd = colProd.multiply(BigInteger.valueOf(solution[i][j][0]));
      }
      colSumTotal = colSumTotal.add(colSum.subtract(summation));
      colProdTotal = colProd.add(colProd.subtract(lenFactorial));
    }
    /*
    colSumTotal = Math.abs(colSumTotal);
    rowSumTotal = Math.abs(rowSumTotal);
    rowProdTotal = Math.abs(rowProdTotal);
    colProdTotal = Math.abs(rowProdTotal);
    */
    return colSumTotal.add(colProdTotal).add(rowSumTotal).add(rowProdTotal);
  }
  
  private BigInteger sumProdNoSubgrid() {
    BigInteger sumProd = sumProductPenalty();
    BigInteger total = BigInteger.ZERO;
    int sy = 0;
    int gridw = (int)Math.sqrt(solution.length);
    while (sy < solution.length) {
      int sx = 0;
      while (sx < solution.length) {
        int bx = sx + gridw;
        int by = sy + gridw;
        BigInteger gridProd = BigInteger.ONE;
        BigInteger gridSum = BigInteger.ZERO;
        for (int i = sx; i < bx; ++i) {
          for (int j = sy; j < by; ++j) {
            gridProd = gridProd.multiply(BigInteger.valueOf(solution[i][j][0]));
            gridSum = gridSum.add(BigInteger.valueOf(solution[i][j][0]));
          }
        }
        total = total.add(gridProd.subtract(lenFactorial));
        total = total.add(gridSum.subtract(summation));
        sx += gridw;
        //System.out.println(sx + " , " + sy + " , " + gridw);
      }
      sy += gridw;
    }
    
 //   total = Math.abs(total);
    return total.add(sumProd);
  }
  
}