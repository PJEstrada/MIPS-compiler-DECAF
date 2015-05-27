// Generated from DECAF.g4 by ANTLR 4.4
package proyecto1compiladores;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DECAFParser}.
 */
public interface DECAFListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DECAFParser#mult_op}.
	 * @param ctx the parse tree
	 */
	void enterMult_op(@NotNull DECAFParser.Mult_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#mult_op}.
	 * @param ctx the parse tree
	 */
	void exitMult_op(@NotNull DECAFParser.Mult_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#add_op}.
	 * @param ctx the parse tree
	 */
	void enterAdd_op(@NotNull DECAFParser.Add_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#add_op}.
	 * @param ctx the parse tree
	 */
	void exitAdd_op(@NotNull DECAFParser.Add_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(@NotNull DECAFParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(@NotNull DECAFParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#bool_literal}.
	 * @param ctx the parse tree
	 */
	void enterBool_literal(@NotNull DECAFParser.Bool_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#bool_literal}.
	 * @param ctx the parse tree
	 */
	void exitBool_literal(@NotNull DECAFParser.Bool_literalContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#rel_op}.
	 * @param ctx the parse tree
	 */
	void enterRel_op(@NotNull DECAFParser.Rel_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#rel_op}.
	 * @param ctx the parse tree
	 */
	void exitRel_op(@NotNull DECAFParser.Rel_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#eq_op}.
	 * @param ctx the parse tree
	 */
	void enterEq_op(@NotNull DECAFParser.Eq_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#eq_op}.
	 * @param ctx the parse tree
	 */
	void exitEq_op(@NotNull DECAFParser.Eq_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#int_literal}.
	 * @param ctx the parse tree
	 */
	void enterInt_literal(@NotNull DECAFParser.Int_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#int_literal}.
	 * @param ctx the parse tree
	 */
	void exitInt_literal(@NotNull DECAFParser.Int_literalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code voidType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void enterVoidType(@NotNull DECAFParser.VoidTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code voidType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void exitVoidType(@NotNull DECAFParser.VoidTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(@NotNull DECAFParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(@NotNull DECAFParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleIf}
	 * labeled alternative in {@link DECAFParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterSimpleIf(@NotNull DECAFParser.SimpleIfContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleIf}
	 * labeled alternative in {@link DECAFParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitSimpleIf(@NotNull DECAFParser.SimpleIfContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifElse}
	 * labeled alternative in {@link DECAFParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfElse(@NotNull DECAFParser.IfElseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifElse}
	 * labeled alternative in {@link DECAFParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfElse(@NotNull DECAFParser.IfElseContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#char_literal}.
	 * @param ctx the parse tree
	 */
	void enterChar_literal(@NotNull DECAFParser.Char_literalContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#char_literal}.
	 * @param ctx the parse tree
	 */
	void exitChar_literal(@NotNull DECAFParser.Char_literalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simpleType}
	 * labeled alternative in {@link DECAFParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterSimpleType(@NotNull DECAFParser.SimpleTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simpleType}
	 * labeled alternative in {@link DECAFParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitSimpleType(@NotNull DECAFParser.SimpleTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code charType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void enterCharType(@NotNull DECAFParser.CharTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code charType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void exitCharType(@NotNull DECAFParser.CharTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link DECAFParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(@NotNull DECAFParser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link DECAFParser#varDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(@NotNull DECAFParser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code booleanType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void enterBooleanType(@NotNull DECAFParser.BooleanTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code booleanType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void exitBooleanType(@NotNull DECAFParser.BooleanTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#simpleVariable}.
	 * @param ctx the parse tree
	 */
	void enterSimpleVariable(@NotNull DECAFParser.SimpleVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#simpleVariable}.
	 * @param ctx the parse tree
	 */
	void exitSimpleVariable(@NotNull DECAFParser.SimpleVariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#multExpr}.
	 * @param ctx the parse tree
	 */
	void enterMultExpr(@NotNull DECAFParser.MultExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#multExpr}.
	 * @param ctx the parse tree
	 */
	void exitMultExpr(@NotNull DECAFParser.MultExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code variableStruct}
	 * labeled alternative in {@link DECAFParser#structLocation}.
	 * @param ctx the parse tree
	 */
	void enterVariableStruct(@NotNull DECAFParser.VariableStructContext ctx);
	/**
	 * Exit a parse tree produced by the {@code variableStruct}
	 * labeled alternative in {@link DECAFParser#structLocation}.
	 * @param ctx the parse tree
	 */
	void exitVariableStruct(@NotNull DECAFParser.VariableStructContext ctx);
	/**
	 * Enter a parse tree produced by the {@code structArray}
	 * labeled alternative in {@link DECAFParser#structLocation}.
	 * @param ctx the parse tree
	 */
	void enterStructArray(@NotNull DECAFParser.StructArrayContext ctx);
	/**
	 * Exit a parse tree produced by the {@code structArray}
	 * labeled alternative in {@link DECAFParser#structLocation}.
	 * @param ctx the parse tree
	 */
	void exitStructArray(@NotNull DECAFParser.StructArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#simpleLocation}.
	 * @param ctx the parse tree
	 */
	void enterSimpleLocation(@NotNull DECAFParser.SimpleLocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#simpleLocation}.
	 * @param ctx the parse tree
	 */
	void exitSimpleLocation(@NotNull DECAFParser.SimpleLocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#arg2}.
	 * @param ctx the parse tree
	 */
	void enterArg2(@NotNull DECAFParser.Arg2Context ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#arg2}.
	 * @param ctx the parse tree
	 */
	void exitArg2(@NotNull DECAFParser.Arg2Context ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#cond_op}.
	 * @param ctx the parse tree
	 */
	void enterCond_op(@NotNull DECAFParser.Cond_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#cond_op}.
	 * @param ctx the parse tree
	 */
	void exitCond_op(@NotNull DECAFParser.Cond_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void enterAddExpr(@NotNull DECAFParser.AddExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#addExpr}.
	 * @param ctx the parse tree
	 */
	void exitAddExpr(@NotNull DECAFParser.AddExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#methodCall}.
	 * @param ctx the parse tree
	 */
	void enterMethodCall(@NotNull DECAFParser.MethodCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#methodCall}.
	 * @param ctx the parse tree
	 */
	void exitMethodCall(@NotNull DECAFParser.MethodCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#structDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStructDeclaration(@NotNull DECAFParser.StructDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#structDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStructDeclaration(@NotNull DECAFParser.StructDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code structDeclType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void enterStructDeclType(@NotNull DECAFParser.StructDeclTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code structDeclType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void exitStructDeclType(@NotNull DECAFParser.StructDeclTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMethodDeclaration(@NotNull DECAFParser.MethodDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#methodDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMethodDeclaration(@NotNull DECAFParser.MethodDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(@NotNull DECAFParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(@NotNull DECAFParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(@NotNull DECAFParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(@NotNull DECAFParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#arrayVariable}.
	 * @param ctx the parse tree
	 */
	void enterArrayVariable(@NotNull DECAFParser.ArrayVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#arrayVariable}.
	 * @param ctx the parse tree
	 */
	void exitArrayVariable(@NotNull DECAFParser.ArrayVariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#methodType}.
	 * @param ctx the parse tree
	 */
	void enterMethodType(@NotNull DECAFParser.MethodTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#methodType}.
	 * @param ctx the parse tree
	 */
	void exitMethodType(@NotNull DECAFParser.MethodTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(@NotNull DECAFParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#unaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(@NotNull DECAFParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code structType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void enterStructType(@NotNull DECAFParser.StructTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code structType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void exitStructType(@NotNull DECAFParser.StructTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(@NotNull DECAFParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(@NotNull DECAFParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#arg}.
	 * @param ctx the parse tree
	 */
	void enterArg(@NotNull DECAFParser.ArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#arg}.
	 * @param ctx the parse tree
	 */
	void exitArg(@NotNull DECAFParser.ArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(@NotNull DECAFParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(@NotNull DECAFParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(@NotNull DECAFParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(@NotNull DECAFParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#relationExpr}.
	 * @param ctx the parse tree
	 */
	void enterRelationExpr(@NotNull DECAFParser.RelationExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#relationExpr}.
	 * @param ctx the parse tree
	 */
	void exitRelationExpr(@NotNull DECAFParser.RelationExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#expressionA}.
	 * @param ctx the parse tree
	 */
	void enterExpressionA(@NotNull DECAFParser.ExpressionAContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#expressionA}.
	 * @param ctx the parse tree
	 */
	void exitExpressionA(@NotNull DECAFParser.ExpressionAContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#parameterType}.
	 * @param ctx the parse tree
	 */
	void enterParameterType(@NotNull DECAFParser.ParameterTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#parameterType}.
	 * @param ctx the parse tree
	 */
	void exitParameterType(@NotNull DECAFParser.ParameterTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(@NotNull DECAFParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(@NotNull DECAFParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code intType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void enterIntType(@NotNull DECAFParser.IntTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code intType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 */
	void exitIntType(@NotNull DECAFParser.IntTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(@NotNull DECAFParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(@NotNull DECAFParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(@NotNull DECAFParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(@NotNull DECAFParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#eqExpr}.
	 * @param ctx the parse tree
	 */
	void enterEqExpr(@NotNull DECAFParser.EqExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#eqExpr}.
	 * @param ctx the parse tree
	 */
	void exitEqExpr(@NotNull DECAFParser.EqExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#assignStatement}.
	 * @param ctx the parse tree
	 */
	void enterAssignStatement(@NotNull DECAFParser.AssignStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#assignStatement}.
	 * @param ctx the parse tree
	 */
	void exitAssignStatement(@NotNull DECAFParser.AssignStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#location}.
	 * @param ctx the parse tree
	 */
	void enterLocation(@NotNull DECAFParser.LocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#location}.
	 * @param ctx the parse tree
	 */
	void exitLocation(@NotNull DECAFParser.LocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#parameter2}.
	 * @param ctx the parse tree
	 */
	void enterParameter2(@NotNull DECAFParser.Parameter2Context ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#parameter2}.
	 * @param ctx the parse tree
	 */
	void exitParameter2(@NotNull DECAFParser.Parameter2Context ctx);
	/**
	 * Enter a parse tree produced by {@link DECAFParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void enterAndExpr(@NotNull DECAFParser.AndExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link DECAFParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void exitAndExpr(@NotNull DECAFParser.AndExprContext ctx);
}