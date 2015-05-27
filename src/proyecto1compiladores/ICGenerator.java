/*
1. Agregar booleana esGlobal a variables,arrays,structs
2. agregar getActivationRecordSize() a metodo
3. Cambiar la forma como se muestran las variables. (Crear metodo getStringPointerFromVariable()
4. Probar location simple variable
5. Probar location array Variable
6. Probar loaction struct Variable
7. Probar Location array de struct
 */
package proyecto1compiladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import static proyecto1compiladores.TypeChecker.ERROR;

/**
 *
 * @author pablo
 */
public class ICGenerator extends DECAFBaseVisitor<IC>{
    boolean inAndOrExpr;
    Temp resultTemp;
    int relativeOffsetCounter;
    Stack<Variable> structStack;
    ArrayList<Quad> programCode;
    Ambito ambitoGlobal;
    Stack<Ambito> ambitos;
    Ambito ambitoActual;
    String booleanContext;
    Metodo metodoActual;
    Stack<Integer> blockNums;
    Stack<Integer> currentNumBlock;
    StructType s;
    int bnum;
    int numLabels;
    int next;
    boolean singleVarExp;
    Object arrayOutOfBoundsInstruction;
    int globalSize;
    String data;
    
    public ICGenerator(Ambito tabla,int S){
        this.globalSize = S;
        this.inAndOrExpr = false;
        this.relativeOffsetCounter =0;
        programCode= new ArrayList<Quad>();
        next=0;
        booleanContext="";
        currentNumBlock = new Stack<Integer>();;
        this.ambitoGlobal = tabla;
        this.ambitos = new Stack<Ambito>();
        this.blockNums = new Stack<Integer>();
        ambitos.push(ambitoGlobal);
        this.ambitoActual= ambitos.peek();
        this.singleVarExp = true;
        this.structStack = new Stack<Variable>();
    }
    

    public void gen(Quad q){
        programCode.add(q);
        next++;
    
    }
    
    public Ambito buscarBlock(Ambito m,int i){
        Ambito r=null;
        for(Ambito a: m.ambitosInternos){
            if(a instanceof Bloque){
                Bloque a1 = (Bloque)a;
                if(a1.numBlock==i){
                    r=a1;
                    return r;
                }
                else{
                    r=buscarBlock(a1,i);
                    if(r!=null){
                        return r;
                    }
                }
            }
        }
        return r;
    }
    public ArrayList<Integer> makeList(int i){
        ArrayList<Integer> r = new ArrayList<Integer>();
        r.add(i);
        return r;
    
    }
    
    public void backPatch(ArrayList<Integer> l,int n){
        //Encontrando la direccion del quad en el buffer de codigo
        int addr =0;
        boolean encontrado = false;
        for(Quad q: programCode){
            if(addr==n){
                encontrado = true;
                break;
                
            }
            addr++;
        }
        Quad r = programCode.get(addr);
        if(!encontrado){
            return;
        }
        else{
            for(int s:l){
                Quad q = programCode.get(s);
                q.target = n;
            
            }
        
        }
    }
    public int getAddrNumber(Quad q1){
        int i =0;
        for(Quad q: programCode){
            if(q1.equals(q)){
                return i;
            }
            i++;
        }
        return -1;
    
    }
    
