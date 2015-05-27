/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto1compiladores;

/**
 *
 * @author pablo
 */
public class RegisterDescriptor {
    
    Registro registro;
    Variable variable;
    
    public RegisterDescriptor(Registro r, Variable v){
        this.registro = r;
        this.variable = v;
    
    }
}
