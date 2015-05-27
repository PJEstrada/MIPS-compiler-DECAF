/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto1compiladores;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author pablo
 */
public class CodeGenerator {
    

    
    BasicBlock bloqueActual;
    ArrayList<Quad> intermediateCode;
    String dataSegment;
    ArrayList<String> code;
    ArrayList<AddressDescriptor> programAddressDescriptors;
    ArrayList<RegisterDescriptor> programRegisterDescriptors;
    ArrayList<String> MIPScode; 
    ArrayList<Registro> resgitrosTotales = new ArrayList<Registro>();
    Variable dummyVar;
    
    public CodeGenerator(ArrayList<Quad> ic , String dataSegment, Ambito tablaSimbolos){
       dummyVar = new Variable(" ",new Int() );
       MIPScode = new ArrayList<String>();
       this.intermediateCode = ic;
       this.programAddressDescriptors = new ArrayList<AddressDescriptor>();
       this.programRegisterDescriptors = new ArrayList<RegisterDescriptor>();
       this.dataSegment = dataSegment;
       this.MIPScode.add(dataSegment);
       String s = "array: .asciiz \"Array Out Of Bounds Exception :) \\n \"";
       gen(s);
       s = "true: .asciiz \"True \\n \"";
       gen(s);
       s = "false: .asciiz \"False \\n \"";
       gen(s);
       
       this.MIPScode.add(".text");
       this.MIPScode.add("li $gp,0x10010000");
       //s = "LABELprin";
       //Agregando registros 
       this.resgitrosTotales.add(Registro.t0);
       this.resgitrosTotales.add(Registro.t1);
       this.resgitrosTotales.add(Registro.t2);
       this.resgitrosTotales.add(Registro.t3);
       this.resgitrosTotales.add(Registro.t4);
       this.resgitrosTotales.add(Registro.t5);
       this.resgitrosTotales.add(Registro.t6);
       this.resgitrosTotales.add(Registro.t7);
       
    }
    
    public void gen(String s){
        this.MIPScode.add(s);
    
    }
    
    public ArrayList<BasicBlock> createBasicBlocks(){
        ArrayList<BasicBlock> result = new ArrayList<BasicBlock>();       
        //Primero identificamos las instrucciones lideres
        ArrayList<Integer> indiceLideres = new ArrayList<Integer>();
        int i =0;
        indiceLideres.add(0); //La primera instruccion siempre es lider
        for(Quad q : intermediateCode){
            // Si la instruccion es un salto conidiconal o incondicional, agregamos la instruccion a la que salta el programa.
            
            //Si encontramos algun salto obtenemos su target para saber el label y buscamos la instruccion que contiene el label.
            String op = q.op;
            if((op.equals("LT")||op.equals("LE")||op.equals("EQ")||op.equals("GE")||op.equals("GT")||op.equals("NE")||op.equals("GOTO"))){
                String label = (String) q.target;
                int instructionIndex = findLabel(label);
                //Agregamos la instruccion que es el target del salto
                if(!indiceLideres.contains(instructionIndex)&&instructionIndex<this.intermediateCode.size()){
                    indiceLideres.add(instructionIndex);
                }

                //Agregamos la instruccion que sigue a la instruccion de salto
                if(!indiceLideres.contains(i+1)&&instructionIndex<this.intermediateCode.size()){
                    indiceLideres.add(i+1);
                }

            }
            else if (op.equals("CALL")){
                String label = (String) q.arg1;
                int instructionIndex = findLabel(label);
                //Agregamos la instruccion que es el target del salto
                if(!indiceLideres.contains(instructionIndex)&&instructionIndex<this.intermediateCode.size()){
                    indiceLideres.add(instructionIndex);
                }

                //Agregamos la instruccion que sigue a la instruccion de salto
                if(!indiceLideres.contains(i+1)&&instructionIndex<this.intermediateCode.size()){
                    indiceLideres.add(i+1);
                }            
            }
            else if (op.equals("HALT")||op.equals("RETURN")){
                //Agregamos la instruccion que sigue a la instruccion de salto
                if(!indiceLideres.contains(i+1)&&(i+1)<this.intermediateCode.size()){
                    indiceLideres.add(i+1);
                }            
            }
                
            
            
            
            i++;
        
        }
        
        //Luego de encontrar las instrucciones lideres, procedemos a crear los bloques en base a las instrucciones
        Collections.sort(indiceLideres);
        for(int k : indiceLideres){
                BasicBlock b = new BasicBlock();
            for(int j =k; j<intermediateCode.size();j++){
                
                if(j==k){
                    b.instructions.add(intermediateCode.get(j));
                    continue;
                }
                if(indiceLideres.contains(j)){
                    break;
                }
                else{
                    b.instructions.add(intermediateCode.get(j));
                }
            
            
            }
            result.add(b);
        
        }
        return result;
    
    }
    
    public int findLabel(String label){
        int i =0;
        for(Quad q: intermediateCode){
            if(q.isLabel&&q.op.equals(label)){
            
                return i;
                
            }
            i++;
        }
        return -1;
    }
    //Metodo que verifica todos los descriptores de registros para ver si existe alguno con la variable especificada
    public Registro variableExisteEnRegistro(Variable variable){
        for(RegisterDescriptor r : this.programRegisterDescriptors){
            if(r.variable.equals(variable)){
                return r.registro;
            }
        
        }
        return null;
    
    
    }  
    //Busca un registro vacio con el registro como parametros de exepcion en la eleccion
    public Registro buscarRegistroVacio(Registro ra,Registro rb,Registro rc){
        
        for(Registro r : this.resgitrosTotales){
            boolean encontrado = false;
            for(RegisterDescriptor des: this.programRegisterDescriptors){
                if(des.registro.equals(r)){
                    encontrado = true;
                    break;
                
                
                }
            
            }
            if(encontrado == true){
                continue;
            }
            else if (!r.equals(ra)&&!r.equals(rb)&&!r.equals(rc)){
                return r;
            }
        
        }
        return null;
    
    }      
    //Busca un registro vacio con el registro como parametros de exepcion en la eleccion
    public Registro buscarRegistroVacio(Registro ra,Registro rb){
        
        for(Registro r : this.resgitrosTotales){
            boolean encontrado = false;
            for(RegisterDescriptor des: this.programRegisterDescriptors){
                if(des.registro.equals(r)){
                    encontrado = true;
                    break;
                
                
                }
            
            }
            if(encontrado == true){
                continue;
            }
            else if (!r.equals(ra)&&!r.equals(rb)){
                return r;
            }
        
        }
        return null;
    
    }    
    //Busca un registro vacio con el registro como parametros de exepcion en la eleccion
    public Registro buscarRegistroVacio(Registro ra){
        
        for(Registro r : this.resgitrosTotales){
            boolean encontrado = false;
            for(RegisterDescriptor des: this.programRegisterDescriptors){
                if(des.registro.equals(r)){
                    encontrado = true;
                    break;
                
                
                }
            
            }
            if(encontrado == true){
                continue;
            }
            else if(!r.equals(ra)){
                return r;
            }
        
        }
        return null;
    
    }
        
    public Registro buscarRegistroVacio(){
        
        for(Registro r : this.resgitrosTotales){
            boolean encontrado = false;
            for(RegisterDescriptor des: this.programRegisterDescriptors){
                if(des.registro.equals(r)){
                    encontrado = true;
                    break;
                
                
                }
            
            }
            if(encontrado == true){
                continue;
            }
            else{
                return r;
            }
        
        }
        return null;
    
    }
    
    public ArrayList<RegisterDescriptor> obtenerDescriptoresRegistro(Registro r){
        ArrayList<RegisterDescriptor> result = new ArrayList<RegisterDescriptor>();
        for(RegisterDescriptor des: this.programRegisterDescriptors){
            if(des.registro.equals(r)){
                result.add(des);
            }
        
        
        }
        return result;
    
    }
    //Metodo busca en los descriptores algun otro registro que no sea el enviado como parametro que contenga la variable 
   
    public Registro buscarRegistrosConteniendo(Variable variable, Registro registroExcluir){
        for(RegisterDescriptor desc: this.programRegisterDescriptors){
            if( (desc.registro==null && desc.variable.equals(variable))   ||  (!desc.registro.equals(registroExcluir)&&desc.variable.equals(variable))){
                return desc.registro;
            
            }
        
        }
        return null;
    }
    public ArrayList<Registro> buscarTodosRegistrosConteniendo(Variable variable, Registro registroExcluir){
        ArrayList<Registro> r = new ArrayList<Registro>();
        for(RegisterDescriptor desc: this.programRegisterDescriptors){
            if( (desc.registro==null && desc.variable.equals(variable))   ||  (!desc.registro.equals(registroExcluir)&&desc.variable.equals(variable))){
                r.add(desc.registro);
            
            }
        
        }
        return r;
    }    
    public boolean registroContieneVariable(Variable v, Registro r){
        for(RegisterDescriptor desc: this.programRegisterDescriptors){
            if(desc.registro.equals(r)&&desc.variable.equals(v)){
                return true;
            }
        
        }
        return false;
    
    }

