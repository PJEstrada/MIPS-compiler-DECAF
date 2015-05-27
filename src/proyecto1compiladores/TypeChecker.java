/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto1compiladores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import proyecto1compiladores.DECAFParser.ArgContext;
import proyecto1compiladores.DECAFParser.VarDeclarationContext;

/**
 *
 * @author pablo
 */


public class TypeChecker extends DECAFBaseVisitor<Tipo>{
    
    boolean insideStruct;
    Stack<String> structVars;
    Estructura currentStruct;
    Stack<Estructura> structStack;
    Ambito ambitoGlobal;
    Stack<Ambito> ambitos;
    Ambito ambitoActual;
    boolean estructuraEncontrada;
    boolean error;
    Metodo metodoActual;
    int bnum;
    int offset;
    Stack<Integer>offsetStack;
   TextEditorFrame frame;
   ArrayList<String> dataSection;
   static final  Tipo ERROR =new Tipo(true);;   
   int globalSize;
        
    public TypeChecker(TextEditorFrame f){
        super();
        dataSection = new ArrayList<String>();
        frame = f;
        ambitoGlobal = new Ambito("global");
        ambitos = new Stack<Ambito>();
        structStack = new Stack<Estructura>();
        structVars = new Stack<String>();
        ambitos.push(ambitoGlobal);
        ambitoActual = ambitos.peek();
        estructuraEncontrada=false;
        error =false;
        offset =0;
        offsetStack = new Stack<Integer>();
        
        //Metodo para imprimir Int
        ArrayList<Variable> v = new ArrayList<Variable>();
        v.add(new Variable("p",new Int("int",0)));
        ambitoGlobal.ambitosInternos.add(new Metodo("print_int",v,v,new VoidType("void",null)));
        //Metodo para leer int
        v = new ArrayList<Variable>();
        ambitoGlobal.ambitosInternos.add(new Metodo("input_int",v,v,new Int()));    
        
        
        //Metodo para imprimir char
        v = new ArrayList<Variable>();
        v.add(new Variable("p",new Char("char",0)));
        ambitoGlobal.ambitosInternos.add(new Metodo("print_char",v,v,new VoidType("void",null)));
        //Metodo para leer char
        v = new ArrayList<Variable>();
        ambitoGlobal.ambitosInternos.add(new Metodo("input_char",v,v,new Char("char",null)));   
        
        
        //Metodo para imprimir boolean
        v = new ArrayList<Variable>();
        v.add(new Variable("p",new Bool("boolean",0)));
        ambitoGlobal.ambitosInternos.add(new Metodo("print_boolean",v,v,new VoidType("void",null)));
        //Metodo para leer boolean
        v = new ArrayList<Variable>();
        ambitoGlobal.ambitosInternos.add(new Metodo("input_boolean",v,v,new Bool("bool",null)));            
    }
    public void error(String s){
        frame.getConsole().append(s+"\n");
    }
    public boolean buscarVariableAmbitoActual(String n){
        
        for(Variable v: ambitoActual.variables){
            
            if(v.name.equals(n)){
                return true;
            }
        
        }
        return false;
    }
    
    
    public Variable buscarVariableStruct(String n, Estructura s){
        for(Variable v: s.variables){
            if(v.name.equals(n)){
                return v;
            }

        }
        return null;
    }
    
    public boolean containsReturn(DECAFParser.StatementContext n){
        for(ParseTree n1: n.children){
            if(n1 instanceof DECAFParser.StatementContext){
                DECAFParser.StatementContext n2=(DECAFParser.StatementContext) n1;
                for(ParseTree n3: n2.children){
                    if(n3 instanceof DECAFParser.StatementContext){
                        return true;
                    }
                
                }            
            }
            if(n1 instanceof DECAFParser.ReturnStatementContext){
                return true;
            }
        
        }
        return false;
    }
        
    public Estructura buscarEstructura(String n,Estructura s){
        if(s==null){
            Estructura result = null;
            //Buscamos ambito por ambito en el stack
             for(int i = ambitos.size()-1;i>=0;i--){
                 Ambito ac = ambitos.get(i);
                 for(Ambito a: ac.ambitosInternos){
                     if(a instanceof Estructura){
                         Estructura a1 = (Estructura)a;
                         //Si encuentra el nombre de la estructura se crea el tipo
                         if(a.name.equals(n)){
                             result = a1; 
                         }
                     }
                 }
             }         
            return result;
        }
        else{
            Estructura result = null;
            for(Ambito a: s.ambitosInternos){
                if(a instanceof Estructura){
                    if(a.name.equals(n)){
                        result = (Estructura) a;
                        return result;
                    }

                }
            }
            for(Ambito a: this.ambitoGlobal.ambitosInternos){
                if(a instanceof Estructura){
                    if(a.name.equals(n)){
                        result = (Estructura) a;
                        return result;
                    }

                }
            }            
            
            return result;
        }
    
    }     
    
    public Variable buscarVariable(String n){
        Variable result = null;
        for(int i = ambitos.size()-1;i>=0;i--){
            Ambito ac = ambitos.get(i);
            for(Variable v: ac.variables){
                //Si encuentra el nombre de la estructura se crea el tipo
                if(v.name.equals(n)){
                    result = v;
                    return result;
                }
            }
        }  
        return result;
    
    }   
    
