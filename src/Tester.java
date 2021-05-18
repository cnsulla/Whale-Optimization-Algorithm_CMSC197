public class Tester{
	public static void main(String[] args){
		/*
		int[] board = {	5,7,4,2,7,3,9,6,7,	
						6,9,2,5,8,6,1,8,5,
						3,1,8,4,9,1,2,3,4,
						8,5,9,6,9,3,8,4,1,
						7,1,6,2,7,4,3,2,6,
						4,2,3,5,8,1,5,7,9,
						8,2,5,3,9,7,4,1,8,
						3,1,4,6,5,8,2,9,5,
						9,7,6,2,1,4,7,6,3};
		*/
		int[] board = {	5,7,4,2,7,3,9,6,7,
						6,4,2,5,4,6,1,6,5,
						3,1,8,4,9,1,2,3,4,
						8,5,9,6,9,3,8,6,1,
						7,1,6,2,7,4,3,2,6,
						4,2,8,5,8,1,5,7,9,
						8,2,5,3,9,7,1,1,8,
						3,1,3,6,2,8,2,9,5,
						9,7,6,2,1,4,7,6,3};
		//ObjectiveFunction missNum = new MissingNumPenalty();
		//System.out.println(missNum.getFitness(board));
		//ObjectiveFunction sumSubgrid = new SumWithSubgrid();
		//System.out.println(sumSubgrid.getFitness(board));
		ObjectiveFunction sumNoSubgrid = new SumNoSubgrid();
		System.out.println(sumNoSubgrid.getFitness(board));
		
	}
}