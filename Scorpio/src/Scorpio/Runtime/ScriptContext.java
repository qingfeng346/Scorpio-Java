package Scorpio.Runtime;

import Scorpio.*;
import Scorpio.Compiler.*;
import Scorpio.CodeDom.*;
import Scorpio.CodeDom.Temp.*;
import Scorpio.Exception.*;
import Scorpio.Function.*;

//执行命令
//注意事项:
//所有调用另一个程序集的地方 都要new一个新的 否则递归调用会相互影响
public class ScriptContext {
    private Script m_script; //脚本类
    private ScriptContext m_parent; //父级执行命令
    private ScriptInstruction[] m_scriptInstructions; //指令集
    private ScriptInstruction m_scriptInstruction; //当前执行的指令
    private int m_InstructionCount; //指令数量
    private Executable_Block m_block = Executable_Block.forValue(0); //指令集类型
    private java.util.HashMap<String, ScriptObject> m_variableDictionary; //当前作用域所有变量
    private ScriptObject m_returnObject = null; //返回值
    private boolean m_Break = false; //break跳出
    private boolean m_Continue = false; //continue跳出
    private boolean m_Over = false; //函数是否已经结束

    public ScriptContext(Script script, ScriptExecutable scriptExecutable) {
        this(script, scriptExecutable, null, Executable_Block.None);
    }
    public ScriptContext(Script script, ScriptExecutable scriptExecutable, ScriptContext parent) {
        this(script, scriptExecutable, parent, Executable_Block.None);
    }
    public ScriptContext(Script script, ScriptExecutable scriptExecutable, ScriptContext parent, Executable_Block block) {
        m_script = script;
        m_parent = parent;
        m_block = block;
        m_variableDictionary = new java.util.HashMap<String, ScriptObject>();
        if (scriptExecutable != null) {
            m_scriptInstructions = scriptExecutable.getScriptInstructions();
            m_InstructionCount = m_scriptInstructions.length;
        }
    }
    private boolean getIsOver() {
        return m_Break || m_Over;
    }
    private boolean getIsExecuted() {
        return m_Break || m_Over || m_Continue;
    }
    public final void Initialize(java.util.HashMap<String, ScriptObject> variable) {
        for (java.util.Map.Entry<String, ScriptObject> pair : variable.entrySet()) {
            m_variableDictionary.put(pair.getKey(), pair.getValue());
        }
    }
    private void Initialize(String name, ScriptObject obj) {
        m_variableDictionary.put(name, obj);
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
            ret.setName(name);
        }
        else {
            ret = ResolveOperand(member.Parent);
//                此处设置一下堆栈位置 否则 函数返回值取值出错会报错位置 例如  
//                    function Get() { 
//                        return null 
//                    } 
//                    Get().a
//                    
//                上述代码报错会报道 return null 那一行 但实际出错 是 .a 的时候 下面这句话就是把堆栈设置回 .a 那一行
//                
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
            m_scriptInstruction = m_scriptInstructions[iInstruction++];
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
        ScriptInstruction[] scriptInstructions = executable.getScriptInstructions();
        int iInstruction = 0;
        int iInstructionCount = scriptInstructions.length;
        while (iInstruction < iInstructionCount) {
            m_scriptInstruction = scriptInstructions[iInstruction++];
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
        ScriptContext context = new ScriptContext(m_script, null, this, Executable_Block.For);
        context.Execute(code.BeginExecutable);
        for (; ;) {
            if (code.Condition != null) {
                if (!context.ResolveOperand(code.Condition).LogicOperation()) {
                    break;
                }
            }
            ScriptContext blockContext = new ScriptContext(m_script, code.BlockExecutable, context, Executable_Block.For);
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
        ScriptContext context;
        for (int i = begin; i <= finished; i += step) {
            context = new ScriptContext(m_script, code.BlockExecutable, this, Executable_Block.For);
            context.Initialize(code.Identifier, m_script.CreateNumber(i));
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
        ScriptFunction func = (ScriptFunction)loop;
        ScriptContext context;
        for (; ;) {
            obj = func.Call();
            if (obj == null) {
                return;
            }
            context = new ScriptContext(m_script, code.BlockExecutable, this, Executable_Block.Foreach);
            context.Initialize(code.Identifier, m_script.CreateObject(obj));
            context.Execute();
            if (context.getIsOver()) {
                break;
            }
        }
    }
    private void ProcessCallIf() {
        CodeIf code = (CodeIf)m_scriptInstruction.getOperand0();
        if (ProcessAllow(code.If)) {
            ProcessCondition(code.If);
            return;
        }
        for (TempCondition ElseIf : code.ElseIf) {
            if (ProcessAllow(ElseIf)) {
                ProcessCondition(ElseIf);
                return;
            }
        }
        if (code.Else != null && ProcessAllow(code.Else)) {
            ProcessCondition(code.Else);
        }
    }
    private boolean ProcessAllow(TempCondition con) {
        if (con.Allow != null && !ResolveOperand(con.Allow).LogicOperation()) {
            return false;
        }
        return true;
    }
    private void ProcessCondition(TempCondition condition) {
        new ScriptContext(m_script, condition.Executable, this, condition.Block).Execute();
    }
    private void ProcessCallWhile() {
        CodeWhile code = (CodeWhile)m_scriptInstruction.getOperand0();
        TempCondition condition = code.While;
        ScriptContext context;
        for (; ;) {
            if (!ProcessAllow(condition)) {
                break;
            }
            context = new ScriptContext(m_script, condition.Executable, this, Executable_Block.While);
            context.Execute();
            if (context.getIsOver()) {
                break;
            }
        }
    }
    private void ProcessCallSwitch() {
        CodeSwitch code = (CodeSwitch)m_scriptInstruction.getOperand0();
        ScriptObject obj = ResolveOperand(code.Condition);
        boolean exec = false;
        for (TempCase Case : code.Cases) {
            for (CodeObject allow : Case.Allow) {
                if (ResolveOperand(allow).equals(obj)) {
                    exec = true;
                    new ScriptContext(m_script, Case.Executable, this, Executable_Block.Switch).Execute();
                    break;
                }
            }
            if (exec) {
                break;
            }
        }
        if (exec == false && code.Default != null) {
            new ScriptContext(m_script, code.Default.Executable, this, Executable_Block.Switch).Execute();
        }
    }
    private void ProcessTry() {
        CodeTry code = (CodeTry)m_scriptInstruction.getOperand0();
        try {
            new ScriptContext(m_script, code.TryExecutable, this).Execute();
        }
        catch (InteriorException ex) {
            ScriptContext context = new ScriptContext(m_script, code.CatchExecutable, this);
            context.Initialize(code.Identifier, ex.obj);
            context.Execute();
        }
        catch (RuntimeException ex) {
            ScriptContext context = new ScriptContext(m_script, code.CatchExecutable, this);
            context.Initialize(code.Identifier, m_script.CreateObject(ex));
            context.Execute();
        }
    }
    private void ProcessThrow() {
        throw new InteriorException(ResolveOperand(((CodeThrow)m_scriptInstruction.getOperand0()).obj));
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
        ParseCallBlock((CodeCallBlock)m_scriptInstruction.getOperand0());
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
        //此处原先使用Clone 是因为 number 和 string 有自运算的操作 会影响常量 但是现在设置变量会调用Assign() 基础类型会自动复制一次 所以去掉clone
        return obj.getObject();
        //return obj.Object.Clone();
    }
    private ScriptObject ParseRegion(CodeRegion region) {
        return ResolveOperand(region.Context);
    }
    private ScriptFunction ParseFunction(CodeFunction func) {
        return func.Func.Create().SetParentContext(this);
    }
    private void ParseCallBlock(CodeCallBlock block) {
        new ScriptContext(m_script, block.Executable, this).Execute();
    }
    private ScriptObject ParseCall(CodeCallFunction scriptFunction, boolean needRet) {
        ScriptObject obj = ResolveOperand(scriptFunction.Member);
        int num = scriptFunction.ParametersCount;
        ScriptObject[] parameters = new ScriptObject[num];
        for (int i = 0; i < num; ++i) {
            //此处要调用Assign 如果传入number string等基础类型  在函数内自运算的话 会影响 传入的值
            parameters[i] = ResolveOperand(scriptFunction.Parameters[i]).Assign();
        }
        m_script.PushStackInfo();
        Object ret = obj.Call(parameters);
        return needRet ? m_script.CreateObject(ret) : null;
    }
    private ScriptArray ParseArray(CodeArray array) {
        ScriptArray ret = m_script.CreateArray();
        for (CodeObject ele : array.Elements) {
            ret.Add(ResolveOperand(ele));
        }
        return ret;
    }
    private ScriptTable ParseTable(CodeTable table) {
        ScriptTable ret = m_script.CreateTable();
        for (CodeTable.TableVariable variable : table.Variables) {
            ret.SetValue(variable.key, ResolveOperand(variable.value));
        }
        for (ScriptScriptFunction func : table.Functions) {
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
        case Minus:
        case Multiply:
        case Divide:
        case Modulo:
        case InclusiveOr:
        case Combine:
        case XOR:
        case Shr:
        case Shi:
            return left.Compute(type, ResolveOperand(operate.Right));
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
            throw new ExecutionException(m_script, "不支持的运算符 " + type);
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