    /*
        Utilizado para los registros de operandos rx,ry
    */
    public Registro obtenerMejorRegistro(Quad q,Registro re,Registro re2,Registro re3){
        ArrayList<Integer> scores = new ArrayList<Integer>();
        ArrayList<ArrayList<String>> loads = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<AddressDescriptor>> descs = new ArrayList<ArrayList<AddressDescriptor>>();
        int i =0;
        for(Registro ri: this.resgitrosTotales){
            scores.add(0);
            descs.add(new ArrayList<AddressDescriptor>());
        
        }
        for(Registro ri: this.resgitrosTotales){
            int currentRegScore = 0;
            loads.add(new ArrayList<String>());

            //Obtenemos los descriptores del registro para iterar sobre cada variable
            ArrayList<RegisterDescriptor> descriptores = obtenerDescriptoresRegistro(ri);
            boolean bestReg = true;
            for(RegisterDescriptor desc: descriptores){
                boolean ok1= false;
                boolean ok2 = false;
                //1. Si los descriptores de registro de la variable v dice que v esta en algun lugar ademas de R. OK
                Registro rTest =  buscarRegistrosConteniendo(desc.variable,ri);
                if(rTest!=null){
                    ok1 = true;
                }
                //2. Si v es x, es decir, el valor de destino que esta siendo computado por la instruccion Y ademas v no esta en el otro operando. OK
                if(desc.variable.equals(q.target) &&( (desc.variable.equals(q.arg1) && !desc.variable.equals(q.arg2))  || (!desc.variable.equals(q.arg1) && desc.variable.equals(q.arg2))   )){
                    ok2 = true;
                }

                //4. Si alguna de los dos primeros casos no es OK necesitamos generar stores, por lo que le sumamos 1 al score de este Registro
                if(!ok1 || !ok2){
                    bestReg = false;
                    int newScore = scores.get(i)+1;
                    scores.set(i, newScore);
                    //Verificamos si la variable es local o global
                    if(desc.variable.esGlobal){
                        String ins = "addi "+Registro.loadStore.nombre+",$gp,"+desc.variable.offset;
                        String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                        descs.get(i).add(new AddressDescriptor(desc.variable,desc.variable));
                        //this.programAddressDescriptors.add(new AddressDescriptor(desc.variable,desc.variable)); // Agregamos al descriptor de variables de v su propia localidad de memoria
                        ArrayList<String> insSet = new ArrayList<String>();
                        insSet.add(ins);
                        insSet.add(ins2);
                        loads.set(i, insSet);
                        
                    }
                    else{
                        String ins = "addi "+Registro.loadStore.nombre+",$sp,"+desc.variable.offset;
                        String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                        descs.get(i).add(new AddressDescriptor(desc.variable,desc.variable));
                        //this.programAddressDescriptors.add(new AddressDescriptor(desc.variable,desc.variable)); // Agregamos al descriptor de variables de v su propia localidad de memoria
                        ArrayList<String> insSet = new ArrayList<String>();
                        insSet.add(ins);
                        insSet.add(ins2);    
                        loads.set(i, insSet);
                    }
                }
                else{
                    continue;
                }
                
            }
            //Si despues de revisar todos los descriptores de variable el registro no tuvo problemas, lo retornamos
            
            if(bestReg){
                if( re==null && re2 ==null && re3 == null){
                    return ri;
                }
                else if(re!=null && !ri.equals(re) && re2 == null && re3 ==null){
                    return ri;
                }
                else if(re!=null && !ri.equals(re) && re2 != null && !ri.equals(re2) && re3==null){
                    return ri;
                }
                else if(re!=null && !ri.equals(re) && re2 != null && !ri.equals(re2) && re3!=null && !ri.equals(re3)){
                    return ri;
                }                
            }
            i++;
        }
        
        //Si no se retorno ningun registro debemos encontrar el registro con el menor score
        int menor =-1;
        int lowestIndex  = 0;
        int exceptionIndex=-1;
        int exceptionIndex2=-1;
        int exceptionIndex3=-1;
        if(re!=null){
           exceptionIndex  = this.resgitrosTotales.indexOf(re);
        }
        if(re2!=null){
           exceptionIndex2  = this.resgitrosTotales.indexOf(re2);
        }
        if(re2!=null){
           exceptionIndex3  = this.resgitrosTotales.indexOf(re3);
        }
        for(int k = 0; k<scores.size();k++){
            int currentScore = scores.get(k);
            if(menor == -1 && k!= exceptionIndex &&k!=exceptionIndex2&&k!=exceptionIndex3){
                menor = currentScore;
                lowestIndex = k;
            }
            else if (currentScore<menor && k!=exceptionIndex &&k!=exceptionIndex2 &&k!=exceptionIndex3){
                menor = currentScore;
                lowestIndex = k;
                
            }
        }
        Registro returnReg = this.resgitrosTotales.get(lowestIndex);
        //Realizamos las instrucciones de carga necesarias
        for(String s: loads.get(lowestIndex)){
            gen(s);
        
        }
        for(AddressDescriptor d: descs.get(lowestIndex)){
            this.programAddressDescriptors.add(d);
        }
        return returnReg;
        
        
    }

    
    public Registro variableExisteEnRegistroUnico(Variable v){
        
       for(Registro r: this.resgitrosTotales){
           ArrayList<RegisterDescriptor> descs = this.obtenerDescriptoresRegistro(r);
           if(descs.size()==1){
               if(descs.get(0).variable.equals(v)){
                   return r;
               
               }
           }
       }
       return null;
            
    
    }
    