    public int numMetodosConNombre(String s){
        int count = 0;
        for(Ambito a: this.ambitoGlobal.ambitosInternos){
            if(a instanceof Metodo){
                Metodo m = (Metodo)a;
                if(m.name.equals(s)){
                    count++;
                }
            
            }
        
        }
        return count;
    }
    public boolean buscarMetodo(Metodo m){
        String methodName = m.name;
        boolean result = false;
        for (Ambito a : ambitoGlobal.ambitosInternos) {
            if(result==true){break;}
            if (a instanceof Metodo) {
                if (a.name.equals(methodName)) {
                    result = true;
                    //si encontramos el metodo, verificamos los tipos de parametros
                    Metodo a1 = (Metodo) a;
                    if (a1.parametros.size() == m.parametros.size()) {
                        for (int i = 0; i < m.parametros.size(); i++) {
                            if (!(a1.parametros.get(i).tipo.getClass().equals(m.parametros.get(i).tipo.getClass()))) {
                                result = false;
                                break;
                            }
                        }
                    }
                    else{result = false;}

                } else {
                    result = false;
                }
            } else {
                result = false;
            }
        }
    
        return result;
    }
    public Metodo buscarMain(){
        String methodName = "main";
        boolean result = false;
        for (Ambito a : ambitoGlobal.ambitosInternos) {
            if(result==true){break;}
            if (a instanceof Metodo) {
                if (a.name.equals(methodName)) {
                    result = true;
                    //si encontramos el metodo, verificamos los tipos de parametros
                    Metodo a1 = (Metodo) a;
                    return a1;
                } else {
                    result = false;
                }
            } else {
                result = false;
            }
        }
    
        return null;
    
    }    
   @Override public Tipo visitProgram(@NotNull DECAFParser.ProgramContext ctx) { 
       boolean mainFound=false;
       

       for(ParseTree n: ctx.children){
           Tipo t = visit(n);
           if(t!= null && t.equals(ERROR)){
               error = true;
               return ERROR;
               
           }
           if(error==true){return ERROR;}
           //Verifcando que no existan dos metodos main en el programa
           if(n instanceof DECAFParser.DeclarationContext){
               if(n.getChild(0) instanceof DECAFParser.MethodDeclarationContext){
                    DECAFParser.MethodDeclarationContext n1 = (DECAFParser.MethodDeclarationContext) n.getChild(0);
                    if(mainFound==true&&n1.ID().getText().equals("main")){                           
                            int line = n1.getStart().getLine();
                            error("Error, No es posible declarar dos metodos main.   Linea: "+line);
                            error = true;
                            return ERROR; 
                    }
                    else if(n1.ID().getText().equals("main")){
                        mainFound = true;
                        Metodo m = buscarMain();
                        if(m!=null && !(m.parametros.isEmpty()) ){
                            int line = n1.getStart().getLine();
                            error("Error, Main no debe llevar parametros.   Linea: "+line);
                            error = true;
                            return ERROR;                             
                        
                        }
                    }
               }
           }
       
       
       }
       if(mainFound==false){
        error = true;
        error("Error, No se ha declarado metodo main en el programa. ");
        return ERROR;        
       }
       return new VoidType(null,null);
     
   
   
   }
    
    
    @Override public Tipo visitStructDeclaration(@NotNull DECAFParser.StructDeclarationContext ctx) { 
       this.insideStruct = true;
        String structName = ctx.ID().getText();
        //Buscamos ambito por ambito en el stack
        for(int i = ambitos.size()-1;i>=0;i--){
            Ambito ac = ambitos.get(i);
            for(Ambito a: ac.ambitosInternos){
                if(a instanceof Estructura){
                    //Si encuentra el nombre de la estructura se crea el tipo
                    if(a.name.equals(structName)){
                        int line = ctx.getStart().getLine();
                        error("Error, Ya sea ha declarado estructura "+structName +"   Linea: "+line);
                        error = true;
                        return ERROR; 
                    }
                }
                if(ambitoActual instanceof Estructura){
                        int line = ctx.getStart().getLine();
                        error = true;
                        error("Error, No es posible declarar estructuras dentro de otras:  "+structName +"   Linea: "+line);
                        return ERROR; 
                                   
                
                }
            
            }
        }        
        //Si no lo encuentra en ninguno de los ambitos creamos la estructura
        List<VarDeclarationContext> vars = ctx.varDeclaration();
        ArrayList<Tipo> tipos = new ArrayList<Tipo>();
        Estructura struct = new Estructura(structName);
        StructType t1 = new StructType(structName,tipos);
        struct.tipo = t1;
        //agregando nuevo ambito a la pila  y al ambito actual correspondiente
        if(ambitoActual!=null){
            ambitoActual.ambitosInternos.add(struct);
            ambitos.push(struct);
            ambitoActual = ambitos.peek();
            
        }
        else{
            ambitoGlobal.ambitosInternos.add(struct);
            ambitos.push(struct);
            ambitoActual = ambitos.peek();
        }      
        
        this.offsetStack.push(this.offset);
        this.offset=0;
        for(VarDeclarationContext x:vars){
            Tipo t = visit(x);
            if(t.equals(ERROR)){
                error = true;
                return ERROR;
            }
            tipos.add(t);
        }
        t1.setWidth(offset);
        this.offset = this.offsetStack.pop();
        
        ambitos.pop();
        ambitoActual = ambitos.peek();
        t1 = new StructType(structName,tipos);
        this.insideStruct = false;
        
        return t1;
        
    }
    
