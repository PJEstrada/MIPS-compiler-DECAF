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
public class Ambito {
    
    String name;
    ArrayList<Variable> variables;
    ArrayList<Ambito> ambitosInternos;
    
    public Ambito(String n){
        this.name = n;
        variables = new ArrayList<Variable>();
        ambitosInternos = new ArrayList<Ambito>();
    }

    
  /*  public ArrayList<AddressDescriptor> getAllAddressDescriptors(Ambito a){
        ArrayList<AddressDescriptor> result = new ArrayList<AddressDescriptor>();
        //Agregamos inicialmente las variables de este ambito
        for(Variable v : this.variables){
            result.add(v.addressDescriptor);
        
        }
        for(Ambito ai: a.ambitosInternos){
            
        
        }
    
    }    */
    public String getName(){
        return this.name;
    }
    
    public int getSizeAmbito(){
        int result = 0;
        for(Variable v: this.variables){
            result+= v.tipo.getWidth();
        }
        for(Ambito a: ambitosInternos){
            result+= a.getSizeAmbito();
        }
        return result;
        
    }
    

        
}
