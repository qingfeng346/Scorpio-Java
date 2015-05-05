package Scorpio.Compiler;

import Scorpio.*;
import Scorpio.Exception.*;
import Scorpio.Runtime.*;
import Scorpio.CodeDom.*;
import Scorpio.CodeDom.Temp.*;
import Scorpio.Variable.*;

public class ScriptParser {
    private Script m_script; //脚本类
    private String m_strBreviary; //当前解析的脚本摘要
    private int m_iNextToken; //当前读到token
    private java.util.ArrayList<Token> m_listTokens; //token列表
    private java.util.Stack<ScriptExecutable> m_Executables = new java.util.Stack<ScriptExecutable>(); //指令栈
    private ScriptExecutable m_scriptExecutable; //当前指令栈
    public ScriptParser(Script script, java.util.ArrayList<Token> listTokens, String strBreviary) {
        m_script = script;
        m_strBreviary = strBreviary;
        m_iNextToken = 0;
        m_listTokens = new java.util.ArrayList<Token>(listTokens);
    }
    public final void BeginExecutable(Executable_Block block) {
        m_scriptExecutable = new ScriptExecutable(m_script, block);
        m_Executables.push(m_scriptExecutable);
    }
    public final void EndExecutable() {
        m_Executables.pop();
        m_scriptExecutable = (m_Executables.size() > 0) ? m_Executables.peek() : null;
    }
    private int GetSourceLine()
    {
        return PeekToken().getSourceLine();
    }
    //解析脚本
    public final ScriptExecutable Parse() {
        m_iNextToken = 0;
        return ParseStatementBlock(Executable_Block.Context, false, TokenType.Finished);
    }
    //解析区域代码内容( {} 之间的内容)
    private ScriptExecutable ParseStatementBlock(Executable_Block block) {
        return ParseStatementBlock(block, true, TokenType.RightBrace);
    }
    //解析区域代码内容( {} 之间的内容)
    private ScriptExecutable ParseStatementBlock(Executable_Block block, boolean readLeftBrace, TokenType finished) {
        BeginExecutable(block);
        if (readLeftBrace && PeekToken().getType() != TokenType.LeftBrace) {
            ParseStatement();
        } else {
	        if (readLeftBrace) ReadLeftBrace();
	        TokenType tokenType;
	        while (HasMoreTokens()) {
	            tokenType = ReadToken().getType();
	            if (tokenType == finished)
	                break;
	            UndoToken();
	            ParseStatement();
	        }
        }
        ScriptExecutable ret = m_scriptExecutable;
        ret.EndScriptInstruction();
        EndExecutable();
        return ret;
    }
    //解析区域代码内容 ({} 之间的内容)
    private void ParseStatement() {
        Token token = ReadToken();
        switch (token.getType()) {
            case Var:
                ParseVar();
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
            case Switch:
                ParseSwtich();
                break;
            case Try:
                ParseTry();
                break;
            case Throw:
                ParseThrow();
                break;
            case Return:
                ParseReturn();
                break;
            case Identifier:
            case Increment:
            case Decrement:
            case Eval:
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
                throw new ParserException("不支持的语法 ", token);
        }
    }
    //解析函数（全局函数或类函数）
    private void ParseFunction() {
        if (m_scriptExecutable.getBlock() == Executable_Block.Context) {
        	Token token = PeekToken();
            UndoToken();
            ScriptFunction func = ParseFunctionDeclaration(true);
            m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.MOV, new CodeMember(func.getName()), new CodeFunction(func, m_strBreviary, token.getSourceLine())));
        }
    }
    //解析函数（返回一个函数）
    private ScriptFunction ParseFunctionDeclaration(boolean needName) {
        Token token = ReadToken();
        if (token.getType() != TokenType.Function) {
            throw new ParserException("Function declaration must start with the 'function' keyword.", token);
        }
        String strFunctionName = needName ? ReadIdentifier() : "";
        ReadLeftParenthesis();
        java.util.ArrayList<String> listParameters = new java.util.ArrayList<String>();
        boolean bParams = false;
        if (PeekToken().getType() != TokenType.RightPar) {
            while (true) {
                token = ReadToken();
                if (token.getType() == TokenType.Params) {
                    token = ReadToken();
                    bParams = true;
                }
                if (token.getType() != TokenType.Identifier) {
                    throw new ParserException("Unexpected token '" + token.getLexeme() + "' in function declaration.", token);
                }
                String strParameterName = token.getLexeme().toString();
                listParameters.add(strParameterName);
                token = PeekToken();
                if (token.getType() == TokenType.Comma && !bParams) {
                    ReadComma();
                }
                else if (token.getType() == TokenType.RightPar) {
                    break;
                }
                else {
                    throw new ParserException("Comma ',' or right parenthesis ')' expected in function declararion.", token);
                }
            }
        }
        ReadRightParenthesis();
        ScriptExecutable executable = ParseStatementBlock(Executable_Block.Function);
        return m_script.CreateFunction(strFunctionName, new ScorpioScriptFunction(m_script, listParameters, executable, bParams));
    }
    //解析Var关键字
    private void ParseVar() {
        for (; ;) {
            m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.VAR, ReadIdentifier()));
            Token peek = PeekToken();
            if (peek.getType() == TokenType.Assign) {
                UndoToken();
                ParseStatement();
            }
            peek = ReadToken();
            if (peek.getType() != TokenType.Comma) {
                UndoToken();
                break;
            }
        }
    }
    //解析普通代码块 {}
    private void ParseBlock() {
        UndoToken();
        m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_BLOCK, new ScriptContext(m_script, ParseStatementBlock(Executable_Block.Block))));
    }
    //解析if(判断语句)
    private void ParseIf() {
        CodeIf ret = new CodeIf();
        ret.If = ParseCondition(true, Executable_Block.If);
        for (; ;) {
            Token token = ReadToken();
            if (token.getType() == TokenType.ElseIf) {
                ret.AddElseIf(ParseCondition(true, Executable_Block.If));
            }
            else if (token.getType() == TokenType.Else) {
                if (PeekToken().getType() == TokenType.If) {
                    ReadToken();
                    ret.AddElseIf(ParseCondition(true, Executable_Block.If));
                }
                else {
                    UndoToken();
                    break;
                }
            }
            else {
                UndoToken();
                break;
            }
        }
        if (PeekToken().getType() == TokenType.Else) {
            ReadToken();
            ret.Else = ParseCondition(false, Executable_Block.If);
        }
        m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_IF, ret));
    }
    //解析判断内容
    private TempCondition ParseCondition(boolean condition, Executable_Block block) {
        CodeObject con = null;
        if (condition) {
            ReadLeftParenthesis();
            con = GetObject();
            ReadRightParenthesis();
        }
        return new TempCondition(m_script, con, ParseStatementBlock(block), block);
    }
    //解析for语句
    private void ParseFor() {
        ReadLeftParenthesis();
        int partIndex = m_iNextToken;
        Token token = ReadToken();
        if (token.getType() == TokenType.Identifier) {
            Token assign = ReadToken();
            if (assign.getType() == TokenType.Assign) {
                CodeObject obj = GetObject();
                Token comma = ReadToken();
                if (comma.getType() == TokenType.Comma) {
                    ParseFor_Simple((String)token.getLexeme(), obj);
                    return;
                }
            }
        }
        m_iNextToken = partIndex;
        ParseFor_impl();
    }
    //解析单纯for循环
    private void ParseFor_Simple(String Identifier, CodeObject obj) {
        CodeForSimple ret = new CodeForSimple(m_script);
        ret.Identifier = Identifier;
        ret.Begin = obj;
        ret.Finished = GetObject();
        if (PeekToken().getType() == TokenType.Comma) {
            ReadToken();
            ret.Step = GetObject();
        }
        ReadRightParenthesis();
        ret.SetContextExecutable(ParseStatementBlock(Executable_Block.For));
        m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_FORSIMPLE, ret));
    }
    //解析正规for循环
    private void ParseFor_impl() {
        CodeFor ret = new CodeFor(m_script);
        Token token = ReadToken();
        if (token.getType() != TokenType.SemiColon) {
            UndoToken();
            ret.BeginExecutable = ParseStatementBlock(Executable_Block.ForBegin, false, TokenType.SemiColon);
        }
        token = ReadToken();
        if (token.getType() != TokenType.SemiColon) {
            UndoToken();
            ret.Condition = GetObject();
            ReadSemiColon();
        }
        token = ReadToken();
        if (token.getType() != TokenType.RightPar) {
            UndoToken();
            ret.LoopExecutable = ParseStatementBlock(Executable_Block.ForLoop, false, TokenType.RightPar);
        }
        ret.SetContextExecutable(ParseStatementBlock(Executable_Block.For));
        m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_FOR, ret));
    }
    //解析foreach语句
    private void ParseForeach() {
        CodeForeach ret = new CodeForeach();
        ReadLeftParenthesis();
        ret.Identifier = ReadIdentifier();
        ReadIn();
        ret.LoopObject = GetObject();
        ReadRightParenthesis();
        ret.Context = new ScriptContext(m_script, ParseStatementBlock(Executable_Block.Foreach), null, Executable_Block.Foreach);
        m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_FOREACH, ret));
    }
    //解析while（循环语句）
    private void ParseWhile() {
        CodeWhile ret = new CodeWhile();
        ret.While = ParseCondition(true, Executable_Block.While);
        m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_WHILE, ret));
    }
    //解析swtich语句
    private void ParseSwtich() {
        CodeSwitch ret = new CodeSwitch();
        ReadLeftParenthesis();
        ret.Condition = GetObject();
        ReadRightParenthesis();
        ReadLeftBrace();
        for (; ;) {
            Token token = ReadToken();
            if (token.getType() == TokenType.Case) {
                java.util.ArrayList<Object> vals = new java.util.ArrayList<Object>();
                ParseCase(vals);
                ret.AddCase(new TempCase(m_script, vals, ParseStatementBlock(Executable_Block.Switch, false, TokenType.Break), Executable_Block.Switch));
            }
            else if (token.getType() == TokenType.Default) {
                ReadColon();
                ret.Default = new TempCase(m_script, null, ParseStatementBlock(Executable_Block.Switch, false, TokenType.Break), Executable_Block.Switch);
            }
            else if (token.getType() != TokenType.SemiColon) {
                UndoToken();
                break;
            }
        }
        ReadRightBrace();
        m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_SWITCH, ret));
    }
    //解析case
    private void ParseCase(java.util.ArrayList<Object> vals) {
        Token token = ReadToken();
        if (token.getType() == TokenType.String || token.getType() == TokenType.SimpleString || token.getType() == TokenType.Number) {
            vals.add(token.getLexeme());
        }
        else {
            throw new ParserException("case 语句 只支持 string和number类型", token);
        }
        ReadColon();
        if (ReadToken().getType() == TokenType.Case) {
            ParseCase(vals);
        }
        else {
            UndoToken();
        }
    }
    //解析try catch
    private void ParseTry() {
        CodeTry ret = new CodeTry(); {
            ScriptExecutable exec = ParseStatementBlock(Executable_Block.Context);
            ret.TryContext = new ScriptContext(m_script, exec);
        } {
            ReadCatch();
            ReadLeftParenthesis();
            ret.Identifier = ReadIdentifier();
            ReadRightParenthesis();
            ScriptExecutable exec = ParseStatementBlock(Executable_Block.Context);
            ret.CatchContext = new ScriptContext(m_script, exec);
        }
        m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_TRY, ret));
    }
    //解析throw
    private void ParseThrow() {
        CodeThrow ret = new CodeThrow();
        ret.obj = GetObject();
        m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.THROW, ret));
    }
    //解析return
    private void ParseReturn() {
        Token peek = PeekToken();
        if (peek.getType() == TokenType.RightBrace || peek.getType() == TokenType.SemiColon || peek.getType() == TokenType.Finished) {
            m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.RET, new CodeScriptObject(m_script, null, m_strBreviary, peek.getSourceLine())));
        }
        else {
            m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.RET, GetObject()));
        }
    }
    //解析表达式
    private void ParseExpression() {
        UndoToken();
        Token peek = PeekToken();
        CodeObject member = GetObject();
        if (member instanceof CodeCallFunction) {
            m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.CALL_FUNCTION, member));
        }
        else if (member instanceof CodeMember) {
            if (((CodeMember)((member instanceof CodeMember) ? member : null)).Calc != CALC.NONE) {
                m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.RESOLVE, member));
            }
            else {
                throw new ParserException("变量后缀不支持此操作符  " + PeekToken().getType(), peek);
            }
        }
        else if (member instanceof CodeAssign || member instanceof CodeEval) {
            m_scriptExecutable.AddScriptInstruction(new ScriptInstruction(Opcode.RESOLVE, member));
        }
        else {
            throw new ParserException("语法不支持起始符号为 " + member.getClass(), peek);
        }
    }
    //获取一个Object
    private CodeObject GetObject() {
        java.util.Stack<TempOperator> operateStack = new java.util.Stack<TempOperator>();
        java.util.Stack<CodeObject> objectStack = new java.util.Stack<CodeObject>();
        while (true) {
            objectStack.push(GetOneObject());
            if (!P_Operator(operateStack, objectStack)) {
                break;
            }
        }
        while (true) {
            if (operateStack.size() <= 0) {
                break;
            }
            TempOperator oper = operateStack.pop();
            CodeOperator binexp = new CodeOperator(objectStack.pop(), objectStack.pop(), oper.Operator, m_strBreviary, GetSourceLine());
            objectStack.push(binexp);
        }
        CodeObject ret = objectStack.pop();
        if (ret instanceof CodeMember) {
            CodeMember member = (CodeMember)((ret instanceof CodeMember) ? ret : null);
            if (member.Calc == CALC.NONE) {
                Token token = ReadToken();
                switch (token.getType()) {
                    case Assign:
                    case AssignPlus:
                    case AssignMinus:
                    case AssignMultiply:
                    case AssignDivide:
                    case AssignModulo:
                    case AssignCombine:
                    case AssignInclusiveOr:
                    case AssignXOR:
                    case AssignShr:
                    case AssignShi:
                        return new CodeAssign(member, GetObject(), token.getType(), m_strBreviary, token.getSourceLine());
                    default:
                        UndoToken();
                        break;
                }
            }
        }
        return ret;
    }
    //解析操作符
    private boolean P_Operator(java.util.Stack<TempOperator> operateStack, java.util.Stack<CodeObject> objectStack) {
        TempOperator curr = TempOperator.GetOper(PeekToken().getType());
        if (curr == null) {
            return false;
        }
        ReadToken();
        while (operateStack.size() > 0) {
            TempOperator oper = operateStack.peek();
            if (oper.Level >= curr.Level) {
                operateStack.pop();
                CodeOperator binexp = new CodeOperator(objectStack.pop(), objectStack.pop(), oper.Operator, m_strBreviary, GetSourceLine());
                objectStack.push(binexp);
            }
            else {
                break;
            }
        }
        operateStack.push(curr);
        return true;
    }
    //获得单一变量
    private CodeObject GetOneObject() {
        CodeObject ret = null;
        Token token = ReadToken();
        boolean not = false;
        boolean negative = false;
        CALC calc = CALC.NONE;
        if (token.getType() == TokenType.Not) {
            not = true;
            token = ReadToken();
        }
        else if (token.getType() == TokenType.Minus) {
            negative = true;
            token = ReadToken();
        }
        else if (token.getType() == TokenType.Increment) {
            calc = CALC.PRE_INCREMENT;
            token = ReadToken();
        }
        else if (token.getType() == TokenType.Decrement) {
            calc = CALC.PRE_DECREMENT;
            token = ReadToken();
        }
        switch (token.getType()) {
            case Identifier:
                ret = new CodeMember((String)token.getLexeme());
                break;
            case Function:
                UndoToken();
                ret = new CodeFunction(ParseFunctionDeclaration(false));
                break;
            case LeftPar:
            	ret = new CodeRegion(GetObject());
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
            case SimpleString:
                ret = new CodeScriptObject(m_script, token.getLexeme());
                break;
            default:
                throw new ParserException("Object起始关键字错误 ", token);
        }
        ret.StackInfo = new StackInfo(m_strBreviary, token.getSourceLine());
        ret = GetVariable(ret);
        ret.Not = not;
        ret = GetTernary(ret);
        ret.Negative = negative;
        if (ret instanceof CodeMember) {
            if (calc != CALC.NONE) {
                ((CodeMember)ret).Calc = calc;
            }
            else {
                Token peek = ReadToken();
                if (peek.getType() == TokenType.Increment) {
                    calc = CALC.POST_INCREMENT;
                }
                else if (peek.getType() == TokenType.Decrement) {
                    calc = CALC.POST_DECREMENT;
                }
                else {
                    UndoToken();
                }
                if (calc != CALC.NONE) {
                    ((CodeMember)ret).Calc = calc;
                }
            }
        }
        else if (calc != CALC.NONE) {
            throw new ParserException("++ 或者 -- 只支持变量的操作", token);
        }
        return ret;
    }
    //返回变量数据
    private CodeObject GetVariable(CodeObject parent) {
        CodeObject ret = parent;
        for (; ;) {
            Token m = ReadToken();
            if (m.getType() == TokenType.Period) {
                String identifier = ReadIdentifier();
                ret = new CodeMember(identifier, ret);
            }
            else if (m.getType() == TokenType.LeftBracket) {
                CodeObject member = GetObject();
                ReadRightBracket();
                if (member instanceof CodeScriptObject) {
                    ScriptObject obj = ((CodeScriptObject)member).getObject();
                    if (obj instanceof ScriptNumber || obj instanceof ScriptString) {
                        ret = new CodeMember(obj.getObjectValue(), ret);
                    }
                    else {
                        throw new ParserException("获取变量只能是 number或string", m);
                    }
                }
                else {
                     ret = new CodeMember(member, ret);
                }
            }
            else if (m.getType() == TokenType.LeftPar) {
                UndoToken();
                ret = GetFunction(ret);
            }
            else {
                UndoToken();
                break;
            }
            ret.StackInfo = new StackInfo(m_strBreviary, m.getSourceLine());
        }
        return ret;
    }
    //返回三元运算符
    private CodeObject GetTernary(CodeObject parent) {
        if (PeekToken().getType() == TokenType.QuestionMark) {
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
    private CodeCallFunction GetFunction(CodeObject member) {
        CodeCallFunction ret = new CodeCallFunction();
        ReadLeftParenthesis();
        java.util.ArrayList<CodeObject> pars = new java.util.ArrayList<CodeObject>();
        Token token = PeekToken();
        while (token.getType() != TokenType.RightPar) {
            pars.add(GetObject());
            token = PeekToken();
            if (token.getType() == TokenType.Comma) {
                ReadComma();
            }
            else if (token.getType() == TokenType.RightPar) {
                break;
            }
            else {
                throw new ParserException("Comma ',' or right parenthesis ')' expected in function declararion.", token);
            }
        }
        ReadRightParenthesis();
        ret.Member = member;
        ret.Parameters = pars;
        return ret;
    }
    //返回数组
    private CodeArray GetArray() {
        ReadLeftBracket();
        Token token = PeekToken();
        CodeArray ret = new CodeArray();
        while (token.getType() != TokenType.RightBracket) {
            if (PeekToken().getType() == TokenType.RightBracket)
                break;
            ret.Elements.add(GetObject());
            token = PeekToken();
            if (token.getType() == TokenType.Comma) {
                ReadComma();
            }
            else if (token.getType() == TokenType.RightBracket) {
                break;
            }
            else {
                throw new ParserException("Comma ',' or right parenthesis ']' expected in array object.", token);
            }
        }
        ReadRightBracket();
        return ret;
    }
    //返回Table数据
    private CodeTable GetTable() {
        CodeTable ret = new CodeTable();
        ReadLeftBrace();
        while (PeekToken().getType() != TokenType.RightBrace) {
            Token token = ReadToken();
            if (token.getType() == TokenType.Identifier || token.getType() == TokenType.String || token.getType() == TokenType.SimpleString || token.getType() == TokenType.Number) {
                Token next = ReadToken();
                if (next.getType() == TokenType.Assign || next.getType() == TokenType.Colon) {
            		ret.Variables.add(new TableVariable(token.getLexeme(), GetObject()));
                    Token peek = PeekToken();
                    if (peek.getType() == TokenType.Comma || peek.getType() == TokenType.SemiColon) {
                        ReadToken();
                    }
                }
                else {
                    throw new ParserException("Table变量赋值符号为[=]或者[:]", token);
                }
            }
            else if (token.getType() == TokenType.Function) {
                UndoToken();
                ret.Functions.add(ParseFunctionDeclaration(true));
            }
            else {
                throw new ParserException("Table开始关键字必须为[变量名称]或者[function]关键字", token);
            }
        }
        ReadRightBrace();
        return ret;
    }
    //返回执行一段字符串
    private CodeEval GetEval() {
        CodeEval ret = new CodeEval();
        ret.EvalObject = GetObject();
        return ret;
    }


    /**  是否还有更多需要解析的语法 
    */
    private boolean HasMoreTokens() {
        return m_iNextToken < m_listTokens.size();
    }
    /**  获得第一个Token 
    */
    private Token ReadToken() {
        if (!HasMoreTokens()) {
            throw new ScriptException("Unexpected end of token stream.");
        }
        return m_listTokens.get(m_iNextToken++);
    }
    /**  返回第一个Token 
    */
    private Token PeekToken() {
        if (!HasMoreTokens()) {
            throw new ScriptException("Unexpected end of token stream.");
        }
        return m_listTokens.get(m_iNextToken);
    }
    /**  回滚Token 
    */
    private void UndoToken() {
        if (m_iNextToken <= 0) {
            throw new ScriptException("No more tokens to undo.");
        }
        --m_iNextToken;
    }
    /**  读取, 
    */
    private void ReadComma() {
        Token token = ReadToken();
        if (token.getType() != TokenType.Comma) {
            throw new ParserException("Comma ',' expected.", token);
        }
    }
    /**  读取 未知字符 
    */
    private String ReadIdentifier() {
        Token token = ReadToken();
        if (token.getType() != TokenType.Identifier) {
            throw new ParserException("Identifier expected.", token);
        }
        return token.getLexeme().toString();
    }
    /**  读取{ 
    */
    private void ReadLeftBrace() {
        Token token = ReadToken();
        if (token.getType() != TokenType.LeftBrace) {
            throw new ParserException("Left brace '{' expected.", token);
        }
    }
    /**  读取} 
    */
    private void ReadRightBrace() {
        Token token = ReadToken();
        if (token.getType() != TokenType.RightBrace) {
            throw new ParserException("Right brace '}' expected.", token);
        }
    }
    /**  读取[ 
    */
    private void ReadLeftBracket() {
        Token token = ReadToken();
        if (token.getType() != TokenType.LeftBracket) {
            throw new ParserException("Left bracket '[' expected for array indexing expression.", token);
        }
    }
    /**  读取] 
    */
    private void ReadRightBracket() {
        Token token = ReadToken();
        if (token.getType() != TokenType.RightBracket) {
            throw new ParserException("Right bracket ']' expected for array indexing expression.", token);
        }
    }
    /**  读取( 
    */
    private void ReadLeftParenthesis() {
        Token token = ReadToken();
        if (token.getType() != TokenType.LeftPar) {
            throw new ParserException("Left parenthesis '(' expected.", token);
        }
    }
    /**  读取) 
    */
    private void ReadRightParenthesis() {
        Token token = ReadToken();
        if (token.getType() != TokenType.RightPar) {
            throw new ParserException("Right parenthesis ')' expected.", token);
        }
    }
    /**  读取; 
    */
    private void ReadSemiColon() {
        Token token = ReadToken();
        if (token.getType() != TokenType.SemiColon) {
            throw new ParserException("SemiColon ';' expected.", token);
        }
    }
    /**  读取in 
    */
    private void ReadIn() {
        Token token = ReadToken();
        if (token.getType() != TokenType.In) {
            throw new ParserException("In 'in' expected.", token);
        }
    }
    /**  读取: 
    */
    private void ReadColon() {
        Token token = ReadToken();
        if (token.getType() != TokenType.Colon) {
            throw new ParserException("Colon ':' expected.", token);
        }
    }
    /**  读取catch 
    */
    private void ReadCatch() {
        Token token = ReadToken();
        if (token.getType() != TokenType.Catch) {
            throw new ParserException("Catch 'catch' expected.", token);
        }
    }
}