    @Override
    public Tipo visitMethodDeclaration(@NotNull DECAFParser.MethodDeclarationContext ctx) {
        String methodName = ctx.ID().getText();
        this.bnum=0;
        ArrayList<Variable> savedParams = new ArrayList<Variable>();
        
        Tipo t = visit(ctx.methodType());
        if(t.equals(ERROR)){
            error = true;
            return ERROR;
        }

        DECAFParser.Parameter2Context paramsTest = ctx.parameter2();
                
        this.offsetStack.push(this.offset);
        this.offset=0;
        //Si no hay paramaetros
        if(paramsTest==null){
           //Contamos el numdero de metodo con el mismo nombre para asignarle el numero a el metodo
           int count = this.numMetodosConNombre(methodName);
           count++;         
           Metodo m = new Metodo(methodName,new ArrayList<Variable>(),new ArrayList<Variable>(),t); 
           m.numMetodo = count;
           ambitoGlobal.ambitosInternos.add(m);
           ambitos.push(m);
           ambitoActual = ambitos.peek();
           this.metodoActual=m;

        }
        //Si hay parametros
        else{
           ArrayList<DECAFParser.ParameterContext> parametros =  (ArrayList<DECAFParser.ParameterContext>) ctx.parameter2().parameter(); 
           savedParams = new ArrayList<Variable>();
           for(DECAFParser.ParameterContext p : parametros){
                String varName = p.ID().toString();
                Tipo varType = visit(p);
                if(varType.equals(ERROR)){
                    error = true;
                    return ERROR;
                }
                Variable newVar = new Variable(varName,varType);

                savedParams.add(newVar);
                
            }
            //Verificando que el metodo no haya sido declarado           
            //Contamos el numdero de metodo con el mismo nombre para asignarle el numero a el metodo
             int count = this.numMetodosConNombre(methodName);
             count++;               
           
            Metodo m = new Metodo(methodName,savedParams,new ArrayList<Variable>(),t); 
            m.numMetodo = count;
            this.metodoActual=m;
            boolean result = buscarMetodo(m);
            if(result){
                error = true;
                int line = ctx.getStart().getLine();
                error("Error, Ya sea ha declarado el metodo  "+m.name +"   Linea: "+line);
                return ERROR;                 
            }
            else{
                m.variables.addAll(savedParams);
                ambitoGlobal.ambitosInternos.add(m);
                ambitos.push(m);
                ambitoActual = ambitos.peek(); 
            }
        }

        
        
        //Visitamos el bloque para obtener variables locales y chequear expresiones
        t=visit(ctx.block());
       /* //Verificamos si hay valor de retorno para agregar offset de este valor
        if(!(metodoActual.tipoResultado instanceof VoidType)){
            this.offset+= metodoActual.tipoResultado.getWidth();
        }
        //Agregamos offset del espacio para AccesLink
        this.offset+= metodoActual.tipoResultado.getWidth();
        
        //Agregamos offset de los parametros
        for(Variable newVar: savedParams){
            //Agregando el offset
            newVar.offset=this.offset;
            this.offset+= newVar.tipo.getWidth();  
        }
        */
        if( !(this.metodoActual.tipoResultado instanceof VoidType)&&t instanceof VoidType){
                VoidType t1 = (VoidType)t;
                if(t1.isReturn==false){
                    int line = ctx.getStop().getLine();
                    error = true;
                    error("Error, No se encuentra statement de retorno al final de funcion  Se espera retorno tipo '"+this.metodoActual.tipoResultado.tipo+"'   Linea: "+line);
                    return ERROR;                          
                }
        }        
        if(t!=null &&t.equals(ERROR)){
            error = true;
            return ERROR;
        }
        
        ambitos.pop();
        ambitoActual = ambitos.peek();   
        offset = this.offsetStack.pop();
        return t; 
    }  

    
    @Override public Tipo visitStructDeclType(@NotNull DECAFParser.StructDeclTypeContext ctx) { 
        Tipo t = visit(ctx.structDeclaration());
        
        return t; 
    
    }
    @Override public Tipo visitStructType(@NotNull DECAFParser.StructTypeContext ctx) { 
        String structName = ctx.ID().getText();
        
        //Buscamos ambito por ambito en el stack
        for(int i = ambitos.size()-1;i>=0;i--){
            Ambito ac = ambitos.get(i);
            for(Ambito a: ac.ambitosInternos){
                if(a instanceof Estructura){
                    //Si encuentra la estructura se crea el tipo
                    if(a.name.equals(structName)){
                        Estructura a1 = (Estructura)a;
                        //Creando tipos internos 
                        this.estructuraEncontrada=true;
                        StructType t =  a1.tipo;
                        return t;
                    }
                }
            }
        }          
        //Si no lo encuentra en ninguno de los  ambitos (locales y global ) lanzamos error
        int line = ctx.getStart().getLine();
        error = true;
        error("Error, no sea ha declarado estructura "+structName +"   Linea: "+line);
        return ERROR;               
    }
    @Override public Tipo visitMethodType(@NotNull DECAFParser.MethodTypeContext ctx) { 
        String t = ctx.getText();
        if(t.equals("int")){
            Tipo t1 = new Int("int",0);
            return t1;
        }
        else if(t.equals("boolean")){
            Tipo t1 = new Bool("boolean",false);
            return t1;        
        }
        else if(t.equals("char")){
            Tipo t1 = new Char("char",' ');
            return t1;        
        }
        else if(t.equals("void")){
            Tipo t1 = new VoidType("void",null);
            return t1;        
        }
        else{
            int line = ctx.getStart().getLine();
            error = true;
            error("Tipo de metodo invalido"+t +"   Linea: "+line);
            return ERROR;             
        }
    }
    @Override public Tipo visitVoidType(@NotNull DECAFParser.VoidTypeContext ctx) { 
        String tipo = ctx.getText();
        Tipo t = new VoidType("void",null);
        
        return t; 
    
    
    }
    
    @Override public Tipo visitCharType(@NotNull DECAFParser.CharTypeContext ctx) { 
        String tipo = ctx.getText();
        Tipo t = new Char(tipo,' ');
        
        return t; 
    } 
    
    @Override public Tipo visitIntType(@NotNull DECAFParser.IntTypeContext ctx) { 
        String tipo = ctx.getText();
        Tipo t = new Int(tipo,0);
        
        return t; 
    
    }    
    
    @Override public Tipo visitBooleanType(@NotNull DECAFParser.BooleanTypeContext ctx) {
        String tipo = ctx.getText();
        Tipo t = new Bool(tipo,false);
        
        return t; 
    
    }
    
    public void agregarVariablesInternas(Variable v, StructType t1){
        Estructura estructura = this.buscarEstructura(t1.tipo, null); 
        int rOffset =0;
        for(Variable vi: estructura.variables){
            Variable newVar = new Variable(vi.name,vi.tipo);
            if(ambitoActual.name.equals("global")){
                newVar.esGlobal=true;
            }
            if(!this.insideStruct){
                newVar.offset = offset;
                newVar.relativeOffset = rOffset;
                if(newVar.tipo instanceof StructType){
                    agregarVariablesInternas(newVar,(StructType)newVar.tipo);
                }
                else{
                    if(newVar.tipo instanceof ArrayType){
                        ArrayType tr = (ArrayType) newVar.tipo;
                        if(tr.elementType instanceof StructType){
                            StructType tx = (StructType) tr.elementType;
                            agregarVariablesInternas(newVar,tx);                
                        }                             
                    
                    }
                    else{
                        rOffset += vi.tipo.getWidth();
                        offset+= newVar.tipo.getWidth();
                    }
                }
          
            }            



            v.variables.add(newVar);

        }    
    }
  /*  
    public ArrayList<String> generarData(Variable v){
        ArrayList<String> result = new ArrayList<String>();
        if(v.tipo instanceof ArrayType){
            ArrayType t = (ArrayType) v.tipo;
            String s1 = v.name+":";
            if(t.elementType instanceof StructType){
                result.add(s1);
                for()
            
            }
            else{
                String s2= ".word ";
                for(int i =0; i< t.arraySize;i++){
                    if(i==t.arraySize-1){
                        s2+=0;
                    
                    }
                    else{
                        s2+= 0+", ";
                    }
                
                }
                result.add(s1);
                result.add(s2);
            
            }
        
        
        
        }
        
        else if( v.tipo instanceof StructType){
        
        
        }
        
        
        else{
        
        
        }
        return result;
    }
    
    */
    //VARDECLARATION
    @Override public Tipo visitSimpleType(@NotNull DECAFParser.SimpleTypeContext ctx) { 
        Tipo t = visit(ctx.varType());
        Variable v = new Variable(ctx.ID().getText(),t);
       // Si la variable es de tipo estructura, agregamos todas las variables internas de la estructura
        if(!this.insideStruct){v.offset=offset;}
        
        if(t instanceof StructType){
            StructType t1 = (StructType) t;
            agregarVariablesInternas(v,t1);
            
        }
        
        else{
            if(t instanceof ArrayType){
                ArrayType tr = (ArrayType) t;
                if(tr.elementType instanceof StructType){
                    StructType t1 = (StructType) tr.elementType;
                    agregarVariablesInternas(v,t1);                
                }               
            }
            else if(!this.insideStruct){
                offset+= v.tipo.getWidth();           
            }             
        }
        
        //String data =
        boolean encontrada = buscarVariableAmbitoActual(ctx.ID().getText());
        
        if(encontrada==true){
            int line = ctx.getStart().getLine();
            error = true;
            error("Error, Ya sea ha declarado la variable : '"+ctx.ID().getText() +"'   Linea: "+line);
            return ERROR;                

        }
        else{
            if(ambitoActual.name.equals("global")){
                v.esGlobal = true;
                this.globalSize  += v.tipo.getWidth();
            }            
        //Agregamos al ambito actual
            if(ambitoActual!=null){
                 ambitoActual.variables.add(v);
            }
            else{
                ambitoGlobal.variables.add(v);
            }

        }
        return t; 
    
    }
    
