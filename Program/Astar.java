import java.util.LinkedList;
import java.io.PrintWriter;

public class Astar {
    private int fopt;                       //f value optioins: 1, 2, or 3
    private int hopt;

//This program solves sliding puzzle using A* algorithm

    //Board class (inner class)
    private class Board {
        private char[][] array;                 //board array
        private int gvalue;                     //path cost
        private int hvalue;                     //heuristic value
        private int fvalue;                     //gvalue plus hvalue
        private Board parent;                   //parent board
                      //h value options: 1 or 2

        //Constructor of board class
        private Board(char[][] array, int size) {//added a new parameter called fopt which is the f value options
            this.array = new char[size][size];  //create board array

            for (int i = 0; i < size; i++)      //copy given array
                for (int j = 0; j < size; j++)
                    this.array[i][j] = array[i][j];



            this.parent = null;                 //no parent
        }
    }

    private Board initial;                         //initial board
    private Board goal;                            //goal board
    private int size;                              //board size

    //Constructor of SlidingAstar class
    public Astar(char[][] initial, char[][] goal, int size, int fopt, int hopt) {
        this.size = size;
        this.fopt = fopt;   //create initial f value
        this.hopt = hopt;   //create initial h value
        this.initial = new Board(initial, size);   //create initial board
        this.goal = new Board(goal, size);         //create goal board


    }

    //Method solves sliding puzzle
    
    public void solve(PrintWriter out) {
        LinkedList<Board> openList = new LinkedList<Board>();  //open list
        LinkedList<Board> closedList = new LinkedList<Board>();//closed list

        openList.addFirst(initial);   //add initial board to open list

        int totalSwaps = 0;      //initialize swaps
        int boardsSearched = 0;  //initialize boards searched


        while (!openList.isEmpty())   //while open list has more boards
        {
            int best = selectBest(openList);       //select best board

            Board board = openList.remove(best);   //remove board

            closedList.addLast(board);             //add board to closed list

            if (goal(board))                       //if board is goal
            {
                displayPath(board, null);                //display path to goal
                return;                            //stop search
            } else                                   //if board is not goal
            {
                LinkedList<Board> children = generate(board);//create children

                for (int i = 0; i < children.size(); i++) {                                     //for each child
                    Board child = children.get(i);

                    if (!exists(child, closedList))   //if child is not in closed list
                    {
                        if (!exists(child, openList))//if child is not in open list
                            openList.addLast(child); //add to open list
                        else {                            //if child is already in open list
                            int index = find(child, openList);
                            if (child.fvalue < openList.get(index).fvalue) {                            //if fvalue of new copy
                                openList.remove(index);  //is less than old copy
                                openList.addLast(child); //replace old copy
                            }                            //with new copy
                        }
                    }
                }
            }
        }

        System.out.println("no solution");
       //no solution if there are
    }                                                  //no boards in open list

    //Method creates children of a board
    private LinkedList<Board> generate(Board board) {
        int i = 0, j = 0;
        boolean found = false;

        for (i = 0; i < size; i++)              //find location of empty slot
        {                                       //of board
            for (j = 0; j < size; j++)
                if (board.array[i][j] == '0') {
                    found = true;
                    break;
                }

            if (found)
                break;
        }

        boolean north, south, east, west;       //decide whether empty slot
        north = i == 0 ? false : true;          //has N, S, E, W neighbors
        south = i == size - 1 ? false : true;
        east = j == size - 1 ? false : true;
        west = j == 0 ? false : true;

        LinkedList<Board> children = new LinkedList<Board>();//list of children

        if (north) children.addLast(createChild(board, i, j, 'N', fopt, hopt)); //add N, S, E, W
        if (south) children.addLast(createChild(board, i, j, 'S', fopt, hopt)); //children if
        if (east) children.addLast(createChild(board, i, j, 'E', fopt, hopt));  //they exist
        if (west) children.addLast(createChild(board, i, j, 'W', fopt, hopt));

        return children;                        //return children
    }

