import java.util.Scanner;
import java.io.File;
import java.io.IOException;

class Sudoku
{
   
   public static void main(String args[]) throws IOException
   {
       Scanner console = new Scanner(System.in);
       String filename;
       boolean isValid;

       System.out.print("Please input the filename: ");
       filename = console.nextLine();

       File file = new File (filename);
       Scanner sc = new Scanner(file);
       
       int [][] givenBoard = new int [9][9];
      
       System.out.println("\nValues provided in input.txt\n");
      
       //Loop to read values from input.txt into array
       while (sc.hasNext())
       {
           for (int i = 0; i < 9; i ++)
           {
               for (int j = 0; j < 9; j++)
               {
                  
                   givenBoard[i][j] = sc.nextInt();
                   System.out.print (givenBoard[i][j] + " ");
               }
               System.out.println();
           }
          
       }
       int lenghtBoard = givenBoard.length;
  
       //To print solution after Backtracking
       System.out.println();
       if (solveSudoku(givenBoard, lenghtBoard))
       {
           System.out.println("****** Solution ******");
           print(givenBoard, lenghtBoard);
       }
       else
       {
           System.out.println("Solution not possible");
       }
   }
  
   public static boolean isSafe(int[][] board, int row, int col, int num)
   {
       //check the rows
       for (int d = 0; d < board.length; d++)
       {
           if (board[row][d] == num)
           {
               return false;
           }
       }
      
       //check the columns
       for (int r = 0; r < board.length; r++)
       {   
           if (board[r][col] == num)
               return false;
       }
  
       //check box
       int sqrt = (int) Math.sqrt(board.length);
       int boxRowStart = row - row % sqrt;
       int boxColStart = col - col % sqrt;
  
       for (int r = boxRowStart; r < boxRowStart + sqrt; r++)
       {
           for (int d = boxColStart; d < boxColStart + sqrt; d++)
           {
               if (board[r][d] == num)
                   return false;
           }
       }
  
       
       return true;
   }
  
   public static boolean solveSudoku(int[][] board, int n)
   {
       int row = -1;
       int col = -1;
       boolean isEmpty = true;
       for (int i = 0; i < n; i++)
       {
           for (int j = 0; j < n; j++)
           {
               if (board[i][j] == 0)
               {
                   row = i;
                   col = j;
                   isEmpty = false;
                   break;
               }
           }
           if (!isEmpty)
               break;
       }
  
       // Returns if no empty space left
       if (isEmpty)
           return true;
  
       // else for each-row backtrack
       for (int num = 1; num <= n; num++)
       {
           if (isSafe(board, row, col, num))
           {
               board[row][col] = num;
               if (solveSudoku(board, n))
               {
                   return true;
               }
               else
                   board[row][col] = 0;
           }
       }
       return false;
   }
  
   public static void print(int[][] board, int N)
   {
   //Print the answer
       for (int r = 0; r < N; r++)
       {
           for (int d = 0; d < N; d++)
           {
               System.out.print(board[r][d]);
               System.out.print(" ");
           }
           System.out.print("\n");
          
           if ((r + 1) % (int) Math.sqrt(N) == 0)
           {
               System.out.print("");
           }
       }
   }
}