    //VARDECLARATION
    @Override public Tipo visitArrayType(@NotNull DECAFParser.ArrayTypeContext ctx) { 
        Tipo t = visit(ctx.varType());
        int size = Integer.parseInt(ctx.NUM().getText());
        if(size==0){
            int line = ctx.getStart().getLine();
            error = true;
            error("Error, No se pueden declarar Arreglos tamanio 0 '"+ctx.ID().getText() +"'   Linea: "+line);
            return ERROR;          
        
        }
        
        
        ArrayType t1 = new ArrayType("array",size,t);
        Variable v = new Variable(ctx.ID().getText(),t1); 
        if(!this.insideStruct){
                v.offset=offset;
                t1.base=offset;
        }
        if(t instanceof StructType){
            StructType t2 = (StructType) t;
            agregarVariablesInternas(v,t2);
            
        }        
        else{
            if(!this.insideStruct){
                this.offset+=t1.getWidth();              
            }
    
        }

        boolean encontrada = buscarVariableAmbitoActual(ctx.ID().getText());
        if(encontrada==true){
            int line = ctx.getStart().getLine();
            error = true;
            error("Error, Ya sea ha declarado la variable : '"+ctx.ID().getText() +"'   Linea: "+line);
            return ERROR;                

        }
        
        
        else{    //Agregamos al ambito actual
            if(ambitoActual.name.equals("global")){
                v.esGlobal = true;
                this.globalSize  += v.tipo.getWidth();
            }     
            if(ambitoActual!=null){
                 ambitoActual.variables.add(v);
            }
            else{
                ambitoGlobal.variables.add(v);
            }
        }
        return t1; 
    
    }
    
    @Override public Tipo visitBlock(@NotNull DECAFParser.BlockContext ctx) {
        Tipo t = new VoidType("void",null);
        int i =0;
        boolean hasReturn=false;
        //Si el padre del bloque es un metodo, no agregamos un nuevo ambito bloque ya que el ambito del metodo almacenara todo
        if(! (ctx.getParent() instanceof DECAFParser.MethodDeclarationContext)){
            Bloque b = new Bloque("Block",this.bnum);
            this.bnum++;
            ambitos.push(b);
            ambitoActual.ambitosInternos.add(b);
            ambitoActual = ambitos.peek();
            t = new VoidType("void",null);
            hasReturn=false;
            i =0;            
            for(ParseTree n : ctx.children){
                
                if(n instanceof DECAFParser.StatementContext){
                    DECAFParser.StatementContext n1 = (DECAFParser.StatementContext) n;
                    if(containsReturn(n1)){
                        if(i==ctx.children.size()-2){//Restamos dos por la llave '}' que siempre es un hijo del block
                            hasReturn=true;
                        }
                        else{
                            int line = n1.getStop().getLine();
                            error = true;
                            error("Error, Unreachable Statementss.   Linea: "+line);
                            return ERROR;                          
                        }
                                            
                    }                
                }
                t = visit(n);
                if(t instanceof VoidType){
                    VoidType t1 = (VoidType)t;
                    if(t1.isReturn){
                        hasReturn=true;
                    }
                }
               
                if(t!= null &&t.equals(ERROR)){
                    error = true;
                    return ERROR;
                }
                else if(t==null){
                  t = new VoidType("void",null);
                }
                i++;

            }
            if(hasReturn){
                t= new VoidType("void",null,true);
            }
            ambitos.pop();
            ambitoActual = ambitos.peek();  
            return t;
        }
        else{
            t = new VoidType("void",null);
            hasReturn=false;
            i =0;
            for(ParseTree n : ctx.children){
                if(n instanceof DECAFParser.StatementContext){
                    DECAFParser.StatementContext n1 = (DECAFParser.StatementContext) n;
                    if(containsReturn(n1)){
                        if(i==ctx.children.size()-2){//Restamos dos por la llave '}' que siempre es un hijo del block
                            hasReturn=true;
                        }
                        else{
                            int line = n1.getStop().getLine();
                            error = true;
                            error("Error, Unreachable Statementss.   Linea: "+line);
                            return ERROR;                          
                        }
                                            
                    }
                }
                t = visit(n);
                if(t instanceof VoidType){
                    VoidType t1 = (VoidType)t;
                    if(t1.isReturn){
                        hasReturn=true;
                    }
                }                
                if(t!= null &&t.equals(ERROR)){
                    
                    error = true;
                    return ERROR;
                }
                else if(t==null){
                  t = new VoidType("void",null);
                }
                i++;

            }
            if(hasReturn){
                t= new VoidType("void",null,true);
            }
            return t;
        }
    }
    