    public Metodo buscarMetodo(Metodo m){
        
        String methodName = m.name;
        Metodo result = null;
        boolean found = false;
        for (Ambito a : ambitoGlobal.ambitosInternos) {
            if(found==true){break;}
            if (a instanceof Metodo) {
                if (a.name.equals(methodName)) {
                    found = true;                   
                    //si encontramos el metodo, verificamos los tipos de parametros
                    Metodo a1 = (Metodo) a;
                    result = a1;
                    if (a1.parametros.size() == m.parametros.size()) {
                        for (int i = 0; i < m.parametros.size(); i++) {
                            if (!(a1.parametros.get(i).tipo.getClass().equals(m.parametros.get(i).tipo.getClass()))) {
                                result = null;
                                found = false;
                                break;
                            }
                        }
                    }
                    else{found = false;}

                } else {
                    found = false;
                }
            } else {
                found = false;
            }
        }
    
        return result;
    
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
    public Variable buscarVariableStack(Variable v,String s){
        for(Variable vi : v.variables){
            if(vi.name.equals(s)){
                return vi;
            }
        }
        return null;
    
    }
    
    public ArrayList<Integer> merge(ArrayList<Integer>a,ArrayList<Integer>b){
        ArrayList<Integer> ret = new ArrayList<Integer>();
        ret.addAll(a);
        ret.addAll(b);
        return ret;
    }
    
    @Override public IC visitProgram(@NotNull DECAFParser.ProgramContext ctx) {
        //Generamos salto a main
        
       //Generamos subrutinas de excepcion
        
        gen(new Quad("GOTO",null,null,"LABELmain0"));
        this.arrayOutOfBoundsInstruction = this.next;
        gen(new Quad("LABEL"+"ArrayOutOfBounds",null,null,null,true));
        gen(new Quad("HALT",null,null,null)); 
        gen(new Quad("LABEL"+"print_int0",null,null,null,true));
        gen(new Quad("LABEL"+"print_char0",null,null,null,true));
        gen(new Quad("LABEL"+"print_boolean0",null,null,null,true));
        gen(new Quad("LABEL"+"input_int0",null,null,null,true));
        gen(new Quad("LABEL"+"input_char0",null,null,null,true));
        gen(new Quad("LABEL"+"input_boolean0",null,null,null,true));        
       IC r =  visitChildren(ctx); 
       
       this.data = ".data \n.space "+this.globalSize;
        for(Quad q:programCode){
            //Si encontramos un entero en target es un salto
            if(q.target instanceof Integer){
                q.target=programCode.get((Integer)q.target).op; //Cambiamos el entero a un label
            
            }
        
        }
       
        return r;
    
    }    
    public IC visitMethodDeclaration(@NotNull DECAFParser.MethodDeclarationContext ctx) {
        Tipo t = visit(ctx.methodType()).tipo;
        bnum=0;
        IC r = new IC();
        String methodName = ctx.ID().getText();
        DECAFParser.Parameter2Context paramsTest = ctx.parameter2();
        boolean isVoid=false;
        //Si no hay paramaetros
        Metodo m;
        if(paramsTest==null){
           m = new Metodo(methodName,new ArrayList<Variable>(),new ArrayList<Variable>(),t); 
           //Buscamos metodo en la ts
           Metodo m1 = buscarMetodo(m);
           if(m1.tipoResultado instanceof VoidType){
               isVoid = true;
           }
           ambitos.push(m1);
           ambitoActual = ambitos.peek();
           this.metodoActual=m1;
           this.metodoActual.finalSize = new Int(this.metodoActual.getActivationRecordSize());
           
           
        }
        //Si hay parametros
        else{
            ArrayList<DECAFParser.ParameterContext> parametros =  (ArrayList<DECAFParser.ParameterContext>) ctx.parameter2().parameter(); 
            ArrayList<Variable> savedParams = new ArrayList<Variable>();
           for(DECAFParser.ParameterContext p : parametros){
                String varName = p.ID().toString();
                Tipo varType = visit(p).tipo;
                Variable newVar = new Variable(varName,varType);
                savedParams.add(newVar);
                
            }
            //Buscando el metodo declarado en la tabla de simbolos        
            m = new Metodo(methodName,savedParams,new ArrayList<Variable>(),t); 

            Metodo result = buscarMetodo(m);
            m = result;
            this.metodoActual=result;
            this.metodoActual.finalSize = new Int(this.metodoActual.getActivationRecordSize());
            isVoid=false;
           if(result.tipoResultado instanceof VoidType){
               isVoid = true;
           }            
            if(result==null){
                return r;
            }
            else{
                ambitos.push(result);
                ambitoActual = ambitos.peek();   
            }
        }
        gen(new Quad("LABEL"+methodName+m.numMetodo,null,null,null,true));
        gen(new Quad("PUSH_AL",null,null,null));
        if(!isVoid){
              gen(new Quad("PUSH_RESULT",new Int(metodoActual.tipoResultado.getWidth()),null,null));
              
        }     
        gen(new Quad("PUSH_LOCALS",this.metodoActual.finalSize,null,null));
        //Visitamos el bloque para obtener generar codigo del metodo
        visit(ctx.block());
        int offset=0;
        offset = (int) this.metodoActual.finalSize.valor;
        //Verificamos si hay valor de retorno para agregar offset de este valor
        if(!(metodoActual.tipoResultado instanceof VoidType)){
            offset+= 4;
        }
        //Agregamos offset del espacio para AccesLink
            offset+= 4;
        
        //Agregamos offset de los parametros
        for(Variable newVar: this.metodoActual.parametros){
            //Agregando el offset
            newVar.offset=offset;
            offset+= 4; 
        }        
        //Si el metodo es void agregamos instruccion de return
        if(isVoid){
            if(!this.programCode.get(next-1).op.equals("RETURN")){
                gen(new Quad("POP_LOCALS",this.metodoActual.finalSize,null,null));
                gen(new Quad("POP_LINK",null,null,null));
                if(metodoActual.name.equals("main")&& metodoActual.parametros.isEmpty()){
                    gen(new Quad("HALT",null,null,null));
                } 
                else{
                    gen(new Quad("RETURN",null,null,null));
                }
            }
            
        }
        ambitos.pop();
        // Reseteamos temporales 
        Temp.tempNum = 0;
        Temp.disponibles.clear();
        ambitoActual = ambitos.peek();        
        return r; 
    }
    @Override public IC visitMethodType(@NotNull DECAFParser.MethodTypeContext ctx) { 
        String t = ctx.getText();
        IC r = new IC();
        if(t.equals("int")){
            r.tipo = new Int("int",0);
            return r;
        }
        else if(t.equals("boolean")){
            r.tipo = new Bool("boolean",false);
            return r;        
        }
        else if(t.equals("char")){
            r.tipo = new Char("char",' ');
            return r;        
        }
        else if(t.equals("void")){
            r.tipo = new VoidType("void",null);
            return r;        
        }
        return r;
    }   
    @Override public IC visitParameter(@NotNull DECAFParser.ParameterContext ctx) {
            IC r = visit(ctx.parameterType());
            return r;
            
               
    }    
   @Override public IC visitParameterType(@NotNull DECAFParser.ParameterTypeContext ctx) { 
        String name = ctx.getText();
        IC r = new IC();
        if(name.equals("boolean")){
            r.tipo = new Bool("boolean",false);
            return r;
        
        }
        else if(name.equals("char")){
            r.tipo = new Char("char",' ');
            return r;
        }
        else if(name.equals("int")){
            r.tipo = new Int("int",0);
            return r;
        }
        
        else if (name.equals("void")){
            r.tipo = new VoidType("void",null);
            return r;            
        }
        return r;

    
    }    
   @Override public IC visitSimpleType(@NotNull DECAFParser.SimpleTypeContext ctx) { 
       
       return new IC(); 
   
   }
    @Override public IC visitArrayType(@NotNull DECAFParser.ArrayTypeContext ctx) { 

        return  new IC();  

    }
    @Override public IC visitSimpleVariable(@NotNull DECAFParser.SimpleVariableContext ctx) { 
        IC r = new IC();
        String varName = ctx.ID().getText();
        if(this.structStack.isEmpty()){
            r.addr = buscarVariable(varName);
        }
        else{
            r.addr = buscarVariableStack(this.structStack.peek(),varName);
            Variable vx = (Variable)r.addr;
            this.relativeOffsetCounter+=vx.relativeOffset; 
        }
        
        return r;
    }  
    
    @Override public IC visitArrayVariable(@NotNull DECAFParser.ArrayVariableContext ctx) { 
        
        IC r = new IC();
        if(!this.structStack.isEmpty()){
               r.array = buscarVariableStack(structStack.peek(),ctx.ID().getText());
            ArrayType t = (ArrayType) r.array.tipo;
            r.tipo = t.elementType;  //Tipo interno del arreglo
            IC exp = visit(ctx.expression()); //obtenemos el resultado y generamos el codigo de la expresion entre corchetes
            //Generamos instruccion para arrayOutOfBounds
            gen(new Quad("GE",exp.addr,new Int(t.arraySize),this.arrayOutOfBoundsInstruction));        
             if(exp.addr instanceof Temp){
                Temp.liberarTemp((Temp)exp.addr);
            }     
            Temp t3;
            Temp test = Temp.checkTemp();
            //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
            if(test==null){
                t3 = new Temp(new Int());
                this.ambitoActual.variables.add(t3);
                t3.offset = (int) this.metodoActual.finalSize.valor;
                this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;
                
            }
            //Sino utilizamos el temporal disponible
            else{
                test.tipo = new Int();
                t3=test;

            }          
            gen(new Quad("MULT",exp.addr,new Int(t.elementType.getWidth()),t3));   //generamos instruccion indice = expr * widthTipoArreglo
             if(exp.addr instanceof Temp){
                Temp.liberarTemp((Temp)exp.addr);
            }

            r.addr = t3;
            if(resultTemp!=null){
                Temp newTemp;
                test = Temp.checkTemp();
                //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                if(test==null){
                    newTemp = new Temp(new Int());
                    this.ambitoActual.variables.add(newTemp);
                    newTemp.offset = (int) this.metodoActual.finalSize.valor;
                    this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;              
                }
                //Sino utilizamos el temporal disponible
                else{
                    test.tipo = new Int();
                    newTemp=test;

                }                   
                gen(new Quad("ADD",t3,this.resultTemp,newTemp));
                if(t3 instanceof Temp){
                    Temp.liberarTemp((Temp)t3);
                }
                if(this.resultTemp instanceof Temp){
                    Temp.liberarTemp((Temp)this.resultTemp);
                }
                this.resultTemp = newTemp;
                
                /** Cambio realizado**/
                //Si el offset relativo es mayor a 0 lo agregamos a la temporal que acumula el resultado
                if(r.array.relativeOffset>0){
                    Temp newTemp2;
                    test = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test==null){
                        newTemp2 = new Temp(new Int());
                        this.ambitoActual.variables.add(newTemp2);
                        newTemp2.offset = (int) this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;   
                        
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test.tipo = new Int();
                        newTemp2=test;
                    }                             
                    gen(new Quad("ADD",new Int(r.array.relativeOffset),this.resultTemp,newTemp2));
                    
                    if(this.resultTemp instanceof Temp){
                        Temp.liberarTemp((Temp)this.resultTemp);
                    }
                    this.resultTemp = newTemp2;                
                }
                /** FIN Cambio realizado**/
            }
            else{
                resultTemp = t3;
            }
             
        }
        else{
             r.array = buscarVariable(ctx.ID().getText()); // Buscando variable en tabla de simbolos 
        
        
            ArrayType t = (ArrayType) r.array.tipo;
            r.tipo = t.elementType;  //Tipo interno del arreglo

            IC exp = visit(ctx.expression()); //obtenemos el resultado y generamos el codigo de la expresion entre corchetes
            gen(new Quad("GE",exp.addr,new Int(t.arraySize),this.arrayOutOfBoundsInstruction));
             if(exp.addr instanceof Temp){
                Temp.liberarTemp((Temp)exp.addr);
            }            
            Temp t3;
            Temp test = Temp.checkTemp();
            //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
            if(test==null){
                t3 = new Temp(new Int());
                this.ambitoActual.variables.add(t3);
                t3.offset = (int) this.metodoActual.finalSize.valor;
                this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4; 
                                 
            }
            //Sino utilizamos el temporal disponible
            else{
                test.tipo = new Int();
                t3=test;
            }                       
            gen(new Quad("MULT",exp.addr,new Int(t.elementType.getWidth()),t3));   //generamos instruccion indice = expr * widthTipoArreglo
             if(exp.addr instanceof Temp){
                Temp.liberarTemp((Temp)exp.addr);
            }
            Temp t2;// Este temporal tendra la suma del indice del array y la direccion base para poder construir la direccion final
            test = Temp.checkTemp();
            //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
            if(test==null){
                t2 = new Temp(new Int());
                this.ambitoActual.variables.add(t2);
                t2.offset = (int) this.metodoActual.finalSize.valor;
                this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;  
            }
            //Sino utilizamos el temporal disponible
            else{
                test.tipo = new Int();
                t2=test;

            }
            gen(new Quad("ADD",t3,new Int(r.array.offset),t2));         
             if(t3 instanceof Temp){
                Temp.liberarTemp((Temp)t3);
            }
            
            r.addr = new ArrayAccess(r.array,t2);
            this.resultTemp = t2;
            
            
            return r;            
            
        }

