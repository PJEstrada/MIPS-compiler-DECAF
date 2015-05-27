/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto1compiladores;

import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public class BasicBlock {
    
    ArrayList<Quad> instructions;
    
    public BasicBlock(){
        instructions = new ArrayList<Quad>();
    }
    public BasicBlock(ArrayList<Quad> qs){
        instructions = new ArrayList<Quad>();
        instructions.addAll(qs);
    }
    
}
