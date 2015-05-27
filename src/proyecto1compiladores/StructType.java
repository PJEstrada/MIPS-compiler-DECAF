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
public class StructType extends Tipo{
    ArrayList<Tipo> tiposContenidos;
    
    public StructType(String tipo, ArrayList<Tipo>t) {
        super(tipo, tipo);
        tiposContenidos=t;
        super.isBasic=false;
        
    }
    
    @Override
    public int getWidth(){
        int r=0;
        for(Tipo t:tiposContenidos){
            r+=t.getWidth();
        
        }
        return r;
    }
    
    
}
