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

/**
 * Clase que se utilizara para el retorno de atributos en la generacion de codigo intermedio
 * 
 * 
 */


public class IC {
    
    Object addr;  // Puede ser una constante (Tipo) variable o temporal (Temp)
    ArrayList<Integer> trueList;
    ArrayList<Integer> falseList;
    Tipo tipo;      //Usado cuando se necesita el tipo de algun metodo
    Variable array; // se guatdar variable de un array en la llamada a arreglos
    ArrayList<Integer> nextList;
    Variable finalVar;
    public IC(Variable addr){
        this.addr = addr;
        trueList = new ArrayList<Integer>();
        falseList = new ArrayList<Integer>();
        
    }
    public IC(){
        addr=null;
        trueList = new ArrayList<Integer>();
        falseList = new ArrayList<Integer>();
        nextList = new ArrayList<Integer>();
    }
    
}
