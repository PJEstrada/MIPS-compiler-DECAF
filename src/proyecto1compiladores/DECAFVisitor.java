// Generated from DECAF.g4 by ANTLR 4.4
package proyecto1compiladores;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DECAFParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DECAFVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DECAFParser#mult_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMult_op(@NotNull DECAFParser.Mult_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#add_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdd_op(@NotNull DECAFParser.Add_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(@NotNull DECAFParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#bool_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool_literal(@NotNull DECAFParser.Bool_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#rel_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRel_op(@NotNull DECAFParser.Rel_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#eq_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEq_op(@NotNull DECAFParser.Eq_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#int_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt_literal(@NotNull DECAFParser.Int_literalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code voidType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVoidType(@NotNull DECAFParser.VoidTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(@NotNull DECAFParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simpleIf}
	 * labeled alternative in {@link DECAFParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleIf(@NotNull DECAFParser.SimpleIfContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ifElse}
	 * labeled alternative in {@link DECAFParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfElse(@NotNull DECAFParser.IfElseContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#char_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChar_literal(@NotNull DECAFParser.Char_literalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code simpleType}
	 * labeled alternative in {@link DECAFParser#varDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleType(@NotNull DECAFParser.SimpleTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code charType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharType(@NotNull DECAFParser.CharTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arrayType}
	 * labeled alternative in {@link DECAFParser#varDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayType(@NotNull DECAFParser.ArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanType(@NotNull DECAFParser.BooleanTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#simpleVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleVariable(@NotNull DECAFParser.SimpleVariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#multExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultExpr(@NotNull DECAFParser.MultExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code variableStruct}
	 * labeled alternative in {@link DECAFParser#structLocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableStruct(@NotNull DECAFParser.VariableStructContext ctx);
	/**
	 * Visit a parse tree produced by the {@code structArray}
	 * labeled alternative in {@link DECAFParser#structLocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructArray(@NotNull DECAFParser.StructArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#simpleLocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleLocation(@NotNull DECAFParser.SimpleLocationContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#arg2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg2(@NotNull DECAFParser.Arg2Context ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#cond_op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCond_op(@NotNull DECAFParser.Cond_opContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#addExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpr(@NotNull DECAFParser.AddExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#methodCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCall(@NotNull DECAFParser.MethodCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#structDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructDeclaration(@NotNull DECAFParser.StructDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code structDeclType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructDeclType(@NotNull DECAFParser.StructDeclTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#methodDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDeclaration(@NotNull DECAFParser.MethodDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(@NotNull DECAFParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(@NotNull DECAFParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#arrayVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayVariable(@NotNull DECAFParser.ArrayVariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#methodType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodType(@NotNull DECAFParser.MethodTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#unaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(@NotNull DECAFParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code structType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructType(@NotNull DECAFParser.StructTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(@NotNull DECAFParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg(@NotNull DECAFParser.ArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(@NotNull DECAFParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(@NotNull DECAFParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#relationExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationExpr(@NotNull DECAFParser.RelationExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#expressionA}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionA(@NotNull DECAFParser.ExpressionAContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#parameterType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameterType(@NotNull DECAFParser.ParameterTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(@NotNull DECAFParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code intType}
	 * labeled alternative in {@link DECAFParser#varType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntType(@NotNull DECAFParser.IntTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(@NotNull DECAFParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(@NotNull DECAFParser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#eqExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExpr(@NotNull DECAFParser.EqExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#assignStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStatement(@NotNull DECAFParser.AssignStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#location}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocation(@NotNull DECAFParser.LocationContext ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#parameter2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter2(@NotNull DECAFParser.Parameter2Context ctx);
	/**
	 * Visit a parse tree produced by {@link DECAFParser#andExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExpr(@NotNull DECAFParser.AndExprContext ctx);
}