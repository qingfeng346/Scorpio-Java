package Scorpio.Runtime;

import java.util.Map;

import Scorpio.*;
import Scorpio.Compiler.*;
import Scorpio.CodeDom.*;
import Scorpio.CodeDom.Temp.*;
import Scorpio.Exception.*;

//执行命令
public class ScriptContext {
    private Script m_script; //脚本类
    private ScriptContext m_parent; //父级执行命令
    private ScriptExecutable m_scriptExecutable; //执行命令堆栈
    private ScriptInstruction m_scriptInstruction; //当前执行
    private java.util.HashMap<String, ScriptObject> m_variableDictionary = new java.util.HashMap<String, ScriptObject>(); //当前作用域所有变量
    private ScriptObject m_returnObject = null; //返回值
    private Executable_Block m_block = Executable_Block.forValue(0); //堆栈类型
    private boolean m_Break = false; //break跳出
    private boolean m_Continue = false; //continue跳出
    private boolean m_Over = false; //函数是否已经结束
    private int m_InstructionCount = 0; //指令数量
    public ScriptContext(Script script, ScriptExecutable scriptExecutable) {
        this(script, scriptExecutable, null, Executable_Block.None);
    }
    public ScriptContext(Script script, ScriptExecutable scriptExecutable, ScriptContext parent, Executable_Block block) {
        m_script = script;
        m_parent = parent;
        m_scriptExecutable = scriptExecutable;
        m_variableDictionary.clear();
        m_block = block;
        m_InstructionCount = m_scriptExecutable != null ? m_scriptExecutable.getCount() : 0;
    }
	//break 或者 return  跳出循环
    private boolean getIsOver() {
        return m_Break || m_Over;
    }
	//continue break return 当前模块是否执行完成
    private boolean getIsExecuted() {
    	return m_Break || m_Over || m_Continue;
    } 
    public final void Initialize(ScriptContext parent, java.util.HashMap<String, ScriptObject> variable) {
        m_parent = parent;
        m_variableDictionary.clear();
        for (Map.Entry<String, ScriptObject> pair : variable.entrySet())
        	m_variableDictionary.put(pair.getKey(), pair.getValue());
    }
    private void Initialize(ScriptContext parent, String name, ScriptObject obj) {
        m_parent = parent;
        m_variableDictionary.clear();
        m_variableDictionary.put(name, obj);
    }
    private void Initialize(ScriptContext parent) {
        m_parent = parent;
        m_variableDictionary.clear();
    }
    private java.util.HashMap<String, ScriptObject> GetContextVariables()
    {
    	java.util.HashMap<String, ScriptObject> vars = new java.util.HashMap<String, ScriptObject>();
        ScriptContext context = this;
        while (context != null) {
            for (Map.Entry<String, ScriptObject> pair : context.m_variableDictionary.entrySet()) {
                if (!vars.containsKey(pair.getKey()))
                    vars.put(pair.getKey(), pair.getValue());
            }
            context = context.m_parent;
        }
        return vars;
    }
    private void ApplyVariableObject(String name) {
        if (!m_variableDictionary.containsKey(name)) {
            m_variableDictionary.put(name, m_script.Null);
        }
    }
    private ScriptObject GetVariableObject(String name) {
        if (m_variableDictionary.containsKey(name)) {
            return m_variableDictionary.get(name);
        }
        if (m_parent != null) {
            return m_parent.GetVariableObject(name);
        }
        return null;
    }
    private boolean SetVariableObject(String name, ScriptObject obj) {
        if (m_variableDictionary.containsKey(name)) {
            Util.SetObject(m_variableDictionary, name, obj);
            return true;
        }
        if (m_parent != null) {
            return m_parent.SetVariableObject(name, obj);
        }
        return false;
    }
    private Object GetMember(CodeMember member) throws Exception {
    	return member.Type == MEMBER_TYPE.VALUE ? member.MemberValue : ResolveOperand(member.MemberObject).getObjectValue();
    }
    private ScriptObject GetVariable(CodeMember member) throws Exception {
        ScriptObject ret = null;
        if (member.Parent == null) {
        	String name = (String)member.MemberValue;
            ScriptObject obj = GetVariableObject(name);
            ret = (obj == null ? m_script.GetValue(name) : obj);
        }
        else {
            ret = ResolveOperand(member.Parent).GetValue(GetMember(member));
        }
        if (ret == null) {
            throw new ExecutionException(m_script, "GetVariable member is error");
        }
        if (member.Calc != CALC.NONE) {
            ScriptNumber num = (ScriptNumber)((ret instanceof ScriptNumber) ? ret : null);
            if (num == null) {
                throw new ExecutionException(m_script, "++或者--只能应用于Number类型");
            }
            return num.Calc(member.Calc);
        }
        return ret;
    }
    private void SetVariable(CodeMember member, ScriptObject variable) throws Exception {
        if (member.Parent == null) {
        	String name = (String)member.MemberValue;
            if (!SetVariableObject(name, variable)) {
                m_script.SetObjectInternal(name, variable);
            }
        }
        else {
        	ResolveOperand(member.Parent).SetValue(GetMember(member), variable);
        }
    }
    private void Reset() {
        m_returnObject = null;
        m_Over = false;
        m_Break = false;
    }
    public final ScriptObject Execute() throws Exception {
        Reset();
        int iInstruction = 0;
        while (iInstruction < m_InstructionCount) {
            m_scriptInstruction = m_scriptExecutable.getItem(iInstruction++);
            ExecuteInstruction();
            if (getIsExecuted()) {
                break;
            }
        }
        return m_returnObject;
    }
    private ScriptObject Execute(ScriptExecutable executable) throws Exception {
        if (executable == null) {
            return null;
        }
        Reset();
        int iInstruction = 0;
        int iInstructionCount = executable.getCount();
        while (iInstruction < iInstructionCount) {
            m_scriptInstruction = executable.getItem(iInstruction++);
            ExecuteInstruction();
            if (getIsExecuted()) {
                break;
            }
        }
        return m_returnObject;
    }
    private void ExecuteInstruction() throws Exception {
        switch (m_scriptInstruction.getOpcode()) {
            case VAR:
                ProcessVar();
                break;
            case MOV:
                ProcessMov();
                break;
            case RET:
                ProcessRet();
                break;
            case RESOLVE:
                ProcessResolve();
                break;
            case CONTINUE:
                ProcessContinue();
                break;
            case BREAK:
                ProcessBreak();
                break;
            case CALL_BLOCK:
                ProcessCallBlock();
                break;
            case CALL_FUNCTION:
                ProcessCallFunction();
                break;
            case CALL_IF:
                ProcessCallIf();
                break;
            case CALL_FOR:
                ProcessCallFor();
                break;
            case CALL_FORSIMPLE:
                ProcessCallForSimple();
                break;
            case CALL_FOREACH:
                ProcessCallForeach();
                break;
            case CALL_WHILE:
                ProcessCallWhile();
                break;
            case CALL_SWITCH:
                ProcessCallSwitch();
                break;
            case CALL_TRY:
                ProcessTry();
                break;
            case THROW:
                ProcessThrow();
                break;
        }
    }
    private boolean SupportReturnValue() {
        return m_block == Executable_Block.Function || m_block == Executable_Block.Context;
    }
    private boolean SupportContinue() {
        return m_block == Executable_Block.For || m_block == Executable_Block.Foreach || m_block == Executable_Block.While;
    }
    private boolean SupportBreak() {
        return m_block == Executable_Block.For || m_block == Executable_Block.Foreach || m_block == Executable_Block.While;
    }
    private void ProcessVar() {
        ApplyVariableObject((String)m_scriptInstruction.getValue());
    }
    private void ProcessMov() throws Exception {
        CodeObject tempVar = m_scriptInstruction.getOperand0();
        SetVariable((CodeMember)((tempVar instanceof CodeMember) ? tempVar : null), ResolveOperand(m_scriptInstruction.getOperand1()));
    }
    private void ProcessContinue() {
        InvokeContinue(m_scriptInstruction.getOperand0());
    }
    private void ProcessBreak() {
        InvokeBreak(m_scriptInstruction.getOperand0());
    }
    private void ProcessCallFor() throws Exception {
        CodeFor code = (CodeFor)m_scriptInstruction.getOperand0();
        ScriptContext context = code.Context;
        ScriptContext blockContext = code.BlockContext;
        context.Initialize(this);
        context.Execute(code.BeginExecutable);
        ScriptBoolean Condition;
        for (; ;) {
            if (code.Condition != null) {
                Object tempVar = context.ResolveOperand(code.Condition);
                Condition = (ScriptBoolean)((tempVar instanceof ScriptBoolean) ? tempVar : null);
                if (Condition == null) {
                    throw new ExecutionException(m_script, "for 跳出条件必须是一个bool型");
                }
                if (!Condition.getValue()) {
                    break;
                }
            }
            blockContext.Initialize(context);
            blockContext.Execute();
            if (blockContext.getIsOver()) {
                break;
            }
            context.Execute(code.LoopExecutable);
        }
    }
    private void ProcessCallForSimple() throws Exception {
        CodeForSimple code = (CodeForSimple)m_scriptInstruction.getOperand0();
        ScriptObject tempVar = ResolveOperand(code.Begin);
        ScriptNumber beginNumber = (ScriptNumber)((tempVar instanceof ScriptNumber) ? tempVar : null);
        if (beginNumber == null) {
            throw new ExecutionException(m_script, "forsimple 初始值必须是number");
        }
        ScriptObject tempVar2 = ResolveOperand(code.Finished);
        ScriptNumber finishedNumber = (ScriptNumber)((tempVar2 instanceof ScriptNumber) ? tempVar2 : null);
        if (finishedNumber == null) {
            throw new ExecutionException(m_script, "forsimple 最大值必须是number");
        }
        int begin = beginNumber.ToInt32();
        int finished = finishedNumber.ToInt32();
        int step;
        if (code.Step != null) {
            ScriptObject tempVar3 = ResolveOperand(code.Step);
            ScriptNumber stepNumber = (ScriptNumber)((tempVar3 instanceof ScriptNumber) ? tempVar3 : null);
            if (stepNumber == null) {
                throw new ExecutionException(m_script, "forsimple Step必须是number");
            }
            step = stepNumber.ToInt32();
        }
        else {
            step = 1;
        }
        java.util.HashMap<String, ScriptObject> variables = code.variables;
        for (int i = begin; i <= finished; i += step) {
        	variables.put(code.Identifier, m_script.CreateNumber(i));
            code.BlockContext.Initialize(this, variables);
            code.BlockContext.Execute();
            if (code.BlockContext.getIsOver()) {
                break;
            }
        }
    }
    private void ProcessCallForeach() throws Exception {
        CodeForeach code = (CodeForeach)m_scriptInstruction.getOperand0();
        ScriptObject loop = ResolveOperand(code.LoopObject);
        if (!loop.getIsFunction()) {
            throw new ExecutionException(m_script, "foreach函数必须返回一个ScriptFunction");
        }
        ScriptObject obj;
        for (; ;) {
            obj = m_script.CreateObject(((ScriptFunction)loop).Call());
            if (obj == null || obj instanceof ScriptNull) {
                return;
            }
            code.Context.Initialize(this, code.Identifier, obj);
            code.Context.Execute();
            if (code.Context.getIsOver()) {
                break;
            }
        }
    }
    private void ProcessCallIf() throws Exception {
        CodeIf code = (CodeIf)m_scriptInstruction.getOperand0();
        if (ProcessCondition(code.If, Executable_Block.If)) {
            return;
        }
        int length = code.ElseIf.size();
        for (int i = 0; i < length; ++i) {
            if (ProcessCondition(code.ElseIf.get(i), Executable_Block.If)) {
                return;
            }
        }
        ProcessCondition(code.Else, Executable_Block.If);
    }
    private boolean ProcessCondition(TempCondition con, Executable_Block block) throws Exception {
        if (con == null) {
            return false;
        }
        if (con.Allow != null) {
            Object b = ResolveOperand(con.Allow).getObjectValue();
            if (b == null || b.equals(false)) return false;
        }
        con.Context.Initialize(this);
        con.Context.Execute();
        return true;
    }
    private void ProcessCallWhile() throws Exception {
        CodeWhile code = (CodeWhile)m_scriptInstruction.getOperand0();
        TempCondition condition = code.While;
        for (; ;) {
            if (!ProcessCondition(condition, Executable_Block.While)) {
                break;
            }
            if (condition.Context.getIsOver()) {
                break;
            }
        }
    }
    private void ProcessCallSwitch() throws Exception {
        CodeSwitch code = (CodeSwitch)m_scriptInstruction.getOperand0();
        ScriptObject obj = ResolveOperand(code.Condition);
        boolean exec = false;
        for (TempCase c : code.Cases) {
            for (Object a : c.Allow) {
                if (a.equals(obj.getObjectValue())) {
                    exec = true;
                    c.Context.Initialize(this);
                    c.Context.Execute();
                    break;
                }
            }
        }
        if (exec == false && code.Default != null) {
            code.Default.Context.Initialize(this);
            code.Default.Context.Execute();
        }
    }
    private void ProcessTry() throws Exception {
        CodeTry code = (CodeTry)m_scriptInstruction.getOperand0();
        try {
            code.TryContext.Initialize(this);
            code.TryContext.Execute();
        }
        catch (InteriorException ex) {
            code.CatchContext.Initialize(this, code.Identifier, ex.obj);
            code.CatchContext.Execute();
        }
        catch (RuntimeException ex) {
            code.CatchContext.Initialize(this, code.Identifier, m_script.CreateObject(ex));
            code.CatchContext.Execute();
        }
    }
    private void ProcessThrow() throws Exception {
        CodeThrow code = (CodeThrow)m_scriptInstruction.getOperand0();
        throw new InteriorException(ResolveOperand(code.obj));
    }
    private void ProcessRet() throws Exception {
        InvokeReturnValue(ResolveOperand(m_scriptInstruction.getOperand0()));
    }
    private void ProcessResolve() throws Exception {
        ResolveOperand(m_scriptInstruction.getOperand0());
    }
    private void ProcessCallBlock() throws Exception {
        ScriptContext context = (ScriptContext)m_scriptInstruction.getValue();
        context.Initialize(this);
        context.Execute();
    }
    private void ProcessCallFunction() throws Exception {
        ParseCall((CodeCallFunction)m_scriptInstruction.getOperand0(), false);
    }
    private void InvokeReturnValue(ScriptObject value) {
        m_Over = true;
        if (SupportReturnValue()) {
            m_returnObject = value;
        }
        else {
            m_parent.InvokeReturnValue(value);
        }
    }
    private void InvokeContinue(CodeObject con) {
        m_Continue = true;
        if (!SupportContinue()) {
            if (m_parent == null) {
                throw new ExecutionException(m_script, "当前模块不支持continue语法");
            }
            m_parent.InvokeContinue(con);
        }
    }
    private void InvokeBreak(CodeObject bre) {
        m_Break = true;
        if (!SupportBreak()) {
            if (m_parent == null) {
                throw new ExecutionException(m_script, "当前模块不支持break语法");
            }
            m_parent.InvokeBreak(bre);
        }
    }
    private ScriptObject ResolveOperand_impl(CodeObject value) throws Exception {
		if (value instanceof CodeScriptObject) {
			return ParseScriptObject((CodeScriptObject)value);
		} else if (value instanceof CodeRegion) {
			return ParseRegion((CodeRegion)value);
		} else if (value instanceof CodeFunction) {
			return ParseFunction((CodeFunction)value);
		} else if (value instanceof CodeCallFunction) {
			return ParseCall((CodeCallFunction)value, true);
		} else if (value instanceof CodeMember) {
			return GetVariable((CodeMember)value);
		} else if (value instanceof CodeArray) {
			return ParseArray((CodeArray)value);
		} else if (value instanceof CodeTable) {
			return ParseTable((CodeTable)value);
		} else if (value instanceof CodeOperator) {
			return ParseOperate((CodeOperator)value);
		} else if (value instanceof CodeTernary) {
			return ParseTernary((CodeTernary)value);
		} else if (value instanceof CodeAssign) {
			return ParseAssign((CodeAssign)value);
		} else if (value instanceof CodeEval) {
			return ParseEval((CodeEval)value);
		}
		return m_script.Null;
    }
    private ScriptObject ResolveOperand(CodeObject value) throws Exception {
        m_script.SetStackInfo(value.StackInfo);
        ScriptObject ret = ResolveOperand_impl(value);
        if (value.Not) {
            ScriptBoolean b = (ScriptBoolean)((ret instanceof ScriptBoolean) ? ret : null);
            if (b == null) {
                throw new ExecutionException(m_script, "Script Object Type [" + ret.getType() + "] is cannot use [!] sign");
            }
            ret = b.Inverse();
        }
        else if (value.Negative) {
            ScriptNumber b = (ScriptNumber)((ret instanceof ScriptNumber) ? ret : null);
            if (b == null) {
                throw new ExecutionException(m_script, "Script Object Type [" + ret.getType() + "] is cannot use [-] sign");
            }
            ret = b.Negative();
        }
        return ret;
    }
    private ScriptObject ParseScriptObject(CodeScriptObject obj) {
        return obj.getObject().clone();
    }
    private ScriptObject ParseRegion(CodeRegion region) throws Exception {
        return ResolveOperand(region.Context);
    }
    private ScriptFunction ParseFunction(CodeFunction func) {
    	return ((ScriptFunction)func.Func.clone()).SetParentVariable(GetContextVariables());
    }
    private ScriptObject ParseCall(CodeCallFunction scriptFunction, boolean needRet) throws Exception {
        ScriptObject obj = ResolveOperand(scriptFunction.Member);
        int num = scriptFunction.Parameters.size();
        ScriptObject[] parameters = new ScriptObject[num];
        for (int i = 0; i < num; ++i) {
            parameters[i] = ResolveOperand(scriptFunction.Parameters.get(i));
        }
        m_script.PushStackInfo();
        Object ret = obj.Call(parameters);
        return needRet ? m_script.CreateObject(ret) : null;
    }
    private ScriptArray ParseArray(CodeArray array) throws Exception {
        ScriptArray ret = m_script.CreateArray();
        int num = array.Elements.size();
        for (int i = 0; i < num; ++i) {
            ret.Add(ResolveOperand(array.Elements.get(i)));
        }
        return ret;
    }
    private ScriptTable ParseTable(CodeTable table) throws Exception {
        ScriptTable ret = m_script.CreateTable();
        for (TableVariable variable : table.Variables) {
            ret.SetValue(variable.key, ResolveOperand(variable.value));
        }
        for (ScriptFunction func : table.Functions) {
            func.SetTable(ret);
            ret.SetValue(func.getName(), func);
        }
        return ret;
    }
    private ScriptObject ParseOperate(CodeOperator operate) throws Exception {
        TokenType type = operate.Operator;
        ScriptObject left = ResolveOperand(operate.Left);
        if (type == TokenType.Plus) {
            ScriptObject right = ResolveOperand(operate.Right);
            if (left instanceof ScriptString || right instanceof ScriptString) {
                return m_script.CreateString(left.toString() + right.toString());
            }
            else if (left instanceof ScriptNumber && right instanceof ScriptNumber) {
                return ((ScriptNumber)left).Compute(TokenType.Plus, (ScriptNumber)right);
            }
            else {
                throw new ExecutionException(m_script, "operate [+] left right is not same type");
            }
        }
        else if (type == TokenType.Minus || type == TokenType.Multiply || type == TokenType.Divide || type == TokenType.Modulo || type == TokenType.InclusiveOr || type == TokenType.Combine || type == TokenType.XOR || type == TokenType.Shr || type == TokenType.Shi) {
            ScriptNumber leftNumber = (ScriptNumber)((left instanceof ScriptNumber) ? left : null);
            if (leftNumber == null) {
                throw new ExecutionException(m_script, "运算符[左边]必须是number类型");
            }
            ScriptObject tempVar = ResolveOperand(operate.Right);
            ScriptNumber rightNumber = (ScriptNumber)((tempVar instanceof ScriptNumber) ? tempVar : null);
            if (rightNumber == null) {
                throw new ExecutionException(m_script, "运算符[右边]必须是number类型");
            }
            return leftNumber.Compute(type, rightNumber);
        }
        else {
            if (left instanceof ScriptBoolean) {
                boolean b1 = ((ScriptBoolean)left).getValue();
                if (type == TokenType.And) {
                    if (b1 == false) {
                        return m_script.False;
                    }
                    ScriptObject tempVar2 = ResolveOperand(operate.Right);
                    ScriptBoolean right = (ScriptBoolean)((tempVar2 instanceof ScriptBoolean) ? tempVar2 : null);
                    if (right == null) {
                        throw new ExecutionException(m_script, "operate [&&] right is not a bool");
                    }
                    return right.getValue() ? m_script.True : m_script.False;
                }
                else if (type == TokenType.Or) {
                    if (b1 == true) {
                        return m_script.True;
                    }
                    ScriptObject tempVar3 = ResolveOperand(operate.Right);
                    ScriptBoolean right = (ScriptBoolean)((tempVar3 instanceof ScriptBoolean) ? tempVar3 : null);
                    if (right == null) {
                        throw new ExecutionException(m_script, "operate [||] right is not a bool");
                    }
                    return right.getValue() ? m_script.True : m_script.False;
                }
                else {
                    ScriptObject tempVar4 = ResolveOperand(operate.Right);
                    ScriptBoolean right = (ScriptBoolean)((tempVar4 instanceof ScriptBoolean) ? tempVar4 : null);
                    if (right == null) {
                        throw new ExecutionException(m_script, "operate [==] [!=] right is not a bool");
                    }
                    boolean b2 = right.getValue();
                    if (type == TokenType.Equal) {
                        return b1 == b2 ? m_script.True : m_script.False;
                    }
                    else if (type == TokenType.NotEqual) {
                        return b1 != b2 ? m_script.True : m_script.False;
                    }
                    else {
                        throw new ExecutionException(m_script, "nonsupport operate [" + type + "]  with bool");
                    }
                }
            }
            else {
                ScriptObject right = ResolveOperand(operate.Right);
                if (left instanceof ScriptNull || right instanceof ScriptNull) {
                    boolean ret = false;
                    if (type == TokenType.Equal) {
                        ret = (left == right);
                    }
                    else if (type == TokenType.NotEqual) {
                        ret = (left != right);
                    }
                    else {
                        throw new ExecutionException(m_script, "nonsupport operate [" + type + "] with null");
                    }
                    return ret ? m_script.True : m_script.False;
                }
                if (type == TokenType.Equal) {
                    return left.getObjectValue().equals(right.getObjectValue()) ? m_script.True : m_script.False;
                }
                else if (type == TokenType.NotEqual) {
                    return !left.getObjectValue().equals(right.getObjectValue()) ? m_script.True : m_script.False;
                }
                if (left.getType() != right.getType()) {
                    throw new ExecutionException(m_script, "[operate] left right is not same type");
                }
                if (left instanceof ScriptString) {
                    return ((ScriptString)left).Compare(type, (ScriptString)right) ? m_script.True : m_script.False;
                }
                else if (left instanceof ScriptNumber) {
                    return ((ScriptNumber)left).Compare(type, (ScriptNumber)right) ? m_script.True : m_script.False;
                }
                else {
                    throw new ExecutionException(m_script, "nonsupport operate [" + type + "] with " + left.getType());
                }
            }
        }
    }
    private ScriptObject ParseTernary(CodeTernary ternary) throws Exception {
        ScriptObject tempVar = ResolveOperand(ternary.Allow);
        ScriptBoolean b = (ScriptBoolean)((tempVar instanceof ScriptBoolean) ? tempVar : null);
        if (b == null) {
            throw new ExecutionException(m_script, "三目运算符 条件必须是一个bool型");
        }
        return b.getValue() ? ResolveOperand(ternary.True) : ResolveOperand(ternary.False);
    }
    private ScriptObject ParseAssign(CodeAssign assign) throws Exception {
        if (assign.AssignType == TokenType.Assign) {
            ScriptObject ret = ResolveOperand(assign.value);
            SetVariable(assign.member, ret);
            return ret;
        }
        else {
            ScriptObject obj = GetVariable(assign.member);
            ScriptString str = (ScriptString)((obj instanceof ScriptString) ? obj : null);
            if (str != null) {
                if (assign.AssignType == TokenType.AssignPlus) {
                    return str.AssignPlus(ResolveOperand(assign.value));
                }
                else {
                    throw new ExecutionException(m_script, "string类型只支持[+=]赋值操作");
                }
            }
            ScriptNumber num = (ScriptNumber)((obj instanceof ScriptNumber) ? obj : null);
            if (num != null) {
                ScriptObject tempVar = ResolveOperand(assign.value);
                ScriptNumber right = (ScriptNumber)((tempVar instanceof ScriptNumber) ? tempVar : null);
                if (right == null) {
                    throw new ExecutionException(m_script, "[+= -=...]值只能为 number类型");
                }
                return num.AssignCompute(assign.AssignType, right);
            }
            throw new ExecutionException(m_script, "[+= -=...]左边值只能为number或者string");
        }
    }
    private ScriptObject ParseEval(CodeEval eval) throws Exception {
        ScriptObject tempVar = ResolveOperand(eval.EvalObject);
        ScriptString obj = (ScriptString)((tempVar instanceof ScriptString) ? tempVar : null);
        if (obj == null) {
            throw new ExecutionException(m_script, "Eval参数必须是一个字符串");
        }
        return m_script.LoadString("", obj.getValue(), this, false);
    }
}