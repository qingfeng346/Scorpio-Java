package Scorpio.Variable;

import Scorpio.Script;
import Scorpio.ScriptArray;
import Scorpio.ScriptNull;
import Scorpio.ScriptObject;
import Scorpio.Runtime.*;
import Scorpio.Collections.*;

public class ScorpioScriptFunction
{
	private Script m_script; //脚本系统
	private java.util.ArrayList<String> m_listParameters; //参数
	private ScriptExecutable m_scriptExecutable; //函数执行命令
	private ScriptContext m_parentContext; //父级上下文
	private ScriptContext m_Context; //执行上下文
	private int m_ParameterCount; //参数个数
	private ScriptArray m_ParamsArray; //不定参数组
	private boolean m_Params; //是否是不定参函数
	public ScorpioScriptFunction(Script script, java.util.ArrayList<String> listParameters, ScriptExecutable scriptExecutable, boolean bParams)
	{
		this.m_script = script;
		this.m_listParameters = new java.util.ArrayList<String>(listParameters);
		this.m_scriptExecutable = scriptExecutable;
		this.m_ParameterCount = listParameters.size();
		this.m_Params = bParams;
		this.m_ParamsArray = bParams ? new ScriptArray() : null;
		this.m_Context = new ScriptContext(m_script, m_scriptExecutable, null, Executable_Block.Function);
	}
	public final void SetParentContext(ScriptContext context)
	{
		m_parentContext = context;
	}
	public final boolean getParams()
	{
		return m_Params;
	}
	public final int getParameterCount()
	{
		return m_ParameterCount;
	}
	public final ScriptObject Call(VariableDictionary objs, ScriptObject[] parameters) throws Exception
	{
		int length = parameters.length;
		if (m_Params)
		{
			m_ParamsArray.Clear();
			for (int i = 0; i < m_ParameterCount - 1; ++i)
			{
				objs.put(m_listParameters.get(i), (parameters != null && length > i) ? parameters[i] : ScriptNull.getInstance());
			}
			for (int i = m_ParameterCount - 1; i < length; ++i)
			{
				m_ParamsArray.Add(parameters[i]);
			}
			objs.put(m_listParameters.get(m_ParameterCount - 1), m_ParamsArray);
		}
		else
		{
			for (int i = 0; i < m_ParameterCount; ++i)
			{
				objs.put(m_listParameters.get(i), (parameters != null && length > i) ? parameters[i] : ScriptNull.getInstance());
			}
		}
		m_Context.Initialize(m_parentContext, objs);
		return m_Context.Execute();
	}
}