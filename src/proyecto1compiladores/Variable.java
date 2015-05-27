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
public class Variable extends Ambito {
    
    Tipo tipo;
    int offset;
    int relativeOffset;
    Tipo base;
    boolean esGlobal;
    boolean isLive;
    int nextUse;
    //AddressDescriptor addressDescriptor;
    
    public Variable(String n,Tipo t) {
        super(n);
        this.tipo = t;
        esGlobal = false;
        this.isLive = true;
        this.nextUse = -1;
        //this.addressDescriptor = new AddressDescriptor(this);
      
    }
    public Variable(String n,Tipo t,boolean t1) {
        super(n);
        esGlobal = false;
        this.tipo = t;
        this.isLive = true;
        this.nextUse = -1;
        //Agregamos direccio base si la variable es un arreglo
        if(t instanceof ArrayType){
            Tipo[] t2 = (Tipo[]) t.valor;
            base = t2[0];
        }
       // this.addressDescriptor = new AddressDescriptor(this);
      
    }    
    
    public String getStringPoinerFromVariable(){
        String result = "";
        //Miramo si utilizamos el global pointer o stack pointer
        if(esGlobal){
            result= "GP["+offset+"]";
            
        
        }
        else{
            result = "SP["+offset+"]";
        }
        return result;
    
    }
         
    
}
