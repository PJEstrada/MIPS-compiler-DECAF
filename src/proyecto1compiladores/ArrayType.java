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
public class ArrayType extends Tipo {
    int arraySize;
    Tipo elementType;
    int base;
    public ArrayType(String tipo, int valor,Tipo elementType) {
        super(tipo, valor);
        Tipo[] v = new Tipo[valor];
        for(int i =0;i<v.length;i++){
            if(elementType instanceof Int){
                v[i]= new Int("int",0);
            }
            else if(elementType instanceof Bool){
                v[i]= new Bool("boolean",false);
            }
            else if (elementType instanceof Char){
                v[i]= new Char("char",' ');
            }
            else if (elementType instanceof StructType){
                StructType st= (StructType)elementType;
                v[i]= new StructType(st.tipo,st.tiposContenidos);
               
            }
        
        }
        super.valor= v;
        arraySize = (int) valor;
        super.isBasic=false;
        this.elementType= elementType;
        
    }
    
    @Override
    public int getWidth(){
        return elementType.getWidth()*arraySize;
    }
}
