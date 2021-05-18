public class Subgrid{
	boolean intrinsicConstraint;
	int dimension;
	int[][] values;
	Subgrid(boolean intrinsicConstraint, int dimension){
		this.intrinsicConstraint = intrinsicConstraint;
		this.dimension = dimension;
		values = new int[dimension][dimension];
	}
	void setValue(int column, int row, int value){
		if(intrinsicConstraint){			
			int index = getIndex(value);
			//value exists in subrid already
			if(index == -1){
				values[row][column] = value;
			}
			else{
				int temp = values[index/dimension][index%dimension];
				values[index/dimension][index%dimension] = values[row][column];
				values[row][column] = temp;
			}
		}
		else{
			values[row][column] = value;
		}
	}
	private int getIndex(int value){
		for(int i = 0; i < values.length; i++){
			for(int j = 0; j < values.length; j++){
				if(values[i][j] == value){
					return i*dimension + j;
				}
			}
		}
		return -1;
	}
}