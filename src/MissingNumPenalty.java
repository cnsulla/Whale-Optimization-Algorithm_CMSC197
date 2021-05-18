public class MissingNumPenalty extends ObjectiveFunction{
	public double getFitness(int[] boardState){
		int dimension = (int)Math.sqrt(boardState.length);
		boolean[] rowTally = new boolean[dimension];
		boolean[] columnTally = new boolean[dimension];
		int rowMissing = 0;
		int columnMissing = 0;
		//count missing in rows
		for(int i = 0; i < boardState.length; i++){
			rowTally[boardState[i]-1] = true;
			//check for right edge
			if( i > 0  && i % dimension == dimension - 1){
				for(int j = 0; j < rowTally.length; j++){
					if(!rowTally[j]){
						rowMissing++;
					}
				}
				rowTally = new boolean[dimension];
			}		
		}
		//count missing in columns
		for(int i = 0; i < boardState.length; ){
			columnTally[boardState[i]-1] = true;
			//check for bottom edge
			if(i/dimension == dimension - 1){
				for(int j = 0; j < columnTally.length; j++){
					if(!columnTally[j]){
						columnMissing++;
					}
				}
				//check if final tile has been reached
				if(i == boardState.length - 1){
					break;
				}
				else{					
					columnTally = new boolean[dimension];
					//move to next column
					i = (i%dimension)+1;
				}
			}
			//increment
			else{
				i+=dimension;
			}
		}
		int penalty = rowMissing + columnMissing;
		return 1.0/(1+penalty);
	}
}