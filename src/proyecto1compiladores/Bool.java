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
public class Bool extends Tipo{

    public Bool(String tipo, Object valor) {
        super(tipo, valor);
        super.setWidth(4);
        super.isBasic=true;
    }

    public Bool() {
        super("boolean", false);
        super.setWidth(1);
        super.isBasic=true;
    }    
    
    
}
