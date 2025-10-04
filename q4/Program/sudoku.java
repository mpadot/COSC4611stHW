package q4.Program;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;

public class sudoku {
    private String[][] board;
    private int size;
    private int subgrid;

    public sudoku(String[][] board, int size) {
        this.board = board;
        this.size = size;
        this.subgrid = (int) Math.sqrt(size);
    }

    public void solve(PrintWriter out) {
        if (fill(0))
            display(out);
        else
            System.out.println("No solution found.");
    }

    private boolean fill(int location) {
        if (location >= size * size)
            return true;

        int x = location / size;
        int y = location % size;
        String cell = board[x][y];

        // Skip blacked-out or pre-filled numeric cells
        if (cell.equalsIgnoreCase("B") || isNumeric(cell))
            return fill(location + 1);

        for (int value = 1; value <= size; value++) {
            if (cell.equalsIgnoreCase("O") && value % 2 == 0) continue;
            if (cell.equalsIgnoreCase("E") && value % 2 != 0) continue;

            board[x][y] = Integer.toString(value);

            if (check(x, y) && fill(location + 1))
                return true;

            board[x][y] = cell; // backtrack
        }

        return false;
    }

    private boolean check(int x, int y) {
        String val = board[x][y];
        if (!isNumeric(val)) return true;

        // Row check
        for (int j = 0; j < size; j++) {
            if (j != y && board[x][j].equals(val)) return false;
        }

        // Column check
        for (int i = 0; i < size; i++) {
            if (i != x && board[i][y].equals(val)) return false;
        }

        // Subgrid check
        int regionRow = (x / subgrid) * subgrid;
        int regionCol = (y / subgrid) * subgrid;
        for (int i = 0; i < subgrid; i++) {
            for (int j = 0; j < subgrid; j++) {
                int r = regionRow + i;
                int c = regionCol + j;
                if (!(r == x && c == y) && board[r][c].equals(val))
                    return false;
            }
        }

        return true;
    }

    private boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Method displays a board neatly
    public void display(PrintWriter out) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // Fixed width output for alignment
                System.out.print(String.format("%3s ", board[i][j]));
                out.print(String.format("%3s ", board[i][j]));
            }
            System.out.println();
            out.println();
        }
        System.out.println();
        out.println();
    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);

        System.out.print("Enter input filename: ");
        String inputFile = console.nextLine();

        System.out.print("Enter output filename: ");
        String outputFile = console.nextLine();

        // Automatically look inside the Output folder for input
        Scanner in = new Scanner(new File("q4/Output/" + inputFile));

        // Create the output file inside the same Output folder
        PrintWriter out = new PrintWriter(new File("q4/Output/" + outputFile));

        int size = in.nextInt();
        String[][] board = new String[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = in.next();
            }
        }

        sudoku solver = new sudoku(board, size);

        System.out.println("Initial Puzzle:");
        solver.display(out);

        System.out.println("\nSolving...\n");
        solver.solve(out);

        out.close();
        in.close();
        console.close();
    }
}
