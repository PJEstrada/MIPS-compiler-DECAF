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
public class AddressDescriptor {
    
    Variable variable;
    Registro registro;
    Variable memoria;
    
    
    public AddressDescriptor(Variable off, Registro r){
        this.variable = off;
        registro = r;
        
    
    }
    public AddressDescriptor(Variable off, Variable m){
        this.variable = off;
        memoria = m;

        
    
    }    
}
