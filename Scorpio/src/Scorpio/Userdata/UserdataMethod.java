package Scorpio.Userdata;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import Scorpio.*;
import Scorpio.Exception.*;

/**  一个类的同名函数 
*/
public class UserdataMethod {
    private static class FunctionMethod {
        private int m_Type; //是普通函数还是构造函数
        private java.lang.reflect.Method m_Method; //普通函数对象
        private java.lang.reflect.Constructor<?> m_Constructor; //构造函数对象
        public java.lang.Class<?>[] ParameterType; //所有参数类型
        public boolean Params; //是否是变长参数
        public java.lang.Class<?> ParamType; //变长参数类型
        public Object[] Args; //参数数组（预创建 可以共用）
        public FunctionMethod(java.lang.reflect.Constructor<?> Constructor, java.lang.Class<?>[] ParameterType, java.lang.Class<?> ParamType, boolean Params) {
            m_Type = 0;
            m_Constructor = Constructor;
            this.ParameterType = ParameterType;
            this.ParamType = ParamType;
            this.Params = Params;
            this.Args = new Object[ParameterType.length];
        }
        public FunctionMethod(java.lang.reflect.Method Method, java.lang.Class<?>[] ParameterType, java.lang.Class<?> ParamType, boolean Params) {
            m_Type = 1;
            m_Method = Method;
            this.ParameterType = ParameterType;
            this.ParamType = ParamType;
            this.Params = Params;
            this.Args = new Object[ParameterType.length];
        }
        public final Object invoke(Object obj, java.lang.Class<?> type) throws Exception {
            return m_Type == 1 ? m_Method.invoke(obj, Args) : m_Constructor.newInstance(Args);
        }
    }
    private Script m_Script;				//所在脚本引擎
    private java.lang.Class<?> m_Type; 		//所在类型
    private int m_Count; 					//相同名字函数数量
    private FunctionMethod[] m_Methods; 	//所有函数对象
    private boolean m_IsStatic;				//是否是静态函数
    private String privateMethodName;
    public final String getMethodName() {
        return privateMethodName;
    }
    private void setMethodName(String value) {
        privateMethodName = value;
    }
    public boolean getIsStatic()
    {
    	return m_IsStatic;
    }
    public UserdataMethod(Script script, java.lang.Class<?> type, String methodName, java.lang.reflect.Method[] methods) {
    	m_Script = script;
        m_Type = type;
        m_IsStatic = Modifier.isStatic(methods[0].getModifiers());
        setMethodName(methodName);
    	java.util.ArrayList<FunctionMethod> functionMethod = new java.util.ArrayList<FunctionMethod>();
        boolean Params = false;
        java.lang.Class<?> ParamType = null;
        Method method = null;
        java.util.ArrayList<java.lang.Class<?>> parameters = new java.util.ArrayList<java.lang.Class<?>>();
        int length = methods.length;
        for (int i = 0; i < length; ++i) {
        	method = methods[i];
        	if (!method.getName().equals(methodName))
        		continue;
            Params = false;
            ParamType = null;
            parameters.clear();
            
            Class<?>[] pars = method.getParameterTypes();
            for (Class<?> par : pars) {
				parameters.add(par);
			}
            if (method.isVarArgs()) {
            	Params = true;
            	ParamType = pars[pars.length - 1].getComponentType();
            }
            functionMethod.add(new FunctionMethod(method, parameters.toArray(new java.lang.Class[]{}), ParamType, Params));
        }
        m_Methods = functionMethod.toArray(new FunctionMethod[]{});
        m_Count = m_Methods.length;
    }
    public UserdataMethod(Script script, java.lang.Class<?> type, String methodName, java.lang.reflect.Constructor<?>[] methods) {
    	m_Script = script;
    	m_Type = type;
    	setMethodName(methodName);
    	m_IsStatic = false;
        java.util.ArrayList<FunctionMethod> functionMethod = new java.util.ArrayList<FunctionMethod>();
        boolean Params = false;
        java.lang.Class<?> ParamType = null;
        java.lang.reflect.Constructor<?> method = null;
        java.util.ArrayList<java.lang.Class<?>> parameters = new java.util.ArrayList<java.lang.Class<?>>();
        int length = methods.length;
        for (int i = 0; i < length; ++i) {
            Params = false;
            ParamType = null;
            parameters.clear();
            method = methods[i];
            Class<?>[] pars = method.getParameterTypes();
            for (Class<?> par : pars)
			{
				parameters.add(par);
				if (method.isVarArgs())
				{
					Params = true;
					ParamType = par;
				}
			}
            functionMethod.add(new FunctionMethod(method, parameters.toArray(new java.lang.Class[]{}), ParamType, Params));
        }
        m_Methods = functionMethod.toArray(new FunctionMethod[]{});
        m_Count = m_Methods.length;
    }
    public final Object Call(Object obj, ScriptObject[] parameters) throws Exception {
        if (m_Count == 0) {
            throw new ExecutionException(m_Script, "找不到函数 [" + getMethodName() + "]");
        }
        FunctionMethod methodInfo = null;
        if (m_Count == 1) {
            methodInfo = m_Methods[0];
        } else {
            for (FunctionMethod method : m_Methods) {
                if (Util.CanChangeType(parameters, method.ParameterType)) {
                    methodInfo = method;
                    break;
                }
            }
        }
        try {
	        if (methodInfo != null && !methodInfo.Params) {
	        	int length = methodInfo.ParameterType.length;
	            Object[] objs = methodInfo.Args;
	            for (int i = 0; i < length; i++)
	                objs[i] = Util.ChangeType(m_Script, parameters[i], methodInfo.ParameterType[i]);
	            return methodInfo.invoke(obj, m_Type);
	        }
	        else {
	            for (FunctionMethod method : m_Methods) {
	                int length = method.ParameterType.length;
	                if (method.Params && parameters.length >= length - 1) {
	                    boolean fit = true;
	                    for (int i = 0; i < parameters.length; ++i) {
	                        if (!Util.CanChangeType(parameters[i], i >= length - 1 ? method.ParamType : method.ParameterType[i])) {
	                            fit = false;
	                            break;
	                        }
	                    }
	                    if (fit) {
	                        Object[] objs = method.Args;
	                        for (int i = 0; i < length - 1; ++i)
	                            objs[i] = Util.ChangeType(m_Script, parameters[i], method.ParameterType[i]);
	                        Object array = Array.newInstance(method.ParamType, parameters.length - length + 1);
	                        for (int i = length - 1; i < parameters.length; ++i)
	                        	Array.set(array, i - length + 1, Util.ChangeType(m_Script, parameters[i], method.ParamType));
	                        objs[length - 1] = array;
	                        return method.invoke(obj, m_Type);
	                    }
	                }
	            }
	        }
        } catch (Exception e) { 
        	throw new ExecutionException(m_Script, "Type[" + m_Type.toString() + "] 调用函数出错 [" + getMethodName() + "] : " + e.getMessage());
        }
        throw new ExecutionException(m_Script, "Type[" + m_Type.toString() + "] 找不到合适的函数 [" + getMethodName() + "]");
    }
}