    @Override public Tipo visitVariableStruct(@NotNull DECAFParser.VariableStructContext ctx){ 
        Tipo t = visit(ctx.simpleVariable());
        String structVar = ctx.simpleVariable().ID().getText();
        String structName =t.tipo;
        //Verificamos que la variable sea una estructura
        if(t instanceof StructType){
            StructType t1 = (StructType) t;
            Estructura s;
            if(this.structStack.isEmpty()){
               s = buscarEstructura(t1.tipo,null);
            }
            else{
               s = buscarEstructura(t1.tipo,this.structStack.peek());
            }
            
            this.structStack.push(s);
            if(s!=null){
                //Si encontramos la estructura y variable, buscamos la variable despues del .
                Tipo ts = visit(ctx.location());
                for(Variable v1: s.variables){
                    if(structVars.peek().equals(v1.name)){
                        this.structStack.pop();
                        this.structVars.pop();
                        if(structVars.size()==1){
                            structVars.pop();
                        }
                        return ts;

                    }

                }
                 int line = ctx.getStart().getLine();
                 error = true;
                 error("Error, No se encuentra variable "+structVars.peek()+" en estructura "+s.name+"  Linea: "+line);
                 return ERROR;                    
            }
            else{
                int line = ctx.getStart().getLine();
                error = true;
                error("Error, No se encuentra estructura "+t1.tipo+"   Linea: "+line);
                return ERROR;             
                        
            }
            
        }
        else{
            int line = ctx.getStart().getLine();
            error = true;
            error("Error, La variable :  '"+ctx.simpleVariable().ID().getText()+"' no es de tipo StructType  Linea: "+line);
            return ERROR;             
            
        }
    
    } 
    
    @Override public Tipo visitStructArray(@NotNull DECAFParser.StructArrayContext ctx) { 
        Tipo t = visit(ctx.arrayVariable());
        String structVar = ctx.arrayVariable().ID().getText();
        String structName =t.tipo;
        if(t instanceof StructType){
            StructType t1 = (StructType) t;
            Estructura s;
            if(this.structStack.isEmpty()){
               s = buscarEstructura(t1.tipo,null);
            }
            else{
               s = buscarEstructura(t1.tipo,this.structStack.peek());
            }
            
            this.structStack.push(s);
            if(s!=null){
                //Si encontramos la estructura y variable, buscamos la variable despues del .
                Tipo ts = visit(ctx.location());
                for(Variable v1: s.variables){
                    if(structVars.peek().equals(v1.name)){
                        this.structStack.pop();
                        this.structVars.pop();
                        if(structVars.size()==1){
                            structVars.pop();
                        }
                        return ts;

                    }

                }
                 int line = ctx.getStart().getLine();
                 error = true;
                 error("Error, No se encuentra variable "+structVars.peek()+" en estructura "+s.name+"  Linea: "+line);
                 return ERROR;                    
            }
            else{
                int line = ctx.getStart().getLine();
                error = true;
                error("Error, No se encuentra estructura "+t1.tipo+"   Linea: "+line);
                return ERROR;             
                        
            }
            
        }
        else{
            int line = ctx.getStart().getLine();
            error = true;
            error("Error, La variable :  '"+ctx.arrayVariable().ID().getText()+"' no es de tipo StructType  Linea: "+line);
            return ERROR;             
            
        }
    }
    
    
    @Override public Tipo visitSimpleVariable(@NotNull DECAFParser.SimpleVariableContext ctx) { 
        String varName = ctx.ID().getText();
        this.structVars.push(varName);
        Variable var;
        if(this.structStack.isEmpty()){
           var = buscarVariable(varName);
        }
        else{
           var = buscarVariableStruct(varName,this.structStack.peek());
        }
        
        if(var==null){
            int line = ctx.getStart().getLine();
            error = true;
            error("Error, No se encuentra declaracion de variable:  '"+varName+"'  Linea: "+line);
            return ERROR;             
        
        }
        else{
            return var.tipo;
        
        }
    
    }    
    
    @Override public Tipo visitArrayVariable(@NotNull DECAFParser.ArrayVariableContext ctx) { 
        String varName = ctx.ID().getText();
        this.structVars.push(varName);
        Tipo t = visit(ctx.expression());
        //Verificando que la expresion entre corchetes sea un entero
        if(!(t instanceof Int)){
            int line = ctx.getStart().getLine();
            error = true;
            error("Error, Tipo invalido en expression de array [:  '"+t.tipo+"]  se espera int.   Linea: "+line);
            return ERROR;             
        
        }
        int index = (int) t.valor;
        Variable var;
        if(this.structStack.isEmpty()){
           var = buscarVariable(varName);
        }
        else{
           var = buscarVariableStruct(varName,this.structStack.peek());
        }
        //Verificando que exista la variable
        if(var==null){
            int line = ctx.getStart().getLine();
            error = true;
            error("Error, No se encuentra declaracion de variable:  '"+varName+"'  Linea: "+line);
            return ERROR;             
        
        }
        //Verificando que sea un arreglo
        if(var.tipo instanceof ArrayType){
            ArrayType t1 = (ArrayType) var.tipo;
            if((t1.arraySize-1)<index){
                int line = ctx.getStart().getLine();
                error = true;
                error("Error, Array index out of bounds. Size  '"+t1.arraySize+" Found index: "+index+"'  Line: "+line);
                return ERROR;                  
            }
        }
        else{
            int line = ctx.getStart().getLine();
            error = true;
            error("Error, No existe un arreglo llamado:  '"+varName+"'  Linea: "+line);
            return ERROR;             
                
        }
        ArrayType t1 = (ArrayType) var.tipo;
        Tipo[] tipos= (Tipo[]) t1.valor;
        return tipos[index];
        
       
   
    
    }   
    @Override public Tipo visitLocation(@NotNull DECAFParser.LocationContext ctx) { 
        Tipo t = visitChildren(ctx); 
        return t;
    
    }
    @Override public Tipo visitAssignStatement(@NotNull DECAFParser.AssignStatementContext ctx) { 
        Tipo t1 = visit(ctx.location());
        this.structStack.clear();
        this.structVars.clear();
        Tipo t2 = visit(ctx.expression());
        if(t1 instanceof ArrayType && t2 instanceof ArrayType){
            ArrayType t1a = (ArrayType) t1;
            ArrayType t2a = (ArrayType) t2;
            int line = ctx.getStart().getLine();
                        error = true;
            error("Error, No es posible asignar arreglos.  '"+t1a.elementType.tipo +"[] ' = '"+t2a.elementType.tipo+"[] '  Linea: "+line);
            return ERROR;                  

            
        
        }
        if(!t1.getClass().equals(t2.getClass())){
            int line = ctx.getStart().getLine();
                        error = true;
            error("Error, Tipo invalido en asignacion se encontro:  '"+t1.tipo +"' = '"+t2.tipo+"'  Linea: "+line);
            return ERROR;               
        }
        t1.valor=t2.valor;
        return t1;
    
    }
    
        
        
