import edu.princeton.cs.introcs.StdDraw;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Joseph Brooksbank
 * Class responsible for running the simulation
 */
public class Environment {
    /** The number of rows and columns of the current setting */
    private int rows, columns;
    /** An array of Cell objects, which represent organisms in the game */
    private Cell[][] cells;

    /**
     * A Constructor which creates an Environment with the correct sized array, from a file. 0=space, 1= filled.
     * @param initConfig    A txt file of a "starting position", first 2 numbers are sizes and rest are content
     */
    Environment(String initConfig){

        /* A Scanner object to read setting files */
        Scanner fileIn = null;
        try {
            fileIn = new Scanner(new FileInputStream(initConfig));
        } catch (FileNotFoundException e){
            System.out.println("File not found");
            System.exit(1);
        }

        /* Setting size of cell array */
        this.rows = fileIn.nextInt();
        this.columns = fileIn.nextInt();
        cells = new Cell[rows][columns];

        //Assuming that the file is correct and have the correct number of entries
        int temp;
        /* Creating Cell array, based on the 1s and 0s of the text file */
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                temp = fileIn.nextInt();
                if (temp == 1) {
                    cells[i][j] = new Cell(true);
                } else {
                    cells[i][j] = new Cell(false);
                }
            }
        }
        /* Closing scanner obj */
        fileIn.close();

        /* Setting up canvas to draw on */
        StdDraw.setCanvasSize(columns*20, rows*20);
        StdDraw.setXscale(0, columns);
        StdDraw.setYscale(0, rows);

    }

    /**
     * A method, called from the driver, which runs the "game"
     */
    public void runSimulation(){

        /* Draws and assigns next board state*/
        //noinspection InfiniteLoopStatement
        for(;;) {
            drawBoard(cells);
            cells = nextBoard();
        }
    }

    /**
     * A method which draws the current state of the board using StdDraw
     */
    private void drawBoard(Cell[][] board){

        StdDraw.clear();

        /* Drawing board based on what is currently occupied */
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++){

                if(board[i][j].getOccupied()){
                    StdDraw.filledRectangle(0.5+ j, board.length - (0.5 + i), 0.5, 0.5);
                }
            }
        }
        StdDraw.show();
        StdDraw.pause(1000);
    }

    /**
     * A method for determining how many neighbors a coordinate in the array has
     * @param row       The INDEX of the position in the first array
     * @param column    The INDEX of the array in the first array
     * @return          The number of neighbors the given position has
     */
    private int numberOfNeighbors(int row, int column){
        int count = 0;
        /* Starting at the top left corner of the 8x8 "cube" around the cell */
        for (int i = row-1; i < row+2; i++){
            for (int j = column -1; j < column+2; j++){
                if (i == row && j == column)
                    continue;

                // Counting places off of the board as uninhabited
                if (i >= rows || i <0 || j < 0 || j >= columns){
                    continue;
                }
                 if (cells[i][j].getOccupied())
                     count++;
            }
        }
        return count;
    }


    /**
     * Creates an updated version of the board, all at once
     * @return      An array of Cells, updated to the next "generation"
     */
    private Cell[][] nextBoard(){

        // Modifying a temp array while observing the "real" array, to make sure changes to the board do not effect
        // other cells of the same generation
        Cell[][] tempArray = new Cell[rows][columns];
        for (int i  = 0; i < tempArray.length; i++){
              for (int j = 0; j < tempArray[i].length; j++){
                  if (cells[i][j].getOccupied()) {
                      tempArray[i][j] = new Cell(true);
                  } else {
                      tempArray[i][j] = new Cell(false);
                  }
              }
        }

        for (int i = 0; i < cells.length; i++){
            for (int j = 0; j < cells[i].length; j++){
                /* Any occupied cell with fewer than two live neighbors dies */
                if (cells[i][j].getOccupied() && numberOfNeighbors(i, j) < 2){
                    tempArray[i][j].setOccupied(false);
                }
                /* Any occupied cell with more than 3 neighbors dies */
                if (cells[i][j].getOccupied() && numberOfNeighbors(i,j) > 3){
                    tempArray[i][j].setOccupied(false);
                }

                /* Any unoccupied cell with exactly three neighbors becomes occupied */
                if (!cells[i][j].getOccupied() && numberOfNeighbors(i,j) == 3){
                    tempArray[i][j].setOccupied(true);
                }
            }
        }
        return tempArray;
    }
}
