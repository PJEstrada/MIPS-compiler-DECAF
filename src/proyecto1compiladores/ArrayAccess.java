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
public class ArrayAccess {
    
    Variable array; // Arreglo a accesar
    Variable index; // Direccion final del acceso (base+offset)
    
    public ArrayAccess(Variable a, Variable i){
        array = a;
        index = i;
    }
    
    public String toStringIC(){
        String result = "";
        if(array.esGlobal){
            result = "GP["+index.name+"]";
        
        }
        else{
            result = "SP["+index.name+"]";
        }   
        return result;
    }
    
}
