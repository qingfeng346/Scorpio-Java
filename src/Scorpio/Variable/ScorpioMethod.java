package Scorpio.Variable;
import java.lang.reflect.InvocationTargetException;

import Scorpio.ScriptObject;
import Scorpio.Util;
import Scorpio.Exception.*;

public class ScorpioMethod
{
	private static class FunctionMethod
	{
		public java.lang.reflect.Method Method;
		public java.lang.reflect.Constructor Constructor;
		public java.lang.Class[] ParameterType;
		private int type;
		public FunctionMethod(java.lang.reflect.Method Method, java.lang.Class[] ParameterType)
		{
			type = 0;
			this.Method = Method;
			this.ParameterType = ParameterType;
		}
		public FunctionMethod(java.lang.reflect.Constructor Constructor, java.lang.Class[] ParameterType)
		{
			type = 1;
			this.Constructor = Constructor;
			this.ParameterType = ParameterType;
		}
		public final Object invoke(Object obj, Object[] parameters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
		{
			return type == 0 ? Method.invoke(obj, parameters) : Constructor.newInstance(parameters);
		}
	}
	private Object m_Object;
	private int m_Count;
	private FunctionMethod[] m_Methods;
	private String privateMethodName;
	public final String getMethodName()
	{
		return privateMethodName;
	}
	private void setMethodName(String value)
	{
		privateMethodName = value;
	}
	public ScorpioMethod(java.lang.Class type, String methodName)
	{
		this(type, methodName, null);
	}
	public ScorpioMethod(java.lang.Class type, String methodName, Object obj)
	{
		m_Object = obj;
		setMethodName(methodName);
		java.util.ArrayList<FunctionMethod> functionMethod = new java.util.ArrayList<FunctionMethod>();
		java.lang.reflect.Method[] methods = type.getMethods();
		int length = methods.length;
		java.util.ArrayList<java.lang.Class> parameters = new java.util.ArrayList<java.lang.Class>();
		for (int i = 0; i < length;++i)
		{
			java.lang.reflect.Method method = methods[i];
			if (method.getName().equals(methodName))
			{
				parameters.clear();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				Class<?>[] pars = methods[i].getParameterTypes();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				for (Class<?> par : pars)
				{
					parameters.add(par);
				}
				functionMethod.add(new FunctionMethod(method, parameters.toArray(new java.lang.Class[]{})));
			}
		}
		m_Methods = functionMethod.toArray(new FunctionMethod[]{});
		m_Count = m_Methods.length;
	}
	public ScorpioMethod(String typeName, java.lang.reflect.Constructor[] methods)
	{
		setMethodName(typeName);
		java.util.ArrayList<FunctionMethod> functionMethod = new java.util.ArrayList<FunctionMethod>();
		int length = methods.length;
		java.util.ArrayList<java.lang.Class> parameters = new java.util.ArrayList<java.lang.Class>();
		for (int i = 0; i < length; ++i)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			java.lang.reflect.Constructor method = methods[i];
			parameters.clear();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			Class<?>[] pars = methods[i].getParameterTypes();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (Class<?> par : pars)
			{
				parameters.add(par);
			}
			functionMethod.add(new FunctionMethod(method, parameters.toArray(new java.lang.Class[]{})));
		}
		m_Methods = functionMethod.toArray(new FunctionMethod[]{});
		m_Count = m_Methods.length;
	}
	public final Object Call(ScriptObject[] parameters) throws Exception
	{
		if (m_Count == 0)
		{
			throw new ScriptException("Method [" + getMethodName() + "] is cannot find");
		}
		FunctionMethod methodInfo = null;
		if (m_Count == 1)
		{
			methodInfo = m_Methods[0];
			if (parameters.length != methodInfo.ParameterType.length)
			{
				throw new ScriptException("Method [" + getMethodName() + "] is cannot find fit");
			}
		}
		else
		{
			for (int i = 0; i < m_Methods.length; ++i)
			{
				FunctionMethod method = m_Methods[i];
				if (Util.CanChangeType(parameters, method.ParameterType))
				{
					methodInfo = method;
					break;
				}
			}
			if (methodInfo == null)
			{
				throw new ScriptException("Method [" + getMethodName() + "] is cannot find fit");
			}
		}
		int length = methodInfo.ParameterType.length;
		Object[] objs = new Object[length];
		for (int i = 0; i < length; i++)
		{
			objs[i] = Util.ChangeType(parameters[i], methodInfo.ParameterType[i]);
		}
		return methodInfo.invoke(m_Object, objs);
	}
}