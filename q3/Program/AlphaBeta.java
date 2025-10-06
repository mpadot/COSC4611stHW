package q3.Program;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.File;

public class AlphaBeta {
    private final char EMPTY = ' ';       // empty slot
    private final char COMPUTER = 'x';    // computer plays 'x'
    private final char PLAYER = 'o';      // human plays 'o'
    private final int MAX = 1;            // max level
    private final int MIN = 0;            // min level
    private static final int LIMIT = 2;   // depth limit

    private PrintWriter out;
    private Scanner console;

    // Board class (inner class)
    private class Board {
        private char[][] array; // board array

        private Board(int size) {
            array = new char[size][size];
            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++)
                    array[i][j] = EMPTY;
        }
    }

    private Board board;  // game board
    private int size;     // board size

    public AlphaBeta(int size, PrintWriter out, Scanner console) {
        this.size = size;
        this.out = out;
        this.console = console;
        this.board = new Board(size);
    }

    // play game
    public void play() {
        while (true) {
            board = playerMove(board);  // player move
            displayBoard(board, out);

            if (full(board)) {
                endGame(board);
                break;
            }

            board = computerMove(board);  // computer move
            displayBoard(board, out);

            if (full(board)) {
                endGame(board);
                break;
            }
        }
    }

    // let the player make a move
    private Board playerMove(Board board) {
        System.out.print("Player move (row col): ");
        int i = console.nextInt();
        int j = console.nextInt();
        board.array[i][j] = PLAYER;
        return board;
    }

    // determine computer's move
    private Board computerMove(Board board) {
        LinkedList<Board> children = generate(board, COMPUTER);
        int maxIndex = -1;
        int maxValue = Integer.MIN_VALUE;

        for (int i = 0; i < children.size(); i++) {
            int currentValue = minmax(children.get(i), MIN, 1,
                                      Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (currentValue > maxValue) {
                maxIndex = i;
                maxValue = currentValue;
            }
        }
        Board result = children.get(maxIndex);
        System.out.println("Computer move:");
        return result;
    }

    // minmax with alpha-beta pruning
    private int minmax(Board board, int level, int depth, int alpha, int beta) {
        if (full(board) || depth >= LIMIT) {
            return evaluate(board);
        } else {
            if (level == MAX) {
                LinkedList<Board> children = generate(board, COMPUTER);
                int maxValue = Integer.MIN_VALUE;
                for (Board child : children) {
                    int currentValue = minmax(child, MIN, depth + 1, alpha, beta);
                    maxValue = Math.max(maxValue, currentValue);
                    if (maxValue >= beta) return maxValue;
                    alpha = Math.max(alpha, maxValue);
                }
                return maxValue;
            } else {
                LinkedList<Board> children = generate(board, PLAYER);
                int minValue = Integer.MAX_VALUE;
                for (Board child : children) {
                    int currentValue = minmax(child, MAX, depth + 1, alpha, beta);
                    minValue = Math.min(minValue, currentValue);
                    if (minValue <= alpha) return minValue;
                    beta = Math.min(beta, minValue);
                }
                return minValue;
            }
        }
    }

    // generate children
    private LinkedList<Board> generate(Board board, char symbol) {
        LinkedList<Board> children = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board.array[i][j] == EMPTY) {
                    Board child = copy(board);
                    child.array[i][j] = symbol;
                    children.addLast(child);
                }
            }
        }
        return children;
    }

    // check full board
    private boolean full(Board board) {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (board.array[i][j] == EMPTY)
                    return false;
        return true;
    }

    // make copy
    private Board copy(Board board) {
        Board result = new Board(size);
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                result.array[i][j] = board.array[i][j];
        return result;
    }

    // display board + scores
    private void displayBoard(Board board, PrintWriter out) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++){
                System.out.print(board.array[i][j] == EMPTY ? '.' : board.array[i][j]);
                out.print(board.array[i][j] == EMPTY ? '.' : board.array[i][j]);
            }
            System.out.println();
            out.println();
        }
            System.out.println();
            out.println();
            
        
        int sComp = score(board, COMPUTER);
        int sPlayer = score(board, PLAYER);
        System.out.println("Score X: " + sComp + "  Score O: " + sPlayer);
        out.println("Score X: " + sComp + "  Score O: " + sPlayer);
    }

    // evaluate board for minimax
    private int evaluate(Board board) {
        return score(board, COMPUTER) - score(board, PLAYER);
    }

    // scoring function
    private int score(Board board, char symbol) {
        int pairs = 0, triples = 0;

        // check rows
        for (int i = 0; i < size; i++) {
            String row = new String(board.array[i]);
            pairs += countSeq(row, "" + symbol + symbol);
            triples += countSeq(row, "" + symbol + symbol + symbol);
        }

        // check cols
        for (int j = 0; j < size; j++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) sb.append(board.array[i][j]);
            String col = sb.toString();
            pairs += countSeq(col, "" + symbol + symbol);
            triples += countSeq(col, "" + symbol + symbol + symbol);
        }

        // diagonals
        for (int k = 0; k <= 2*(size-1); k++) {
            StringBuilder d1 = new StringBuilder();
            StringBuilder d2 = new StringBuilder();
            for (int i = 0; i < size; i++) {
                int j1 = k - i;
                int j2 = (size-1-k) + i;
                if (j1 >= 0 && j1 < size) d1.append(board.array[i][j1]);
                if (j2 >= 0 && j2 < size) d2.append(board.array[i][j2]);
            }
            pairs += countSeq(d1.toString(), "" + symbol + symbol);
            triples += countSeq(d1.toString(), "" + symbol + symbol + symbol);
            pairs += countSeq(d2.toString(), "" + symbol + symbol);
            triples += countSeq(d2.toString(), "" + symbol + symbol + symbol);
        }

        return 2 * pairs + 3 * triples;
    }

    // count substrings with overlap
    private int countSeq(String line, String pat) {
        int count = 0;
        for (int i = 0; i <= line.length() - pat.length(); i++) {
            if (line.substring(i, i + pat.length()).equals(pat))
                count++;
        }
        return count;
    }

    // end game and declare winner
    private void endGame(Board board) {
        int sComp = score(board, COMPUTER);
        int sPlayer = score(board, PLAYER);
        if (sComp > sPlayer) System.out.println("Computer wins!");
        else if (sComp < sPlayer) System.out.println("Player wins!");
        else System.out.println("Draw!");
    }

    public static void main(String[] args) throws Exception {
        Scanner console = new Scanner(System.in);
        System.out.print("Enter board size (even): ");
        int size = console.nextInt();
        System.out.print("Enter output filename: ");
        String outputFile = console.next();

        // Create PrintWriter in q3/Output/
        PrintWriter out = new PrintWriter(new File("q3/Output/" + outputFile));
        AlphaBeta game = new AlphaBeta(size, out, console);
        game.play();
        out.close();
    }
}