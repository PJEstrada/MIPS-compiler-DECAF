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
public class Metodo extends Ambito {
    ArrayList<Variable> parametros;
    Tipo tipoResultado;
    int numMetodo;
    Int finalSize;
    
    public Metodo(String n,ArrayList<Variable> parametros, ArrayList<Variable> variablesLocales, Tipo tipoResultado) {
        super(n);
        numMetodo = 0;
        this.parametros = parametros;
        this.tipoResultado = tipoResultado;
        
    }

    public ArrayList<Variable> getParametros() {
        return parametros;
    }

    public void setParametros(ArrayList<Variable> parametros) {
        this.parametros = parametros;
        char a = (char)50000;
    }

    public Tipo getTipoResultado() {
        return tipoResultado;
    }

    public void setTipoResultado(Tipo tipoResultado) {
        this.tipoResultado = tipoResultado;
    }
    
    public int getActivationRecordSize(){
        int result=0;
        for(Variable v: this.variables){
            if(!parametros.contains(v)){
                result+=v.tipo.getWidth();
            }

        
        }
        for(Ambito a: this.ambitosInternos){
            result+= a.getSizeAmbito();
        }
        return result;
        
    }
    
}
