public class SumNoSubgrid extends ObjectiveFunction{
	public double getFitness(int[] boardState){
		int dimension = (int)Math.sqrt(boardState.length);
		int subgridWidth = (int) Math.sqrt(dimension);
		float total = 0; 
		int rowSum = 0;
		int[] colSum = new int[dimension];
		int[] subgridSum = new int[dimension];
		//iterate through board
		for(int i = 0; i < boardState.length; i++){
			rowSum += boardState[i];
			colSum[i%dimension] += boardState[i];
			int subgridIndex = (i/dimension/subgridWidth) * subgridWidth + (i%dimension)/subgridWidth;
			subgridSum[subgridIndex] += boardState[i];
			//check for right edge
			if(i % dimension == dimension -1){
				//add rowSums to total
				total += Math.abs(rowSum - dimension*(dimension+1)/2);
				rowSum = 0;
			}
		}
		//compute column sums and add to total
		for(int i = 0; i < colSum.length; i++){
			total += Math.abs(colSum[i] - dimension * (dimension+1)/2);
		}
		//compute subgrid sums and add to total
		for(int i = 0; i < subgridSum.length; i++){
			total += Math.abs(subgridSum[i] - dimension * (dimension+1)/2);
		}
		//System.out.println(total);
		return 1.0/ (1+total);
	}
}