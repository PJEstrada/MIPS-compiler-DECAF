package proyecto1compiladores;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.gui.TreeViewer;


public class Proyecto1Compiladores {

    public static void main(String[] args) {
        

        TextEditorFrame frame = new TextEditorFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        System.out.println(fibonacci(30));
        
  
        


    }
    static int fibonacci( int x){
        int fib;
         if(x==0 || x==1){
            return 1;
        }
        else{
            fib = fibonacci(x-1)+fibonacci(x-2);
        }
        return fib;

    }    
}
