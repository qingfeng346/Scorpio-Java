package Scorpio.Runtime;

import Scorpio.*;
import Scorpio.Runtime.*;
import Scorpio.Compiler.*;
import Scorpio.CodeDom.*;
import Scorpio.CodeDom.Temp.*;
import Scorpio.Exception.*;
import Scorpio.Collections.*;

//执行命令
public class ScriptContext
{
	private Script m_script; //脚本类
	private ScriptContext m_parent; //父级执行命令
	private ScriptExecutable m_scriptExecutable; //执行命令堆栈
	private ScriptInstruction m_scriptInstruction; //当前执行
	private VariableDictionary m_variableDictionary = new VariableDictionary(); //当前作用域所有变量
	private ScriptObject m_returnObject = null; //返回值
	private Executable_Block m_block = Executable_Block.forValue(0); //堆栈类型
	private boolean m_Break = false; //break跳出
	private boolean m_Over = false; //函数是否已经结束
	private int m_InstructionCount = 0; //指令数量
	public ScriptContext(Script script, ScriptExecutable scriptExecutable)
	{
		this(script, scriptExecutable, null, Executable_Block.None);
	}
	public ScriptContext(Script script, ScriptExecutable scriptExecutable, ScriptContext parent, Executable_Block block)
	{
		m_script = script;
		m_parent = parent;
		m_scriptExecutable = scriptExecutable;
		m_variableDictionary.clear();
		m_block = block;
		m_InstructionCount = m_scriptExecutable != null ? m_scriptExecutable.getCount() : 0;
	}
	private boolean getIsBreak()
	{
		return m_Break;
	}
	private boolean getIsOver()
	{
		return m_Break || m_Over;
	}
	public final void Initialize(ScriptContext parent, VariableDictionary variable)
	{
		m_parent = parent;
		m_variableDictionary = variable;
	}
	private void Initialize(String name, ScriptObject obj)
	{
		m_variableDictionary.clear();
		m_variableDictionary.put(name, obj);
	}
	private void Initialize(ScriptContext parent)
	{
		m_parent = parent;
		m_variableDictionary.clear();
	}
	private void ApplyVariableObject(String name)
	{
		if (!m_variableDictionary.containsKey(name))
		{
			m_variableDictionary.put(name, ScriptNull.getInstance());
		}
	}
	private ScriptObject GetVariableObject(String name)
	{
		if (m_variableDictionary.containsKey(name))
		{
			return m_variableDictionary.get(name);
		}
		if (m_parent != null)
		{
			return m_parent.GetVariableObject(name);
		}
		return null;
	}
	private boolean SetVariableObject(String name, ScriptObject obj)
	{
		if (m_variableDictionary.containsKey(name))
		{
			Util.SetObject(m_variableDictionary, name, obj);
			return true;
		}
		if (m_parent != null)
		{
			return m_parent.SetVariableObject(name, obj);
		}
		return false;
	}
	private boolean ContainsVariable(String name)
	{
		if (m_variableDictionary.containsKey(name))
		{
			return true;
		}
		if (m_parent != null)
		{
			return m_parent.ContainsVariable(name);
		}
		return false;
	}
	private ScriptObject GetVariable(CodeMember member) throws Exception
	{
		ScriptObject ret = null;
		if (member.Parent == null)
		{
			String name = member.MemberString;
			ScriptObject obj = GetVariableObject(name);
			ret = (obj == null ? m_script.GetValue(name) : obj);
		}
		else
		{
			ScriptObject parent = ResolveOperand(member.Parent);
			if (parent == null || parent instanceof ScriptNull)
			{
				throw new ExecutionException("GetVariable parent is null");
			}
			if (parent instanceof ScriptArray)
			{
				if (member.Type == MEMBER_TYPE.NUMBER)
				{
					ret = parent.GetValue(member.MemberNumber);
				}
				else if (member.Type == MEMBER_TYPE.OBJECT)
				{
					ScriptObject tempVar = ResolveOperand(member.Member);
					ScriptNumber mem = (ScriptNumber)((tempVar instanceof ScriptNumber) ? tempVar : null);
					if (mem == null)
					{
						throw new ExecutionException("GetVariable Array Element is must a number");
					}
					ret = parent.GetValue(mem.ToInt32());
				}
				else
				{
					throw new ExecutionException("GetVariable Array Element is must a number");
				}
			}
			else if (parent instanceof ScriptTable)
			{
				if (member.Type == MEMBER_TYPE.NUMBER)
				{
					return parent.GetValue(member.MemberNumberObject);
				}
				else if (member.Type == MEMBER_TYPE.STRING)
				{
					return parent.GetValue(member.MemberString);
				}
				else if (member.Type == MEMBER_TYPE.OBJECT)
				{
					ScriptObject mem = ResolveOperand(member.Member);
					if (!(mem instanceof ScriptString || mem instanceof ScriptNumber))
					{
						throw new ExecutionException("GetVariable Table Element is must a string or number");
					}
					ret = parent.GetValue(mem.getObjectValue());
				}
			}
			else if (parent instanceof ScriptUserdata)
			{
				if (member.Type == MEMBER_TYPE.NUMBER)
				{
					return parent.GetValue(member.MemberNumberObject);
				}
				else if (member.Type == MEMBER_TYPE.STRING)
				{
					return parent.GetValue(member.MemberString);
				}
				else if (member.Type == MEMBER_TYPE.OBJECT)
				{
					ScriptObject tempVar2 = ResolveOperand(member.Member);
					ScriptString mem = (ScriptString)((tempVar2 instanceof ScriptString) ? tempVar2 : null);
					if (mem == null)
					{
						throw new ExecutionException("GetVariable Table Element is must a string");
					}
					ret = parent.GetValue(mem.getValue());
				}
				else
				{
					throw new ExecutionException("GetVariable Table Element is must a string");
				}
			}
			else
			{
				throw new ExecutionException("GetVariable member parent is not table or array or userdata");
			}
		}
		if (ret == null)
		{
			throw new ExecutionException("GetVariable member is error");
		}
		if (member.Calc != CALC.NONE)
		{
			ScriptNumber num = (ScriptNumber)((ret instanceof ScriptNumber) ? ret : null);
			if (num == null)
			{
				throw new ExecutionException("++或者--只能应用于Number类型");
			}
			return num.Calc(member.Calc);
		}
		return ret;
	}
	private void SetVariable(CodeMember member, CodeObject obj) throws Exception
	{
		if (member.Parent == null)
		{
			String name = member.MemberString;
			ScriptObject variable = ResolveOperand(obj);
			if (!SetVariableObject(name, variable))
			{
				m_script.SetObjectInternal(name, variable);
			}
		}
		else
		{
			ScriptObject parent = ResolveOperand(member.Parent);
			if (parent == null || parent instanceof ScriptNull)
			{
				throw new ExecutionException("SetVariable parent is null");
			}
			if (parent instanceof ScriptArray)
			{
				if (member.Type == MEMBER_TYPE.NUMBER)
				{
					parent.SetValue(member.MemberNumber, ResolveOperand(obj));
				}
				else if (member.Type == MEMBER_TYPE.OBJECT)
				{
					ScriptObject tempVar = ResolveOperand(member.Member);
					ScriptNumber mem = (ScriptNumber)((tempVar instanceof ScriptNumber) ? tempVar : null);
					if (mem == null)
					{
						throw new ExecutionException("SetVariable Array Element is must a number");
					}
					parent.SetValue(mem.ToInt32(), ResolveOperand(obj));
				}
				else
				{
					throw new ExecutionException("SetVariable Array Element is must a number");
				}
			}
			else if (parent instanceof ScriptTable)
			{
				if (member.Type == MEMBER_TYPE.NUMBER)
				{
					parent.SetValue(member.MemberNumberObject, ResolveOperand(obj));
				}
				else if (member.Type == MEMBER_TYPE.STRING)
				{
					parent.SetValue(member.MemberString, ResolveOperand(obj));
				}
				else if (member.Type == MEMBER_TYPE.OBJECT)
				{
					ScriptObject tempVar2 = ResolveOperand(member.Member);
					ScriptString mem = (ScriptString)((tempVar2 instanceof ScriptString) ? tempVar2 : null);
					if (mem == null)
					{
						throw new ExecutionException("GetVariable Table Element is must a string or number");
					}
					parent.SetValue(mem.getValue(), ResolveOperand(obj));
				}
			}
			else if (parent instanceof ScriptUserdata)
			{
				if (member.Type == MEMBER_TYPE.NUMBER)
				{
					parent.SetValue(member.MemberNumberObject, ResolveOperand(obj));
				}
				else if (member.Type == MEMBER_TYPE.STRING)
				{
					parent.SetValue(member.MemberString, ResolveOperand(obj));
				}
				else if (member.Type == MEMBER_TYPE.OBJECT)
				{
					ScriptObject tempVar3 = ResolveOperand(member.Member);
					ScriptString mem = (ScriptString)((tempVar3 instanceof ScriptString) ? tempVar3 : null);
					if (mem == null)
					{
						throw new ExecutionException("GetVariable Table Element is must a string");
					}
					parent.SetValue(mem.getValue(), ResolveOperand(obj));
				}
				else
				{
					throw new ExecutionException("GetVariable Table Element is must a string");
				}
			}
			else
			{
				throw new ExecutionException("SetVariable member parent is not table or array");
			}
		}
	}
	private void Reset()
	{
		m_returnObject = null;
		m_Over = false;
		m_Break = false;
	}
	public final ScriptObject Execute() throws Exception
	{
		Reset();
		int iInstruction = 0;
		while (iInstruction < m_InstructionCount)
		{
			m_scriptInstruction = m_scriptExecutable.getItem(iInstruction++);
			ExecuteInstruction();
			if (getIsOver())
			{
				break;
			}
		}
		return m_returnObject;
	}
	private ScriptObject Execute(ScriptExecutable executable) throws Exception
	{
		if (executable == null)
		{
			return null;
		}
		Reset();
		int iInstruction = 0;
		int iInstructionCount = executable.getCount();
		while (iInstruction < iInstructionCount)
		{
			m_scriptInstruction = executable.getItem(iInstruction++);
			ExecuteInstruction();
			if (getIsOver())
			{
				break;
			}
		}
		return m_returnObject;
	}
	private void ExecuteInstruction() throws Exception
	{
		switch (m_scriptInstruction.getOpcode())
		{
			case VAR:
				ProcessVar();
				break;
			case MOV:
				ProcessMov();
				break;
			case RET:
				ProcessRet();
				break;
			case CALC:
				ProcessCalc();
				break;
			case EVAL:
				ProcessEval();
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
			case CALL_FOREACH:
				ProcessCallForeach();
				break;
			case CALL_WHILE:
				ProcessCallWhile();
				break;
		}
	}
	private boolean SupportReturnValue()
	{
		return m_block == Executable_Block.Function || m_block == Executable_Block.Context;
	}
	private boolean SupportContinue()
	{
		return m_block == Executable_Block.For || m_block == Executable_Block.Foreach || m_block == Executable_Block.While;
	}
	private boolean SupportBreak()
	{
		return m_block == Executable_Block.For || m_block == Executable_Block.Foreach || m_block == Executable_Block.While;
	}
	private void ProcessVar()
	{
		ApplyVariableObject((String)m_scriptInstruction.getValue());
	}
	private void ProcessMov() throws Exception
	{
		SetVariable((CodeMember)m_scriptInstruction.getOperand0(), m_scriptInstruction.getOperand1());
	}
	private void ProcessContinue()
	{
		InvokeContinue(m_scriptInstruction.getOperand0());
	}
	private void ProcessBreak()
	{
		InvokeBreak(m_scriptInstruction.getOperand0());
	}
	private void ProcessCallFor() throws Exception
	{
		CodeFor code = (CodeFor)m_scriptInstruction.getOperand0();
		ScriptContext context = code.Context;
		ScriptContext blockContext = code.BlockContext;
		context.Initialize(this);
		context.Execute(code.BeginExecutable);
		ScriptBoolean Condition;
		for (; ;)
		{
			if (code.Condition != null)
			{
				Object tempVar = context.ResolveOperand(code.Condition);
				Condition = (ScriptBoolean)((tempVar instanceof ScriptBoolean) ? tempVar : null);
				if (Condition == null)
				{
					throw new ExecutionException("for 跳出条件必须是一个bool型");
				}
				if (!Condition.getValue())
				{
					break;
				}
			}
			blockContext.Initialize(context);
			blockContext.Execute();
			if (blockContext.getIsBreak())
			{
				break;
			}
			context.Execute(code.LoopExecutable);
		}
	}
	private void ProcessCallForeach() throws Exception
	{
		CodeForeach code = (CodeForeach)m_scriptInstruction.getOperand0();
		ScriptObject loop = ResolveOperand(code.LoopObject);
		if (!loop.getIsFunction())
		{
			throw new ExecutionException("foreach函数必须返回一个ScriptFunction");
		}
		ScriptContext context = new ScriptContext(m_script, code.Executable, this, Executable_Block.Foreach);
		for (; ;)
		{
			ScriptObject obj = ((ScriptFunction)loop).Call();
			if (obj == null || obj.getIsNull())
			{
				return;
			}
			context.Initialize(code.Identifier, obj);
			context.Execute();
			if (context.getIsBreak())
			{
				break;
			}
		}
	}
	private void ProcessCallIf() throws Exception
	{
		CodeIf code = (CodeIf)m_scriptInstruction.getOperand0();
		if (ProcessCondition(code.If, Executable_Block.If))
		{
			return;
		}
		int length = code.ElseIf.size();
		for (int i = 0; i < length; ++i)
		{
			if (ProcessCondition(code.ElseIf.get(i), Executable_Block.If))
			{
				return;
			}
		}
		ProcessCondition(code.Else, Executable_Block.If);
	}
	private boolean ProcessCondition(TempCondition con, Executable_Block block) throws Exception
	{
		if (con == null)
		{
			return false;
		}
		if (con.Allow != null)
		{
			ScriptObject tempVar = ResolveOperand(con.Allow);
			ScriptBoolean b = (ScriptBoolean)((tempVar instanceof ScriptBoolean) ? tempVar : null);
			if (b == null)
			{
				throw new ExecutionException("if 条件必须是一个bool型");
			}
			if (b.getValue() == false)
			{
				return false;
			}
		}
		con.Context.Initialize(this);
		con.Context.Execute();
		return true;
	}
	private void ProcessCallWhile() throws Exception
	{
		CodeWhile code = (CodeWhile)m_scriptInstruction.getOperand0();
		TempCondition condition = code.While;
		for (; ;)
		{
			if (!ProcessCondition(condition, Executable_Block.While))
			{
				break;
			}
			if (condition.Context.getIsBreak())
			{
				break;
			}
		}
	}
	private void ProcessRet() throws Exception
	{
		InvokeReturnValue(ResolveOperand(m_scriptInstruction.getOperand0()));
	}
	private void ProcessCalc() throws Exception
	{
		ResolveOperand(m_scriptInstruction.getOperand0());
	}
	private void ProcessEval() throws Exception
	{
		ResolveOperand(m_scriptInstruction.getOperand0());
	}
	private void ProcessCallBlock() throws Exception
	{
		ScriptContext context = (ScriptContext)m_scriptInstruction.getValue();
		context.Initialize(this);
		context.Execute();
	}
	private void ProcessCallFunction() throws Exception
	{
		ParseCall((CodeCallFunction)m_scriptInstruction.getOperand0());
	}
	private void InvokeReturnValue(ScriptObject value)
	{
		m_Over = true;
		if (SupportReturnValue())
		{
			m_returnObject = value;
		}
		else
		{
			m_parent.InvokeReturnValue(value);
		}
	}
	private void InvokeContinue(CodeObject con)
	{
		m_Over = true;
		if (!SupportContinue())
		{
			if (m_parent == null)
			{
				throw new ExecutionException("this block is not support continue");
			}
			m_parent.InvokeContinue(con);
		}
	}
	private void InvokeBreak(CodeObject bre)
	{
		m_Break = true;
		if (!SupportBreak())
		{
			if (m_parent == null)
			{
				throw new ExecutionException("this block is not support break");
			}
			m_parent.InvokeBreak(bre);
		}
	}
	private ScriptObject ResolveOperand_impl(CodeObject value) throws Exception
	{
		if (value instanceof CodeScriptObject)
		{
			return ParseScriptObject((CodeScriptObject)value);
		}
		else if (value instanceof CodeFunction)
		{
			return ParseFunction((CodeFunction)value);
		}
		else if (value instanceof CodeCallFunction)
		{
			return ParseCall((CodeCallFunction)((value instanceof CodeCallFunction) ? value : null));
		}
		else if (value instanceof CodeMember)
		{
			return GetVariable((CodeMember)((value instanceof CodeMember) ? value : null));
		}
		else if (value instanceof CodeArray)
		{
			return ParseArray((CodeArray)((value instanceof CodeArray) ? value : null));
		}
		else if (value instanceof CodeTable)
		{
			return ParseTable((CodeTable)((value instanceof CodeTable) ? value : null));
		}
		else if (value instanceof CodeOperator)
		{
			return ParseOperate((CodeOperator)((value instanceof CodeOperator) ? value : null));
		}
		else if (value instanceof CodeTernary)
		{
			return ParseTernary((CodeTernary)((value instanceof CodeTernary) ? value : null));
		}
		else if (value instanceof CodeEval)
		{
			return ParseEval((CodeEval)((value instanceof CodeEval) ? value : null));
		}
		return ScriptNull.getInstance();
	}
	private ScriptObject ResolveOperand(CodeObject value) throws Exception
	{
		m_script.SetStackInfo(value.StackInfo);
		ScriptObject ret = ResolveOperand_impl(value);
		if (value.Not)
		{
			ScriptBoolean b = (ScriptBoolean)((ret instanceof ScriptBoolean) ? ret : null);
			if (b == null)
			{
				throw new ExecutionException("Script Object Type [" + ret.getType() + "] is cannot use [!] sign");
			}
			ret = b.Inverse();
		}
		else if (value.Negative)
		{
			ScriptNumber b = (ScriptNumber)((ret instanceof ScriptNumber) ? ret : null);
			if (b == null)
			{
				throw new ExecutionException("Script Object Type [" + ret.getType() + "] is cannot use [-] sign");
			}
			ret = b.Negative();
		}
		return ret;
	}
	private ScriptObject ParseScriptObject(CodeScriptObject obj) throws Exception
	{
		return m_script.CreateObject(obj.getObject());
	}
	private ScriptFunction ParseFunction(CodeFunction func)
	{
		func.Func.SetParentContext(this);
		return func.Func;
	}
	private ScriptObject ParseCall(CodeCallFunction scriptFunction) throws Exception
	{
		ScriptObject obj = ResolveOperand(scriptFunction.Member);
		int num = scriptFunction.Parameters.size();
		ScriptObject[] parameters = new ScriptObject[num];
		for (int i = 0; i < num; ++i)
		{
			parameters[i] = ResolveOperand(scriptFunction.Parameters.get(i));
		}
		m_script.PushStackInfo();
		return obj.Call(parameters);
	}
	private ScriptArray ParseArray(CodeArray array) throws Exception
	{
		ScriptArray ret = new ScriptArray();
		int num = array.Elements.size();
		for (int i = 0; i < num; ++i)
		{
			ret.Add(ResolveOperand(array.Elements.get(i)));
		}
		return ret;
	}
	private ScriptTable ParseTable(CodeTable table) throws Exception
	{
		ScriptTable ret = new ScriptTable();
		for (TableVariable variable : table.Variables)
		{
			ret.SetValue(variable.Key, ResolveOperand(variable.Value));
		}
		for (ScriptFunction func : table.Functions)
		{
			func.SetTable(ret);
			ret.SetValue(func.getName(), func);
		}
		return ret;
	}
	private ScriptObject ParseOperate(CodeOperator operate) throws Exception
	{
		TokenType type = operate.Operator;
		ScriptObject left = ResolveOperand(operate.Left);
		if (type == TokenType.Plus)
		{
			ScriptObject right = ResolveOperand(operate.Right);
			if (left instanceof ScriptString || right instanceof ScriptString || (left instanceof ScriptNumber && right instanceof ScriptNumber))
			{
				return left.Plus(right);
			}
			else
			{
				throw new ExecutionException("operate [+] left right is not same type");
			}
		}
		else if (type == TokenType.Minus || type == TokenType.Multiply || type == TokenType.Divide || type == TokenType.Modulo)
		{
			if (!(left instanceof ScriptNumber))
			{
				throw new ExecutionException("operate [+ - * /] left is not number");
			}
			ScriptObject right = ResolveOperand(operate.Right);
			if (!(right instanceof ScriptNumber))
			{
				throw new ExecutionException("operate [+ - * /] right is not number");
			}
			if (operate.Operator == TokenType.Minus)
			{
				return left.Minus(right);
			}
			else if (operate.Operator == TokenType.Multiply)
			{
				return left.Multiply(right);
			}
			else if (operate.Operator == TokenType.Divide)
			{
				return left.Divide(right);
			}
			else if (operate.Operator == TokenType.Modulo)
			{
				return left.Modulo(right);
			}
		}
		else
		{
			if (left instanceof ScriptBoolean)
			{
				if (type == TokenType.And)
				{
					boolean b1 = ((ScriptBoolean)left).getValue();
					if (b1 == false)
					{
						return ScriptBoolean.False;
					}
					ScriptObject tempVar = ResolveOperand(operate.Right);
					ScriptBoolean right = (ScriptBoolean)((tempVar instanceof ScriptBoolean) ? tempVar : null);
					if (right == null)
					{
						throw new ExecutionException("operate [&&] right is not a bool");
					}
					return right.getValue() ? ScriptBoolean.True : ScriptBoolean.False;
				}
				else if (type == TokenType.Or)
				{
					boolean b1 = ((ScriptBoolean)left).getValue();
					if (b1 == true)
					{
						return ScriptBoolean.True;
					}
					ScriptObject tempVar2 = ResolveOperand(operate.Right);
					ScriptBoolean right = (ScriptBoolean)((tempVar2 instanceof ScriptBoolean) ? tempVar2 : null);
					if (right == null)
					{
						throw new ExecutionException("operate [||] right is not a bool");
					}
					return right.getValue() ? ScriptBoolean.True : ScriptBoolean.False;
				}
				else
				{
					boolean b1 = ((ScriptBoolean)left).getValue();
					ScriptObject tempVar3 = ResolveOperand(operate.Right);
					ScriptBoolean right = (ScriptBoolean)((tempVar3 instanceof ScriptBoolean) ? tempVar3 : null);
					if (right == null)
					{
						throw new ExecutionException("operate [==] [!=] right is not a bool");
					}
					boolean b2 = right.getValue();
					if (type == TokenType.Equal)
					{
						return b1 == b2 ? ScriptBoolean.True : ScriptBoolean.False;
					}
					else if (type == TokenType.NotEqual)
					{
						return b1 != b2 ? ScriptBoolean.True : ScriptBoolean.False;
					}
					else
					{
						throw new ExecutionException("nonsupport operate [" + type + "]  with bool");
					}
				}
			}
			else
			{
				ScriptObject right = ResolveOperand(operate.Right);
				if (left instanceof ScriptNull || right instanceof ScriptNull)
				{
					boolean ret = false;
					if (type == TokenType.Equal)
					{
						ret = (left == right);
					}
					else if (type == TokenType.NotEqual)
					{
						ret = (left != right);
					}
					else
					{
						throw new ExecutionException("nonsupport operate [" + type + "] with null");
					}
					return ret ? ScriptBoolean.True : ScriptBoolean.False;
				}
				if (left.getType() != right.getType())
				{
					throw new ExecutionException("[operate] left right is not same type");
				}
				if (left instanceof ScriptString)
				{
					String str1 = ((ScriptString)left).getValue();
					String str2 = ((ScriptString)right).getValue();
					boolean ret = false;
					if (type == TokenType.Equal)
					{
						ret = str1.equals(str2);
					}
					else if (type == TokenType.NotEqual)
					{
						ret = !str1.equals(str2);
					}
					else if (type == TokenType.Greater)
					{
						ret = str1.compareTo(str2) < 0;
					}
					else if (type == TokenType.GreaterOrEqual)
					{
						ret = str1.compareTo(str2) <= 0;
					}
					else if (type == TokenType.Less)
					{
						ret = str1.compareTo(str2) > 0;
					}
					else if (type == TokenType.LessOrEqual)
					{
						ret = str1.compareTo(str2) >= 0;
					}
					else
					{
						throw new ExecutionException("nonsupport operate [" + type + "] with string");
					}
					return ret ? ScriptBoolean.True : ScriptBoolean.False;
				}
				else if (left instanceof ScriptNumber)
				{
					return ((ScriptNumber)left).Compare(type, operate, (ScriptNumber)right) ? ScriptBoolean.True : ScriptBoolean.False;
				}
				else if (left instanceof ScriptEnum)
				{
					boolean ret = false;
					if (type == TokenType.Equal)
					{
						ret = (left.getObjectValue() == right.getObjectValue());
					}
					else if (type == TokenType.NotEqual)
					{
						ret = (left.getObjectValue() != right.getObjectValue());
					}
					else
					{
						throw new ExecutionException("nonsupport operate [" + type + "] with enum");
					}
					return ret ? ScriptBoolean.True : ScriptBoolean.False;
				}
			}
		}
		throw new ExecutionException("错误的操作符号 " + operate.Operator);
	}
	private ScriptObject ParseTernary(CodeTernary ternary) throws Exception
	{
		ScriptObject tempVar = ResolveOperand(ternary.Allow);
		ScriptBoolean b = (ScriptBoolean)((tempVar instanceof ScriptBoolean) ? tempVar : null);
		if (b == null)
		{
			throw new ExecutionException("三目运算符 条件必须是一个bool型");
		}
		return b.getValue() ? ResolveOperand(ternary.True) : ResolveOperand(ternary.False);
	}
	private ScriptObject ParseEval(CodeEval eval) throws Exception
	{
		ScriptObject tempVar = ResolveOperand(eval.EvalObject);
		ScriptString obj = (ScriptString)((tempVar instanceof ScriptString) ? tempVar : null);
		if (obj == null)
		{
			throw new ExecutionException("Eval参数必须是一个字符串");
		}
		return m_script.LoadString("", obj.getValue(), this);
	}
}