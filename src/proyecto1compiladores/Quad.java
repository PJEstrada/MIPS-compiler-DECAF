/*
PARA MIPS:
    - Agregar metodo TypeToAssemblyValue(Tipo t) : recibe un tipo con una constante y lo pasa a su respectiva representacion numerica para ser utilizada en MIPS
 */
package proyecto1compiladores;

/**
 *
 * @author pablo
 */
public class Quad {
    boolean isLabel;
    String op;
    Object target; //Target puede ser una variable o un int representando la direccion de un quad en el buffer de instrucciones
    Object arg1;
    Object arg2;
    
    boolean arg1Live;
    boolean arg2Live;
    boolean targLive;
    int arg1NextUse;
    int arg2NextUse;
    int targNextUse;
    //Arg1 y arg2 pueden ser variables, temporales o constantes. Las temporales tienen un tipo pero el atributo valor de este tipo no es utilizado
    // Si arg1 o arg2 es una constante seran un objeto TIPO y su valor estara en el atributo del valor
    public Quad(String op, Object arg1, Object arg2, Object target){
        this.op = op;
        this.arg1=arg1;
        this.arg2=arg2;
        this.target=target;
    
    }
    public Quad(String op, Object arg1, Object arg2, Object target,boolean s){
        this.op = op;
        this.arg1=arg1;
        this.arg2=arg2;
        this.target=target;
        this.isLabel =s;
    
    }    
    public String toString(){
        String s="";
        Object ar1=null;
        Object ar2 = null;
        Object targ = null;
        if(target instanceof Quad){
            Quad q = (Quad) target;
            targ = q.target.toString();
        }
        else if(target instanceof Variable){
            if(target instanceof Temp){
                Temp t = (Temp) target;
                targ = t.name;
                
            }
            else{
                Variable v = (Variable) target;
                targ = v.getStringPoinerFromVariable();            
            }

        }
        else if( target instanceof ArrayAccess){
            ArrayAccess ac = (ArrayAccess) target;
            targ = ac.toStringIC();
        
        }
        else if(target instanceof Integer){
            targ = target;
        
        }
        else if(target instanceof String){
            targ = target;
        }
        
        if(arg1 instanceof Variable){
            if(arg1 instanceof Temp){
                Temp var1 =(Temp) arg1;
                ar1 = var1.name;                  
                
            }
            else{
                Variable var1 =(Variable) arg1;
                ar1 = var1.getStringPoinerFromVariable();            
            }
            
        }
        else if( arg1 instanceof ArrayAccess){
            ArrayAccess ac = (ArrayAccess) arg1;
            ar1 = ac.toStringIC();
        
        } 
        else if(arg1 instanceof String){
            ar1 = arg1;
        }
        else if(arg1 !=null){
            Tipo t1 = (Tipo) arg1;
            ar1 = t1.valor;
            if(t1 instanceof Char ){
                ar1 = "'"+t1.valor+"'";
            }            
        }
        
        
        if(arg2 instanceof Variable){
            if(arg2 instanceof Temp){
                Temp var2 = (Temp) arg2;
                ar2 = var2.name;
          
            }
            else{
                Variable var2 =(Variable) arg2;
                ar2 = var2.getStringPoinerFromVariable();           
            }

        }
        
        else if( arg2 instanceof ArrayAccess){
            ArrayAccess ac = (ArrayAccess) arg2;
            ar2 = ac.toStringIC();
        
        }   
        else if(arg2 instanceof String){
            ar2 = arg2;
        }        
        else if(arg2 !=null){
            Tipo t2 = (Tipo) arg2;
            ar2 = t2.valor;
            if(t2 instanceof Char ){
                ar2 = "'"+t2.valor+"'";
            }
        }        
        //Para operadores binarios
        if(op.equals("ADD")||op.equals("SUB")||op.equals("MULT")||op.equals("DIV")||op.equals("AND")||op.equals("OR")||op.equals("LT")||op.equals("LE")
                ||op.equals("EQ")||op.equals("GE")||op.equals("GT")||op.equals("NE")||op.equals("MOD")){
            
            if(op.equals("LT")||op.equals("LE")
                ||op.equals("EQ")||op.equals("GE")||op.equals("GT")||op.equals("NE")){
                s= "B"+op+" "+ar1+", "+ar2 +" goTo "+targ;
            
            }
            else{
                s= targ+" = "+ar1+" "+op+" "+ar2;
            }
        }
        //Operadores unarios
        if(op.equals("NOT")||op.equals("MINUS")||op.equals("(INT)")||op.equals("(CHAR)")){
            s = targ+" = "+op+" "+ar1;
        }
        //Para asignaciones
        else if(op.equals("ASSIGN")){
            s= targ+" = "+ar1;
        }
        else if(op.equals("ASSIGN-AR")){
        
            s = targ+" = "+ar1+" [ "+ar2+" ]";
        }
        //Goto
        else if(op.equals("GOTO")){
            s = "b "+targ;
        
        }
        else if(this.isLabel==true){
            s = op+": ";
        }
        else if (op.equals("RETURN")){
            if(this.arg1==null){
                s = op;
            }
            else{
                s= op+" "+ar1;
            }
        
        }
        else if(op.equals("PARAM")){
            s = op+" "+ar1;
        }
        else if(op.equals("CALL")){
            if(this.target==null){
                s = op+" "+ar1;
            }
            else{
                s = targ+" = "+op+" "+ar1+" "+ar2;
            }
        }
        else if(op.equals("HALT")){
            s = op;
        }
        else if(op.equals("INIT_PARAMS")){
            s = op;
        }
        else if(op.equals("PUSH_RESULT")){
            s = op+" "+ar1;
        }
        else if(op.equals("PUSH_AL")){
            s = op;
        }          
        else if(op.equals("PUSH_LOCALS")){
            s = op+" "+ar1;
        }       
        else if(op.equals("POP_RESULT")){
            s = op+" "+ar1;
        }       
        else if(op.equals("POP_LOCALS")){
            s = op+" "+ar1;
        }     
        else if(op.equals("POP_LINK")){
            s = op+" ";
        }    
        else if(op.equals("POP_PARAMS")){
            s = op+" "+ar1;
        }         
        return s;
    }
}
