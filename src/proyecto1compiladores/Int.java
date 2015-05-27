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
public class Int extends Tipo{

    public Int(String tipo, Object valor) {
        super(tipo, valor);
        super.setWidth(4);
        super.isBasic=true;
        
    }
    public Int(){
        super("int",0);
        super.setWidth(4);
        super.isBasic=true;
    }
    
    public Int(int v){
        super("int",v);
        super.setWidth(4);
        super.isBasic=true;        
    }
    
    
    
}
