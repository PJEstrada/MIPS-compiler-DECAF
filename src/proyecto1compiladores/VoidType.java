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
public class VoidType extends Tipo{
    boolean isReturn;
    public VoidType(String tipo, Object valor,boolean s) {
        super(tipo, valor);
        isReturn=s;
        super.isBasic=true;
        super.setWidth(0);
    }
    public VoidType(String tipo, Object valor) {
        super(tipo, valor);
        isReturn = false;
        super.isBasic=true;
        super.setWidth(0);
    }    
}
