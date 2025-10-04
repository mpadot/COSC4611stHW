
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Astar {
    private int fopt; // f value options: 1, 2, or 3
    private int hopt; // heuristic options: 1 or 2
    private int size;

    private Board initial; // initial board
    private Board goal;    // goal board

    // Inner Board class
    private class Board {
        private int[][] array;
        private int gvalue;
        private int hvalue;
        private int fvalue;
        private Board parent;

        private Board(int[][] array, int size) {
            this.array = new int[size][size];
            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++)
                    this.array[i][j] = array[i][j];
            this.parent = null;
        }
    }

    // Constructor
    public Astar(int[][] initial, int[][] goal, int size, int fopt, int hopt) {
        this.size = size;
        this.fopt = fopt;
        this.hopt = hopt;
        this.initial = new Board(initial, size);
        this.goal = new Board(goal, size);
    }

    // Solve method
    public void solve(PrintWriter out) {
        LinkedList<Board> openList = new LinkedList<>();
        LinkedList<Board> closedList = new LinkedList<>();
        openList.addFirst(initial);

        int boardsSearched = 0;

        long startTime = System.nanoTime();

        while (!openList.isEmpty()) {
            int best = selectBest(openList);
            Board board = openList.remove(best);
            boardsSearched++;
            closedList.addLast(board);

            if (goal(board)) {
                displayPath(board, out);
                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1_000_000;
                System.out.println("Total swaps: " + board.gvalue);
                System.out.println("Boards searched: " + boardsSearched);
                System.out.println("Runtime: " + duration + " ms");
                out.println("Total swaps: " + board.gvalue);
                out.println("Boards searched: " + boardsSearched);
                out.println("Runtime: " + duration + " ms");
                return;
            }

            LinkedList<Board> children = generate(board);
            for (Board child : children) {
                if (!exists(child, closedList)) {
                    if (!exists(child, openList)) {
                        openList.addLast(child);
                    } else {
                        int index = find(child, openList);
                        if (child.fvalue < openList.get(index).fvalue) {
                            openList.remove(index);
                            openList.addLast(child);
                        }
                    }
                }
            }
        }

        System.out.println("No solution found");
        out.println("No solution found");
    }

    // Generate children boards
    private LinkedList<Board> generate(Board board) {
        int i = 0, j = 0;
        boolean found = false;
        for (i = 0; i < size && !found; i++)
            for (j = 0; j < size; j++)
                if (board.array[i][j] == 0) {
                    found = true;
                    break;
                }
        i--; // adjust because loop incremented

        boolean north = i > 0, south = i < size - 1, west = j > 0, east = j < size - 1;
        LinkedList<Board> children = new LinkedList<>();
        if (north) children.addLast(createChild(board, i, j, 'N'));
        if (south) children.addLast(createChild(board, i, j, 'S'));
        if (east) children.addLast(createChild(board, i, j, 'E'));
        if (west) children.addLast(createChild(board, i, j, 'W'));
        return children;
    }

    // Create child board
    private Board createChild(Board board, int i, int j, char direction) {
        Board child = copy(board);
        switch (direction) {
            case 'N': child.array[i][j] = child.array[i-1][j]; child.array[i-1][j] = 0; break;
            case 'S': child.array[i][j] = child.array[i+1][j]; child.array[i+1][j] = 0; break;
            case 'E': child.array[i][j] = child.array[i][j+1]; child.array[i][j+1] = 0; break;
            case 'W': child.array[i][j] = child.array[i][j-1]; child.array[i][j-1] = 0; break;
        }

        child.gvalue = board.gvalue + 1;
        if (fopt == 1) {
            child.hvalue = (hopt == 1) ? heuristic_M(child) : heuristic_D(child);
            child.fvalue = child.hvalue;
        } else if (fopt == 2) {
            child.fvalue = child.gvalue;
        } else if (fopt == 3) {
            child.hvalue = (hopt == 1) ? heuristic_M(child) : heuristic_D(child);
            child.fvalue = child.gvalue + child.hvalue;
        }

        child.parent = board;
        return child;
    }

    // Heuristics
    private int heuristic_M(Board board) {
        int value = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board.array[i][j] != 0 && board.array[i][j] != goal.array[i][j]) value++;
        return value;
    }

    private int heuristic_D(Board board) {
        int value = 0;
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board.array[i][j] != 0 && board.array[i][j] != goal.array[i][j]) {
                    int val = board.array[i][j];
                    for (int x = 0; x < size; x++)
                        for (int y = 0; y < size; y++)
                            if (goal.array[x][y] == val)
                                value += Math.abs(x-i) + Math.abs(y-j);
                }
        return value;
    }

    private int selectBest(LinkedList<Board> list) {
        int minIndex = 0;
        int minValue = list.get(0).fvalue;
        for (int i = 1; i < list.size(); i++)
            if (list.get(i).fvalue < minValue) {
                minValue = list.get(i).fvalue;
                minIndex = i;
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
        for (Board b : list) if (identical(board, b)) return true;
        return false;
    }

    private int find(Board board, LinkedList<Board> list) {
        for (int i = 0; i < list.size(); i++) if (identical(board, list.get(i))) return i;
        return -1;
    }

    private boolean identical(Board a, Board b) {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (a.array[i][j] != b.array[i][j]) return false;
        return true;
    }

    private void displayPath(Board board, PrintWriter out) {
        LinkedList<Board> path = new LinkedList<>();
        Board pointer = board;
        while (pointer != null) {
            path.addFirst(pointer);
            pointer = pointer.parent;
        }
        for (Board b : path) displayBoard(b, out);
    }

    private void displayBoard(Board board, PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(String.format("%2d ", board.array[i][j]));
            }
            sb.append("\n");
        }
        System.out.print(sb.toString() + "\n");
        if (out != null) out.print(sb.toString() + "\n");
    }

    // Main method reads input file
    // Main method reads input file
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.print("Enter input filename: ");
        String inputFile = console.nextLine().trim();
        System.out.print("Enter output filename: ");
        String outputFile = console.nextLine().trim();

        // input file is in the current project folder
        Scanner sc = new Scanner(new File(inputFile));

        // output goes into the Output folder
        File outputDir = new File("Output");
        if (!outputDir.exists()) {
            outputDir.mkdir(); // create folder if it doesn’t exist
        }
        PrintWriter out = new PrintWriter("Output/" + outputFile);

        // Read puzzle size
        int size = sc.nextInt();

        // Initialize boards
        int[][] start = new int[size][size];
        int[][] goal = new int[size][size];

        // Read initial board
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                start[i][j] = sc.nextInt();
            }
        }

        // Read goal board
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                goal[i][j] = sc.nextInt();
            }
        }

        // Read options
        int fopt = sc.nextInt();
        int hopt = sc.nextInt();

        // Create solver
        Astar solver = new Astar(start, goal, size, fopt, hopt);

        System.out.println("Initial Puzzle:");
        solver.displayPath(solver.initial, out); // display initial puzzle

        System.out.println("\nSolving...\n");
        solver.solve(out);

        out.close();
        sc.close();
        console.close();
    }

}