    @Override public Tipo visitSimpleIf(@NotNull DECAFParser.SimpleIfContext ctx) {
        Tipo t = visit(ctx.expression());
        if(t.equals(ERROR)){
            
            error = true;
            return ERROR;
        }        
        String expr = ctx.expression().getText();
        if(!(t instanceof Bool)){
            error = true;
            int line = ctx.getStart().getLine();
            error("Error, Tipo invalido en expresion if:  "+expr +" Se espera tipo boolean, Se encontro "+t.tipo+"  Linea: "+line);
            return ERROR;         
        }

        else{
            String testFalse = ctx.expression().getText();
            if(testFalse.equals("false")){
                int line = ctx.getStart().getLine();
                error = true;
                error("Error, Unreachable code. Line:  "+line);
                return ERROR;              
            }
            t = visit(ctx.block());
            if(t.equals(ERROR)){
                error = true;
                return ERROR;
            }
            else{
                return new VoidType("void",null);
            
            }        
        }
    
    }
    @Override public Tipo visitIfElse(@NotNull DECAFParser.IfElseContext ctx) { 
        Tipo t = visit(ctx.expression());
        if(t.equals(ERROR)){
            error = true;
            return ERROR;
        }
        String expr = ctx.expression().getText();
        if(!(t instanceof Bool)){
            error = true;
            int line = ctx.getStart().getLine();
            error("Error, Tipo invalido en expresion if:  "+expr +" Se espera tipo boolean, Se encontro "+t.tipo+"  Linea: "+line);
            return ERROR;         
        }
        else{
            String testFalse = ctx.expression().getText();            
            if(testFalse.equals("false")){
                error = true;
                int line = ctx.getStart().getLine();
                error("Error, Unreachable code. Line:  "+line);
                return ERROR;              
            }            
            Tipo t1 = visit(ctx.block(0));
            if(t1.equals(ERROR)){
                error = true;
                return ERROR;
            }
            Tipo t2 = visit(ctx.block(1));
            if(t2.equals(ERROR)){
                error = true;
                return ERROR;
            }            
            
            if(t1 instanceof VoidType && t2 instanceof VoidType){
                VoidType t1v =(VoidType) t1;
                VoidType t2v =(VoidType) t2;
                if(t1v.isReturn&&t2v.isReturn){
                    t = new VoidType("void",null,true);
                
                }
            }
        }
        return t;
    }
    
   @Override public Tipo visitWhileStatement(@NotNull DECAFParser.WhileStatementContext ctx) { 
        Tipo t = visit(ctx.expression());
        if(t.equals(ERROR)){
            error = true;
            return ERROR;
        }        
        String expr = ctx.expression().getText();
        if(!(t instanceof Bool)){
            int line = ctx.getStart().getLine();
            error = true;
            error("Error, Tipo invalido en expresion while:  "+expr +" Se espera tipo boolean, Se encontro "+t.tipo+"  Linea: "+line);
            return ERROR;         
        }
        else{
            String testFalse = ctx.expression().getText();            
            if(testFalse.equals("false")){
                error = true;
                int line = ctx.getStart().getLine();
                error("Error, Unreachable code. Line:  "+line);
                return ERROR;              
            }           
            t = visit(ctx.block());
            if(t.equals(ERROR)){
                error = true;
                return ERROR;
            }
            else{
                return new VoidType("void",null);
            
            }        
        }
   }
   
   @Override public Tipo visitReturnStatement(@NotNull DECAFParser.ReturnStatementContext ctx) { 
       if(ctx.expressionA().children==null){
           if(this.metodoActual.tipoResultado instanceof VoidType){
               return new VoidType("void",null,true);
           }
           else{
                    error = true;
                    int line = ctx.getStart().getLine();
                    error("Error, Tipo invalido de retorno Se espera : "+this.metodoActual.tipoResultado.tipo+".  Linea: "+line);
                    return ERROR;            
           }
           
       
       }
       else{
           Tipo t = visit(ctx.expressionA());
           if(t.equals(ERROR)){
               error = true;
               return ERROR;
           }
           else{
               if(t.tipo.equals(this.metodoActual.tipoResultado.tipo)){
                   return t;
                   
               
               }
               else{
                    error = true;
                    int line = ctx.getStart().getLine();
                    error("Error, Tipo invalido de retorno  "+t.tipo +". Se espera : "+this.metodoActual.tipoResultado.tipo+"  Linea: "+line);
                    return ERROR;                
               
               }
           }
       
       }
   
   }
   
   @Override public Tipo visitMethodCall(@NotNull DECAFParser.MethodCallContext ctx) { 
       String methodName = ctx.ID().getText();
       String tipos = "";
       DECAFParser.Arg2Context argTest = ctx.arg2();
       Metodo m= new Metodo(null,null,null,null);
       ArrayList<Tipo> paramTypes = new ArrayList<Tipo>();
       if(argTest!=null){
          List<ArgContext> args = argTest.arg();
          for(ArgContext g: args){
              Tipo t = visit(g);
              tipos+=" "+t.tipo+", ";
              paramTypes.add(t);
          
          }
       
       }
       boolean encontrado = false;
       for(Ambito a: this.ambitoGlobal.ambitosInternos){
           if( a instanceof Metodo && a.name.equals(methodName)){
               Metodo a1 = (Metodo)a;

               if (a1.parametros.size() == paramTypes.size()) {
                   encontrado = true;
                   for (int i =0; i<a1.parametros.size();i++ ) {
                       if(a1.parametros.get(i).tipo.getClass().equals(paramTypes.get(i).getClass())){
                           a1.parametros.get(i).tipo.valor=paramTypes.get(i).valor;
                           continue;
                       
                       }
                       else{
                           encontrado = false;
                           /* //Obteniendo strings para error del metodo declarado
                            String tDeclarados="";
                            for(Variable v :a1.parametros){
                                tDeclarados +=" "+v.tipo.tipo+", ";
                            }                           
                            int line = ctx.getStart().getLine();
                            error = true;
                            error("Error, Tipos no coinciden en el metodo  "+methodName +" Se encontro: "+tipos+"Se requiere: "+ tDeclarados+"   Linea: "+line);
                            return ERROR;      */                        
                               
                       }

                   }
                   // Si no existio error, el metodo esta correcto
                   m = a1;
                   if(encontrado==true){
                       break;
                   }
                   
               }

           }
       
       }
       if(encontrado){
           return m.tipoResultado;
       
       }
       else{
            int line = ctx.getStart().getLine();
            error = true;            
            error("Error, No se ha declarado el metodo metodo  "+methodName +"   Linea: "+line);
            return ERROR;                                                           
       }
   
   }   
   
