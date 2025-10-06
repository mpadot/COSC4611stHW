package q2.Program;

import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

// Best-first sliding puzzle solver for the R/G + numbered tiles variant
public class question2 {
    // Board inner class
    private class Board {
        private String[][] array;   // board array
        private int hvalue;         // heuristic value
        private Board parent;       // parent board

        private Board(String[][] array, int size) {
            this.array = new String[size][size];
            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++)
                    this.array[i][j] = array[i][j];
            this.hvalue = 0;
            this.parent = null;
        }
    }

    private Board initial;
    private Board goal;
    private int size;

    // Constructor: initial and goal boards supplied
    public question2(String[][] initial, String[][] goal, int size) {
        this.size = size;
        this.initial = new Board(initial, size);
        this.goal = new Board(goal, size);

        // initialize initial board heuristic so selectBest has a meaningful value
        this.initial.hvalue = heuristic_M(this.initial);
    }

    // Best-first search
    public void solve(PrintWriter out) {
        LinkedList<Board> openList = new LinkedList<>();
        LinkedList<Board> closedList = new LinkedList<>();

        openList.addFirst(initial);

        while (!openList.isEmpty()) {
            int best = selectBest(openList);
            Board board = openList.remove(best);

            closedList.addLast(board);

            if (goal(board)) {
                displayPath(board, out);
                return;
            } else {
                LinkedList<Board> children = generate(board);

                for (int i = 0; i < children.size(); i++) {
                    Board child = children.get(i);
                    if (!exists(child, openList) && !exists(child, closedList)) {
                        openList.addLast(child);
                    }
                }
            }
        }

        out.println("no solution");
        System.out.println("no solution");
    }

    // Generate neighboring boards by swapping adjacent pairs (right and down)
    private LinkedList<Board> generate(Board board) {
        LinkedList<Board> children = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Right neighbor
                if (j + 1 < size && isValidSwap(board.array[i][j], board.array[i][j + 1])) {
                    children.add(createChild(board, i, j, i, j + 1));
                }
                // Down neighbor
                if (i + 1 < size && isValidSwap(board.array[i][j], board.array[i + 1][j])) {
                    children.add(createChild(board, i, j, i + 1, j));
                }
            }
        }

        return children;
    }

    // Swap validity: same tile => no, both digits => no (per your specification),
    // R can swap only with G and vice versa (otherwise disallowed)
    private boolean isValidSwap(String a, String b) {
        if (a == null || b == null) return false;
        if (a.equals(b)) return false;

        // Both numbers → can't swap
        if (isDigit(a) && isDigit(b)) return false;

        // If one is R and the other is G -> allowed
        if ((a.equals("R") && b.equals("G")) || (a.equals("G") && b.equals("R"))) return true;

        // Allow swapping number <-> R/G
        if ((isDigit(a) && (b.equals("R") || b.equals("G"))) ||
            (isDigit(b) && (a.equals("R") || a.equals("G")))) return true;

        return false;
    }


    private boolean isDigit(String s) {
        return s != null && s.matches("\\d+");
    }

    // Create child by swapping two positions
    private Board createChild(Board board, int i1, int j1, int i2, int j2) {
        Board child = copy(board);

        String temp = child.array[i1][j1];
        child.array[i1][j1] = child.array[i2][j2];
        child.array[i2][j2] = temp;

        child.hvalue = heuristic_M(child);
        child.parent = board;
        return child;
    }

    // Heuristic: misplaced count (uses equals for Strings)
    private int heuristic_M(Board board) {
        int value = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (!board.array[i][j].equals(goal.array[i][j]))
                    value += 1;
        return value;
    }

    // Heuristic: taxi (Manhattan) distances of misplaced values
    private int heuristic_D(Board board) {
        int value = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (!board.array[i][j].equals(goal.array[i][j])) {
                    boolean found = false;
                    for (int x = 0; x < size && !found; x++) {
                        for (int y = 0; y < size; y++) {
                            if (goal.array[x][y].equals(board.array[i][j])) {
                                value += Math.abs(x - i) + Math.abs(y - j);
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return value;
    }

    // Choose the board in list with minimum hvalue
    private int selectBest(LinkedList<Board> list) {
        int minValue = list.get(0).hvalue;
        int minIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            int value = list.get(i).hvalue;
            if (value < minValue) {
                minValue = value;
                minIndex = i;
            }
        }
        return minIndex;
    }

    private Board copy(Board board) {
        return new Board(board.array, size);
    }

    private boolean goal(Board board) {
        return identical(board, goal);
    }

    private boolean exists(Board board, LinkedList<Board> list) {
        for (Board b : list)
            if (identical(board, b))
                return true;
        return false;
    }

    // Compare boards using String.equals
    private boolean identical(Board p, Board q) {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (!p.array[i][j].equals(q.array[i][j]))
                    return false;
        return true;
    }

    // Display path from initial to goal (both console and file)
    private void displayPath(Board board, PrintWriter out) {
        LinkedList<Board> list = new LinkedList<>();
        Board pointer = board;
        while (pointer != null) {
            list.addFirst(pointer);
            pointer = pointer.parent;
        }
        for (Board b : list)
            displayBoard(b, out);
    }

    // Print board to console and file
    private void displayBoard(Board board, PrintWriter out) {
        for (int i = 0; i < size; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < size; j++) {
                line.append(board.array[i][j]).append(" ");
                System.out.print(board.array[i][j] + " ");
            }
            System.out.println();
            out.println(line.toString().trim());
        }
        System.out.println();
        out.println();
        out.flush();
    }

    // Build goal array: put numeric tiles first (countNums) then R and G
    private static String[][] buildGoalArray(int size, String[][] initArray) {
        int n2 = size * size;
        int countR = 0, countG = 0;

        // count R and G in the initial board
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                if (initArray[i][j].equals("R")) countR++;
                else if (initArray[i][j].equals("G")) countG++;
            }

        int countNums = n2 - countR - countG;

        // fill goal with: 1..N then Rs then Gs
        String[] flat = new String[n2];
        int idx = 0;
        for (int i = 1; i <= countNums; i++) flat[idx++] = String.valueOf(i);
        for (int i = 0; i < countR; i++) flat[idx++] = "R";
        for (int i = 0; i < countG; i++) flat[idx++] = "G";

        // convert to 2D
        String[][] goalArray = new String[size][size];
        idx = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                goalArray[i][j] = flat[idx++];

        return goalArray;
    }


    // ---------- main ----------
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.print("Enter input filename: ");
        String inputFile = console.nextLine();


        System.out.print("Enter output filename: ");
        String outputFile = console.nextLine();

        // read from q2/Output/<inputFile> as you had
        Scanner in = new Scanner(new File("q2/Output/" + inputFile));
        PrintWriter out = new PrintWriter(new File("q2/Output/" + outputFile));

        int size = in.nextInt();
        String[][] board = new String[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                board[i][j] = in.next();

        String[][] goalArray = buildGoalArray(size, board);
        question2 solver = new question2(board, goalArray, size);

        System.out.println("Initial Puzzle:");
        solver.displayBoard(solver.initial, out);

        System.out.println("\nSolving...\n");
        solver.solve(out);

        out.close();
        in.close();
        console.close();
    }
}
