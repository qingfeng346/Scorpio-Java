package Scorpio.Runtime;

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
    public ScriptContext(Script script, ScriptExecutable scriptExecutable, Executable_Block block) {
        this(script, scriptExecutable, null, block);
    }
    public ScriptContext(Script script, ScriptExecutable scriptExecutable, ScriptContext parent, Executable_Block block) {
        m_script = script;
        m_parent = parent;
        m_scriptExecutable = scriptExecutable;
        m_variableDictionary.clear();
        m_block = block;
        m_InstructionCount = m_scriptExecutable != null ? m_scriptExecutable.getCount() : 0;
    }
    private boolean getIsOver() {
        return m_Break || m_Over;
    }
    private boolean getIsExecuted() {
        return m_Break || m_Over || m_Continue;
    }
    public final void Initialize(ScriptContext parent, java.util.HashMap<String, ScriptObject> variable) {
        m_parent = parent;
        m_variableDictionary.clear();
        for (java.util.Map.Entry<String, ScriptObject> pair : variable.entrySet()) {
            m_variableDictionary.put(pair.getKey(), pair.getValue());
        }
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
    //初始化所有数据 每次调用 Execute 调用
    private void Reset() {
        m_returnObject = null;
        m_Over = false;
        m_Break = false;
        m_Continue = false;
    }
    private void ApplyVariableObject(String name) {
        if (!m_variableDictionary.containsKey(name)) {
            m_variableDictionary.put(name, m_script.getNull());
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
    private Object GetMember(CodeMember member) {
        return member.Type == MEMBER_TYPE.VALUE ? member.MemberValue : ResolveOperand(member.MemberObject).getObjectValue();
    }
    private ScriptObject GetVariable(CodeMember member) {
        ScriptObject ret = null;
        if (member.Parent == null) {
            String name = (String)member.MemberValue;
            ScriptObject obj = GetVariableObject(name);
            ret = (obj == null ? m_script.GetValue(name) : obj);
        }
        else {
            ret = ResolveOperand(member.Parent);
            m_script.SetStackInfo(member.StackInfo);
            ret = ret.GetValue(GetMember(member));
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
    private void SetVariable(CodeMember member, ScriptObject variable) {
        if (member.Parent == null) {
            String name = (String)member.MemberValue;
            variable.setName(name);
            if (!SetVariableObject(name, variable)) {
                m_script.SetObjectInternal(name, variable);
            }
        }
        else {
            ResolveOperand(member.Parent).SetValue(GetMember(member), variable);
        }
    }
    public final ScriptObject Execute() {
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
    private ScriptObject Execute(ScriptExecutable executable) {
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
    private void ExecuteInstruction() {
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
    private void ProcessMov() {
        CodeObject tempVar = m_scriptInstruction.getOperand0();
        SetVariable((CodeMember)((tempVar instanceof CodeMember) ? tempVar : null), ResolveOperand(m_scriptInstruction.getOperand1()));
    }
    private void ProcessContinue() {
        InvokeContinue(m_scriptInstruction.getOperand0());
    }
    private void ProcessBreak() {
        InvokeBreak(m_scriptInstruction.getOperand0());
    }
    private void ProcessCallFor() {
        CodeFor code = (CodeFor)m_scriptInstruction.getOperand0();
        ScriptContext context = code.GetContext();
        context.Initialize(this);
        context.Execute(code.BeginExecutable);
        for (; ;) {
            if (code.Condition != null) {
                if (!context.ResolveOperand(code.Condition).LogicOperation()) {
                    break;
                }
            }
            ScriptContext blockContext = code.GetBlockContext();
            blockContext.Initialize(context);
            blockContext.Execute();
            if (blockContext.getIsOver()) {
                break;
            }
            context.Execute(code.LoopExecutable);
        }
    }
    private void ProcessCallForSimple() {
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
        ScriptContext context;
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
        for (int i = begin; i <= finished; i += step) {
            context = code.GetBlockContext();
            context.Initialize(this, code.Identifier, m_script.CreateNumber(i));
            context.Execute();
            if (context.getIsOver()) {
                break;
            }
        }
    }
    private void ProcessCallForeach() {
        CodeForeach code = (CodeForeach)m_scriptInstruction.getOperand0();
        ScriptObject loop = ResolveOperand(code.LoopObject);
        if (!(loop instanceof ScriptFunction)) {
            throw new ExecutionException(m_script, "foreach函数必须返回一个ScriptFunction");
        }
        Object obj;
        ScriptContext context;
        ScriptFunction func = (ScriptFunction)loop;
        for (; ;) {
            context = code.GetBlockContext();
            obj = func.Call();
            if (obj == null) {
                return;
            }
            context.Initialize(this, code.Identifier, m_script.CreateObject(obj));
            context.Execute();
            if (context.getIsOver()) {
                break;
            }
        }
    }
    private void ProcessCallIf() {
        CodeIf code = (CodeIf)m_scriptInstruction.getOperand0();
        if (ProcessCondition(code.If, code.If.GetContext(), Executable_Block.If)) {
            return;
        }
        int length = code.ElseIfCount;
        for (int i = 0; i < length; ++i) {
            if (ProcessCondition(code.ElseIf[i], code.ElseIf[i].GetContext(), Executable_Block.If)) {
                return;
            }
        }
        if (code.Else != null) {
            ProcessCondition(code.Else, code.Else.GetContext(), Executable_Block.If);
        }
    }
    private boolean ProcessCondition(TempCondition con, ScriptContext context, Executable_Block block) {
        if (con == null) {
            return false;
        }
        if (con.Allow != null) {
            Object b = ResolveOperand(con.Allow).getObjectValue();
            if (b == null || b.equals(false)) {
                return false;
            }
        }
        context.Initialize(this);
        context.Execute();
        return true;
    }
    private void ProcessCallWhile() {
        CodeWhile code = (CodeWhile)m_scriptInstruction.getOperand0();
        TempCondition condition = code.While;
        for (; ;) {
            ScriptContext context = condition.GetContext();
            if (!ProcessCondition(condition, context, Executable_Block.While)) {
                break;
            }
            if (context.getIsOver()) {
                break;
            }
        }
    }
    private void ProcessCallSwitch() {
        CodeSwitch code = (CodeSwitch)m_scriptInstruction.getOperand0();
        ScriptObject obj = ResolveOperand(code.Condition);
        boolean exec = false;
        for (TempCase c : code.Cases) {
            for (CodeObject all : c.Allow) {
                if (ResolveOperand(all).equals(obj)) {
                    exec = true;
                    ScriptContext context = c.GetContext();
                    context.Initialize(this);
                    context.Execute();
                    break;
                }
            }
            if (exec) {
                break;
            }
        }
        if (exec == false && code.Default != null) {
            ScriptContext context = code.Default.GetContext();
            context.Initialize(this);
            context.Execute();
        }
    }
    private void ProcessTry() {
        CodeTry code = (CodeTry)m_scriptInstruction.getOperand0();
        try {
            ScriptContext context = code.GetTryContext();
            context.Initialize(this);
            context.Execute();
        }
        catch (InteriorException ex) {
            ScriptContext context = code.GetCatchContext();
            context.Initialize(this, code.Identifier, ex.obj);
            context.Execute();
        }
        catch (RuntimeException ex) {
            ScriptContext context = code.GetCatchContext();
            context.Initialize(this, code.Identifier, m_script.CreateObject(ex));
            context.Execute();
        }
    }
    private void ProcessThrow() {
        CodeThrow code = (CodeThrow)m_scriptInstruction.getOperand0();
        throw new InteriorException(ResolveOperand(code.obj));
    }
    private void ProcessRet() {
        if (m_scriptInstruction.getOperand0() == null) {
            InvokeReturnValue(null);
        }
        else {
            InvokeReturnValue(ResolveOperand(m_scriptInstruction.getOperand0()));
        }
    }
    private void ProcessResolve() {
        ResolveOperand(m_scriptInstruction.getOperand0());
    }
    private void ProcessCallBlock() {
        ScriptContext context = new ScriptContext(m_script, (ScriptExecutable)m_scriptInstruction.getValue());
        context.Initialize(this);
        context.Execute();
    }
    private void ProcessCallFunction() {
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
    private ScriptObject ResolveOperand_impl(CodeObject value) {
        if (value instanceof CodeScriptObject) {
            return ParseScriptObject((CodeScriptObject)value);
        }
        else if (value instanceof CodeRegion) {
            return ParseRegion((CodeRegion)value);
        }
        else if (value instanceof CodeFunction) {
            return ParseFunction((CodeFunction)value);
        }
        else if (value instanceof CodeCallFunction) {
            return ParseCall((CodeCallFunction)value, true);
        }
        else if (value instanceof CodeMember) {
            return GetVariable((CodeMember)value);
        }
        else if (value instanceof CodeArray) {
            return ParseArray((CodeArray)value);
        }
        else if (value instanceof CodeTable) {
            return ParseTable((CodeTable)value);
        }
        else if (value instanceof CodeOperator) {
            return ParseOperate((CodeOperator)value);
        }
        else if (value instanceof CodeTernary) {
            return ParseTernary((CodeTernary)value);
        }
        else if (value instanceof CodeAssign) {
            return ParseAssign((CodeAssign)value);
        }
        else if (value instanceof CodeEval) {
            return ParseEval((CodeEval)value);
        }
        return m_script.getNull();
    }
    private ScriptObject ResolveOperand(CodeObject value) {
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
    private ScriptObject ParseRegion(CodeRegion region) {
        return ResolveOperand(region.Context);
    }
    private ScriptFunction ParseFunction(CodeFunction func) {
        return ((ScriptFunction)func.Func.clone()).SetParentContext(this);
    }
    private ScriptObject ParseCall(CodeCallFunction scriptFunction, boolean needRet) {
        ScriptObject obj = ResolveOperand(scriptFunction.Member);
        int num = scriptFunction.ParametersCount;
        ScriptObject[] parameters = new ScriptObject[num];
        for (int i = 0; i < num; ++i) {
            parameters[i] = ResolveOperand(scriptFunction.Parameters[i]);
        }
        m_script.PushStackInfo();
        Object ret = obj.Call(parameters);
        return needRet ? m_script.CreateObject(ret) : null;
    }
    private ScriptArray ParseArray(CodeArray array) {
        ScriptArray ret = m_script.CreateArray();
        int num = array.Elements.size();
        for (int i = 0; i < num; ++i) {
            ret.Add(ResolveOperand(array.Elements.get(i)));
        }
        return ret;
    }
    private ScriptTable ParseTable(CodeTable table) {
        ScriptTable ret = m_script.CreateTable();
        for (CodeTable.TableVariable variable : table.Variables) {
            ret.SetValue(variable.key, ResolveOperand(variable.value));
        }
        for (ScriptFunction func : table.Functions) {
            func.SetTable(ret);
            ret.SetValue(func.getName(), func);
        }
        return ret;
    }
    private ScriptObject ParseOperate(CodeOperator operate) {
        TokenType type = operate.Operator;
        ScriptObject left = ResolveOperand(operate.Left);
        switch (type) {
        case Plus:
            ScriptObject right = ResolveOperand(operate.Right);
            if (left instanceof ScriptString || right instanceof ScriptString) {
                return m_script.CreateString(left.toString() + right.toString());
            }
            return left.Compute(type, right);
        case And:
            if (!left.LogicOperation()) {
                return m_script.getFalse();
            }
            return m_script.GetBoolean(ResolveOperand(operate.Right).LogicOperation());
        case Or:
            if (left.LogicOperation()) {
                return m_script.getTrue();
            }
            return m_script.GetBoolean(ResolveOperand(operate.Right).LogicOperation());
        case Equal:
            return m_script.GetBoolean(left.equals(ResolveOperand(operate.Right)));
        case NotEqual:
            return m_script.GetBoolean(!left.equals(ResolveOperand(operate.Right)));
        case Greater:
        case GreaterOrEqual:
        case Less:
        case LessOrEqual:
            return m_script.GetBoolean(left.Compare(type, ResolveOperand(operate.Right)));
        default:
            return left.Compute(type, ResolveOperand(operate.Right));
        }
    }
    private ScriptObject ParseTernary(CodeTernary ternary) {
        return ResolveOperand(ternary.Allow).LogicOperation() ? ResolveOperand(ternary.True) : ResolveOperand(ternary.False);
    }
    private ScriptObject ParseAssign(CodeAssign assign) {
        if (assign.AssignType == TokenType.Assign) {
            ScriptObject ret = ResolveOperand(assign.value);
            SetVariable(assign.member, ret);
            return ret;
        }
        else {
            return GetVariable(assign.member).AssignCompute(assign.AssignType, ResolveOperand(assign.value));
        }
    }
    private ScriptObject ParseEval(CodeEval eval) {
        ScriptObject tempVar = ResolveOperand(eval.EvalObject);
        ScriptString obj = (ScriptString)((tempVar instanceof ScriptString) ? tempVar : null);
        if (obj == null) {
            throw new ExecutionException(m_script, "Eval参数必须是一个字符串");
        }
        return m_script.LoadString("", obj.getValue(), this, false);
    }
}