import java.io.File;
import java.io.PrintWriter;
public class AstarTester{
    public static void main (String [] args){
        //test #1
        //3x3
        char [] [] start = {
            {'5', '7', '1'}, 
            {'2', '0', '8'},
            {'4', '6', '3'}
        };
     
        char [] [] goal = {
            {'0', '4', '8'},
            {'1', '5', '2'}, 
            {'6', '3', '7'}
        };

        int fopt  =1;
        int hopt = 2;
        Astar solver = new Astar(start, goal, 3, fopt, hopt);

        try{
                File outFile = new File("../Output/output.txt");
                outFile.getParentFile().mkdirs(); // creates Output folder if missing
                PrintWriter out = new PrintWriter(outFile);


                long startTime = System.nanoTime();
                solver.solve(out);
                long endTime = System.nanoTime();
                long duration = (endTime - startTime)/1000000; //this converts it to miliseconds
                System.out.println("Runtime is: " + duration + " milliseconds");
                out.println("Runtime is: " + duration + " milliseconds");
                
                
                out.close();
        }catch(Exception e){
            e.printStackTrace();
        }




    }
}