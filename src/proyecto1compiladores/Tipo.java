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
public class Tipo {
    
    String tipo; //Nombre del tipo
    Object valor;
    
    Estructura estructura;
    boolean isBasic;
    private int width; // bytes para el tipo

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    boolean error;
    
     public Tipo(String tipo,Object valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.width = 0;
        error = false;
        
        
    }
      public Tipo(boolean error){
          this.error = error;
      }
   

}
