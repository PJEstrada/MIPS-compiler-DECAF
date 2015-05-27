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
public class TablaSimbolos {
    String nombre;
    ArrayList<Ambito> tabla;

    public TablaSimbolos(String nombre, ArrayList<Ambito> tabla) {
        this.nombre = nombre;
        this.tabla = tabla;
    }
    public TablaSimbolos(String n){
        nombre = n;
        tabla = new ArrayList<Ambito>();
    }
    
    
    
}
