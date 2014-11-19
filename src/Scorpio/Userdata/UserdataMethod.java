package Scorpio.Userdata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Scorpio.*;
import Scorpio.Exception.*;

/**  一个类的同名函数 
*/
public class UserdataMethod {
    private static class FunctionMethod {
        private int m_Type; //是普通函数还是构造函数
        private java.lang.reflect.Method m_Method; //普通函数对象
        private java.lang.reflect.Constructor m_Constructor; //构造函数对象
        public java.lang.Class[] ParameterType; //所有参数类型
        public boolean Params; //是否是变长参数
        public java.lang.Class ParamType; //变长参数类型
        public Object[] Args; //参数数组（预创建 可以共用）
        public FunctionMethod(java.lang.reflect.Constructor Constructor, java.lang.Class[] ParameterType, java.lang.Class ParamType, boolean Params) {
            m_Type = 0;
            m_Constructor = Constructor;
            this.ParameterType = ParameterType;
            this.ParamType = ParamType;
            this.Params = Params;
            this.Args = new Object[ParameterType.length];
        }
        public FunctionMethod(java.lang.reflect.Method Method, java.lang.Class[] ParameterType, java.lang.Class ParamType, boolean Params) {
            m_Type = 1;
            m_Method = Method;
            this.ParameterType = ParameterType;
            this.ParamType = ParamType;
            this.Params = Params;
            this.Args = new Object[ParameterType.length];
        }
        public final Object invoke(Object obj, java.lang.Class type) throws Exception {
            return m_Type == 1 ? m_Method.invoke(obj, Args) : m_Constructor.newInstance(Args);
        }
    }
    private java.lang.Class m_Type; //所在类型
    private int m_Count; //相同名字函数数量
    private FunctionMethod[] m_Methods; //所有函数对象
    private String privateMethodName;
    public final String getMethodName() {
        return privateMethodName;
    }
    private void setMethodName(String value) {
        privateMethodName = value;
    }
    public UserdataMethod(java.lang.Class type, String methodName, java.lang.reflect.Method[] methods) {
        m_Type = type;
        setMethodName(methodName);
    	java.util.ArrayList<FunctionMethod> functionMethod = new java.util.ArrayList<FunctionMethod>();
        boolean Params = false;
        java.lang.Class ParamType = null;
        Method method = null;
        java.util.ArrayList<java.lang.Class> parameters = new java.util.ArrayList<java.lang.Class>();
        int length = methods.length;
        for (int i = 0; i < length; ++i) {
        	method = methods[i];
        	if (!method.getName().equals(methodName))
        		continue;
            Params = false;
            ParamType = null;
            parameters.clear();
            
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
    public UserdataMethod(java.lang.Class type, String methodName, java.lang.reflect.Constructor[] methods) {
    	m_Type = type;
    	setMethodName(methodName);
        java.util.ArrayList<FunctionMethod> functionMethod = new java.util.ArrayList<FunctionMethod>();
        boolean Params = false;
        java.lang.Class ParamType = null;
        java.lang.reflect.Constructor method = null;
        java.util.ArrayList<java.lang.Class> parameters = new java.util.ArrayList<java.lang.Class>();
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
            throw new ScriptException("找不到函数 [" + getMethodName() + "]");
        }
        FunctionMethod methodInfo = null;
        if (m_Count == 1) {
            if (parameters.length == m_Methods[0].ParameterType.length) {
                methodInfo = m_Methods[0];
            }
        }
        else {
            for (FunctionMethod method : m_Methods) {
                if (Util.CanChangeType(parameters, method.ParameterType)) {
                    methodInfo = method;
                    break;
                }
            }
        }
        if (methodInfo != null) {
            Object[] objs = methodInfo.Args;
            int length = methodInfo.ParameterType.length;
            for (int i = 0; i < length; i++) {
                objs[i] = Util.ChangeType(parameters[i], methodInfo.ParameterType[i]);
            }
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
                        for (int i = 0; i < length - 1; ++i) {
                            objs[i] = Util.ChangeType(parameters[i], method.ParameterType[i]);
                        }
                        java.util.ArrayList<Object> param = new java.util.ArrayList<Object>();
                        for (int i = length - 1; i < parameters.length; ++i) {
                            param.add(Util.ChangeType(parameters[i], method.ParamType));
                        }
                        objs[length - 1] = param.toArray(new Object[]{});
                        return method.invoke(obj, m_Type);
                    }
                }
            }
            throw new ScriptException("Type[" + m_Type.toString() + "] 找不到合适的函数 [" + getMethodName() + "]");
        }
    }
}