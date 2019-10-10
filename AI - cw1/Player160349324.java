import java.awt.Color;
import java.util.*;
import java.lang.*;

class Player160349324 extends GomokuPlayer{

  private Color myColor;
  private Color opponentColor;

  public Move chooseMove(Color[][] board, Color myColour){
    if(board[1][0]==null){//first move
      return new Move(1,0);
    }

    this.myColor = myColour;
    this.opponentColor = (myColor == Color.white) ? Color.black : Color.white;

    int [] a;
    a = miniMax(board, myColor, 1, -1000000, 1000000);
    return new Move(a[1],a[2]);
  }

  public int[] miniMax(Color[][] board, Color player, int depth, int alpha, int beta){
    //System.out.println("White");
    //System.out.println(Arrays.deepToString(board));

    if (terminalStateTest(board, player) || depth ==0){
      int[] eval= new int[1];
      eval[0]= evalHeuristic(board, player);
      return eval;//return score of board
    }
    //find all possible moves
    ArrayList<int[]>posMoves = findMoves(board);// list of possibleMoves
    if (player==myColor){//if im black// true
      int[]maxValue = {-1000000, -1, -1};//set
      for(int i=0; i<posMoves.size()-1; i++){
        board[posMoves.get(i)[0]][posMoves.get(i)[1]] = player;
        int []newMax = miniMax(board, opponentColor, depth -1, alpha, beta);
        board[posMoves.get(i)[0]][posMoves.get(i)[1]] = null;
        if(newMax[0]>maxValue[0]){
          maxValue[0] = newMax[0];
          maxValue[1] = posMoves.get(i)[0];
          maxValue[2] = posMoves.get(i)[1];
        }
        alpha = Math.max(alpha, maxValue[0]);
        if (beta<=alpha){
          break;
        }
      }
      System.out.println("Min Value: "+ Arrays.toString(maxValue));
      return maxValue;
    }else{
      int[]minValue = {1000000, -1, -1};
      for(int i=0; i<posMoves.size()-1; i++){
        board[posMoves.get(i)[0]][posMoves.get(i)[1]] = player;
        int[] newMin = miniMax(board, myColor, depth -1, alpha, beta);
        board[posMoves.get(i)[0]][posMoves.get(i)[1]] = null;
        if(newMin[0]<minValue[0]){
          minValue[0] = newMin[0];
          minValue[1] = posMoves.get(i)[0];
          minValue[2] = posMoves.get(i)[1];
        }
        beta = Math.min(beta, minValue[0]);
        if (beta<=alpha){
          break;
        }
      }
      System.out.println("Max value: "+Arrays.toString(minValue));
      return minValue;
    }
  }


  public ArrayList<int[]> findMoves(Color [][] board){
    ArrayList<int[]> posMoves = new ArrayList<int[]>();

    for (int i=0; i<8; i++){
      for (int j=0; j<8; j++){
        if (board[i][j]==null){
          int []a={i,j};
          posMoves.add(a);
        }
      }
    }
    return posMoves;
  }

  public int evalHeuristic(Color[][]board, Color myColour){
    int score=0;
    Color against;
    if(myColour==Color.WHITE){
      against=Color.BLACK;
    }else{
      against=Color.WHITE;
    }
    //Calculate score for columns
    score+=scoreRow(board, myColour);
    score-=scoreRow(board, against);
    return score;
  }

  public int scoreRow(Color [][] board, Color myColor){
    int score =0;
    for(int row=0; row<=6;row++){
      for (int col=0; col<=7; col++){
        if(board[row][col]==myColor && board[row+1][col]==myColor)
          score+=2;
      }
    }
    for(int row=0; row<=3;row++){
      for (int col=0; col<=7; col++){
        if(board[row][col]==myColor && board[row+1][col]==myColor && board[row+2][col]==myColor  && board[row+3][col]==myColor  && board[row+4][col]==myColor)
          score+=4;
      }
    }
    return score;

  }

  public boolean terminalStateTest(Color[][] board, Color myColour){
    // Check if there is a 5 in a row in any col
    for(int row=0; row<4; row++){
      for(int col=0; col<8; col++){
        if(board[row][col]==myColour && board[row+1][col]==myColour && board[row+2][col]==myColour && board[row+3][col]==myColour && board[row+4][col]==myColour){
          return true;
        }
      }
    }

    //Check if there is a 5 in a row in any row
    for(int row=0; row<8; row++){
      for(int col=0; col<4; col++){
        if(board[row][col]==myColour && board[row][col+1]==myColour && board[row][col+2]==myColour && board[row][col+3]==myColour && board[row][col+4]==myColour){
          return true;
        }
      }
    }
    boolean diagonal;
    //Check for diagonal from left to right down
      int[] row = {3,2,3,1,2,3,0,1,2,3,0,1,2,0,1,0};
      int[] col = {0,0,1,0,1,2,0,1,2,3,1,2,3,2,3,3};
      for (int i=0; i<row.length; i++){
         diagonal = checkDiagonal(board,myColour, row[i],col[i]);
         if (diagonal) return true;
      }

    //check for Diagonal from left to right up
      int[] row1 = {4,5,4,6,5,4,7,6,5,4,7,6,5,7,6,7};
      int[] col1 = {0,0,1,0,1,2,0,1,2,3,1,2,3,2,3,3};
      for (int i=0; i<row1.length; i++){
        diagonal = checkDiagonal1(board,myColour, row1[i],col1[i]);
        if (diagonal) return true;
      }

      //check if draw
      boolean draw = false;
      outerloop:
      for (int i=0; i<=7; i++){
        for (int j=0; j<=7; j++){
          if(board[i][j]==null)
            break outerloop;
            }
      }
      return false;
    }
  //helper method to check diagonal
  public boolean checkDiagonal(Color[][]board, Color myColour, int row, int col){
    if(board[row][col]==myColour && board[row+1][col+1]==myColour && board[row+2][col+2]==myColour && board[row+3][col+3]==myColour && board[row+4][col+4]==myColour)
      return true;
    return false;
  }

  //helper method to check diagonal
  public boolean checkDiagonal1(Color[][]board, Color myColour, int row, int col){
    if(board[row][col]==myColour && board[row-1][col+1]==myColour && board[row-2][col+2]==myColour && board[row-3][col+3]==myColour && board[row-4][col+4]==myColour)
      return true;
    return false;
  }
}
