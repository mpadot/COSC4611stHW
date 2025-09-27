package q2.Program;

public class tester{
    public static void main (String [] args){
        //initial board
        char[][] initial = {{'5', '7', '1'},
                            {'2', ' ', '8'},
                            {'4', '6', '3'}};
        //final board
        char[][] goal = {{'1', '4', '8'},
                         {'5', '2', '6'},
                         {' ', '3', '7'}};

        //solve sliding puzzle
        question2 s = new question2(initial, goal, 3);
        s.solve();
    
    }
       
}