    /* Devuelve un arraylist con registros en este orden  
        - result[0] -- > arg1
        - restul[1] --> arg2
        - result[2] --> target
        
    Si alguno de los 3 no necesita registros, habra un null.
    */
    public ArrayList<Registro> getReg(Quad q){
        ArrayList<Registro> result = new ArrayList<Registro>();
        result.add(null); // arg1
        result.add(null); // arg2
        result.add(null); // arg3
        result.add(null); //Direccion arrayAccces (Solo para target)
        
        if(q.op.equals("ADD")||q.op.equals("SUB")||q.op.equals("MULT")||q.op.equals("DIV")||q.op.equals("MOD")||q.op.equals("AND")||q.op.equals("OR")||q.op.equals("RETURN")){
             /*  Obtenemos registros del argumento 1*/
            if(q.arg1 != null){
                if(q.arg1 instanceof Variable){
                    Variable ar1 = (Variable) q.arg1;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro ry = this.variableExisteEnRegistro(ar1);
                    if(ry!= null){
                        result.set(0, ry);
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(0, rVacio);
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(0, rVacio);
                        }
                    }

                }

                else if(q.arg1 instanceof Bool){
                        Bool ar1 = (Bool) q.arg1;
                        int valor = 0;
                        if((Boolean)ar1.valor==true){
                            valor = 0;
                        }
                        else{
                            valor = 1;
                        }
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }

                else if(q.arg1 instanceof Char){
                        Char ar1 = (Char) q.arg1;
                        int valor = 0;
                        valor = (char) ar1.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }   
                else if(q.arg1 instanceof Int){
                        Int ar1 = (Int) q.arg1;
                        int valor = 0;
                        valor = (int) ar1.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }
                else if( q.arg1 instanceof ArrayAccess){
                    ArrayAccess ar1 = (ArrayAccess)q.arg1;
                    /*  Para el array access asumimos siempre el caso en el que necesitaremos realizar un store entonces:
                        Solo verificamos si existe un registro vacio para cargar el valor de la temporal correspondiente al acceso del arreglo
                        Sino buscamos el mejor registro en base al numero de stores que hay realizar
                        Despues de esto debemos generar la instruccion para cargar el valor de la direccion en el temporal y asignarlo al mismo registro que obtuvimos anteriormente.
                    */
                    //Buscamos registro vacio
                    Registro rVacio = this.buscarRegistroVacio();
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rVacio == null){
                        rVacio = obtenerMejorRegistro(q,null,null,null);
                        
                        

                    }
                    
                    /* Buscamos registro con la temporal de la direccion del arreglo*/
                    Registro rt = this.variableExisteEnRegistro(ar1.index);
                    //Si es null buscamos uno vacio
                    if(rt==null){
                        rt = this.buscarRegistroVacio(rVacio);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);                        
                    }
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rt == null){
                        rt = obtenerMejorRegistro(q,rVacio,null,null);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);
                  
                    } 

                    String ins="";
                    if(ar1.array.esGlobal){
                        ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                    }
                    else{
                        ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                    }
                    
                    gen(ins);
                    ins = "lw "+rVacio.nombre+", "+" ("+rt.nombre+")";
                    gen(ins);
                    //Vaciamos descriptores de ry
                    this.vaciarDescriptoresRegistro(rVacio);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);                                        
                    this.vaciarDescriptoresRegistro(rt);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rt);
                    result.set(0, rVacio);

                }        
            }
            if(q.arg2 != null){
                /*  Obtenemos registros del argumento 2*/
                if(q.arg2 instanceof Variable){
                    Variable ar2 = (Variable) q.arg2;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro rz = this.variableExisteEnRegistro(ar2);
                    if(rz!= null && !rz.equals(result.get(0))){
                        result.set(1, rz);
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio(result.get(0));
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio= obtenerMejorRegistro(q,result.get(0),null,null);
                            result.set(1, rVacio);
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(1, rVacio);
                        }
                    }

                }
                else if(q.arg2 instanceof Bool){
                        Bool ar2 = (Bool) q.arg2;
                        int valor = 0;
                        if((Boolean)ar2.valor==true){
                            valor = 0;
                        }
                        else{
                            valor = 1;
                        }
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate2.nombre+", "+valor;
                        gen(ins);
                        result.set(1, Registro.inmediate2);
                }

                else if(q.arg2 instanceof Char){
                        Char ar2 = (Char) q.arg2;
                        int valor = 0;
                        valor = (char) ar2.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate2.nombre+", "+valor;
                        gen(ins);
                        result.set(1, Registro.inmediate2);
                }   
                else if(q.arg2 instanceof Int){
                        Int ar2 = (Int) q.arg2;
                        int valor = 0;
                        valor = (int) ar2.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate2.nombre+", "+valor;
                        gen(ins);
                        result.set(1, Registro.inmediate2);
                }
                else if( q.arg2 instanceof ArrayAccess){
                    ArrayAccess ar2 = (ArrayAccess)q.arg2;
                    /*  Para el array access asumimos siempre el caso en el que necesitaremos realizar un store entonces:
                        Solo verificamos si existe un registro vacio para cargar el valor de la temporal correspondiente al acceso del arreglo
                        Sino buscamos el mejor registro en base al numero de stores que hay realizar
                        Despues de esto debemos generar la instruccion para cargar el valor de la direccion en el temporal y asignarlo al mismo registro que obtuvimos anteriormente.
                    */
                    //Buscamos registro vacio
                    Registro rVacio = this.buscarRegistroVacio(result.get(0));
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rVacio == null){
                        rVacio = obtenerMejorRegistro(q,result.get(0),null,null);
                        
                        

                    }
                    /* Buscamos registro con la temporal de la direccion del arreglo*/
                    Registro rt = this.variableExisteEnRegistro(ar2.index);
                    //Si es null buscamos uno vacio
                    if(rt==null){
                        rt = this.buscarRegistroVacio(rVacio,result.get(0));
                        String s1 ="";
                        if(ar2.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar2.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar2.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);                        
                    }
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rt == null){
                        rt = obtenerMejorRegistro(q,rVacio,result.get(0),null);
                        String s1 ="";
                        if(ar2.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar2.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar2.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);
                  
                    } 
                    
                    String ins="";
                    if(ar2.array.esGlobal){
                        ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                    }
                    else{
                        ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                    }
                    
                    gen(ins);
                    ins = "lw "+rVacio.nombre+", "+" ("+rt.nombre+")";
                    gen(ins);
                    //Vaciamos descriptores de rz
                    this.vaciarDescriptoresRegistro(rVacio);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);
                    this.vaciarDescriptoresRegistro(rt);
                                        
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rt);
                    result.set(1, rVacio);

                }            

            }
            if(q.target != null){
                /*  Obtenemos registros del espacio de destino*/
                if(q.target instanceof Variable){
                    Variable tar = (Variable) q.target;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro ry = this.variableExisteEnRegistroUnico(tar);
                    if(ry!= null){
                        result.set(2, ry);
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(2, rVacio);
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(2, rVacio);
                        }
                    }

                }
                else if( q.target instanceof ArrayAccess){
                        ArrayAccess tar = (ArrayAccess) q.target;
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            
                        }
                        result.set(2, rVacio);
                        
                        Registro rt = this.variableExisteEnRegistro(tar.index);
                        //Si es null buscamos uno vacio
                        if(rt==null){
                            rt = this.buscarRegistroVacio(result.get(0),result.get(1),rVacio);
                            String s1 ="";
                            if(tar.index.esGlobal){
                                 s1 = "add "+Registro.loadStore.nombre+", $gp, "+tar.index.offset;

                            }
                            else{
                                s1 = "add "+Registro.loadStore.nombre+", $sp, "+tar.index.offset;

                            }
                            gen(s1);
                            String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                            gen(s);                            
                        }
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rt == null){
                            rt = obtenerMejorRegistro(q,result.get(0),result.get(1),rVacio);
                            String s1 ="";
                            if(tar.index.esGlobal){
                                 s1 = "add "+Registro.loadStore.nombre+", $gp, "+tar.index.offset;

                            }
                            else{
                                s1 = "add "+Registro.loadStore.nombre+", $sp, "+tar.index.offset;

                            }
                            gen(s1);
                            String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                            gen(s);

                        } 
                    
                        String ins;
                        if(tar.array.esGlobal){
                            ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                        }
                        else{
                            ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                        }   
                        gen(ins);
                        
                        result.set(3, rt);
                        this.vaciarDescriptoresRegistro(rt);
                        this.removerTodosLosDescriptoresDeVariableQueContengan(rt);

                }                 

            }
            else{

            }

        }
        //Operadores Unarios
        else if (q.op.equals("MINUS")||q.op.equals("NOT")||q.op.equals("(INT)")||q.op.equals("(CHAR)")){
             /*  Obtenemos registros del argumento 1*/
            if(q.arg1 != null){
                if(q.arg1 instanceof Variable){
                    Variable ar1 = (Variable) q.arg1;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro ry = this.variableExisteEnRegistro(ar1);
                    if(ry!= null){
                        result.set(0, ry);
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(0, rVacio);
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(0, rVacio);
                        }
                    }

                }
                else if(q.arg1 instanceof Bool){
                        Bool ar1 = (Bool) q.arg1;
                        int valor = 0;
                        if((Boolean)ar1.valor==true){
                            valor = 0;
                        }
                        else{
                            valor = 1;
                        }
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }

                else if(q.arg1 instanceof Char){
                        Char ar1 = (Char) q.arg1;
                        int valor = 0;
                        valor = (char) ar1.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }   
                else if(q.arg1 instanceof Int){
                        Int ar1 = (Int) q.arg1;
                        int valor = 0;
                        valor = (int) ar1.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }
                else if( q.arg1 instanceof ArrayAccess){
                    ArrayAccess ar1 = (ArrayAccess)q.arg1;
                    /*  Para el array access asumimos siempre el caso en el que necesitaremos realizar un store entonces:
                        Solo verificamos si existe un registro vacio para cargar el valor de la temporal correspondiente al acceso del arreglo
                        Sino buscamos el mejor registro en base al numero de stores que hay realizar
                        Despues de esto debemos generar la instruccion para cargar el valor de la direccion en el temporal y asignarlo al mismo registro que obtuvimos anteriormente.
                    */
                    //Buscamos registro vacio
                    Registro rVacio = this.buscarRegistroVacio();
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rVacio == null){
                        rVacio = obtenerMejorRegistro(q,null,null,null);
                        
                        

                    }
                    
                    /* Buscamos registro con la temporal de la direccion del arreglo*/
                    Registro rt = this.variableExisteEnRegistro(ar1.index);
                    //Si es null buscamos uno vacio
                    if(rt==null){
                        rt = this.buscarRegistroVacio(rVacio);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);                        
                    }
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rt == null){
                        rt = obtenerMejorRegistro(q,rVacio,null,null);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);
                  
                    } 

                    String ins="";
                    if(ar1.array.esGlobal){
                        ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                    }
                    else{
                        ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                    }
                    
                    gen(ins);
                    ins = "lw "+rVacio.nombre+", "+" ("+rt.nombre+")";
                    gen(ins);
                    //Vaciamos descriptores de ry
                    this.vaciarDescriptoresRegistro(rVacio);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);                                        
                    this.vaciarDescriptoresRegistro(rt);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rt);  
                    result.set(0, rVacio);

                }        
            }
            
            if(q.target != null){
                /*  Obtenemos registros del espacio de destino*/
                if(q.target instanceof Variable){
                    Variable tar = (Variable) q.target;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro ry = this.variableExisteEnRegistroUnico(tar);
                    if(ry!= null){
                        result.set(2, ry);
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(2, rVacio);
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(2, rVacio);
                        }
                    }

                }
                else if( q.target instanceof ArrayAccess){
                        ArrayAccess tar = (ArrayAccess) q.target;
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            
                        }
                        result.set(2, rVacio);
                        
                        Registro rt = this.variableExisteEnRegistro(tar.index);
                        //Si es null buscamos uno vacio
                        if(rt==null){
                            rt = this.buscarRegistroVacio(result.get(0),result.get(1),rVacio);
                            String s1 ="";
                            if(tar.index.esGlobal){
                                 s1 = "add "+Registro.loadStore.nombre+", $gp, "+tar.index.offset;

                            }
                            else{
                                s1 = "add "+Registro.loadStore.nombre+", $sp, "+tar.index.offset;

                            }
                            gen(s1);
                            String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                            gen(s);                            
                        }
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rt == null){
                            rt = obtenerMejorRegistro(q,result.get(0),result.get(1),rVacio);
                            String s1 ="";
                            if(tar.index.esGlobal){
                                 s1 = "add "+Registro.loadStore.nombre+", $gp, "+tar.index.offset;

                            }
                            else{
                                s1 = "add "+Registro.loadStore.nombre+", $sp, "+tar.index.offset;

                            }
                            gen(s1);
                            String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                            gen(s);

                        } 
                    
                        String ins;
                        if(tar.array.esGlobal){
                            ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                        }
                        else{
                            ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                        }   
                        gen(ins);
                        
                        result.set(3, rt);
                        this.vaciarDescriptoresRegistro(rt);

                }                 

            }            
       
        
        }
        //Comparaciones  
       else if(q.op.equals("LT")||q.op.equals("LE")||q.op.equals("EQ")||q.op.equals("GE")||q.op.equals("GT")||q.op.equals("NE")){
            /*  Obtenemos registros del argumento 1*/
            if(q.arg1 != null){
                if(q.arg1 instanceof Variable){
                    Variable ar1 = (Variable) q.arg1;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro ry = this.variableExisteEnRegistro(ar1);
                    if(ry!= null){
                        result.set(0, ry);
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(0, rVacio);
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(0, rVacio);
                        }
                    }

                }

                else if(q.arg1 instanceof Bool){
                        Bool ar1 = (Bool) q.arg1;
                        int valor = 0;
                        if((Boolean)ar1.valor==true){
                            valor = 0;
                        }
                        else{
                            valor = 1;
                        }
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }

                else if(q.arg1 instanceof Char){
                        Char ar1 = (Char) q.arg1;
                        int valor = 0;
                        valor = (char) ar1.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }   
                else if(q.arg1 instanceof Int){
                        Int ar1 = (Int) q.arg1;
                        int valor = 0;
                        valor = (int) ar1.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }
                else if( q.arg1 instanceof ArrayAccess){
                    ArrayAccess ar1 = (ArrayAccess)q.arg1;
                    /*  Para el array access asumimos siempre el caso en el que necesitaremos realizar un store entonces:
                        Solo verificamos si existe un registro vacio para cargar el valor de la temporal correspondiente al acceso del arreglo
                        Sino buscamos el mejor registro en base al numero de stores que hay realizar
                        Despues de esto debemos generar la instruccion para cargar el valor de la direccion en el temporal y asignarlo al mismo registro que obtuvimos anteriormente.
                    */
                    //Buscamos registro vacio
                    Registro rVacio = this.buscarRegistroVacio();
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rVacio == null){
                        rVacio = obtenerMejorRegistro(q,null,null,null);
                        
                        

                    }
                    
                    /* Buscamos registro con la temporal de la direccion del arreglo*/
                    Registro rt = this.variableExisteEnRegistro(ar1.index);
                    //Si es null buscamos uno vacio
                    if(rt==null){
                        rt = this.buscarRegistroVacio(rVacio);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);                        
                    }
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rt == null){
                        rt = obtenerMejorRegistro(q,rVacio,null,null);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);
                  
                    } 

                    String ins="";
                    if(ar1.array.esGlobal){
                        ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                    }
                    else{
                        ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                    }
                    
                    gen(ins);
                    ins = "lw "+rVacio.nombre+", "+" ("+rt.nombre+")";
                    gen(ins);
                    //Vaciamos descriptores de ry
                    this.vaciarDescriptoresRegistro(rVacio);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);                                        
                    this.vaciarDescriptoresRegistro(rt);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rt);  
                    result.set(0, rVacio);

                }        
            }
            if(q.arg2 != null){
                /*  Obtenemos registros del argumento 2*/
                if(q.arg2 instanceof Variable){
                    Variable ar2 = (Variable) q.arg2;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro rz = this.variableExisteEnRegistro(ar2);
                    if(rz!= null && !rz.equals(result.get(0))){
                        result.set(1, rz);
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio(result.get(0));
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio= obtenerMejorRegistro(q,result.get(0),null,null);
                            result.set(1, rVacio);
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(1, rVacio);
                        }
                    }

                }
                else if(q.arg2 instanceof Bool){
                        Bool ar2 = (Bool) q.arg2;
                        int valor = 0;
                        if((Boolean)ar2.valor==true){
                            valor = 0;
                        }
                        else{
                            valor = 1;
                        }
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate2.nombre+", "+valor;
                        gen(ins);
                        result.set(1, Registro.inmediate2);
                }

                else if(q.arg2 instanceof Char){
                        Char ar2 = (Char) q.arg2;
                        int valor = 0;
                        valor = (char) ar2.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate2.nombre+", "+valor;
                        gen(ins);
                        result.set(1, Registro.inmediate2);
                }   
                else if(q.arg2 instanceof Int){
                        Int ar2 = (Int) q.arg2;
                        int valor = 0;
                        valor = (int) ar2.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate2.nombre+", "+valor;
                        gen(ins);
                        result.set(1, Registro.inmediate2);
                }
                else if( q.arg2 instanceof ArrayAccess){
                    ArrayAccess ar2 = (ArrayAccess)q.arg2;
                    /*  Para el array access asumimos siempre el caso en el que necesitaremos realizar un store entonces:
                        Solo verificamos si existe un registro vacio para cargar el valor de la temporal correspondiente al acceso del arreglo
                        Sino buscamos el mejor registro en base al numero de stores que hay realizar
                        Despues de esto debemos generar la instruccion para cargar el valor de la direccion en el temporal y asignarlo al mismo registro que obtuvimos anteriormente.
                    */
                    //Buscamos registro vacio
                    Registro rVacio = this.buscarRegistroVacio(result.get(0));
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rVacio == null){
                        rVacio = obtenerMejorRegistro(q,result.get(0),null,null);
                        
                        

                    }
                    /* Buscamos registro con la temporal de la direccion del arreglo*/
                    Registro rt = this.variableExisteEnRegistro(ar2.index);
                    //Si es null buscamos uno vacio
                    if(rt==null){
                        rt = this.buscarRegistroVacio(rVacio,result.get(0));
                        String s1 ="";
                        if(ar2.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar2.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar2.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);                        
                    }
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rt == null){
                        rt = obtenerMejorRegistro(q,rVacio,result.get(0),null);
                        String s1 ="";
                        if(ar2.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar2.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar2.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);
                  
                    } 
                    
                    String ins="";
                    if(ar2.array.esGlobal){
                        ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                    }
                    else{
                        ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                    }
                    
                    gen(ins);
                    ins = "lw "+rVacio.nombre+", "+" ("+rt.nombre+")";
                    gen(ins);
                    //Vaciamos descriptores de rz
                    this.vaciarDescriptoresRegistro(rVacio);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);  
                    this.vaciarDescriptoresRegistro(rt);                 
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rt);  
                    result.set(1, rVacio);

                }            
            }           
        }
        //Asignaciones
        else if(q.op.equals("ASSIGN")){
         /*  Obtenemos registros del argumento 1*/
            if(q.arg1 != null){
                if(q.arg1 instanceof Variable){
                    Variable ar1 = (Variable) q.arg1;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro ry = this.variableExisteEnRegistro(ar1);
                    if(ry!= null){
                        result.set(0, ry);
                        //Como es asignacion siempre hacemos rx = ry
                         result.set(2, ry);                          
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(0, rVacio);
                            //Como es asignacion siempre hacemos rx = ry
                             result.set(2, rVacio);                              
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(0, rVacio);
                            //Como es asignacion siempre hacemos rx = ry
                            result.set(2, rVacio);                              
                        }
                    }
                }
                else if(q.arg1 instanceof Bool){
                        Bool ar1 = (Bool) q.arg1;
                        int valor = 0;
                        if((Boolean)ar1.valor==true){
                            valor = 0;
                        }
                        else{
                            valor = 1;
                        }
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(0, rVacio);
                            //Como es asignacion siempre hacemos rx = ry
                            result.set(2, rVacio);                              
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(0, rVacio);
                            //Como es asignacion siempre hacemos rx = ry
                            result.set(2, rVacio);                              
                        }                        
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+rVacio.nombre+", "+valor;
                        //Seteamos el descriptor de registro para que soloc ontenga el valor de la variable
                        //Eliminamos descriptores de registros de Ry
                         vaciarDescriptoresRegistro(rVacio);
                        //a.i eliminamos todos los descriptores que tengan a ry como registro
                         this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);                            
                        //Agregamos la variable ar1 al descriptor 
                         if(q.target instanceof Variable){
                            this.programRegisterDescriptors.add(new RegisterDescriptor(rVacio,(Variable)q.target)); 
                         }
                                                
                        gen(ins);                         
                }
                else if(q.arg1 instanceof Char){
                        Char ar1 = (Char) q.arg1;
                        int valor = 0;
                        valor = (char) ar1.valor;
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(0, rVacio);
                            //Como es asignacion siempre hacemos rx = ry
                             result.set(2, rVacio);                              
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(0, rVacio);
                            //Como es asignacion siempre hacemos rx = ry
                            result.set(2, rVacio);                              
                        }                        
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+rVacio.nombre+", "+valor;
                        //Seteamos el descriptor de registro para que soloc ontenga el valor de la variable
                        //Eliminamos descriptores de registros de Ry
                         vaciarDescriptoresRegistro(rVacio);
                        //a.i eliminamos todos los descriptores que tengan a ry como registro
                         this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);                              
                        //Agregamos la variable ar1 al descriptor 
                         if(q.target instanceof Variable){
                            this.programRegisterDescriptors.add(new RegisterDescriptor(rVacio,(Variable)q.target)); 
                         }
                                                   
                        gen(ins);                       
                }   
                else if(q.arg1 instanceof Int){
                        Int ar1 = (Int) q.arg1;
                        int valor = 0;
                        valor = (int) ar1.valor;
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(0, rVacio);
                            //Como es asignacion siempre hacemos rx = ry
                            result.set(2, rVacio);                              
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(0, rVacio);
                            //Como es asignacion siempre hacemos rx = ry
                            result.set(2, rVacio);                              
                        }                        
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+rVacio.nombre+", "+valor;
                        //Seteamos el descriptor de registro para que soloc ontenga el valor de la variable
                        //Eliminamos descriptores de registros de Ry
                         vaciarDescriptoresRegistro(rVacio);
                        //a.i eliminamos todos los descriptores que tengan a ry como registro
                         this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);                              
                        //Agregamos la variable ar1 al descriptor 
                         if(q.target instanceof Variable){
                            this.programRegisterDescriptors.add(new RegisterDescriptor(rVacio,(Variable)q.target)); 
                         }
                                                   
                        gen(ins);                          
                }
                else if( q.arg1 instanceof ArrayAccess){
                    ArrayAccess ar1 = (ArrayAccess)q.arg1;
                    /*  Para el array access asumimos siempre el caso en el que necesitaremos realizar un store entonces:
                        Solo verificamos si existe un registro vacio para cargar el valor de la temporal correspondiente al acceso del arreglo
                        Sino buscamos el mejor registro en base al numero de stores que hay realizar
                        Despues de esto debemos generar la instruccion para cargar el valor de la direccion en el temporal y asignarlo al mismo registro que obtuvimos anteriormente.
                    */
                    //Buscamos registro vacio
                    Registro rVacio = this.buscarRegistroVacio();
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rVacio == null){
                        rVacio = obtenerMejorRegistro(q,null,null,null);
                        
                        

                    }
                    /* Buscamos registro con la temporal de la direccion del arreglo*/
                    Registro rt = this.variableExisteEnRegistro(ar1.index);
                    //Si es null buscamos uno vacio
                    if(rt==null){
                        rt = this.buscarRegistroVacio(rVacio,result.get(0));
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);                        
                    }
                    if(rt == null){
                        rt = obtenerMejorRegistro(q,rVacio,null,null);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);                        
                    }
                    String ins="";
                    if(ar1.array.esGlobal){
                        ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                    }
                    else{
                        ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                    }
                    
                    gen(ins);
                    ins = "lw "+rVacio.nombre+", "+" ("+rt.nombre+")";
                    gen(ins);
                    //Vaciamos descriptores de ry
                    this.vaciarDescriptoresRegistro(rVacio);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);  
                    this.vaciarDescriptoresRegistro(rt);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rt);  
                    result.set(0, rVacio);
                    //Como es asignacion siempre hacemos rx = ry
                    result.set(2, rVacio);     
                } 
                if(q.target instanceof ArrayAccess){
                    ArrayAccess tar = (ArrayAccess) q.target;
                    
                    //En este caso ya tenemos el registro con el valor, solo neceistamos obtener el registro con la direccion para realiza el store al traducir la instruccion
                    
                    Registro rt = this.variableExisteEnRegistro(tar.index);
                    //Si es null buscamos uno vacio
                    if(rt==null){
                        rt = this.buscarRegistroVacio(result.get(0));
                        String s1 ="";
                        if(tar.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+tar.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+tar.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);                        
                    }
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rt == null){
                        rt = obtenerMejorRegistro(q,result.get(0),null,null);
                        String s1 ="";
                        if(tar.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+tar.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+tar.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);
                  
                    } 
                    
                    
                    
                    
                    String ins;
                    if(tar.array.esGlobal){
                        ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                    }
                    else{
                        ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                    }   
                    gen(ins);
                    result.set(3, rt);
                    this.vaciarDescriptoresRegistro(rt); 
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rt);
                
                
                }
            }            
        }
        else if (q.op.equals("CALL")){
            if(q.target != null){
                /*  Obtenemos registros del espacio de destino*/
                if(q.target instanceof Variable){
                    Variable tar = (Variable) q.target;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro ry = this.variableExisteEnRegistroUnico(tar);
                    if(ry!= null){
                        result.set(2, ry);
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(2, rVacio);
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(2, rVacio);
                        }
                    }

                }
                else if( q.target instanceof ArrayAccess){
                        ArrayAccess tar = (ArrayAccess) q.target;
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            
                        }
                        result.set(2, rVacio);
                        
                        Registro rt = this.variableExisteEnRegistro(tar.index);
                        //Si es null buscamos uno vacio
                        if(rt==null){
                            rt = this.buscarRegistroVacio(result.get(0),result.get(1),rVacio);
                            String s1 ="";
                            if(tar.index.esGlobal){
                                 s1 = "add "+Registro.loadStore.nombre+", $gp, "+tar.index.offset;

                            }
                            else{
                                s1 = "add "+Registro.loadStore.nombre+", $sp, "+tar.index.offset;

                            }
                            gen(s1);
                            String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                            gen(s);                            
                            }
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rt == null){
                            rt = obtenerMejorRegistro(q,result.get(0),result.get(1),rVacio);
                            String s1 ="";
                            if(tar.index.esGlobal){
                                 s1 = "add "+Registro.loadStore.nombre+", $gp, "+tar.index.offset;

                            }
                            else{
                                s1 = "add "+Registro.loadStore.nombre+", $sp, "+tar.index.offset;

                            }
                            gen(s1);
                            String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                            gen(s);

                        } 
                    
                        String ins;
                        if(tar.array.esGlobal){
                            ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                        }
                        else{
                            ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                        }   
                        gen(ins);
                        
                        result.set(3, rt);
                        this.vaciarDescriptoresRegistro(rt);
                        this.removerTodosLosDescriptoresDeVariableQueContengan(rt);  

                }                 

            }        
        
        
        }
        //Parametros (Pueden no ser necesarios)
        else if(q.op.equals("PARAM")){
            /*  Obtenemos registros del argumento 1*/
            if(q.arg1 != null){
                if(q.arg1 instanceof Variable){
                    Variable ar1 = (Variable) q.arg1;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro ry = this.variableExisteEnRegistro(ar1);
                    if(ry!= null){
                        result.set(0, ry);
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(0, rVacio);
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(0, rVacio);
                        }
                    }

                }

                else if(q.arg1 instanceof Bool){
                        Bool ar1 = (Bool) q.arg1;
                        int valor = 0;
                        if((Boolean)ar1.valor==true){
                            valor = 0;
                        }
                        else{
                            valor = 1;
                        }
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }

                else if(q.arg1 instanceof Char){
                        Char ar1 = (Char) q.arg1;
                        int valor = 0;
                        valor = (char) ar1.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }   
                else if(q.arg1 instanceof Int){
                        Int ar1 = (Int) q.arg1;
                        int valor = 0;
                        valor = (int) ar1.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }
                else if( q.arg1 instanceof ArrayAccess){
                    ArrayAccess ar1 = (ArrayAccess)q.arg1;
                    /*  Para el array access asumimos siempre el caso en el que necesitaremos realizar un store entonces:
                        Solo verificamos si existe un registro vacio para cargar el valor de la temporal correspondiente al acceso del arreglo
                        Sino buscamos el mejor registro en base al numero de stores que hay realizar
                        Despues de esto debemos generar la instruccion para cargar el valor de la direccion en el temporal y asignarlo al mismo registro que obtuvimos anteriormente.
                    */
                    //Buscamos registro vacio
                    Registro rVacio = this.buscarRegistroVacio();
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rVacio == null){
                        rVacio = obtenerMejorRegistro(q,null,null,null);
                        
                        

                    }
                    
                    /* Buscamos registro con la temporal de la direccion del arreglo*/
                    Registro rt = this.variableExisteEnRegistro(ar1.index);
                    //Si es null buscamos uno vacio
                    if(rt==null){
                        rt = this.buscarRegistroVacio(rVacio);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);                        
                    }
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rt == null){
                        rt = obtenerMejorRegistro(q,rVacio,null,null);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);
                  
                    } 

                    String ins="";
                    if(ar1.array.esGlobal){
                        ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                    }
                    else{
                        ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                    }
                    
                    gen(ins);
                    ins = "lw "+rVacio.nombre+", "+" ("+rt.nombre+")";
                    gen(ins);
                    //Vaciamos descriptores de ry
                    this.vaciarDescriptoresRegistro(rVacio);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);                                        
                    this.vaciarDescriptoresRegistro(rt);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rt);  
                    result.set(0, rVacio);

                }        
            }        
        }
        //Retorno de funcion
        else if(q.op.equals("POP_RESULT")){
            /*  Obtenemos registros del argumento 1*/
            if(q.arg1 != null){
                if(q.arg1 instanceof Variable){
                    Variable ar1 = (Variable) q.arg1;
                    // 1. Si la variable existe en un registro obtenemos el registro
                    Registro ry = this.variableExisteEnRegistro(ar1);
                    if(ry!= null){
                        result.set(0, ry);
                    
                    }
                    //2. Si no existe registro buscamos uno vacio
                    else{
                        Registro rVacio = this.buscarRegistroVacio();
                        //3. Si no hay vacios, debemos guardar en memoria
                        if(rVacio == null){
                            rVacio = obtenerMejorRegistro(q,null,null,null);
                            result.set(0, rVacio);
                        
                        }
                        //2.Si hay vacio lo seteamos al resultado
                        else{
                            result.set(0, rVacio);
                        }
                    }

                }

                else if(q.arg1 instanceof Bool){
                        Bool ar1 = (Bool) q.arg1;
                        int valor = 0;
                        if((Boolean)ar1.valor==true){
                            valor = 0;
                        }
                        else{
                            valor = 1;
                        }
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }

                else if(q.arg1 instanceof Char){
                        Char ar1 = (Char) q.arg1;
                        int valor = 0;
                        valor = (char) ar1.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }   
                else if(q.arg1 instanceof Int){
                        Int ar1 = (Int) q.arg1;
                        int valor = 0;
                        valor = (int) ar1.valor;
                        //Generamos instruccion de carga de valor inmediato
                        String ins = "li "+Registro.inmediate1.nombre+", "+valor;
                        gen(ins);
                        result.set(0, Registro.inmediate1);
                }
                else if( q.arg1 instanceof ArrayAccess){
                    ArrayAccess ar1 = (ArrayAccess)q.arg1;
                    /*  Para el array access asumimos siempre el caso en el que necesitaremos realizar un store entonces:
                        Solo verificamos si existe un registro vacio para cargar el valor de la temporal correspondiente al acceso del arreglo
                        Sino buscamos el mejor registro en base al numero de stores que hay realizar
                        Despues de esto debemos generar la instruccion para cargar el valor de la direccion en el temporal y asignarlo al mismo registro que obtuvimos anteriormente.
                    */
                    //Buscamos registro vacio
                    Registro rVacio = this.buscarRegistroVacio();
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rVacio == null){
                        rVacio = obtenerMejorRegistro(q,null,null,null);
                        
                        

                    }
                    
                    /* Buscamos registro con la temporal de la direccion del arreglo*/
                    Registro rt = this.variableExisteEnRegistro(ar1.index);
                    //Si es null buscamos uno vacio
                    if(rt==null){
                        rt = this.buscarRegistroVacio(rVacio);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);
                    }
                    //3. Si no hay vacios, debemos guardar en memoria
                    if(rt == null){
                        rt = obtenerMejorRegistro(q,rVacio,null,null);
                        String s1 ="";
                        if(ar1.index.esGlobal){
                             s1 = "add "+Registro.loadStore.nombre+", $gp, "+ar1.index.offset;
                             
                        }
                        else{
                            s1 = "add "+Registro.loadStore.nombre+", $sp, "+ar1.index.offset;
                        
                        }
                        gen(s1);
                        String s = "lw "+rt.nombre+", ("+Registro.loadStore.nombre+")";
                        gen(s);
                  
                    } 

                    String ins="";
                    if(ar1.array.esGlobal){
                        ins = "add "+rt.nombre+" ,"+"$gp, "+rt.nombre;
                    }
                    else{
                        ins = "add "+rt.nombre+" ,"+"$sp, "+rt.nombre;
                    }
                    
                    gen(ins);
                    ins = "lw "+rVacio.nombre+", "+" ("+rt.nombre+")";
                    gen(ins);
                    //Vaciamos descriptores de ry
                    this.vaciarDescriptoresRegistro(rVacio);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rVacio);                                        
                    this.vaciarDescriptoresRegistro(rt);
                    this.removerTodosLosDescriptoresDeVariableQueContengan(rt);  
                    result.set(0, rVacio);

                }        
            }        
        
        }
        
        return result;
    }
    
    public void vaciarDescriptoresRegistro(Registro r){
        ArrayList<RegisterDescriptor> descEliminar = new ArrayList<RegisterDescriptor>();
        for(RegisterDescriptor desc: this.programRegisterDescriptors){
            if(desc.registro.equals(r)){
                descEliminar.add(desc);
            
            }
        
        }
        for(RegisterDescriptor d: descEliminar){
            this.programRegisterDescriptors.remove(d);
        
        }
        
        
    }
    public void vaciarDescriptoresVariable(Variable v){
        ArrayList<AddressDescriptor> descEliminar = new ArrayList<AddressDescriptor>();
        for(AddressDescriptor a: this.programAddressDescriptors){
            if(a.variable.equals(v)){
                descEliminar.add(a);
            
            }        
        
        }
        for(AddressDescriptor d: descEliminar){
            this.programAddressDescriptors.remove(d);
        
        }
        
            
    
    }
    //Elimina todos los descriptores de registro de rx a excepcion de la variable enviada como parametro
    public void removerTodosLosDescriptoresDeVariableQueContengan(Registro r){
        ArrayList<AddressDescriptor> remove = new ArrayList<AddressDescriptor>();
        int k =0;
        for(AddressDescriptor d: this.programAddressDescriptors){
            
            if(d.registro!=null && d.registro.equals(r)){
               remove.add(d);
            }
            
            k++;
        }
        for(AddressDescriptor s: remove){
            this.programAddressDescriptors.remove(s);
        }
    
    
    }    
    //Elimina todos los descriptores de registro de rx a excepcion de la variable enviada como parametro
    public void removerTodosLosDescriptoresDeVariableQueContengan(Registro r, Variable exepcion){
        ArrayList<AddressDescriptor> remove = new ArrayList<AddressDescriptor>();
        int k =0;
        for(AddressDescriptor d: this.programAddressDescriptors){
            
            if(d.variable.equals(exepcion)||(d.memoria!=null && d.memoria.equals(exepcion))){
                continue;
            
            }
            else{
                if(d.registro!=null && d.registro.equals(r)){
                   remove.add(d);
                }
            }
            k++;
        }
        for(AddressDescriptor s: remove){
            this.programAddressDescriptors.remove(s);
        }
    
    
    }
   public void vaciarDescriptoresRegistroConVariable(Variable v){
       ArrayList<RegisterDescriptor> remove = new ArrayList<RegisterDescriptor>();
       for(RegisterDescriptor rs: this.programRegisterDescriptors){
           if(rs.variable.equals(v)){
               remove.add(rs);
           }
       
       
       }
       for(RegisterDescriptor rs: remove){
           this.programRegisterDescriptors.remove(rs);
       }
   
   }
   public boolean variableEnMemoria(Variable v){
       for(AddressDescriptor desc: this.programAddressDescriptors){
       if(desc.variable.equals(v)&& desc.memoria!=null && desc.memoria.equals(v)){
           return true;
       
       }
       
       }
       return false;
   
   }
   
   public void removerDescriptoresVariableTodosLosRegistros(){
       for(Registro r: this.resgitrosTotales){
           this.removerTodosLosDescriptoresDeVariableQueContengan(r);
       
       }
   
   }
    
    public void traducirBloque(BasicBlock block){
        this.programRegisterDescriptors.clear();
        this.removerDescriptoresVariableTodosLosRegistros();
        boolean param = false;
        int paramCount =1;
        for(Quad I: block.instructions){
            ArrayList<Registro> registros = getReg(I);
            Registro Ry = registros.get(0);
            Registro Rz = registros.get(1);
            Registro Rx = registros.get(2);
            Registro Rt = registros.get(3);
            
            boolean storesGen= false;
            boolean targetArray = false;
            int test = block.instructions.indexOf(I);
                /*---------------------TEST----*/
            if((block.instructions.indexOf(I)== block.instructions.size()-1 &&param ==false && I.op.equals("GOTO")|| I.op.equals("CALL")||I.op.equals("LT")||
                    I.op.equals("LE")||I.op.equals("EQ")||I.op.equals("GE")||I.op.equals("GT")||I.op.equals("NE")|| I.op.equals("PARAM")||I.op.equals("HALT")||I.op.equals("POP_LOCALS")) ){
                
                if(I.op.equals("PARAM")||I.op.equals("POP_LOCALS")){
                    param = true;
                }
                storesGen = true;
                
               //Al finalizar el bloque verificamos cada descriptor de variable, y si no esta en memoria generamos instruccion store para guardarlo
               ArrayList<AddressDescriptor> tempDescs = new ArrayList<AddressDescriptor>();
               for(AddressDescriptor desc: this.programAddressDescriptors){
                   boolean enMemoria = this.variableEnMemoria(desc.variable);
                   if(!enMemoria){
                       if(desc.variable.esGlobal){
                           String ins = "addi "+Registro.loadStore.nombre+",$gp,"+desc.variable.offset;
                           gen(ins);
                           String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                           gen(ins2);
                           tempDescs.add(new AddressDescriptor(desc.variable,desc.variable));
                       }
                       else{
                           String ins = "addi "+Registro.loadStore.nombre+",$sp,"+desc.variable.offset;
                           gen(ins);
                           String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                           gen(ins2);
                           tempDescs.add(new AddressDescriptor(desc.variable,desc.variable));                


                       }
                   }
               }
               this.programAddressDescriptors.addAll(tempDescs);                

            }
            /*---------------------------------------------------------------------*/       
            
            if(I.op.equals("ADD")||I.op.equals("SUB")||I.op.equals("MULT")||I.op.equals("DIV")||I.op.equals("MOD")||I.op.equals("AND")||I.op.equals("OR")){
                //Verificamos si y (arg1 ) esta en ry
                if(I.arg1 instanceof Variable){
                    Variable ar1 = (Variable) I.arg1;
                    boolean contieneVariable = this.registroContieneVariable(ar1, Ry);
                    //Si no contiene la variable cargamos la instruccon
                    if(!contieneVariable){
                        if(ar1.esGlobal){
                            String i = "addi "+Registro.loadStore.nombre+", "+" $gp,"+ar1.offset;
                            gen(i);
                            String i2 = "lw "+Ry.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);
                            /* a. Cambiar descriptor de registro para que incluya solo y*/
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Ry);
                            //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,ar1));
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Ry, ar1);
                             
                             // b. Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar1,Ry));
                        }
                        else{
                            String i = "addi "+Registro.loadStore.nombre+", "+" $sp,"+ar1.offset;
                            gen(i);
                            String i2 = "lw "+Ry.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);    
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Ry);
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Ry, ar1);
                                                           

                             //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,ar1));   
                             //Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar1,Ry));                             
                        }
                    }
                }
                else if (I.arg1 instanceof ArrayAccess){
                    
                    

                }
                //Verificamos si y (arg2 ) esta en rz
                if(I.arg2 instanceof Variable){
                    Variable ar2 = (Variable) I.arg2;
                    boolean contieneVariable = this.registroContieneVariable(ar2, Rz);
                    //Si no contiene la variable cargamos la instruccon
                    if(!contieneVariable){
                        if(ar2.esGlobal){
                            String i = "addi "+Registro.loadStore.nombre+", "+" $gp,"+ar2.offset;
                            gen(i);
                            String i2 = "lw "+Rz.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Rz);
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Rz, ar2);                             
                            //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Rz,ar2)); 
                             //Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar2,Rz));                             
                        }
                        else{
                            String i = "addi "+Registro.loadStore.nombre+", "+" $sp,"+ar2.offset;
                            gen(i);
                            String i2 = "lw "+Rz.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);         
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Rz);
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Rz, ar2);                             
                            //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Rz,ar2));  
                             //Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar2,Rz));                             
                        }
                    }
                }
                else if (I.arg2 instanceof ArrayAccess){

                }
                //Verificamos si y (target) esta en rz
                if(I.target instanceof Variable){
                    Variable tar = (Variable) I.target;
                    /* a. Cambiar Descriptor de Registro de Rx para que contenga solo x*/
                    //Eliminamos descriptores de registros de Ry
                     vaciarDescriptoresRegistro(Rx);
                     vaciarDescriptoresRegistroConVariable(tar);  // <<<--- Preguntar si esto es necesario... Al computar una nueva variable debemos vaciar todos los registros que la tenian porque sera recomputada
                    //Agregamos la variable ar1 al descriptor 
                     this.programRegisterDescriptors.add(new RegisterDescriptor(Rx,tar));      
                     /* b. Cambiar Descriptor de Variable de x para que contenga solo Rx*/
                     //Eliminamos descriptores de variable de x
                     vaciarDescriptoresVariable(tar);
                     //Agregamos Rx al descriptor de variable de x
                     this.programAddressDescriptors.add(new AddressDescriptor(tar,Rx));
                    /* c. Removemos Rx los descriptores de variable de cualquier otra variable que no sea x*/
                     removerTodosLosDescriptoresDeVariableQueContengan(Rx,tar);
                }
                else if (I.target instanceof ArrayAccess){
                    targetArray = true;

                }      
                if(I.op.equals("ADD")){
                    String ins = "add "+Rx.nombre+" ,"+Ry.nombre+" ,"+Rz.nombre;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                
                }
                else if(I.op.equals("SUB")){
                    String ins = "sub "+Rx.nombre+" ,"+Ry.nombre+" ,"+Rz.nombre;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                    

                }
                else if(I.op.equals("DIV")){
                    String ins = "div "+Ry.nombre+" ,"+Rz.nombre;
                    gen(ins);
                    ins = "mflo "+Rx.nombre;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                         

                }
                else if(I.op.equals("MULT")){
                    String ins = "mult "+Ry.nombre+" ,"+Rz.nombre;
                    gen(ins);
                    ins = "mflo "+Rx.nombre;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                    

                }
                else if(I.op.equals("AND")){
                    String ins = "and "+Rx.nombre+" ,"+Ry.nombre+" ,"+Rz.nombre;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                   

                }            
                else if(I.op.equals("OR")){
                    String ins = "or "+Rx.nombre+" ,"+Ry.nombre+" ,"+Rz.nombre;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                    

                }
                else if (I.op.equals("MOD")){
                    String ins = "div "+Ry.nombre+" ,"+Rz.nombre;
                    gen(ins);
                    ins = "mfhi "+Rx.nombre;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }               
                }                
            }
            
            

            else if (I.op.equals("LT")||I.op.equals("LE")||I.op.equals("EQ")||I.op.equals("GE")||I.op.equals("GT")||I.op.equals("NE")){         
                //Verificamos si y (arg1 ) esta en ry
                if(I.arg1 instanceof Variable){
                    Variable ar1 = (Variable) I.arg1;
                    boolean contieneVariable = this.registroContieneVariable(ar1, Ry);
                    //Si no contiene la variable cargamos la instruccon
                    if(!contieneVariable){
                        if(ar1.esGlobal){
                            String i = "addi "+Registro.loadStore.nombre+", "+" $gp,"+ar1.offset;
                            gen(i);
                            String i2 = "lw "+Ry.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);
                            /* a. Cambiar descriptor de registro para que incluya solo y*/
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Ry);
                            //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,ar1));
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Ry, ar1);
                             
                             // b. Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar1,Ry));
                        }
                        else{
                            String i = "addi "+Registro.loadStore.nombre+", "+" $sp,"+ar1.offset;
                            gen(i);
                            String i2 = "lw "+Ry.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);    
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Ry);
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Ry, ar1);
                                                           

                             //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,ar1));   
                             //Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar1,Ry));                             
                        }
                    }
                }
                else if (I.arg1 instanceof ArrayAccess){
                    
                    

                }
                //Verificamos si y (arg2 ) esta en rz
                if(I.arg2 instanceof Variable){
                    Variable ar2 = (Variable) I.arg2;
                    boolean contieneVariable = this.registroContieneVariable(ar2, Rz);
                    //Si no contiene la variable cargamos la instruccon
                    if(!contieneVariable){
                        if(ar2.esGlobal){
                            String i = "addi "+Registro.loadStore.nombre+", "+" $gp,"+ar2.offset;
                            gen(i);
                            String i2 = "lw "+Rz.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Rz);
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Rz, ar2);                             
                            //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Rz,ar2)); 
                             //Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar2,Rz));                             
                        }
                        else{
                            String i = "addi "+Registro.loadStore.nombre+", "+" $sp,"+ar2.offset;
                            gen(i);
                            String i2 = "lw "+Rz.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);         
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Rz);
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Rz, ar2);                             
                            //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Rz,ar2));  
                             //Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar2,Rz));                             
                        }
                    }
                }   
                /*---------------------TEST----*/
            if((block.instructions.indexOf(I)== block.instructions.size()-1 &&param ==false && I.op.equals("GOTO")|| I.op.equals("CALL")||I.op.equals("LT")||
                    I.op.equals("LE")||I.op.equals("EQ")||I.op.equals("GE")||I.op.equals("GT")||I.op.equals("NE")|| I.op.equals("PARAM")||I.op.equals("HALT")||I.op.equals("POP_LOCALS")) ){
                
                if(I.op.equals("PARAM")||I.op.equals("POP_LOCALS")){
                    param = true;
                }
                storesGen = true;
                
               //Al finalizar el bloque verificamos cada descriptor de variable, y si no esta en memoria generamos instruccion store para guardarlo
               ArrayList<AddressDescriptor> tempDescs = new ArrayList<AddressDescriptor>();
               for(AddressDescriptor desc: this.programAddressDescriptors){
                   boolean enMemoria = this.variableEnMemoria(desc.variable);
                   if(!enMemoria){
                       if(desc.variable.esGlobal){
                           String ins = "addi "+Registro.loadStore.nombre+",$gp,"+desc.variable.offset;
                           gen(ins);
                           String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                           gen(ins2);
                           tempDescs.add(new AddressDescriptor(desc.variable,desc.variable));
                       }
                       else{
                           String ins = "addi "+Registro.loadStore.nombre+",$sp,"+desc.variable.offset;
                           gen(ins);
                           String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                           gen(ins2);
                           tempDescs.add(new AddressDescriptor(desc.variable,desc.variable));                


                       }
                   }
               }
               this.programAddressDescriptors.addAll(tempDescs);                

            }
            /*---------------------------------------------------------------------*/
                if(I.op.equals("LT")){
                    String ins = "blt "+Ry.nombre+" ,"+Rz.nombre+", "+I.target;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                
                }
                else if(I.op.equals("LE")){
                    String ins = "ble "+Ry.nombre+" ,"+Rz.nombre+", "+I.target;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                     

                }
                else if(I.op.equals("EQ")){
                    String ins = "beq "+Ry.nombre+" ,"+Rz.nombre+", "+I.target;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                            

                }
                else if(I.op.equals("GE")){
                    String ins = "bge "+Ry.nombre+" ,"+Rz.nombre+", "+I.target;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                      

                }
                else if(I.op.equals("GT")){
                    String ins = "bgt "+Ry.nombre+" ,"+Rz.nombre+", "+I.target;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                     

                }            
                else if(I.op.equals("NE")){
                    String ins = "bne "+Ry.nombre+" ,"+Rz.nombre+", "+I.target;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                    

                }            
            }
           
            else if (I.op.equals("NOT")||I.op.equals("MINUS")||I.op.equals("(INT)")||I.op.equals("(CHAR)")){
                //Verificamos si y (arg1 ) esta en ry
                if(I.arg1 instanceof Variable){
                    Variable ar1 = (Variable) I.arg1;
                    boolean contieneVariable = this.registroContieneVariable(ar1, Ry);
                    //Si no contiene la variable cargamos la instruccon
                    if(!contieneVariable){
                        if(ar1.esGlobal){
                            String i = "addi "+Registro.loadStore.nombre+", "+" $gp,"+ar1.offset;
                            gen(i);
                            String i2 = "lw "+Ry.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);
                            /* a. Cambiar descriptor de registro para que incluya solo y*/
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Ry);
                            //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,ar1));
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Ry, ar1);
                             
                             // b. Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar1,Ry));
                        }
                        else{
                            String i = "addi "+Registro.loadStore.nombre+", "+" $sp,"+ar1.offset;
                            gen(i);
                            String i2 = "lw "+Ry.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);    
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Ry);
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Ry, ar1);
                                                           

                             //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,ar1));   
                             //Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar1,Ry));                             
                        }
                    }
                }
                else if (I.arg1 instanceof ArrayAccess){
                    
                    

                }                    
                //Verificamos si y (target) esta en rz
                if(I.target instanceof Variable){
                    Variable tar = (Variable) I.target;
                    /* a. Cambiar Descriptor de Registro de Rx para que contenga solo x*/
                    //Eliminamos descriptores de registros de Ry
                     vaciarDescriptoresRegistro(Rx);
                     vaciarDescriptoresRegistroConVariable(tar);  // <<<--- Preguntar si esto es necesario... Al computar una nueva variable debemos vaciar todos los registros que la tenian porque sera recomputada
                    //Agregamos la variable ar1 al descriptor 
                     this.programRegisterDescriptors.add(new RegisterDescriptor(Rx,tar));      
                     /* b. Cambiar Descriptor de Variable de x para que contenga solo Rx*/
                     //Eliminamos descriptores de variable de x
                     vaciarDescriptoresVariable(tar);
                     //Agregamos Rx al descriptor de variable de x
                     this.programAddressDescriptors.add(new AddressDescriptor(tar,Rx));
                    /* c. Removemos Rx los descriptores de variable de cualquier otra variable que no sea x*/
                     removerTodosLosDescriptoresDeVariableQueContengan(Rx,tar);
                }
                else if (I.target instanceof ArrayAccess){
                    targetArray = true;

                }                         
                if(I.op.equals("NOT")){
                    String ins = "not "+Rx.nombre+" ,"+Ry.nombre;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                
                }
                else if(I.op.equals("MINUS")){
                    String ins = "sub "+Rx.nombre+" ,"+"$zero, "+Ry.nombre;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                    

                }
                else if(I.op.equals("(INT)")||I.op.equals("(CHAR)")){
                    String ins = "mov "+Rx.nombre+" ,"+Ry.nombre;
                    gen(ins);
                    //Si el targer es un arrayaccess debemos obtener la direccion con el temporal en getreg y guardar el valor 
                    if(targetArray){
                        String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                        this.vaciarDescriptoresRegistro(Rx);

                    }                         

                }
            }       
            else if (I.op.equals("ASSIGN")){
                //Verificamos si y (arg1 ) esta en ry
                if(I.arg1 instanceof Variable){
                    Variable ar1 = (Variable) I.arg1;
                    boolean contieneVariable = this.registroContieneVariable(ar1, Ry);
                    //Si no contiene la variable cargamos la instruccon
                    if(!contieneVariable){
                        if(ar1.esGlobal){
                            String i = "addi "+Registro.loadStore.nombre+", "+" $gp,"+ar1.offset;
                            gen(i);
                            String i2 = "lw "+Ry.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Ry);
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Ry, ar1);                               
                            //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,ar1)); 
                             //Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar1,Ry));                             
                        }
                        else{
                            String i = "addi "+Registro.loadStore.nombre+", "+" $sp,"+ar1.offset;
                            gen(i);
                            String i2 = "lw "+Ry.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2); 
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Ry);
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Ry, ar1);                              
                            //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,ar1));        
                             //Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar1,Ry));                             
                        }
                    }
                }
                else if (I.arg1 instanceof ArrayAccess){

                }      
                //Verificamos si y (target) esta en rz
                if(I.target instanceof Variable){
                    Variable tar = (Variable) I.target;                    
                    //Como es instruccion del tipo x = y. Agregamos x al descriptor de registro de Ry
                    if(!this.registroContieneVariable(tar, Ry)){
                        this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,tar));
                    
                    
                    }
                    //Cambiamos el descriptor de variable de x para que su unica localizacion sea Ry
                    this.vaciarDescriptoresVariable(tar);
                    this.programAddressDescriptors.add(new AddressDescriptor(tar,Ry));
                    //Buscamos otros resgistros que contengan la variable target y movemos los valores
                    ArrayList<Registro> moves = this.buscarTodosRegistrosConteniendo(tar, Rt);
                    for(Registro r: moves){      
                        String s = "move "+r.nombre+","+Ry.nombre;
                         gen(s);
                    }               

                }
                else if (I.target instanceof ArrayAccess){
                    targetArray = true;

                }  

                    
                
                
                
                if(targetArray){
                    String i = "sw "+Rx.nombre+", ("+Rt.nombre+")";
                    gen(i);
                    this.vaciarDescriptoresRegistro(Rx);    
                    this.removerTodosLosDescriptoresDeVariableQueContengan(Rx);  /* Ultimo Cambio arrays*/
                
                }
                
               //String ins = "move "+Rx.nombre+" ,"+Ry.nombre;
               //gen(ins);

            
            }   
            else if(I.isLabel){
                if(I.op.equals("LABEL"+"ArrayOutOfBounds")){
                    gen(I.op+": ");                    
                    String s = "li $v0 ,4";
                    gen(s);
                    s = "la $a0, array";
                    gen(s);
                    s = "syscall";
                    gen(s);                
                }
                else if(I.op.equals("LABELprint_int0")){
                    gen(I.op+": ");         
                    String s1 = "addi $sp, $sp, -4 #Push Access Link";
                    gen(s1);
                    s1 = "sw $ra , 0($sp)";
                    gen(s1);
                    s1 = "addi $sp, $sp, -0 #Push Local Variables";
                    gen(s1);
                    s1 = "addi $t8,  $sp,4";
                    gen(s1);
                    s1 = "lw $t9, ($t8)";
                    gen(s1);
                    s1 = "li $v0, 1 # load appropriate system call code into register $v0. Code for printing int is 1";
                    gen(s1);
                    s1 = "move $a0, $t9 # move integer to be printed into $a0:  $a0 = $t2";
                    gen(s1);
                    s1 = "syscall";
                    gen(s1);                  
                    s1 = "addi $sp, $sp, 0 # POP locals";
                    gen(s1);
                    s1 = "lw $ra, 0($sp) #POP Access Link";
                    gen(s1);
                    s1 = "addi $sp, $sp, 4";
                    gen(s1);                     
                    s1 = "jr $ra ";
                    gen(s1);
                    
                    
                    
                    
                    
                    
                }
                else if(I.op.equals("LABELprint_char0")){
                    gen(I.op+": "); 
                    String s1 = "addi $sp, $sp, -4 #Push Access Link";
                    gen(s1);
                    s1 = "sw $ra , 0($sp)";
                    gen(s1);
                    s1 = "addi $sp, $sp, -0 #Push Local Variables";
                    gen(s1);
                    s1 = "addi $t8,  $sp,4";
                    gen(s1);
                    s1 = "lw $t9, ($t8)";
                    gen(s1);
                    s1 = "li $v0, 11 # load appropriate system call code into register $v0. Code for printing char is 11";
                    gen(s1);
                    s1 = "move $a0, $t9 # move integer to be printed into $a0:  $a0 = $t2";
                    gen(s1);
                    s1 = "syscall";
                    gen(s1);                  
                    s1 = "addi $sp, $sp, 0 # POP locals";
                    gen(s1);
                    s1 = "lw $ra, 0($sp) #POP Access Link";
                    gen(s1);
                    s1 = "addi $sp, $sp, 4";
                    gen(s1);                     
                    s1 = "jr $ra ";
                    gen(s1);
                                        
                }
                else if(I.op.equals("LABELprint_boolean0")){
                    gen(I.op+": ");    
                    String s1 = "addi $sp, $sp, -4 #Push Access Link";
                    gen(s1);
                    s1 = "sw $ra , 0($sp)";
                    gen(s1);
                    s1 = "addi $sp, $sp, -0 #Push Local Variables";
                    gen(s1);
                    s1 = "addi $t8,  $sp,4";
                    gen(s1);
                    s1 = "lw $t9, ($t8)";
                    gen(s1);
                    s1 = "beq $t9, $zero, isTrue";
                    gen(s1);
                    s1 = "isFalse:";
                    gen(s1);
                    s1 = "la $t9,false";
                    gen(s1);
                    s1 = "b End";
                    gen(s1);
                    s1 = "isTrue:";
                    gen(s1);
                    s1 = "la $t9,true";
                    gen(s1);
                    s1 = "End:";
                    gen(s1);                   
                    s1 = "li $v0, 4 # load appropriate system call code into register $v0. Code for printing String  is 4";
                    gen(s1);
                    s1 = "move $a0, $t9 # move integer to be printed into $a0:  $a0 = $t2";
                    gen(s1);
                    s1 = "syscall";
                    gen(s1);                  
                    s1 = "addi $sp, $sp, 0 # POP locals";
                    gen(s1);
                    s1 = "lw $ra, 0($sp) #POP Access Link";
                    gen(s1);
                    s1 = "addi $sp, $sp, 4";
                    gen(s1);                     
                    s1 = "jr $ra ";
                    gen(s1);
                                        
                }
                else if(I.op.equals("LABELinput_int0")){                  
                    gen(I.op+": ");                   
                    String s1 = "addi $sp, $sp, -4 #Push Access Link";
                    gen(s1);
                    s1 = "sw $ra , 0($sp)";
                    gen(s1);                  
                    s1 = "addi $sp, $sp, -0 #Push Local Variables";
                    gen(s1);
                    s1 = "addi $t8,  $sp,4";
                    gen(s1);
                    s1 = "lw $t9, ($t8)";
                    gen(s1);
                    s1 = "li $v0, 5 # load appropriate system call code into register $v0. Code for input int is 5";                    
                    gen(s1);
                    s1 = "syscall";
                    gen(s1);                                    
                    s1 = "lw $ra, 0($sp) #POP Access Link";
                    gen(s1);
                    s1 = "addi $sp, $sp, 4";
                    gen(s1);     
                    s1 = "jr $ra  #Retornando";
                    gen(s1);
                    
                                        
                }
                else if(I.op.equals("LABELinput_char0")){
                    gen(I.op+": ");   
                    String s1 = "addi $sp, $sp, -4 #Push Access Link";
                    gen(s1);
                    s1 = "sw $ra , 0($sp)";
                    gen(s1);                  
                    s1 = "addi $sp, $sp, -0 #Push Local Variables";
                    gen(s1);
                    s1 = "addi $t8,  $sp,4";
                    gen(s1);
                    s1 = "lw $t9, ($t8)";
                    gen(s1);
                    s1 = "li $v0, 12 # load appropriate system call code into register $v0. Code for input char is 12";                    
                    gen(s1);
                    s1 = "syscall";
                    gen(s1);                                    
                    s1 = "lw $ra, 0($sp) #POP Access Link";
                    gen(s1);
                    s1 = "addi $sp, $sp, 4";
                    gen(s1);     
                    s1 = "jr $ra  #Retornando";
                    gen(s1);                    
                }
                else if(I.op.equals("LABELinput_boolean0")){
                    gen(I.op+": ");                    
                    String s1 = "addi $sp, $sp, -4 #Push Access Link";
                    gen(s1);
                    s1 = "sw $ra , 0($sp)";
                    gen(s1);                  
                    s1 = "addi $sp, $sp, -0 #Push Local Variables";
                    gen(s1);
                    s1 = "addi $t8,  $sp,4";
                    gen(s1);
                    s1 = "lw $t9, ($t8)";
                    gen(s1);
                    s1 = "li $v0, 5 # load appropriate system call code into register $v0. Code for input int is 5";                    
                    gen(s1);
                    s1 = "syscall";
                    gen(s1);    
                    s1 = "beq $v0,0, endBoolInput";
                    gen(s1);
                    s1 = "li $v0, 1";
                    gen(s1);
                    s1 = "endBoolInput:";
                    gen(s1);
                    s1 = "lw $ra, 0($sp) #POP Access Link";
                    gen(s1);
                    s1 = "addi $sp, $sp, 4";
                    gen(s1);     
                    s1 = "jr $ra  #Retornando";
                    gen(s1);
                                        
                }                
                else{
                    gen(I.op+": ");                
                }
            }            
            else if (I.op.equals("GOTO")){
                
                gen("b "+I.target);
            
            } 
            else if (I.op.equals("RETURN")){

                String s = "jr $ra #Retornando";
                gen(s);
            
            }             
            else if (I.op.equals("PARAM")){
                //Verificamos si y (arg1 ) esta en ry
                if(I.arg1 instanceof Variable){
                    Variable ar1 = (Variable) I.arg1;
                    boolean contieneVariable = this.registroContieneVariable(ar1, Ry);
                    //Si no contiene la variable cargamos la instruccon
                    if(!contieneVariable){
                        if(ar1.esGlobal){
                            String i = "addi "+Registro.loadStore.nombre+", "+" $gp,"+ar1.offset;
                            gen(i);
                            String i2 = "lw "+Ry.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);
                            /* a. Cambiar descriptor de registro para que incluya solo y*/
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Ry);
                            //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,ar1));
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Ry, ar1);
                             
                             // b. Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar1,Ry));
                        }
                        else{
                            String i = "addi "+Registro.loadStore.nombre+", "+" $sp,"+ar1.offset;
                            gen(i);
                            String i2 = "lw "+Ry.nombre+", "+"("+Registro.loadStore.nombre+")";
                            gen(i2);    
                            //Eliminamos descriptores de registros de Ry
                             vaciarDescriptoresRegistro(Ry);
                             //a.i eliminamos todos los descriptores que tengan a ry como registro
                             this.removerTodosLosDescriptoresDeVariableQueContengan(Ry, ar1);
                                                           

                             //Agregamos la variable ar1 al descriptor 
                             this.programRegisterDescriptors.add(new RegisterDescriptor(Ry,ar1));   
                             //Agregamos registro al descriptor de variable
                             this.programAddressDescriptors.add(new AddressDescriptor(ar1,Ry));                             
                        }
                    }
                }
                else if (I.arg1 instanceof ArrayAccess){                                      
                }    
                    /*---------------------TEST----*/
                if((block.instructions.indexOf(I)== block.instructions.size()-1 &&param ==false && I.op.equals("GOTO")|| I.op.equals("CALL")||I.op.equals("LT")||
                        I.op.equals("LE")||I.op.equals("EQ")||I.op.equals("GE")||I.op.equals("GT")||I.op.equals("NE")|| I.op.equals("PARAM")||I.op.equals("HALT")||I.op.equals("POP_LOCALS")) ){

                    if(I.op.equals("PARAM")||I.op.equals("POP_LOCALS")){
                        param = true;
                    }
                    storesGen = true;

                   //Al finalizar el bloque verificamos cada descriptor de variable, y si no esta en memoria generamos instruccion store para guardarlo
                   ArrayList<AddressDescriptor> tempDescs = new ArrayList<AddressDescriptor>();
                   for(AddressDescriptor desc: this.programAddressDescriptors){
                       boolean enMemoria = this.variableEnMemoria(desc.variable);
                       if(!enMemoria){
                           if(desc.variable.esGlobal){
                               String ins = "addi "+Registro.loadStore.nombre+",$gp,"+desc.variable.offset;
                               gen(ins);
                               String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                               gen(ins2);
                               tempDescs.add(new AddressDescriptor(desc.variable,desc.variable));
                           }
                           else{
                               String ins = "addi "+Registro.loadStore.nombre+",$sp,"+desc.variable.offset;
                               gen(ins);
                               String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                               gen(ins2);
                               tempDescs.add(new AddressDescriptor(desc.variable,desc.variable));                


                           }
                       }
                   }
                   this.programAddressDescriptors.addAll(tempDescs);                

                }
                /*---------------------------------------------------------------------*/                
               /* //Hacemos push al stack
                String s = "addi $sp, $sp, -4 #PARAM";
                gen(s);
                //Guardamos el parametro
                s = "sw "+Ry.nombre+", 0($sp) ";
                gen(s);
                */
                String s = "addi $s6,$s6,-4"; //Agregamos al contador de parametros para hacer push
                gen(s);
                int num = -4*(paramCount);
                s = "sw "+Ry.nombre+", "+num+"($sp)";
                paramCount++;
                gen(s);
            }    
            else if (I.op.equals("HALT")){
                String s = "li $v0, 10";
                gen(s);
                s = "syscall";
                gen(s);
            
            }                  
             
            else if (I.op.equals("PUSH_RESULT")){
                //Hacemos push al stack
                String s = "addi $sp, $sp, -4 "+"#Push Result";
                gen(s);
       
            
            }
            else if (I.op.equals("PUSH_AL")){
                //Hacemos push al stack
                String s = "addi $sp, $sp, -4"+"#Push Access Link";
                gen(s);
                //Guardamos el parametro
                s = "sw $ra"+", 0($sp) ";
                gen(s);            
            
            }               
            else if (I.op.equals("PUSH_LOCALS")){
                if(I.arg1 instanceof Int){
                    Int ar1 = (Int)I.arg1;
                    int val =(int) ar1.valor;
                    //Hacemos push al stack
                   String s = "addi $sp, $sp, -"+val+"#Push Local Variables";
                   gen(s);                   
                
                }
            
            
            }   
            
            else if (I.op.equals("CALL")){
                    /*---------------------TEST----*/
                if((block.instructions.indexOf(I)== block.instructions.size()-1 &&param ==false && I.op.equals("GOTO")|| I.op.equals("CALL")||I.op.equals("LT")||
                        I.op.equals("LE")||I.op.equals("EQ")||I.op.equals("GE")||I.op.equals("GT")||I.op.equals("NE")|| I.op.equals("PARAM")||I.op.equals("HALT")||I.op.equals("POP_LOCALS")) ){

                    if(I.op.equals("PARAM")||I.op.equals("POP_LOCALS")){
                        param = true;
                    }
                    storesGen = true;

                   //Al finalizar el bloque verificamos cada descriptor de variable, y si no esta en memoria generamos instruccion store para guardarlo
                   ArrayList<AddressDescriptor> tempDescs = new ArrayList<AddressDescriptor>();
                   for(AddressDescriptor desc: this.programAddressDescriptors){
                       boolean enMemoria = this.variableEnMemoria(desc.variable);
                       if(!enMemoria){
                           if(desc.variable.esGlobal){
                               String ins = "addi "+Registro.loadStore.nombre+",$gp,"+desc.variable.offset;
                               gen(ins);
                               String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                               gen(ins2);
                               tempDescs.add(new AddressDescriptor(desc.variable,desc.variable));
                           }
                           else{
                               String ins = "addi "+Registro.loadStore.nombre+",$sp,"+desc.variable.offset;
                               gen(ins);
                               String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                               gen(ins2);
                               tempDescs.add(new AddressDescriptor(desc.variable,desc.variable));                


                           }
                       }
                   }
                   this.programAddressDescriptors.addAll(tempDescs);                

                }
                
                /*---------------------------------------------------------------------*/    
                //Verificamos si y (target) esta en rz
                if(I.target instanceof Variable){
                    Variable tar = (Variable) I.target;
                    /* a. Cambiar Descriptor de Registro de Rx para que contenga solo x*/
                    //Eliminamos descriptores de registros de Ry
                     vaciarDescriptoresRegistro(Rx);
                     vaciarDescriptoresRegistroConVariable(tar);  // <<<--- Preguntar si esto es necesario... Al computar una nueva variable debemos vaciar todos los registros que la tenian porque sera recomputada
                    //Agregamos la variable ar1 al descriptor 
                     this.programRegisterDescriptors.add(new RegisterDescriptor(Rx,tar));      
                     /* b. Cambiar Descriptor de Variable de x para que contenga solo Rx*/
                     //Eliminamos descriptores de variable de x
                     vaciarDescriptoresVariable(tar);
                     //Agregamos Rx al descriptor de variable de x
                     this.programAddressDescriptors.add(new AddressDescriptor(tar,Rx));
                    /* c. Removemos Rx los descriptores de variable de cualquier otra variable que no sea x*/
                     removerTodosLosDescriptoresDeVariableQueContengan(Rx,tar);
                }
                else if (I.target instanceof ArrayAccess){
                    targetArray = true;

                }        
                String s2 = "add $sp, $sp, $s6";
                gen(s2);
                s2 = "li $s6,0";
                gen(s2);
                paramCount =1;
                String  s = "jal "+I.arg1;
                gen(s);                
                //Hacemos pop a los parametros
                if(I.arg2 instanceof Int){
                    Int ar2 = (Int)I.arg2;
                    int val =(int) ar2.valor;
                    //Hacemos push al stack
                   String s1 = "addi $sp, $sp, "+val+ "#POP PARAMS";
                   gen(s1);                   
                
                }               
                if(I.target!=null){
                    //Movemos el resultado al registro target
                    s ="move "+Rx.nombre+", $v0";
                    gen(s);
                                
                }

            
            }
            
            else if (I.op.equals("POP_RESULT")){
                if(I.arg1!=null){
                    String a = "move $v0, "+Ry.nombre+" #Moviendo al registro de retorno";
                    gen(a);
                
                }                
                
                String s = "addi $sp, $sp, 4";
                gen(s);
            
            }   
            else if (I.op.equals("POP_LOCALS")){
                if(I.arg1 instanceof Int){
                    Int ar1 = (Int)I.arg1;
                    int val =(int) ar1.valor;
                    //Hacemos push al stack
                   String s = "addi $sp, $sp, "+val+ "#POP LOCALS";
                   gen(s);                   
                
                }                
            
            }   
            //Puede que esto no se use mas...
            else if (I.op.equals("POP_PARAMS")){
                if(I.arg1 instanceof Int){
                    Int ar1 = (Int)I.arg1;
                    int val =(int) ar1.valor;
                    //Hacemos push al stack
                   String s = "addi $sp, $sp, "+val+ "#POP PARAMS";
                   gen(s);                   
                
                }                
            
            }               
            else if (I.op.equals("POP_LINK")){
                //Guardamos el parametro
                String s = "lw $ra"+", 0($sp) #POP Access Link";
                gen(s);                   
                //Hacemos push al stack
                
                s = "addi $sp, $sp, 4";
                gen(s);
          
            
            }  
            
            if(block.instructions.indexOf(I)== block.instructions.size()-1 ){
                //Al finalizar el bloque verificamos cada descriptor de variable, y si no esta en memoria generamos instruccion store para guardarlo
                ArrayList<AddressDescriptor> tempDescs = new ArrayList<AddressDescriptor>();
                for(AddressDescriptor desc: this.programAddressDescriptors){
                    boolean enMemoria = this.variableEnMemoria(desc.variable);
                    if(!enMemoria){
                        if(desc.variable.esGlobal){
                            String ins = "addi "+Registro.loadStore.nombre+",$gp,"+desc.variable.offset;
                            gen(ins);
                            String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                            gen(ins2);
                            tempDescs.add(new AddressDescriptor(desc.variable,desc.variable));
                        }
                        else{
                            String ins = "addi "+Registro.loadStore.nombre+",$sp,"+desc.variable.offset;
                            gen(ins);
                            String ins2 = "sw "+desc.registro.nombre+", 0("+Registro.loadStore.nombre+")";
                            gen(ins2);
                            tempDescs.add(new AddressDescriptor(desc.variable,desc.variable));                


                        }


                    }


                }
                this.programAddressDescriptors.addAll(tempDescs);                

             }
        }

        
        
    
    }
    
    public void getLivenessInfo(BasicBlock b){
        for(int i = b.instructions.size()-1;i>=0;i--){
            Quad q = b.instructions.get(i);
            if(q.op.equals("ADD")||q.op.equals("SUB")||q.op.equals("MULT")||q.op.equals("DIV")||q.op.equals("MOD")||q.op.equals("AND")||q.op.equals("OR")){
                if(q.target instanceof Variable){
                    Variable  vx= (Variable) q.target;
                    q.targLive = vx.isLive;   
                    q.targNextUse = vx.nextUse;
                }                
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    q.arg1Live = vy.isLive;
                    q.arg1NextUse = vy.nextUse;                
                }
                if(q.arg2 instanceof Variable){
                    Variable vz = (Variable) q.arg2;
                    q.arg2Live = vz.isLive;    
                    q.arg2NextUse = vz.nextUse;
                }
                
                if(q.target instanceof Variable){
                    Variable  vx= (Variable) q.target;
                    vx.isLive=false;
                    vx.nextUse = -1;
                }                
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    vy.isLive = true;
                    vy.nextUse = this.intermediateCode.indexOf(q);            
                }
                if(q.arg2 instanceof Variable){
                    Variable vz = (Variable) q.arg2;
                    vz.isLive = true;
                    vz.nextUse = this.intermediateCode.indexOf(q);
                }                
            }
            else if (q.op.equals("MIN")||q.op.equals("NOT")||q.op.equals("(INT)")||q.op.equals("(CHAR)")){
                if(q.target instanceof Variable){
                    Variable  vx= (Variable) q.target;
                    q.targLive = vx.isLive;   
                    q.targNextUse = vx.nextUse;
                }                
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    q.arg1Live = vy.isLive;
                    q.arg1NextUse = vy.nextUse;                
                }
                if(q.target instanceof Variable){
                    Variable  vx= (Variable) q.target;
                    vx.isLive=false;
                    vx.nextUse = -1;
                }                
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    vy.isLive = true;
                    vy.nextUse = this.intermediateCode.indexOf(q);            
                }                
            
            }
            //Comparaciones 
            else if(q.op.equals("LT")||q.op.equals("LE")||q.op.equals("EQ")||q.op.equals("GE")||q.op.equals("GT")||q.op.equals("NE")){             
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    q.arg1Live = vy.isLive;
                    q.arg1NextUse = vy.nextUse;                
                }
                if(q.arg2 instanceof Variable){
                    Variable vz = (Variable) q.arg2;
                    q.arg2Live = vz.isLive;    
                    q.arg2NextUse = vz.nextUse;
                }
                              
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    vy.isLive = true;
                    vy.nextUse = this.intermediateCode.indexOf(q);            
                }
                if(q.arg2 instanceof Variable){
                    Variable vz = (Variable) q.arg2;
                    vz.isLive = true;
                    vz.nextUse = this.intermediateCode.indexOf(q);
                }      

            }
            //Asignaciones
            else if(q.op.equals("ASSIGN")){
                if(q.target instanceof Variable){
                    Variable  vx= (Variable) q.target;
                    q.targLive = vx.isLive;   
                    q.targNextUse = vx.nextUse;
                }                
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    q.arg1Live = vy.isLive;
                    q.arg1NextUse = vy.nextUse;                  
               }         
                if(q.target instanceof Variable){
                    Variable  vx= (Variable) q.target;
                    vx.isLive=false;
                    vx.nextUse = -1;
                }                
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    vy.isLive = true;
                    vy.nextUse = this.intermediateCode.indexOf(q);            
                }
            }
            //Parametros (Pueden no ser necesarios)
            else if(q.op.equals("PARAM")){
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    q.arg1Live = vy.isLive;
                    q.arg1NextUse = vy.nextUse;                
                } 
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    vy.isLive = true;
                    vy.nextUse = this.intermediateCode.indexOf(q);            
                }                
            }
            //Retorno de funcion
            else if(q.op.equals("RETURN")){
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    q.arg1Live = vy.isLive;
                    q.arg1NextUse = vy.nextUse;                
                } 
                if(q.arg1 instanceof Variable){
                    Variable vy = (Variable) q.arg1;
                    vy.isLive = true;
                    vy.nextUse = this.intermediateCode.indexOf(q);            
                }  

            }
        } 

    }
    
    public void generateCode(){
        
        //Primero Dividmos el codigo intermedio en Bloques Basicos
        ArrayList<BasicBlock> basicBlocks =this.createBasicBlocks();
       int i =0;
        for(BasicBlock b: basicBlocks){
           
            System.out.println("-------------------BASIC BLOCK # "+i+"------------------------");
            for(Quad q: b.instructions){
                System.out.println(this.intermediateCode.indexOf(q)+". "+q.toString());
            }
            
            i++;
        }
        
       for(BasicBlock b: basicBlocks){
           
           this.bloqueActual = b;
           this.getLivenessInfo(this.bloqueActual);
           traducirBloque(this.bloqueActual);
       
       }
        
    
    }
    
}
