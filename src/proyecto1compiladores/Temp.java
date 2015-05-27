/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto1compiladores;


import java.util.Stack;

/**
 *
 * @author pablo
 */
public class Temp extends Variable {
    
    static int tempNum=0;
    static Stack<Temp> disponibles= new Stack<Temp>();
    int num;

    
    public Temp(Tipo t) {
        super("$temp"+tempNum, t);
        num =tempNum;
        tempNum++;
        super.esGlobal= false;
    }
    
    public Temp(Tipo t,boolean t1) {
        super("$temp"+tempNum, t,t1);
        tempNum++;
        super.esGlobal = false;

    }
    
    public static Temp checkTemp(){
        if(disponibles.isEmpty()){
            return null;
        }
        else{
            return disponibles.pop();
        }
       
    
    }
    
    public static void liberarTemp(Temp t){
        disponibles.push(t);
    
    }

}
