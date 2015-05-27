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
public class Registro {
    
    static final Registro t0 = new Registro("$t0");
    static final Registro t1 = new Registro("$t1");
    static final Registro t2 = new Registro("$t2");
    static final Registro t3 = new Registro("$t3");
    static final Registro t4 = new Registro("$t4");
    static final Registro t5 = new Registro("$t5");
    static final Registro t6 = new Registro("$t6");
    static final Registro t7 = new Registro("$t7");
    static final Registro loadStore = new Registro("$t8"); //Paracargas
    static final Registro inmediate1 = new Registro("$s0");
    static final Registro inmediate2 = new Registro("$s1");
    String nombre;
    
    public Registro(String n){
        this.nombre = n;
    }
    
}
