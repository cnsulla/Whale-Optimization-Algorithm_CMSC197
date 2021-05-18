public class SumWithSubgrid extends ObjectiveFunction{
	public double getFitness(int[] boardState){
		int dimension = (int)Math.sqrt(boardState.length);
		float total = 0; 
		int rowSum = 0;
		int[] colSum = new int[dimension];
		//iterate through board
		for(int i = 0; i < boardState.length; i++){
			rowSum += boardState[i];
			colSum[i%dimension] += boardState[i];
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
		return 1.0/ (1+total);
	}
}