    //Method creates a child of a board by swapping empty slot in a
    //given direction
    private Board createChild(Board board, int i, int j, char direction, int fopt, int hopt) {
        Board child = copy(board);                   //create copy of board

        if (direction == 'N')                        //swap empty slot to north
        {
            child.array[i][j] = child.array[i - 1][j];
            child.array[i - 1][j] = '0';
        } else if (direction == 'S')                   //swap empty slot to south
        {
            child.array[i][j] = child.array[i + 1][j];
            child.array[i + 1][j] = '0';
        } else if (direction == 'E')                   //swap empty slot to east
        {
            child.array[i][j] = child.array[i][j + 1];
            child.array[i][j + 1] = '0';
        } else                                         //swap empty slot to west
        {
            child.array[i][j] = child.array[i][j - 1];
            child.array[i][j - 1] = '0';
        }

        if(fopt ==3) {
            child.gvalue = board.gvalue + 1;             //parent path cost plus one

            if(hopt ==1){
                child.hvalue = heuristic_M(child);
            }
            else{
                child.hvalue = heuristic_D(child);
            }

            child.fvalue = child.gvalue + child.hvalue;  //gvalue plus hvalue

            child.parent = board;
        }
        else if(fopt == 2){
            child.gvalue = board.gvalue + 1;
            child.fvalue = child.gvalue;
        }
        else if(fopt == 1){
            child.hvalue = heuristic_M(child);
            child.fvalue = child.hvalue;
            child.gvalue = board.gvalue+1; //This tracks the amount of swaps since everytime we go to a new board the cost is 1, count the total number for the gvalue and that is the total number of swaps
        }                                                                               //assign parent to child

        return child;                                //return child
    }

    //Method computes heuristic value of board based on misplaced values
    private int heuristic_M(Board board) {
        int value = 0;                               //initial heuristic value

        for (int i = 0; i < size; i++)               //go thru board and
            for (int j = 0; j < size; j++)           //count misplaced values
                if (board.array[i][j] != goal.array[i][j])
                    value += 1;

        return value;                                //return heuristic value
    }

    //Method computes heuristic value of board
    //Heuristic value is the sum of taxi distances of misplaced values
    private int heuristic_D(Board board) {
        //initial heuristic value
        int value = 0;

        //go thru board
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                //if value mismatches in goal board
                if (board.array[i][j] != goal.array[i][j]) {
                    //locate value in goal board
                    int x = 0, y = 0;
                    boolean found = false;
                    for (x = 0; x < size; x++) {
                        for (y = 0; y < size; y++)
                            if (goal.array[x][y] == board.array[i][j]) {
                                found = true;
                                break;
                            }
                        if (found)
                            break;
                    }

                        //find city distance between two locations
                        value += (int) Math.abs(x - i) + (int) Math.abs(y - j);


                }

        //return heuristic value
        return value;
    }

    //Method locates the board with minimum fvalue in a list of boards
    private int selectBest(LinkedList<Board> list) {
        int minValue = list.get(0).fvalue;           //initialize minimum
        int minIndex = 0;                            //value and location

        for (int i = 0; i < list.size(); i++) {
            int value = list.get(i).fvalue;
            if (value < minValue)                    //updates minimums if
            {                                        //board with smaller
                minValue = value;                    //fvalue is found
                minIndex = i;
            }
        }

        return minIndex;                             //return minimum location
    }

    //Method creates copy of a board
    private Board copy(Board board) {
        return new Board(board.array, size);
    }

    //Method decides whether a board is goal
    private boolean goal(Board board) {
        return identical(board, goal);           //compare board with goal
    }

    //Method decides whether a board exists in a list
    private boolean exists(Board board, LinkedList<Board> list) {
        for (int i = 0; i < list.size(); i++)    //compare board with each
            if (identical(board, list.get(i)))   //element of list
                return true;

        return false;
    }

    //Method finds location of a board in a list
    private int find(Board board, LinkedList<Board> list) {
        for (int i = 0; i < list.size(); i++)    //compare board with each
            if (identical(board, list.get(i)))   //element of list
                return i;

        return -1;
    }

    //Method decides whether two boards are identical
    private boolean identical(Board p, Board q) {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (p.array[i][j] != q.array[i][j])
                    return false;      //if there is a mismatch then false

        return true;                   //otherwise true
    }



    //Method displays path from initial to current board
    private void displayPath(Board board, PrintWriter out) {
        LinkedList<Board> list = new LinkedList<Board>();

        Board pointer = board;         //start at current board

        while (pointer != null)        //go back towards initial board
        {
            list.addFirst(pointer);    //add boards to beginning of list

            pointer = pointer.parent;  //keep going back
        }
        //print boards in list
        for (int i = 0; i < list.size(); i++)
            displayBoard(list.get(i), out);
    }

    //Method displays board
    private void displayBoard(Board board, PrintWriter out) {
        for (int i = 0; i < size; i++) //print each element of board
        {
            for (int j = 0; j < size; j++){
                System.out.print(board.array[i][j] + '0');
                out.print(board.array[i][j] + '0');
            System.out.println();
            out.println();
            }
        }
        System.out.println();
    }
}