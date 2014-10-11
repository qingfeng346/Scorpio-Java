package Scorpio.Compiler;

import Scorpio.*;
import Scorpio.Exception.*;
import Scorpio.Runtime.*;
import Scorpio.CodeDom.*;
import Scorpio.CodeDom.Temp.*;
import Scorpio.Variable.*;

public class ScriptParser
{
	private Script m_script; //脚本类
	private String m_strBreviary; //当前解析的脚本摘要
	private int m_iNextToken; //当前读到token
	private java.util.ArrayList<Token> m_listTokens; //token列表
	private java.util.Stack<ScriptExecutable> m_Executables = new java.util.Stack<ScriptExecutable>(); //指令栈
	private ScriptExecutable m_scriptExecutable; //当前指令栈
	public ScriptParser(Script script, java.util.ArrayList<Token> listTokens, String strBreviary)
	{
		m_script = script;
		m_strBreviary = strBreviary;
		m_iNextToken = 0;
		m_listTokens = new java.util.ArrayList<Token>(listTokens);
	}
	public final void BeginExecutable(Executable_Block block)
	{
		m_scriptExecutable = new ScriptExecutable(m_script, block);
		m_Executables.push(m_scriptExecutable);
	}
	public final void EndExecutable()
	{
		m_Executables.pop();
		m_scriptExecutable = (m_Executables.size() > 0) ? m_Executables.peek() : null;
	}
	//解析脚本
	public final ScriptExecutable Parse() throws Exception
	{
		m_iNextToken = 0;
		return ParseStatementBlock(Executable_Block.Context, false, TokenType.Finished);
	}
	//解析区域代码内容( {} 之间的内容)
	private ScriptExecutable ParseStatementBlock(Executable_Block block) throws Exception
	{
		return ParseStatementBlock(block, true, TokenType.RightBrace);
	}
	//解析区域代码内容( {} 之间的内容)
	private ScriptExecutable ParseStatementBlock(Executable_Block block, boolean readLeftBrace, TokenType finished) throws Exception
	{
		BeginExecutable(block);
		if (readLeftBrace)
		{
			ReadLeftBrace();
		}
		TokenType tokenType;
		while (HasMoreTokens())
		{
			tokenType = ReadToken().getType();
			if (tokenType == finished)
			{
				break;
			}
			UndoToken();
			ParseStatement();
		}
		ScriptExecutable ret = m_scriptExecutable;
		ret.EndScriptInstruction();
		EndExecutable();
		return ret;
	}
	//解析区域代码内容 ({} 之间的内容)
	private void ParseStatement() throws Exception
	{
		Token token = ReadToken();
		switch (token.getType())
		{
			case Var:
				String str = ReadIdentifier();
				m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.VAR, str));
				if (PeekToken().getType() == TokenType.Assign)
				{
					UndoToken();
					ParseStatement();
				}
				break;
			case LeftBrace:
				ParseBlock();
				break;
			case If:
				ParseIf();
				break;
			case For:
				ParseFor();
				break;
			case Foreach:
				ParseForeach();
				break;
			case While:
				ParseWhile();
				break;
			case Return:
				Token peek = PeekToken();
				if (peek.getType() == TokenType.RightBrace || peek.getType() == TokenType.SemiColon || peek.getType() == TokenType.Finished)
				{
					m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.RET, new CodeScriptObject(null)));
				}
				else
				{
					m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.RET, GetObject()));
				}
				break;
			case Identifier:
			case Increment:
			case Decrement:
			case Eval:
				UndoToken();
				ParseExpression();
				break;
			case Break:
				m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.BREAK, new CodeObject(m_strBreviary, token.getSourceLine())));
				break;
			case Continue:
				m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CONTINUE, new CodeObject(m_strBreviary, token.getSourceLine())));
				break;
			case Function:
				ParseFunction();
				break;
			case SemiColon:
				break;
			default:
				throw new ParserException("nonsupport syntax ", token);
		}
	}
	//解析函数（全局函数或类函数）
	private void ParseFunction() throws Exception
	{
		if (m_scriptExecutable.getBlock() == Executable_Block.Context)
		{
			UndoToken();
			ScriptFunction func = ParseFunctionDeclaration();
			m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.MOV, new CodeMember(func.getName()), new CodeFunction(func)));
		}
	}
	//解析函数（返回一个函数）
	private ScriptFunction ParseFunctionDeclaration() throws Exception
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.Function)
		{
			throw new ParserException("Function declaration must start with the 'function' keyword.", token);
		}
		String strFunctionName = "";
		Token identifierToken = PeekToken();
		if (identifierToken.getType() == TokenType.Identifier)
		{
			strFunctionName = ReadIdentifier();
		}
		ReadLeftParenthesis();
		java.util.ArrayList<String> listParameters = new java.util.ArrayList<String>();
		boolean bParams = false;
		if (PeekToken().getType() != TokenType.RightPar)
		{
			while (true)
			{
				token = ReadToken();
				if (token.getType() == TokenType.Params)
				{
					token = ReadToken();
					bParams = true;
				}
				if (token.getType() != TokenType.Identifier)
				{
					throw new ParserException("Unexpected token '" + token.getLexeme() + "' in function declaration.", token);
				}
				String strParameterName = token.getLexeme().toString();
				listParameters.add(strParameterName);
				token = PeekToken();
				if (token.getType() == TokenType.Comma && !bParams)
				{
					ReadComma();
				}
				else if (token.getType() == TokenType.RightPar)
				{
					break;
				}
				else
				{
					throw new ParserException("Comma ',' or right parenthesis ')' expected in function declararion.", token);
				}
			}
		}
		ReadRightParenthesis();
		ScriptExecutable executable = ParseStatementBlock(Executable_Block.Function);
		return m_script.CreateFunction(strFunctionName, new ScorpioScriptFunction(m_script, listParameters, executable, bParams));
	}
	//解析普通代码块 {}
	private void ParseBlock() throws Exception
	{
		UndoToken();
		m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_BLOCK, new ScriptContext(m_script, ParseStatementBlock(Executable_Block.Block))));
	}
	//解析if(判断语句)
	private void ParseIf() throws Exception
	{
		CodeIf ret = new CodeIf();
		ret.If = ParseCondition(true, Executable_Block.If);
		Token token = PeekToken();
		while (token.getType() == TokenType.ElseIf)
		{
			ReadToken();
			ret.AddElseIf(ParseCondition(true, Executable_Block.If));
			token = PeekToken();
		}
		if (token.getType() == TokenType.Else)
		{
			ReadToken();
			ret.Else = ParseCondition(false, Executable_Block.If);
		}
		m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_IF, ret));
	}
	//解析判断内容
	private TempCondition ParseCondition(boolean condition, Executable_Block block) throws Exception
	{
		CodeObject con = null;
		if (condition)
		{
			ReadLeftParenthesis();
			con = GetObject();
			ReadRightParenthesis();
		}
		return new TempCondition(m_script, con, ParseStatementBlock(block), block);
	}
	//解析for语句
	private void ParseFor() throws Exception
	{
		CodeFor ret = new CodeFor(m_script);
		ReadLeftParenthesis();
		Token token = ReadToken();
		if (token.getType() != TokenType.SemiColon)
		{
			UndoToken();
			ret.BeginExecutable = ParseStatementBlock(Executable_Block.ForBegin, false, TokenType.SemiColon);
		}
		token = ReadToken();
		if (token.getType() != TokenType.SemiColon)
		{
			UndoToken();
			ret.Condition = GetObject();
			ReadSemiColon();
		}
		token = ReadToken();
		if (token.getType() != TokenType.RightPar)
		{
			UndoToken();
			ret.LoopExecutable = ParseStatementBlock(Executable_Block.ForLoop, false, TokenType.RightPar);
		}
		ret.SetContextExecutable(ParseStatementBlock(Executable_Block.For));
		m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_FOR, ret));
	}
	//解析foreach语句
	private void ParseForeach() throws Exception
	{
		CodeForeach ret = new CodeForeach();
		ReadLeftParenthesis();
		ret.Identifier = ReadIdentifier();
		ReadIn();
		ret.LoopObject = GetObject();
		ReadRightParenthesis();
		ret.Executable = ParseStatementBlock(Executable_Block.Foreach);
		m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_FOREACH, ret));
	}
	//解析while（循环语句）
	private void ParseWhile() throws Exception
	{
		CodeWhile ret = new CodeWhile();
		ret.While = ParseCondition(true, Executable_Block.While);
		m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_WHILE, ret));
	}
	//解析表达式
	private void ParseExpression() throws Exception
	{
		Token peek = PeekToken();
		CodeObject member = GetObject();
		if (member instanceof CodeCallFunction)
		{
			m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_FUNCTION, member));
		}
		else if (member instanceof CodeMember)
		{
			if (((CodeMember)member).Calc != CALC.NONE)
			{
				m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALC, member));
			}
			else
			{
				Token token = ReadToken();
				if (token.getType() == TokenType.Assign)
				{
					m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.MOV, member, GetObject()));
				}
				else if (token.getType() == TokenType.AssignPlus)
				{
					m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.MOV, member, new CodeOperator(GetObject(), member, TokenType.Plus)));
				}
				else if (token.getType() == TokenType.AssignMinus)
				{
					m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.MOV, member, new CodeOperator(GetObject(), member, TokenType.Minus)));
				}
				else if (token.getType() == TokenType.AssignMultiply)
				{
					m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.MOV, member, new CodeOperator(GetObject(), member, TokenType.Multiply)));
				}
				else if (token.getType() == TokenType.AssignDivide)
				{
					m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.MOV, member, new CodeOperator(GetObject(), member, TokenType.Divide)));
				}
				else if (token.getType() == TokenType.AssignModulo)
				{
					m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.MOV, member, new CodeOperator(GetObject(), member, TokenType.Modulo)));
				}
				else
				{
					throw new ParserException("变量后缀不支持此操作符  ", token);
				}
			}
		}
		else if (member instanceof CodeEval)
		{
			m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.EVAL, member));
		}
		else
		{
			throw new ParserException("语法不支持起始符号为 " + member.getClass(), peek);
		}
	}
	//获取一个Object
	private CodeObject GetObject() throws Exception
	{
		java.util.Stack<TempOperator> operateStack = new java.util.Stack<TempOperator>();
		java.util.Stack<CodeObject> objectStack = new java.util.Stack<CodeObject>();
		while (true)
		{
			objectStack.push(GetOneObject());
			if (!P_Operator(operateStack, objectStack))
			{
				break;
			}
		}
		while (true)
		{
			if (operateStack.size() <= 0)
			{
				break;
			}
			TempOperator oper = operateStack.pop();
			CodeOperator binexp = new CodeOperator(objectStack.pop(), objectStack.pop(), oper.Operator);
			objectStack.push(binexp);
		}
		return objectStack.pop();
	}
	//解析操作符
	private boolean P_Operator(java.util.Stack<TempOperator> operateStack, java.util.Stack<CodeObject> objectStack)
	{
		TempOperator curr = TempOperator.GetOper(PeekToken().getType());
		if (curr == null)
		{
			return false;
		}
		ReadToken();
		while (operateStack.size() > 0)
		{
			TempOperator oper = operateStack.peek();
			if (oper.Level >= curr.Level)
			{
				operateStack.pop();
				CodeOperator binexp = new CodeOperator(objectStack.pop(), objectStack.pop(), oper.Operator);
				objectStack.push(binexp);
			}
			else
			{
				break;
			}
		}
		operateStack.push(curr);
		return true;
	}
	//获得单一变量
	private CodeObject GetOneObject() throws Exception
	{
		CodeObject ret = null;
		Token token = ReadToken();
		boolean not = false;
		boolean negative = false;
		CALC calc = CALC.NONE;
		if (token.getType() == TokenType.Not)
		{
			not = true;
			token = ReadToken();
		}
		else if (token.getType() == TokenType.Minus)
		{
			negative = true;
			token = ReadToken();
		}
		else if (token.getType() == TokenType.Increment)
		{
			calc = CALC.PRE_INCREMENT;
			token = ReadToken();
		}
		else if (token.getType() == TokenType.Decrement)
		{
			calc = CALC.PRE_DECREMENT;
			token = ReadToken();
		}
		switch (token.getType())
		{
			case Identifier:
				ret = new CodeMember((String)token.getLexeme());
				break;
			case Function:
				UndoToken();
				ret = new CodeFunction(ParseFunctionDeclaration());
				break;
			case LeftPar:
				ret = GetObject();
				ReadRightParenthesis();
				break;
			case LeftBracket:
				UndoToken();
				ret = GetArray();
				break;
			case LeftBrace:
				UndoToken();
				ret = GetTable();
				break;
			case Eval:
				ret = GetEval();
				break;
			case Null:
			case Boolean:
			case Number:
			case String:
				ret = new CodeScriptObject(token.getLexeme());
				break;
			default:
				throw new ParserException("parse is error ", token);
		}
		ret.StackInfo = new StackInfo(m_strBreviary, token.getSourceLine());
		ret = GetVariable(ret);
		ret.Not = not;
		ret = GetTernary(ret);
		ret.Negative = negative;
		if (ret instanceof CodeMember)
		{
			if (calc != CALC.NONE)
			{
				((CodeMember)ret).Calc = calc;
			}
			else
			{
				Token peek = ReadToken();
				if (peek.getType() == TokenType.Increment)
				{
					calc = CALC.POST_INCREMENT;
				}
				else if (peek.getType() == TokenType.Decrement)
				{
					calc = CALC.POST_DECREMENT;
				}
				else
				{
					UndoToken();
				}
				if (calc != CALC.NONE)
				{
					((CodeMember)ret).Calc = calc;
				}
			}
		}
		else if (calc != CALC.NONE)
		{
			throw new ParserException("++ 或者 -- 只支持变量的操作");
		}
		return ret;
	}
	//返回变量数据
	private CodeObject GetVariable(CodeObject parent) throws Exception
	{
		CodeObject ret = parent;
		for (; ;)
		{
			Token m = ReadToken();
			if (m.getType() == TokenType.Period)
			{
				String identifier = ReadIdentifier();
				ret = new CodeMember(identifier, ret);
			}
			else if (m.getType() == TokenType.LeftBracket)
			{
				CodeObject member = GetObject();
				ReadRightBracket();
				if (member instanceof CodeScriptObject)
				{
					ScriptObject obj = m_script.CreateObject(((CodeScriptObject)member).getObject());
					if (obj.getIsNumber())
					{
						ret = new CodeMember((ScriptNumber)obj, ret);
					}
					else if (obj.getIsString())
					{
						ret = new CodeMember(((ScriptString)obj).getValue(), ret);
					}
					else
					{
						throw new ParserException("获取变量只能是 number或string");
					}
				}
				else
				{
					 ret = new CodeMember(member, ret);
				}
			}
			else if (m.getType() == TokenType.LeftPar)
			{
				UndoToken();
				ret = GetFunction(ret);
			}
			else
			{
				UndoToken();
				break;
			}
		}
		return ret;
	}
	//返回三元运算符
	private CodeObject GetTernary(CodeObject parent) throws Exception
	{
		if (PeekToken().getType() == TokenType.QuestionMark)
		{
			CodeTernary ret = new CodeTernary();
			ret.Allow = parent;
			ReadToken();
			ret.True = GetObject();
			ReadColon();
			ret.False = GetObject();
			return ret;
		}
		return parent;
	}
	//返回一个调用函数 Object
	private CodeCallFunction GetFunction(CodeObject member) throws Exception
	{
		CodeCallFunction ret = new CodeCallFunction();
		ReadLeftParenthesis();
		java.util.ArrayList<CodeObject> pars = new java.util.ArrayList<CodeObject>();
		Token token = PeekToken();
		while (token.getType() != TokenType.RightPar)
		{
			pars.add(GetObject());
			token = PeekToken();
			if (token.getType() == TokenType.Comma)
			{
				ReadComma();
			}
			else if (token.getType() == TokenType.RightPar)
			{
				break;
			}
			else
			{
				throw new ParserException("Comma ',' or right parenthesis ')' expected in function declararion.", token);
			}
		}
		ReadRightParenthesis();
		ret.Member = member;
		ret.Parameters = pars;
		return ret;
	}
	//返回数组
	private CodeArray GetArray() throws Exception
	{
		ReadLeftBracket();
		Token token = PeekToken();
		CodeArray ret = new CodeArray();
		while (token.getType() != TokenType.RightBracket)
		{
			ret.Elements.add(GetObject());
			token = PeekToken();
			if (token.getType() == TokenType.Comma)
			{
				ReadComma();
			}
			else if (token.getType() == TokenType.RightBracket)
			{
				break;
			}
			else
			{
				throw new ParserException("Comma ',' or right parenthesis ']' expected in array object.", token);
			}
		}
		ReadRightBracket();
		return ret;
	}
	//返回Table数据
	private CodeTable GetTable() throws Exception
	{
		CodeTable ret = new CodeTable();
		ReadLeftBrace();
		while (PeekToken().getType() != TokenType.RightBrace)
		{
			Token token = ReadToken();
			if (token.getType() == TokenType.Identifier)
			{
				Token next = ReadToken();
				if (next.getType() == TokenType.Assign || next.getType() == TokenType.Colon)
				{
					ret.Variables.add(new TableVariable((String)token.getLexeme(), GetObject()));
					Token peek = PeekToken();
					if (peek.getType() == TokenType.Comma || peek.getType() == TokenType.SemiColon)
					{
						ReadToken();
					}
				}
				else
				{
					throw new ParserException("Table变量赋值符号为[=]或者[:]", token);
				}
			}
			else if (token.getType() == TokenType.Function)
			{
				UndoToken();
				ScriptFunction func = ParseFunctionDeclaration();
				if (func.getName() == null || func.getName().isEmpty())
				{
					throw new ParserException("Table内部函数名称 不能为空", token);
				}
				ret.Functions.add(func);
			}
			else
			{
				throw new ParserException("Table开始关键字必须为 变量名称或者function关键字", token);
			}
		}
		ReadRightBrace();
		return ret;
	}
	//返回执行一段字符串
	private CodeEval GetEval() throws Exception
	{
		CodeEval ret = new CodeEval();
		ret.EvalObject = GetObject();
		return ret;
	}


	/**  是否还有更多需要解析的语法 
	*/
	private boolean HasMoreTokens()
	{
		return m_iNextToken < m_listTokens.size();
	}
	/**  获得第一个Token 
	*/
	private Token ReadToken()
	{
		if (!HasMoreTokens())
		{
			throw new ScriptException("Unexpected end of token stream.");
		}
		return m_listTokens.get(m_iNextToken++);
	}
	/**  返回第一个Token 
	*/
	private Token PeekToken()
	{
		if (!HasMoreTokens())
		{
			throw new ScriptException("Unexpected end of token stream.");
		}
		return m_listTokens.get(m_iNextToken);
	}
	/**  返回上一个Token 
	*/
	private Token LastToken()
	{
		if (m_iNextToken <= 0)
		{
			throw new ScriptException("No more tokens to last.");
		}
		return m_listTokens.get(m_iNextToken - 1);
	}
	/**  回滚Token 
	*/
	private void UndoToken()
	{
		if (m_iNextToken <= 0)
		{
			throw new ScriptException("No more tokens to undo.");
		}
		--m_iNextToken;
	}
	/**  读取, 
	*/
	private void ReadComma()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.Comma)
		{
			throw new ParserException("Comma ',' expected.", token);
		}
	}
	/**  读取. 
	*/
	private void ReadPeriod()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.Period)
		{
			throw new ParserException("Period '.' expected for member variable expression.", token);
		}
	}
	/**  读取 未知字符 
	*/
	private String ReadIdentifier()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.Identifier)
		{
			throw new ParserException("Identifier expected.", token);
		}
		return token.getLexeme().toString();
	}
	/**  读取{ 
	*/
	private void ReadLeftBrace()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.LeftBrace)
		{
			throw new ParserException("Left brace '{' expected.", token);
		}
	}
	/**  读取} 
	*/
	private void ReadRightBrace()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.RightBrace)
		{
			throw new ParserException("Right brace '}' expected.", token);
		}
	}
	/**  读取[ 
	*/
	private void ReadLeftBracket()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.LeftBracket)
		{
			throw new ParserException("Left bracket '[' expected for array indexing expression.", token);
		}
	}
	/**  读取] 
	*/
	private void ReadRightBracket()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.RightBracket)
		{
			throw new ParserException("Right bracket ']' expected for array indexing expression.", token);
		}
	}
	/**  读取( 
	*/
	private void ReadLeftParenthesis()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.LeftPar)
		{
			throw new ParserException("Left parenthesis '(' expected.", token);
		}
	}
	/**  读取) 
	*/
	private void ReadRightParenthesis()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.RightPar)
		{
			throw new ParserException("Right parenthesis ')' expected.", token);
		}
	}
	/**  读取; 
	*/
	private void ReadSemiColon()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.SemiColon)
		{
			throw new ParserException("SemiColon ';' expected.", token);
		}
	}
	/**  读取in 
	*/
	private void ReadIn()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.In)
		{
			throw new ParserException("In 'in' expected.", token);
		}
	}
	/**  读取: 
	*/
	private void ReadColon()
	{
		Token token = ReadToken();
		if (token.getType() != TokenType.Colon)
		{
			throw new ParserException("Colon ';' expected.", token);
		}
	}
}