   @Override public Tipo visitParameterType(@NotNull DECAFParser.ParameterTypeContext ctx) { 
        String name = ctx.getText();
        if(name.equals("boolean")){
            Tipo t = new Bool("boolean",false);
            return t;
        
        }
        else if(name.equals("char")){
            Tipo t = new Char("char",' ');
            return t;
        }
        else if(name.equals("int")){
            Tipo t = new Int("int",0);
            return t;
        }
        
        else if (name.equals("void")){
            Tipo t = new VoidType("void",null);
            return t;            
        }
        else{
            int line =ctx.getStart().getLine();
            error = true;
            error("Error, Tipo de parametro invalido: "+name+" Solo se permite int,char,boolean    Linea:"+line);
            return ERROR;                 
        }

    
    }
    
    @Override public Tipo visitParameter(@NotNull DECAFParser.ParameterContext ctx) {
        if(ctx.children.size()>2){
            int line =ctx.getStart().getLine();
            error = true;
            error("Error, no es posible declarar arreglos como parametros  Linea:"+line);
            return ERROR;            
        
        }
        else{
            Tipo t = visit(ctx.parameterType());
            return t;
            
        }        
    }
    
    
   @Override public Tipo visitExpression(@NotNull DECAFParser.ExpressionContext ctx) { 
        if(ctx.children.size()>1){
           Tipo op1 = visit(ctx.children.get(0));
           String op = ctx.children.get(1).getText();
           Tipo op2 = visit(ctx.children.get(2));
           if((op1 instanceof Bool && op2 instanceof Bool)&& op.equals("||")){
               boolean val =(boolean) op1.valor||(boolean)op2.valor;
               Bool returnTipo = new Bool("boolean",val);
               return returnTipo;

           }
           else{
                int line = ctx.getStart().getLine();
                error = true;
                error("Se esperan tipo boolean  para operador '||'  Se encontro: " + op1.tipo +", "+op2.tipo +"Linea: " + line);
                return ERROR;       

           }

        } 
        else{
            return visit(ctx.children.get(0));
        
        }         
     
   
          
   }    
   
   @Override public Tipo visitAndExpr(@NotNull DECAFParser.AndExprContext ctx) { 
        if(ctx.children.size()>1){
           Tipo op1 = visit(ctx.children.get(0));
           String op = ctx.children.get(1).getText();
           Tipo op2 = visit(ctx.children.get(2));
           if((op1 instanceof Bool && op2 instanceof Bool)&& op.equals("&&")){
               boolean val =(boolean) op1.valor&&(boolean)op2.valor;
               Bool returnTipo = new Bool("boolean",val);
               return returnTipo;

           }
           else{
                int line = ctx.getStart().getLine();
                error = true;
                error("Se esperan tipo boolean  para operador '&&'  Se encontro: " + op1.tipo +", "+op2.tipo +"Linea: " + line);
                return ERROR;       

           }

        } 
        else{
            return visit(ctx.children.get(0));
        
        }         
     
   
   
   }

   @Override public Tipo visitEqExpr(@NotNull DECAFParser.EqExprContext ctx) {
        if(ctx.children.size()>1){
           Tipo op1 = visit(ctx.children.get(0));
           String op = ctx.children.get(1).getText();
           Tipo op2 = visit(ctx.children.get(2));
           if((op1 instanceof Int && op2 instanceof Int)||(op1 instanceof Char && op2 instanceof Char)||(op1 instanceof Bool && op2 instanceof Bool)&& op.equals("==")){
               boolean val = op1.valor==op2.valor;
               Bool returnTipo = new Bool("boolean",val);
               return returnTipo;

           }
           if((op1 instanceof Int && op2 instanceof Int)||(op1 instanceof Char && op2 instanceof Char)||(op1 instanceof Bool && op2 instanceof Bool)&& op.equals("!=")){
               boolean val = op1.valor!=op2.valor;
               Bool returnTipo = new Bool("boolean",val);
               return returnTipo;

           }     
           
           else{
                int line = ctx.getStart().getLine();
                error = true;
                error("Se esperan tipo int,int  char,char  boolean,boolean  para operadores '==' ,'!=',  Se encontro: " + op1.tipo +", "+op2.tipo +" .Linea: " + line);
                return ERROR;       

           }

        } 
        else{
            return visit(ctx.children.get(0));
        
        }          
   }   
   
    @Override
    public Tipo visitRelationExpr(@NotNull DECAFParser.RelationExprContext ctx) {
        if(ctx.children.size()>1){
           Tipo op1 = visit(ctx.children.get(0));
           String op = ctx.children.get(1).getText();
           Tipo op2 = visit(ctx.children.get(2));
           if(op1 instanceof Int && op2 instanceof Int && op.equals(">")){
               boolean val = (int)op1.valor>(int)op2.valor;
               Bool returnTipo = new Bool("boolean",val);
               return returnTipo;

           }
           else if(op1 instanceof Int && op2 instanceof Int && op.equals(">=")){
               boolean val = (int)op1.valor>=(int)op2.valor;
               Bool returnTipo = new Bool("boolean",val);
               return returnTipo;

           }
           else if(op1 instanceof Int && op2 instanceof Int && op.equals("<")){
               boolean val = (int)op1.valor<(int)op2.valor;
               Bool returnTipo = new Bool("boolean",val);
               return returnTipo;

           }
           else if(op1 instanceof Int && op2 instanceof Int && op.equals("<=")){
               boolean val = (int)op1.valor<=(int)op2.valor;
               Bool returnTipo = new Bool("boolean",val);
               return returnTipo;
           }           
           
           else{
                int line = ctx.getStart().getLine();
                error = true;
                error("Se esperan tipo int para operadores '<' ,'<=','>' , '>=' Se encontro: " + op1.tipo +", "+op2.tipo +"Linea: " + line);
                return ERROR;       

           }

        } 
        else{
            return visit(ctx.children.get(0));
        
        }       

    }
   