        return r;
    }   
    
    @Override public IC visitVariableStruct(@NotNull DECAFParser.VariableStructContext ctx){ 
        IC r = new IC();
        IC ic = visit(ctx.simpleVariable());
        String structVar = ctx.simpleVariable().ID().getText();
        Variable structName1 =(Variable) ic.addr;
        if(structName1.tipo instanceof StructType){
            StructType t1 = (StructType) structName1.tipo;
            Variable s;
            if(this.structStack.isEmpty()){
               s = buscarVariable(structVar);
            }
            else{
               s = buscarVariableStack(this.structStack.peek(),structVar);
            }
            this.structStack.push(s);
            if(s!=null){
            //Obtenemos la variable despues del punto
            IC ts = visit(ctx.location());
            if(ts.addr instanceof Variable){
            
            }
            else if(ts.addr instanceof Temp){
            
            }
            this.structStack.pop();
            r.addr = ts.addr;
            }
            
        }
        if(this.structStack.isEmpty()){this.relativeOffsetCounter=0;}
        return r;
    } 
    
    @Override public IC visitStructArray(@NotNull DECAFParser.StructArrayContext ctx) { 
        IC r = new IC();
        IC ic = visit(ctx.arrayVariable());

        if(ic.addr instanceof ArrayAccess){
            
            String structVar = ctx.arrayVariable().ID().getText();
            ArrayAccess arrayReference = (ArrayAccess)ic.addr;
            ArrayType tx = (ArrayType) arrayReference.array.tipo;
            Tipo t = tx.elementType;
            if(t instanceof StructType){
                StructType t1 = (StructType) t;
                Variable s;
                if(this.structStack.isEmpty()){
                   s = buscarVariable(arrayReference.array.name);
                }
                else{
                   s = buscarVariableStack(this.structStack.peek(),arrayReference.array.name);
                }
                this.structStack.push(s);

                if(s!=null){
                    //Si encontramos la estructura y variable, buscamos la variable despues del .
                    IC ts = visit(ctx.location());
                    if(ts.finalVar !=null){
                    Temp result;
                    Temp test = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test==null){
                        result = new Temp(new Int());
                        this.ambitoActual.variables.add(result);
                        result.offset = (int)this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;
                                               
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test.tipo = new Int();
                        result=test;

                    }                        

                    gen(new Quad("ADD",arrayReference.index,ts.addr,result));
                    r.addr = ts.addr;
                    }
                    else{
                        r.addr = ts.addr;
                    }
                    this.structStack.pop();
                    

                }
            }
            if(this.structStack.isEmpty()){
                //Si esta vacion generamos la instruccion para calcular la direccion final 
                Temp ts;
                ts = this.resultTemp;
                
                Variable tempVariable = (Variable)r.addr;
                int offsetFinal = this.relativeOffsetCounter;
                gen(new Quad("ADD",resultTemp,new Int(offsetFinal),ts));
                r.addr = new ArrayAccess(arrayReference.array,ts);
                relativeOffsetCounter =0;
            }


        }
        else{
            String structVar = ctx.arrayVariable().ID().getText();
            ArrayType tx = (ArrayType) ic.array.tipo;
            Tipo t = tx.elementType;
            if(t instanceof StructType){
                StructType t1 = (StructType) t;
                Variable s;
                if(this.structStack.isEmpty()){
                   s = buscarVariable(ic.array.name);
                }
                else{
                   s = buscarVariableStack(this.structStack.peek(),ic.array.name);
                }
                this.structStack.push(s);

                if(s!=null){
                    //Si encontramos la estructura y variable, buscamos la variable despues del .
                    IC ts2 = visit(ctx.location());
                    //Si el hijo es la ultima variable
                    if(ctx.location().simpleLocation()!=null){
                        this.structStack.pop();
                        Variable vc =(Variable) ts2.addr;
                        r.addr = ts2.addr;    
                        //r.finalVar = vc;

                    }
                    else if(ctx.location().structLocation()!=null){
                        this.structStack.pop();
                        r.addr = ts2.addr;
                    
                    }
                   
                }
            }
        
        }
         return r;
    }
        
    @Override public IC visitStatement(@NotNull DECAFParser.StatementContext ctx) { 
        if(ctx.ifStatement()!=null){
            return visit(ctx.ifStatement());
        }
        else if(ctx.whileStatement()!=null){
            return visit(ctx.whileStatement());
        }
        else if(ctx.returnStatement()!=null){
            return visit(ctx.returnStatement());
        }
        else if(ctx.methodCall()!=null){
            return visit(ctx.methodCall());
        }
        else if(ctx.assignStatement()!=null){
            return visit(ctx.assignStatement());
        }
        else if(ctx.varDeclaration()!=null){
            return visit(ctx.varDeclaration());
        }  
        else if(ctx.expressionA()!=null){
            return visit(ctx.expressionA());
        }  
        else if(ctx.block()!=null){
            return visit(ctx.block());
        }          
        return new IC();
    }    
    @Override public IC visitAssignStatement(@NotNull DECAFParser.AssignStatementContext ctx) { 
        IC r = new IC();
        r.tipo=new Int();
        this.booleanContext="assignment";
        IC i1 = visit(ctx.location());
        IC i2 = visit(ctx.expression());
        
        //Si es un array

        gen(new Quad("ASSIGN",i2.addr,null,i1.addr));
        if(i2.addr instanceof Temp){
            Temp.liberarTemp((Temp)i2.addr);
        }  
        if(i1.addr instanceof ArrayAccess){
            ArrayAccess ac =(ArrayAccess) i1.addr; 
            Temp.liberarTemp((Temp)ac.index);
        }       
        if(i2.addr instanceof ArrayAccess){
            ArrayAccess ac =(ArrayAccess) i2.addr; 
            Temp.liberarTemp((Temp)ac.index);
        }            



        return r;
    }  
    @Override public IC visitBlock(@NotNull DECAFParser.BlockContext ctx) {
        IC r = new IC();
        int i =0;
        boolean hasReturn=false;
        //Si el padre del bloque es un metodo, no agregamos un nuevo ambito bloque ya que el ambito del metodo almacenara todo
        if(! (ctx.getParent() instanceof DECAFParser.MethodDeclarationContext)){
            //Buscamos block porque el padre no es un metodo
            ambitoActual = buscarBlock(this.metodoActual,bnum);
            ambitos.push(ambitoActual);
            bnum++;
            for(ParseTree n : ctx.children){
                String test = n.getText();
                IC ic = visit(n);
                    if(n instanceof DECAFParser.StatementContext){
                        if(n.getChild(0) instanceof DECAFParser.IfStatementContext || n.getChild(0) instanceof DECAFParser.WhileStatementContext){
                            int m = next;
                           gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                           this.numLabels++;                           
                           backPatch(ic.nextList,m);
                           r.nextList=ic.nextList;                       
                        
                        }

                    }
            }
            ambitos.pop();
        }
        else{
            for(ParseTree n : ctx.children){
                String s = n.getText();
                IC ic = visit(n);
                    if(n instanceof DECAFParser.StatementContext){
                        if(n.getChild(0) instanceof DECAFParser.IfStatementContext || n.getChild(0) instanceof DECAFParser.WhileStatementContext){
                            int m = next;
                           gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                           this.numLabels++;                           
                           backPatch(ic.nextList,m);
                           r.nextList=ic.nextList;                       
                        
                        }

                    }               
            }
        }
        return r;
    } 
    
   @Override public IC visitReturnStatement(@NotNull DECAFParser.ReturnStatementContext ctx) { 
       IC r = new IC();
       //Tipo es void
       if(ctx.expressionA().children==null){
           
                if(metodoActual.name.equals("main")&& metodoActual.parametros.isEmpty()){
                    gen(new Quad("HALT",null,null,null));
                } 
                else{
                    gen(new Quad("RETURN",null,null,null));
                }
       }
       else{
           IC exp = visit(ctx.expressionA());
           gen(new Quad("POP_LOCALS",metodoActual.finalSize,null,null));
           if(!(this.metodoActual.tipoResultado instanceof VoidType)){
               gen(new Quad("POP_RESULT",exp.addr,null,null));
           }
           gen(new Quad("POP_LINK",null,null,null));
           
           gen(new Quad("RETURN",null,null,null));
           
           r.addr = exp.addr;
       }
       return r;
   
   }
   public Metodo buscarMetodo(String nombre, ArrayList<Tipo> tipos){
        String methodName = nombre;
        Metodo result = null;
        boolean found = false;
        for (Ambito a : ambitoGlobal.ambitosInternos) {
            if(found==true){break;}
            if (a instanceof Metodo) {
                if (a.name.equals(methodName)) {
                    found = true;                   
                    //si encontramos el metodo, verificamos los tipos de parametros
                    Metodo a1 = (Metodo) a;
                    result = a1;
                    if (a1.parametros.size() == tipos.size()) {
                        for (int i = 0; i < tipos.size(); i++) {
                            if (!(a1.parametros.get(i).tipo.getClass().equals(tipos.get(i).getClass()))) {
                                result = null;
                                found = false;
                                break;
                            }
                        }
                    }
                    else{found = false;}

                } else {
                    found = false;
                }
            } else {
                found = false;
            }
        }
    
        return result;   
   }
   @Override 
   public IC visitArg(@NotNull DECAFParser.ArgContext ctx) { 
       this.booleanContext="assignment";
       IC ic = visit(ctx.expression());
       return ic;
   
   }
   @Override public IC visitMethodCall(@NotNull DECAFParser.MethodCallContext ctx) { 
       IC r = new IC();
       String methodName = ctx.ID().getText();
       String tipos = "";
       DECAFParser.Arg2Context argTest = ctx.arg2();
       Metodo m;
       ArrayList<Tipo> paramTypes = new ArrayList<Tipo>();
       //Obteniendo cada parametro
       if(argTest!=null){
            List<DECAFParser.ArgContext> args = argTest.arg();
            int paramCounter=0;
            ArrayList<IC> adresses = new ArrayList<IC>();
            for(DECAFParser.ArgContext g: args){
                IC argIc = visit(g);
                adresses.add(argIc);

                paramCounter++;

            }
            ArrayList<Tipo> Settipos = new ArrayList<Tipo>();
            for(int i = 0; i<adresses.size();i++){
                Object current = adresses.get(i).addr;
                if(current instanceof Variable){
                    Variable v = (Variable) current;
                    Settipos.add(v.tipo);
                    
                }
                else if (current instanceof Tipo){
                    Tipo t = (Tipo) current;
                    Settipos.add(t);
                }            
            
            }
            m = this.buscarMetodo(methodName, Settipos);
            //Lllamando a la instruccion param en reversa para hacer push al stack

            for(int i = adresses.size()-1;i>=0;i--){
                Object current = adresses.get(i);
                gen(new Quad("PARAM",adresses.get(i).addr,null,null));
                
                
            }
            if(! (m.tipoResultado instanceof VoidType)){
                Temp result;
                Temp test = Temp.checkTemp();
                //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                if(test==null){
                    result = new Temp(new Int());
                    this.ambitoActual.variables.add(result);
                    result.offset = (int)this.metodoActual.finalSize.valor;
                    this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                 
                }
                //Sino utilizamos el temporal disponible
                else{
                    test.tipo = new Int();
                    result=test;
                }                        

                gen(new Quad("CALL","LABEL"+m.name+m.numMetodo,new Int(paramCounter*4),result));
                r.addr = result;
                //gen(new Quad("POP_PARAMS",new Int(paramCounter*4),null,null));            

            }
            else{
                gen(new Quad("CALL","LABEL"+m.name+m.numMetodo,new Int(paramCounter*4),null));
            
            }

        }
       else{
           m = this.buscarMetodo(methodName, new ArrayList<Tipo>());
            if(! (m.tipoResultado instanceof VoidType)){
                Temp result;
                Temp test = Temp.checkTemp();
                //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                if(test==null){
                    result = new Temp(new Int());
                    this.ambitoActual.variables.add(result);
                    result.offset = (int)this.metodoActual.finalSize.valor;
                    this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                 
                }
                //Sino utilizamos el temporal disponible
                else{
                    test.tipo = new Int();
                    result=test;
                }                        

                gen(new Quad("CALL","LABEL"+m.name+m.numMetodo,new Int(0),result));
                r.addr = result;
                //gen(new Quad("POP_PARAMS",new Int(paramCounter*4),null,null));            

            }
            else{
                gen(new Quad("CALL","LABEL"+m.name+m.numMetodo,new Int(0),null));
            
            }
       
       }
       return r;
   
   }     
    @Override public IC visitSimpleIf(@NotNull DECAFParser.SimpleIfContext ctx) { 
        this.singleVarExp = true;
        this.booleanContext="controlFlow";
        IC r = new IC();
        IC exp = visit(ctx.expression());
        this.booleanContext = "assignment";
        int m = next;
        gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
        this.numLabels++;          
        IC block = visit(ctx.block());
        backPatch(exp.trueList,m);
        r.nextList = merge(exp.falseList,block.nextList);
                
        return r;


    }

    @Override public IC visitIfElse(@NotNull DECAFParser.IfElseContext ctx) { 
        this.singleVarExp = true;
        this.booleanContext="controlFlow";
        this.inAndOrExpr = false;
        IC r = new IC();
        IC B = visit(ctx.expression());
        this.booleanContext = "assignment";
        int m1 = next;
        gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
        this.numLabels++;            
        IC S1 = visit(ctx.block(0));
        ArrayList<Integer> NnextList = makeList(next);
        gen(new Quad("GOTO",null,null,null));
        int m2 = next;
        gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
        this.numLabels++;            
        IC S2 = visit(ctx.block(1));
        backPatch(B.trueList,m1);
        backPatch(B.falseList,m2);
        ArrayList<Integer> temp = merge(S1.nextList,NnextList);
        r.nextList = merge(temp,S2.nextList);
                
        return r;

    }

    @Override public IC visitWhileStatement(@NotNull DECAFParser.WhileStatementContext ctx) { 
        this.singleVarExp=true;
        this.booleanContext="controlFlow";
        this.inAndOrExpr = false;
        IC r = new IC();
        int m1 = next;
        gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
        this.numLabels++;            
        IC B = visit(ctx.expression());
                this.booleanContext = "assignment";
        int m2 = next;
        gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
        this.numLabels++;            
        IC S1 = visit(ctx.block());
        backPatch(S1.nextList,m1);
        backPatch(B.trueList,m2);
        r.nextList = B.falseList;
        gen(new Quad("GOTO",null,null,m1));
        
        return r;
    }
        
   @Override public IC visitExpression(@NotNull DECAFParser.ExpressionContext ctx) { 
       IC r = new IC(); 
       if(this.booleanContext.equals("assignment")){
            if(ctx.children.size()>1){
                this.inAndOrExpr = true;
                IC op1 = visit(ctx.children.get(0));
                String op = ctx.children.get(1).getText();
                IC op2 = visit(ctx.children.get(2));
                if(op.equals("||")){
                        Temp t;
                        Temp test = Temp.checkTemp();
                        //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                        if(test==null){
                            r.addr=new Temp(new Bool());    
                            t = (Temp)r.addr;
                            this.ambitoActual.variables.add(t);
                            t.offset = (int)this.metodoActual.finalSize.valor;
                            this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                          
                        }
                        //Sino utilizamos el temporal disponible
                        else{
                            test.tipo = new Bool();
                            r.addr=test;
                            t = (Temp)r.addr;
                        }                    
                     gen(new Quad("OR",op1.addr,op2.addr,r.addr));
                     //Verificamos que los operandos sean temporables para liberalos
                    if(op1.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op1.addr);
                    }
                    if(op2.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op2.addr);
                    }
                }

             } 
             else{
                IC r1= visit(ctx.children.get(0));
                return r1;

             }         
       }
       else if(this.booleanContext.equals("controlFlow")){
             if(ctx.children.size()>1){
                 this.inAndOrExpr = true;
                this.singleVarExp = false;
                String op = ctx.children.get(1).getText();
                if(op.equals("||")){
                    IC op1 = visit(ctx.children.get(0));
                    int m = next;
                    gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                    this.numLabels++;                      
                    IC op2 = visit(ctx.children.get(2));                    
                    backPatch(op1.falseList,m);
                    r.trueList = merge(op1.trueList,op2.trueList);
                    r.falseList=op2.falseList;
                }

             } 
             else{
                IC r1= visit(ctx.children.get(0));
                return r1;

             }             
       }
       return r;
   }    
       
    
   
   @Override public IC visitAndExpr(@NotNull DECAFParser.AndExprContext ctx) { 
       IC r = new IC();
       if (this.booleanContext.equals("assignment")) {
            if (ctx.children.size() > 1) {
                this.inAndOrExpr = true;
                IC op1 = visit(ctx.children.get(0));
                String op = ctx.children.get(1).getText();
                IC op2 = visit(ctx.children.get(2));
                if(op.equals("&&")){
                        Temp t;
                        Temp test = Temp.checkTemp();
                        //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                        if(test==null){
                            r.addr=new Temp(new Bool());
                            t = (Temp)r.addr;
                            this.ambitoActual.variables.add(t);
                            t.offset = (int)this.metodoActual.finalSize.valor;
                            this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                              
                        }
                        //Sino utilizamos el temporal disponible
                        else{
                            test.tipo = new Bool();
                            r.addr=test;

                            t = (Temp)r.addr;
                        }                    
                     gen(new Quad("AND",op1.addr,op2.addr,r.addr));
                     //Verificamos que los operandos sean temporables para liberalos
                    if(op1.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op1.addr);
                    }
                    if(op2.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op2.addr);
                    }
                }

            }              
             else {
                IC r1= visit(ctx.children.get(0));
                return r1;

           }
       }
       else if(this.booleanContext.equals("controlFlow")){
            if(ctx.children.size()>1){
                this.inAndOrExpr = true;
                this.singleVarExp = false;
                String op = ctx.children.get(1).getText();
               if (op.equals("&&")) {
                   IC op1 = visit(ctx.children.get(0));
                   int m = next;
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++;                  
                   IC op2 = visit(ctx.children.get(2));                   
                   backPatch(op1.trueList,m);                  
                   r.trueList = op2.trueList;
                   r.falseList = merge(op1.falseList,op2.falseList);

               }
            }
            else{
                IC r1= visit(ctx.children.get(0));
                return r1;            
            }
       }
       return r;
   }	
    
    @Override public IC visitEqExpr(@NotNull DECAFParser.EqExprContext ctx) {
        IC r = new IC();
        if(this.booleanContext.equals("assignment")){
            if(ctx.children.size()>1){
               IC op1 = visit(ctx.children.get(0));
               String op = ctx.children.get(1).getText();
               IC op2 = visit(ctx.children.get(2));

               if(op.equals("==")){
                    Temp t;
                    Temp test = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test==null){
                        r.addr=new Temp(new Bool());
                        t = (Temp)r.addr;
                        this.ambitoActual.variables.add(t);
                        t.offset = (int)this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                          
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test.tipo = new Bool();
                        r.addr=test;
                        
                        t = (Temp)r.addr;
                    }
                   gen(new Quad("EQ",op1.addr,op2.addr,this.next+3));
                   gen(new Quad("ASSIGN",new Bool("boolean",false),null,r.addr));
                   gen(new Quad("GOTO",null,null,next+3));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++;                   
                   gen(new Quad("ASSIGN",new Bool("boolean",true),null,r.addr));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++; 
                                        //Verificamos que los operandos sean temporables para liberalos
                    if(op1.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op1.addr);
                    }
                    if(op2.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op2.addr);
                    }
               }
               if(op.equals("!=")){
                    Temp t;
                    Temp test = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test==null){
                        r.addr=new Temp(new Bool());
                        t = (Temp)r.addr;
                        this.ambitoActual.variables.add(t);
                        t.offset = (int)this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                                
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test.tipo = new Bool();
                        r.addr=test;
                        
                        t = (Temp)r.addr;
                    }
                   gen(new Quad("NE",op1.addr,op2.addr,this.next+3));
                   gen(new Quad("ASSIGN",new Bool("boolean",false),null,r.addr));
                   gen(new Quad("GOTO",null,null,next+3));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++;                   
                   gen(new Quad("ASSIGN",new Bool("boolean",true),null,r.addr));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++; 
                                        //Verificamos que los operandos sean temporables para liberalos
                    if(op1.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op1.addr);
                    }
                    if(op2.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op2.addr);
                    }
               }     
            } 
            else{
                IC r1= visit(ctx.children.get(0));
                return r1;

            }             
        }
        else if (this.booleanContext.equals("controlFlow")){
              if(ctx.children.size()>1){
               this.singleVarExp = false;
               boolean wasTrue = false;
               if(this.inAndOrExpr){
                   wasTrue =true;
               }
               this.inAndOrExpr = false;
               IC op1 = visit(ctx.children.get(0));
               String op = ctx.children.get(1).getText();
               IC op2 = visit(ctx.children.get(2));
               if(wasTrue){
                   this.inAndOrExpr = true;
               }
               if(op.equals("==")){
                   r.trueList = makeList(next);
                   r.falseList = makeList(next+1);
                   Quad q = new Quad("EQ",op1.addr,op2.addr,null);
                   gen(q);
                   gen(new Quad("GOTO",null,null,null));
               }
               if(op.equals("!=")){
                   r.trueList = makeList(next);
                   r.falseList = makeList(next+1);
                   Quad q = new Quad("NE",op1.addr,op2.addr,null);
                   gen(q);
                   gen(new Quad("GOTO",null,null,null));
               }     
            } 
            else{
                IC r1= visit(ctx.children.get(0));
                return r1;

            }             
        }
        return r;

    }    
    @Override
    public IC visitRelationExpr(@NotNull DECAFParser.RelationExprContext ctx) {
        IC r = new IC();
        if(this.booleanContext.equals("assignment")){
           
            // Si hay mas de un hijo generamos codigo para operadores
            if(ctx.children.size()>1){
              
               IC op1 = visit(ctx.children.get(0));
               String op = ctx.children.get(1).getText();
               IC op2 = visit(ctx.children.get(2));
               if(op.equals(">")){
                    Temp t;
                    Temp test = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test==null){
                        r.addr=new Temp(new Bool());
                        t = (Temp)r.addr;
                        this.ambitoActual.variables.add(t);
                        t.offset = (int)this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                                 
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test.tipo = new Bool();
                        r.addr=test;
                        
                        t = (Temp)r.addr;
                    }
                   gen(new Quad("GT",op1.addr,op2.addr,this.next+3));
                   gen(new Quad("ASSIGN",new Bool("boolean",false),null,r.addr));
                   gen(new Quad("GOTO",null,null,next+3));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++;                   
                   gen(new Quad("ASSIGN",new Bool("boolean",true),null,r.addr));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++; 
                                        //Verificamos que los operandos sean temporables para liberalos
                    if(op1.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op1.addr);
                    }
                    if(op2.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op2.addr);
                    }
               }
               
               else if(op.equals(">=")){
                    Temp t;
                    Temp test = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test==null){
                        r.addr=new Temp(new Bool());
                        t = (Temp)r.addr;
                        this.ambitoActual.variables.add(t);
                        t.offset = (int)this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                                  
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test.tipo = new Bool();
                        r.addr=test;
                        
                        t = (Temp)r.addr;
                    }
                   gen(new Quad("GE",op1.addr,op2.addr,this.next+3));
                   gen(new Quad("ASSIGN",new Bool("boolean",false),null,r.addr));
                   gen(new Quad("GOTO",null,null,next+3));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++;                   
                   gen(new Quad("ASSIGN",new Bool("boolean",true),null,r.addr));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++; 
                                        //Verificamos que los operandos sean temporables para liberalos
                    if(op1.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op1.addr);
                    }
                    if(op2.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op2.addr);
                    }                

               }
               else if(op.equals("<")){
                    Temp t;
                    Temp test = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test==null){
                        r.addr=new Temp(new Bool());
                        t = (Temp)r.addr;
                        this.ambitoActual.variables.add(t);
                        t.offset = (int)this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                                   
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test.tipo = new Bool();
                        r.addr=test;
                        
                        t = (Temp)r.addr;
                    }
                   gen(new Quad("LT",op1.addr,op2.addr,this.next+3));
                   gen(new Quad("ASSIGN",new Bool("boolean",false),null,r.addr));
                   gen(new Quad("GOTO",null,null,next+3));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++;                   
                   gen(new Quad("ASSIGN",new Bool("boolean",true),null,r.addr));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++; 
                                        //Verificamos que los operandos sean temporables para liberalos
                    if(op1.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op1.addr);
                    }
                    if(op2.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op2.addr);
                    }
                   

               }
               else if(op.equals("<=")){
                    Temp t;
                    Temp test = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test==null){
                        r.addr=new Temp(new Bool());
                        t = (Temp)r.addr;
                        this.ambitoActual.variables.add(t);
                        t.offset = (int)this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                                 
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test.tipo = new Bool();
                        r.addr=test;
                        
                        t = (Temp)r.addr;
                    }
                   gen(new Quad("LE",op1.addr,op2.addr,this.next+3));
                   gen(new Quad("ASSIGN",new Bool("boolean",false),null,r.addr));
                   gen(new Quad("GOTO",null,null,next+3));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++;                   
                   gen(new Quad("ASSIGN",new Bool("boolean",true),null,r.addr));
                   gen(new Quad("LABEL"+this.numLabels,null,null,null,true));
                   this.numLabels++; 
                                        //Verificamos que los operandos sean temporables para liberalos
                    if(op1.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op1.addr);
                    }
                    if(op2.addr instanceof Temp){
                        Temp.liberarTemp((Temp)op2.addr);
                    }
               }           
            } 
            else{
                IC r1= visit(ctx.children.get(0));
                return r1;

            }       
            return r;
        
        }
        
        else if(booleanContext.equals("controlFlow")){
            r = new IC();
            // Si hay mas de un hijo generamos codigo para operadores
            if(ctx.children.size()>1){
                this.singleVarExp = false;
               IC op1 = visit(ctx.children.get(0));
               String op = ctx.children.get(1).getText();
               IC op2 = visit(ctx.children.get(2));
               if(op.equals(">")){
                   r.trueList = makeList(next);
                   r.falseList = makeList(next+1);
                   Quad q = new Quad("GT",op1.addr,op2.addr,null);
                   gen(q);
                   gen(new Quad("GOTO",null,null,null));
                 
               }
               else if(op.equals(">=")){
                   r.trueList = makeList(next);
                   r.falseList = makeList(next+1);
                   Quad q = new Quad("GE",op1.addr,op2.addr,null);
                   gen(q);
                   gen(new Quad("GOTO",null,null,null));

               }
               else if(op.equals("<")){
                   r.trueList = makeList(next);
                   r.falseList = makeList(next+1);
                   Quad q = new Quad("LT",op1.addr,op2.addr,null);
                   gen(q);
                   gen(new Quad("GOTO",null,null,null));
               }
               else if(op.equals("<=")){
                   r.trueList = makeList(next);
                   r.falseList = makeList(next+1);
                   Quad q = new Quad("LE",op1.addr,op2.addr,null);
                   gen(q);
                   gen(new Quad("GOTO",null,null,null));
               }           

               return r;
        
            }
        
            else{
               IC r1= visit(ctx.children.get(0));
               return r1;
            }        

    }
        return r;
    }
   
    
    //Pendiendte  suma/resta char-int
   @Override public IC visitAddExpr(@NotNull DECAFParser.AddExprContext ctx) {
        IC r = new IC();
        if(ctx.children.size()>1){
           IC op1 = visit(ctx.children.get(0));
           String op = ctx.children.get(1).getText();
           IC op2 = visit(ctx.children.get(2));
           if(op.equals("+")){
                Temp t;
                Temp test = Temp.checkTemp();
                //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                if(test==null){
                    t = new Temp(new Int("int",null));
                    this.ambitoActual.variables.add(t);
                    t.offset = (int)this.metodoActual.finalSize.valor;
                    this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                           
                }
                //Sino utilizamos el temporal disponible
                else{
                    test.tipo = new Int();
                    t=test;
                }

                r.addr=t;
                gen(new Quad("ADD",op1.addr,op2.addr,t));
                if(op1.addr instanceof Temp){
                    Temp.liberarTemp((Temp)op1.addr);
                }
                if(op2.addr instanceof Temp){
                    Temp.liberarTemp((Temp)op2.addr);
                }

           }
           else if( op.equals("-")){
                Temp t;
                Temp test = Temp.checkTemp();
                //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                if(test==null){
                    t = new Temp(new Int("int",null));
                    this.ambitoActual.variables.add(t);
                    t.offset = (int)this.metodoActual.finalSize.valor;
                    this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                            
                }
                //Sino utilizamos el temporal disponible
                else{
                    test.tipo = new Int();
                    t=test;
                }

                r.addr=t;
                gen(new Quad("SUB",op1.addr,op2.addr,t));
                if(op1.addr instanceof Temp){
                    Temp.liberarTemp((Temp)op1.addr);
                }
                if(op2.addr instanceof Temp){
                    Temp.liberarTemp((Temp)op2.addr);
                }

           }
        } 
        else{
           IC r1= visit(ctx.children.get(0));
           return r1;
        
        }
        return r;
    
   }    
    
   @Override public IC visitMultExpr(@NotNull DECAFParser.MultExprContext ctx){
       IC r = new IC();
        if(ctx.children.size()>1){
           IC op1 = visit(ctx.children.get(0));
           String op = ctx.children.get(1).getText();
           IC op2 = visit(ctx.children.get(2));
           
           if(op.equals("*")){
                Temp t;
                Temp test = Temp.checkTemp();
                //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                if(test==null){
                    t = new Temp(new Int("int",null));
                    this.ambitoActual.variables.add(t);
                    t.offset = (int)this.metodoActual.finalSize.valor;
                    this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                        
                }
                //Sino utilizamos el temporal disponible
                else{
                    test.tipo = new Int();
                    t=test;
                }

                r.addr=t;
                gen(new Quad("MULT",op1.addr,op2.addr,t));
                if(op1.addr instanceof Temp){
                    Temp.liberarTemp((Temp)op1.addr);
                }
                if(op2.addr instanceof Temp){
                    Temp.liberarTemp((Temp)op2.addr);
                }
           }
           else if( op.equals("/")){
                Temp t;
                Temp test = Temp.checkTemp();
                //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                if(test==null){
                    t = new Temp(new Int("int",null));
                    this.ambitoActual.variables.add(t);
                    t.offset = (int)this.metodoActual.finalSize.valor;
                    this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                       
                }
                //Sino utilizamos el temporal disponible
                else{
                    test.tipo = new Int();
                    t=test;
                }

                r.addr=t;
                gen(new Quad("DIV",op1.addr,op2.addr,t));
                if(op1.addr instanceof Temp){
                    Temp.liberarTemp((Temp)op1.addr);
                }
                if(op2.addr instanceof Temp){
                    Temp.liberarTemp((Temp)op2.addr);
                }
           }
           else if(op.equals("%")){
                Temp t;
                Temp test = Temp.checkTemp();
                //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                if(test==null){
                    t = new Temp(new Int("int",null));
                    this.ambitoActual.variables.add(t);
                    t.offset = (int)this.metodoActual.finalSize.valor;
                    this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                         
                }
                //Sino utilizamos el temporal disponible
                else{
                    test.tipo = new Int();
                    t=test;
                }

                r.addr=t;
                gen(new Quad("MOD",op1.addr,op2.addr,t));
                if(op1.addr instanceof Temp){
                    Temp.liberarTemp((Temp)op1.addr);
                }
                if(op2.addr instanceof Temp){
                    Temp.liberarTemp((Temp)op2.addr);
                }
           }
        } 
        else{
           IC r1= visit(ctx.children.get(0));
           return r1;
        }
        return r;
    }
    @Override public IC visitValue(@NotNull DECAFParser.ValueContext ctx) {
       if(ctx.children.size()>1){
           return visit(ctx.expression());
       
       }
       else{
          IC ic = visit(ctx.children.get(0));
          //Si estamos en una estructura de control y  solo hay un valor, generamos codigo comparandolo con false
          if(this.booleanContext.equals("controlFlow")&&this.singleVarExp==true){
                IC r = new IC();    
                r.trueList = makeList(next);
                r.falseList = makeList(next+1);
                Tipo falso = new Bool("boolean",false);
                Quad q = new Quad("NE",ic.addr,falso,null);
                gen(q);
                gen(new Quad("GOTO",null,null,null));   
                return r;
          
          }
          return ic;
          
          
        }
   
    }   
       
   @Override 
   public IC visitUnaryExpr(@NotNull DECAFParser.UnaryExprContext ctx) { 
        // Si hay mas de un hijo, existe un operador
        IC r = new IC();
        if(ctx.children.size()>1){
            String operador = ctx.children.get(0).getText();
            if(operador.equals("!")){
                if(this.booleanContext.equals("assignment")){
                    //Si es ! 
                   IC  ic = visit(ctx.value());  //Generacion de codigo de la expresion value (location-literal o expression)
                    Temp t ;
                    Temp test = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test==null){
                        t = new Temp(new Bool("boolean",null));
                        this.ambitoActual.variables.add(t);
                        t.offset = (int)this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                           
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test.tipo = new Bool();
                        t=test;

                    }                   
                                
                   r.addr=t;
                   gen(new Quad("NOT",ic.addr,null,t));                
                }
                
                else if(this.booleanContext.equals("controlFlow")){
                    IC ic = visit(ctx.value());
                    r.trueList = ic.falseList;
                    r.falseList = ic.trueList;
                   
                
                }
            }
            //Si es - 
            else if(operador.equals("-")){
                //Si es - el valor debe ser int
                IC  ic = visit(ctx.value());  //Generacion de codigo de la expresion value (location-literal o expression)
                Temp t ;
                Temp test = Temp.checkTemp();
                //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                if(test==null){
                    t = new Temp(new Int("int",null));
                    this.ambitoActual.variables.add(t);
                    t.offset = (int)this.metodoActual.finalSize.valor;
                    this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                             
                }
                //Sino utilizamos el temporal disponible
                else{
                    test.tipo = new Int();
                    t=test;

                }                                                
                r.addr=t;
                gen(new Quad("MINUS",ic.addr,null,t));
            }
             //Casting
            else if(operador.equals("(")){
                IC ic = visit(ctx.value());
                TerminalNode test = ctx.CHARS();
                //Si CHARS es null el casteo es a int
                if(test==null){
                    Temp t ;
                    Temp test2 = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test2==null){
                        t = new Temp(new Int("int",null));
                        this.ambitoActual.variables.add(t);
                        t.offset = (int)this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                                
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test2.tipo = new Int();
                        t=test2;

                    }                                   
                    r.addr=t;
                    
                    //PARA CASTING DE VALORES
                    /*if(ic.addr instanceof Char){
                        Char x = (Char)ic.addr;
                        Character c = (Character) x.valor ;
                        int val = (int)c;
                        ic.addr = new Int("int",val);
                    }                    
                    gen(new Quad("ASSIGN",ic.addr,null,t));
                    */
                    gen(new Quad("(INT)",ic.addr,null,t));
                }
                else{
                    Temp t ;
                    Temp test2 = Temp.checkTemp();
                    //Si no encontramos un temp disponible, creamos uno y lo almacenamos en la tabla de simbolos
                    if(test2==null){
                        t = new Temp(new Char("char",null));
                        this.ambitoActual.variables.add(t);
                        t.offset = (int)this.metodoActual.finalSize.valor;
                        this.metodoActual.finalSize.valor = (int)this.metodoActual.finalSize.valor+4;                                 
                    }
                    //Sino utilizamos el temporal disponible
                    else{
                        test2.tipo = new Char("char",null);
                        t=test2;

                    }                      

                    r.addr=t;
                    
                    // para casting de valores
                    /*
                    if(ic.addr instanceof Int){
                        Int x = (Int)ic.addr;
                        Character val  = Character.valueOf((Character)x.valor);
                        ic.addr = new Char("char",val);
                        
                        
                    }
                    gen(new Quad("ASSIGN",ic.addr,null,t));                
                            */
                    gen(new Quad("(CHAR)",ic.addr,null,t));
                }
              
                
            }
            else{
                    r= visit(ctx.value());

            }              
        }
        else{
           IC r1= visit(ctx.children.get(0));
           return r1;
        }
        return r;
    }
   
   
   
    
   @Override public IC visitInt_literal(@NotNull DECAFParser.Int_literalContext ctx) { 
      String t1 = ctx.getText();
      int v = Integer.parseInt(t1);
      IC ic = new IC();
      ic.addr = new Int("int",v);
      return ic;
    
    }
    
   @Override public IC visitChar_literal(@NotNull DECAFParser.Char_literalContext ctx) { 
        int a = ctx.getText().length();
        String t1 = ctx.getText();
        char v;
        if(a>3){
            String finalVal = t1.substring(1,3);
            if(finalVal.equals("\\n")){
                 v = '\n';
            }
            else if (finalVal.equals("\\'")){
                v = '\'';
            }
            else if (finalVal.equals("\\t")){
                v = '\t';
            }            
            else{
                v = '\\';
            }
            
        }
        else{
            v = Character.valueOf(ctx.getText().charAt(1));
        }
        
        
        Tipo t = new Char("char",v);
        IC ic = new IC();
        ic.addr=t;
        return ic;

    
    }
    
   @Override public IC visitBool_literal(@NotNull DECAFParser.Bool_literalContext ctx) { 
       IC ic = new IC(); 
       if(this.booleanContext.equals("assignment")){
            if(ctx.getText().equals("true")){
               boolean v = true ; 
               Tipo t = new Bool("boolean",v);  
               ic = new IC();
               ic.addr=t;
               return ic;
             }
             else{
               boolean v = false ; 
               Tipo t = new Bool("boolean",v);              
               ic = new IC();
               ic.addr=t;
               return ic;

            }
       }
       else if(this.booleanContext.equals("controlFlow")){
            if(ctx.getText().equals("true")){
               boolean v = true ; 
               Tipo t = new Bool("boolean",v);  
               ic = new IC();
               ic.trueList = makeList(next);
               if(this.inAndOrExpr){
                    gen(new Quad("GOTO",null,null,null));
               }

               ic.addr=t;
               return ic;
             }
             else{
               boolean v = false ; 
               Tipo t = new Bool("boolean",v);  
               ic = new IC();
               ic.falseList = makeList(next);
               if(this.inAndOrExpr){
                    gen(new Quad("GOTO",null,null,null));
               }
                ic.addr=t;
               return ic;

            }                    
                    
                    
       
       }
       return ic;
    }
}
