import java.awt.Color;
import java.util.*;

class Player160594898 extends GomokuPlayer {

	private Color[][] board;
	private Color myColor;
	private Color otherColor;



	public Move chooseMove(Color[][] board, Color me) {
		this.board = board;
		this.myColor = me;
		this.otherColor = (myColor == Color.BLACK) ? Color.WHITE : Color.BLACK;

		// returns an array of [score][row][column]
		int[] result = alphaBetaPruning(4, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

		return new Move(result[1],result[2]);
	} // chooseMove()


	private int[] alphaBetaPruning(int depthLevel, int alpha, int beta, boolean player){
		List<int[]> possibleMoves = checkBoard();
		int currentScore=0;
		int row = -1;
		int col = -1;

		//gets the score of the current alpha/beta
		int number = (player) ? alpha : beta;

		if (depthLevel ==0 || possibleMoves.isEmpty()) {
			currentScore = scoreEvaluation();

			return new int[] {currentScore, row, col};
		} else {
			for(int[] trymove : possibleMoves){

				// maximising the player/computer
				if(player){

					// trying out the available moves on the board
					board[trymove[0]][trymove[1]] = myColor;
					currentScore = alphaBetaPruning(depthLevel-1, alpha, beta, false)[0];
					board[trymove[0]][trymove[1]] = null;

					// compares the score returned from the score returned from the evaluation with
					// the current alpha number and assigns number to the higher value
					number = Math.max(number, currentScore);

					if(number >= beta) return new int[] {number, row, col};
					if(number > alpha) {
						alpha = number;
						row = trymove[0];
						col = trymove[1];
					}

				} else { // minimisng the opponent
					board[trymove[0]][trymove[1]] = otherColor;
					currentScore = alphaBetaPruning(depthLevel-1, alpha, beta, true)[0];
					board[trymove[0]][trymove[1]] = null;
					number = Math.min(number, currentScore);

					if(number <= alpha) return new int[] {number, row, col};
					if(number < beta) {
						beta = number;
						row = trymove[0];
						col = trymove[1];
					}
				}

				if(alpha >= beta) break;
			}
		}
		return new int[] {number, row, col};

	}

	private List<int[]> checkBoard(){
		List<int[]> freeBlocks = new ArrayList<int[]>();

		for(int i = 0; i < board[0].length; i++) {
			for (int j = 0; j < board[1].length; j++) {
				if (board[i][j] == null)
					freeBlocks.add(new int[] {i,j});
			}
		}
		return freeBlocks;
	}

	private int scoreEvaluation(){
		int score = 0;

		// Evaluating rows
		for (int x = 0; x < 4; x++) {
			for(int y = 0; y < 8; y++){
				score += lineEvaulation(x, y,x+1, y,x+2, y, x+3, y, x+4, y);
			}
		}


		//Evaluating columns
		for (int y = 0; y < 4; y++) {
			for(int x = 0; x < 8; x++){
				score += lineEvaulation(x, y, x, y+1, x, y+2, x, y+3, x, y+4);
			}
		}

		//Evaluating diagonals
		for(int x = 4; x < 8; x++){
			for(int y =0; y < 4; y++){
				score += lineEvaulation(x, y, x-1, y+1, x-2, y+2, x-3, y+3, x-4, y+4);
			}
		}

		//Evaluating alternative diagonals
		for(int x = 3; x >= 0; x--){
			for(int y =0; y < 4; y++){
				score += lineEvaulation(x, y, x+1, y+1, x+2, y+2, x+3, y+3, x+4, y+4);
			}
		}
		return score;
	}

	private int lineEvaulation(int row1, int col1, int row2, int col2, int row3, int col3, int row4, int col4, int row5, int col5){
		int score =0;

		// scoring for 1-in-a-row
		if(board[row1][col1] == myColor){
			score = 1; // M
		} else if (board[row1][col1] == otherColor){
			score = -1; // O
		}

		// scoring for 2-in-a-row
		if(board[row2][col2] == myColor){
			if(score == 1){
				score = 10; // MM
			} else if(score == -1){
				score = 1; // OM
			} else {
				score = 1; // _M
			}
		} else if (board[row2][col2] == otherColor){
			if(score == 1) {
				score = -1; // MO
			} else if (score == -1) {
				score = -20; // OO
			} else {
				score = -1; // _O
			}
		}

		// scoring for 3-in-a-row
		if(board[row3][col3] ==  myColor){
			if(score == 10) {
				score = 100; // MMM
			} else if (score == 1){
				score = 10; // _MM
			} else if (score == -1){
				score = 1; // _OM
			} else if (score == -20){
				score = 200; // OOM
			} else {
				score = 1; // __M
			}
		} else if (board[row3][col3] ==  otherColor){
			if(score == 10) {
				score = -1; // MMO
			} else if (score == 1){
				score = -1; // _MO
			} else if (score == -1){
				score = -20; // _OO
			} else if (score == -20){
				score = -300; // OOO
			} else {
				score = -1; // __O
			}
		}

		// scoring for 4-in-a-row
		if(board[row4][col4] ==  myColor){
			if(score == 200) {
				score = 10; // OOMM
			} else if (score == 100){
				score = 1000; // MMMM
			} else if (score == 10){
				score = 100; // _MMM
			} else if (score == 1){
				score = 10; // __MM
			} else if (score == -1){
				score = 1; // __OM
			} else if (score == -20){
				score = 200; // _OOM
			} else if (score == -300){
				score = 2000; // OOOM
			} else {
				score = 1; // ___M
			}
		} else if(board[row4][col4] ==  otherColor){
			if(score == 200) {
				score = -1; // OOMO
			} else if (score == 100){
				score = -20; // MMMO
			} else if (score == 10){
				score = -1; // _MMO
			} else if (score == 1){
				score = -1; // __MO
			} else if (score == -1){
				score = -20; // __OO
			} else if (score == -20){
				score = -3000; // _OOO
			} else if (score == -300){
				score = -40000; // OOOO
			} else {
				score = -1; // ___O
			}
		}

		// scoring for 5-in-a-row
		if(board[row5][col5] ==  myColor){
			if(score == 2000) {
				score = 10; // OOOMM
			} else if (score == 1000){
				score = 10000; // MMMMM
			} else if (score == 200){
				score = 10; // _OOMM
			} else if (score == 100){
				score = 1000; // _MMMM
			} else if (score == 10){
				score = 100; // __MMM
			} else if (score == 1){
				score = 10; // ___MM
			} else if (score == -1){
				score = 1; // ___OM
			} else if (score == -20){
				score = 200; // __OOM
			} else if (score == -3000){
				score = 20000; // _OOOM
			} else if (score == -40000){
				score = 200000; // OOOOM
			} else {
				score = 1; // ____M
			}
		} else if(board[row5][col5] ==  otherColor) {
			if (score == 20000) {
				score = -1; // OOOMO
			} else if (score == 1000) {
				score = -300; // MMMMO
			} else if (score == 2000) {
				score = -1; // _OOMO
			} else if (score == 100) {
				score = -20; // _MMMO
			} else if (score == 10) {
				score = -1; // __MMO
			} else if (score == 1) {
				score = -1; // ___MO
			} else if (score == -1) {
				score = -20; // ___OO
			} else if (score == -20) {
				score = -300; // __OOO
			} else if (score == -3000) {
				score = -300000; // _OOOO
			} else if (score == -40000) {
				score = -5000000; // OOOOO
			} else {
				score = 1; // ____
			}
		}
		return score;
	}
} // class Player160594898