    //Pendiendte  suma/resta char-int
   @Override public Tipo visitAddExpr(@NotNull DECAFParser.AddExprContext ctx) {
        if(ctx.children.size()>1){
           Tipo op1 = visit(ctx.children.get(0));
           String op = ctx.children.get(1).getText();
           Tipo op2 = visit(ctx.children.get(2));
           if(op1 instanceof Int && op2 instanceof Int && op.equals("+")){
               int val = (int)op1.valor+(int)op2.valor;
               Int returnTipo = new Int("int",val);
               return returnTipo;

           }
           else if(op1 instanceof Int && op2 instanceof Int && op.equals("-")){
               int val = (int)op1.valor-(int)op2.valor;
               Int returnTipo = new Int("int",val);
               return returnTipo;

           }
           else{
                int line = ctx.getStart().getLine();
                error = true;
                error("Se esperan tipo int para operadores '+' , '-', Se encontro: " + op1.tipo +", "+op2.tipo +"  Linea: " + line);
                return ERROR;       

           }

        } 
        else{
            return visit(ctx.children.get(0));
        
        }
    
   }
   
   @Override public Tipo visitMultExpr(@NotNull DECAFParser.MultExprContext ctx){
        if(ctx.children.size()>1){
           Tipo op1 = visit(ctx.children.get(0));
           String op = ctx.children.get(1).getText();
           Tipo op2 = visit(ctx.children.get(2));
           if(op1 instanceof Int && op2 instanceof Int && op.equals("*")){
               int val = (int)op1.valor*(int)op2.valor;
               Int returnTipo = new Int("int",val);
               return returnTipo;

           }
           else if(op1 instanceof Int && op2 instanceof Int && op.equals("/")){
               try {
                   int val = (int)op1.valor/(int)op2.valor;
                   Int returnTipo = new Int("int",val);
                   return returnTipo;
               }
               catch(Exception e){
                    int line = ctx.getStart().getLine();
                    error = true;
                    error("Error: Division con 0. Linea: " + line);
                    return ERROR;                     
               }
           }
           else if(op1 instanceof Int && op2 instanceof Int && op.equals("%")){
               int val = (int)op1.valor%(int)op2.valor;
               Int returnTipo = new Int("int",val);
               return returnTipo;

           }
           else{
                int line = ctx.getStart().getLine();
                error = true;
                error("Se esperan tipo int para operadores '*,/,%', Se encontro: " + op1.tipo +", "+op2.tipo +" Linea: " + line);
                return ERROR;       

           }

        } 
        else{
            return visit(ctx.children.get(0));
        
        }
    }
   
    //CASTING PENDIENTE
   @Override public Tipo visitUnaryExpr(@NotNull DECAFParser.UnaryExprContext ctx) { 
        // Si hay mas de un hijo, existe un operador
        if(ctx.children.size()>1){
            String operador = ctx.children.get(0).getText();
            if(operador.equals("!")){
                //Si es ! el valor debe ser booleano
                Tipo  t= visit(ctx.value());
                if(!t.tipo.equals("boolean")){
                   int line=ctx.getStart().getLine();
                   error = true;
                   error("Se espera tipo booleano despues de '!', linea"+line);
                   return ERROR; //creando tipo ERROR
                    
                }
                else{
                    boolean newVal = ! (Boolean)t.valor;
                    t.valor = newVal;
                    return t;
                }
                  
                
            }
            //Si es - debe ser entero
            else if(operador.equals("-")){
                //Si es - el valor debe ser int
                Tipo  t= visit(ctx.value());
                if(!t.tipo.equals("int")){
                   int line=ctx.getStart().getLine();
                   error = true;
                   error("Se espera tipo enter despues de '-', linea"+line);
                   return ERROR; //creando tipo ERROR 
                }
                else{
                    int newVal = - (Integer)t.valor;
                    t.valor = newVal;
                    return t;
                }                
            
            }
            //Si es - debe ser entero
            else if(operador.equals("(")){
                TerminalNode test = ctx.CHARS();
                //Si CHARS es null el casteo es a int
                if(test==null){
                    Tipo t = visit(ctx.value());
                    if((t instanceof Char)){
                        int val = Character.getNumericValue((char)t.valor);
                        return new Int("int",val);
                    
                    }
                    else{
                        int line=ctx.getStart().getLine();
                        error = true;
                        error("ERROR: Tipo incorrecto de casting (int), Se esperan tipos int char"+"Se encontro : "+t.tipo+"  Linea: "+line);
                        return ERROR;             
                    }
                
                }
                else{
                    Tipo t = visit(ctx.value());
                    if((t instanceof Int)){
                        int a = (int)t.valor;
                        char val = (char) a;
                        return new Char("char",val);
                    
                    }                
                    else{
                        int line=ctx.getStart().getLine();
                        error = true;
                        error("ERROR: Tipo incorrecto de casting (char), Se esperan tipos int, char"+"Se encontro:  "+t.tipo+"  Linea: "+line);
                        return ERROR;                     
                    }                
                }
            }  
            error = true;
            return ERROR;
            
        }
        else{
            return visit(ctx.value());
        }
    }
    
   @Override public Tipo visitValue(@NotNull DECAFParser.ValueContext ctx) { 
       if(ctx.children.size()>1){
           return visit(ctx.expression());
       
       }
       else{
           return visit(ctx.children.get(0));
        }
   
   }
    
   @Override public Tipo visitInt_literal(@NotNull DECAFParser.Int_literalContext ctx) { 
        String t1 = ctx.getText();
        try{
            int v = Integer.parseInt(t1);

            Tipo t = new Int("int",v);
            return t;            
        }
        catch(Exception e){
            int line = ctx.getStart().getLine();
            error = true;
            error("Error los valores de int deben ser entre -2147483648 y 2147483647.");
            return ERROR;        
        
        }
       
    
    }
    
   @Override public Tipo visitChar_literal(@NotNull DECAFParser.Char_literalContext ctx) { 
        char v = Character.valueOf(ctx.getText().charAt(1));
        
        Tipo t = new Char("char",v);
        return t;
    
    }
    
   @Override public Tipo visitBool_literal(@NotNull DECAFParser.Bool_literalContext ctx) { 

        if(ctx.getText().equals("true")){
          boolean v = true ; 
          Tipo t = new Bool("boolean",v);  
          return t;
        }
        else{
          boolean v = false ; 
          Tipo t = new Bool("boolean",v);              
          return t; 
            
        }
    }    
    
}
