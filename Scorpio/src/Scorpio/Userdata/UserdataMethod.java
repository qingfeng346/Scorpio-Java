package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Exception.*;

/**  一个类的同名函数 
*/
public class UserdataMethod {
    private abstract static class FunctionBase {
        public java.lang.Class<?>[] ParameterType; //所有参数类型
        public boolean Params; //是否是变长参数
        public java.lang.Class<?> ParamType; //变长参数类型
        public String ParameterTypes; //传递参数的类型
        public Object[] Args; //参数数组（预创建 可以共用）
        public boolean IsValid; //是否是有效的函数 (模版函数没有声明的时候就是无效的)
        public FunctionBase(java.lang.Class<?>[] ParameterType, java.lang.Class<?> ParamType, boolean Params, String ParameterTypes) {
            this.ParameterType = ParameterType;
            this.ParamType = ParamType;
            this.Params = Params;
            this.ParameterTypes = ParameterTypes;
            this.Args = new Object[ParameterType.length];
        }
        public abstract Object invoke(Object obj, java.lang.Class<?> type) throws Exception;
    }
    private static class FunctionMethod extends FunctionBase {
        public java.lang.reflect.Method Method; //普通函数对象
        public FunctionMethod(java.lang.reflect.Method Method, java.lang.Class<?>[] ParameterType, java.lang.Class<?> ParamType, boolean Params, String ParameterTypes) {
            super(ParameterType, ParamType, Params, ParameterTypes);
            this.Method = Method;
            this.IsValid = true;
        }
        @Override
        public Object invoke(Object obj, java.lang.Class<?> type) throws Exception {
            return Method.invoke(obj, Args);
        }
    }
    private static class FunctionConstructor extends FunctionBase {
        public java.lang.reflect.Constructor<?> Constructor; //构造函数对象
        public FunctionConstructor(java.lang.reflect.Constructor<?> Constructor, java.lang.Class<?>[] ParameterType, java.lang.Class<?> ParamType, boolean Params, String ParameterTypes) {
            super(ParameterType, ParamType, Params, ParameterTypes);
            this.IsValid = true;
            this.Constructor = Constructor;
        }
        @Override
        public Object invoke(Object obj, java.lang.Class<?> type) throws Exception {
            return Constructor.newInstance(Args);
        }
    }
    private static class FunctionFastMethod extends FunctionBase {
        public IScorpioFastReflectMethod Method;
        public FunctionFastMethod(IScorpioFastReflectMethod Method, java.lang.Class<?>[] ParameterType, java.lang.Class<?> ParamType, boolean Params, String ParameterTypes) {
            super(ParameterType, ParamType, Params, ParameterTypes);
            this.IsValid = true;
            this.Method = Method;
        }
        @Override
        public Object invoke(Object obj, java.lang.Class<?> type) {
            return Method.Call(obj, ParameterTypes, Args);
        }
    }
    private static class MethodInfo {
        private java.lang.reflect.Method m_Method; //普通函数对象
        private java.lang.reflect.Constructor<?> m_Constructor; //构造函数对象
        public MethodInfo(java.lang.reflect.Constructor<?> Constructor) {
            m_Constructor = Constructor;
        }
        public MethodInfo(java.lang.reflect.Method Method) {
            m_Method = Method;
        }
        public java.lang.reflect.Method GetMethod() {
        	return m_Method;
        }
        public java.lang.reflect.Constructor<?> GetConstructor() {
        	return m_Constructor;
        }
        public Class<?>[] GetParameters() {
        	if (m_Constructor != null)
        		return m_Constructor.getParameterTypes();
        	return m_Method.getParameterTypes();
        }
        public boolean isVarArgs() {
        	if (m_Constructor != null)
        		return m_Constructor.isVarArgs();
        	return m_Method.isVarArgs();
        }
        public boolean isStatic() {
        	if (m_Constructor != null)
        		return false;
        	return java.lang.reflect.Modifier.isStatic(m_Method.getModifiers());
        }
    }
    private Script m_Script; //所在脚本引擎
    private java.lang.Class<?> m_Type; //所在类型
    private int m_Count; //相同名字函数数量
    private FunctionBase[] m_Methods; //所有函数对象
    private String m_MethodName; //函数名字
    private boolean m_IsStatic; //是否是静态函数
    public final String getMethodName() {
        return m_MethodName;
    }
    public final boolean getIsStatic() {
        return m_IsStatic;
    }
    public UserdataMethod() {
    }
    protected final void Initialize(Script script, java.lang.Class<?> type, String methodName, java.lang.reflect.Method[] methods) {
        m_Script = script;
        java.util.ArrayList<MethodInfo> methodBases = new java.util.ArrayList<MethodInfo>(); 
    	for (java.lang.reflect.Method method : methods) {
    		if (method.getName().equals(methodName))
    			methodBases.add(new MethodInfo(method));
    	}
    	m_IsStatic = methodBases.size() > 0 ? methodBases.get(0).isStatic() : false;
        Initialize_impl(type, methodName, methodBases);
    }
    protected final void Initialize(Script script, java.lang.Class<?> type, String methodName, java.lang.reflect.Constructor<?>[] cons) {
        m_Script = script;
        m_IsStatic = false;
        java.util.ArrayList<MethodInfo> methodBases = new java.util.ArrayList<MethodInfo>(); 
    	for (java.lang.reflect.Constructor<?> con : cons) {
    		methodBases.add(new MethodInfo(con));
    	}
        Initialize_impl(type, methodName, methodBases);
    }
    private void Initialize_impl(java.lang.Class<?> type, String methodName, java.util.ArrayList<MethodInfo> methods) {
        m_Type = type;
        m_MethodName = methodName;
        java.util.ArrayList<FunctionBase> functionMethod = new java.util.ArrayList<FunctionBase>();
        boolean Params = false;
        java.lang.Class<?> ParamType = null;
        String ParameterTypes = null;
        MethodInfo method = null;
        java.util.ArrayList<java.lang.Class<?>> parameters = new java.util.ArrayList<java.lang.Class<?>>();
        int length = methods.size();
        for (int i = 0; i < length; ++i) {
            Params = false;
            ParamType = null;
            ParameterTypes = "";
            parameters.clear();
            method = methods.get(i);
            Class<?>[] pars = method.GetParameters();
            for (Class<?> par : pars) {
            	ParameterTypes += (par.getName() + "+");
                parameters.add(par);
            }
            if (method.isVarArgs()) {
            	Params = true;
            	ParamType = pars[pars.length - 1].getComponentType();
            }
            if (method.GetMethod() != null) {
                functionMethod.add(new FunctionMethod(method.GetMethod(), parameters.toArray(new java.lang.Class[]{}), ParamType, Params, ParameterTypes));
            }
            else {
                functionMethod.add(new FunctionConstructor(method.GetConstructor(), parameters.toArray(new java.lang.Class[]{}), ParamType, Params, ParameterTypes));
            }
        }
        m_Methods = functionMethod.toArray(new FunctionBase[]{});
        m_Count = m_Methods.length;
    }
    protected final void Initialize(boolean isStatic, Script script, java.lang.Class<?> type, String methodName, ScorpioMethodInfo[] methods, IScorpioFastReflectMethod fastMethod) {
        m_Script = script;
        m_IsStatic = isStatic;
        m_Type = type;
        m_MethodName = methodName;
        java.util.ArrayList<FunctionBase> functionMethod = new java.util.ArrayList<FunctionBase>();
        for (ScorpioMethodInfo method : methods) {
            functionMethod.add(new FunctionFastMethod(fastMethod, method.ParameterType, method.ParamType, method.Params, method.ParameterTypes));
        }
        m_Methods = functionMethod.toArray(new FunctionBase[]{});
        m_Count = m_Methods.length;
    }
    public final Object Call(Object obj, ScriptObject[] parameters) {
        FunctionBase methodInfo = null;
        FunctionBase functionBase = null;
        for (int i = 0; i < m_Count; ++i) {
            functionBase = m_Methods[i];
            if (functionBase.IsValid) {
                if (functionBase.Params) {
                    boolean fit = true;
                    int length = functionBase.ParameterType.length;
                    int length1 = parameters.length;
                    if (length1 >= length - 1) {
                        for (int j = 0; j < length1; ++j) {
                            if (!Util.CanChangeType(parameters[j], j >= length - 1 ? functionBase.ParamType : functionBase.ParameterType[j])) {
                                fit = false;
                                break;
                            }
                        }
                    }
                    if (fit) {
                        methodInfo = functionBase;
                        break;
                    }
                }
                else if (Util.CanChangeType(parameters, functionBase.ParameterType)) {
                    methodInfo = functionBase;
                    break;
                }
            }
        }
        try {
            if (methodInfo != null) {
                int length = methodInfo.ParameterType.length;
                Object[] objs = methodInfo.Args;
                if (methodInfo.Params) {
                    for (int i = 0; i < length - 1; ++i) {
                        objs[i] = Util.ChangeType(m_Script, parameters[i], methodInfo.ParameterType[i]);
                    }
                    Object array = java.lang.reflect.Array.newInstance(methodInfo.ParamType, parameters.length - length + 1);
                    for (int i = length - 1; i < parameters.length; ++i)
                    	java.lang.reflect.Array.set(array, i - length + 1, Util.ChangeType(m_Script, parameters[i], methodInfo.ParamType));
                    objs[length - 1] = array;
                    return methodInfo.invoke(obj, m_Type);
                }
                else {
                    for (int i = 0; i < length; i++) {
                        objs[i] = Util.ChangeType(m_Script, parameters[i], methodInfo.ParameterType[i]);
                    }
                    return methodInfo.invoke(obj, m_Type);
                }
            }
        }
        catch (Exception e) {
            throw new ExecutionException(m_Script, "Type[" + m_Type.toString() + "] 调用函数出错 [" + getMethodName() + "] : " + e.toString());
        }
        throw new ExecutionException(m_Script, "Type[" + m_Type.toString() + "] 找不到合适的函数 [" + getMethodName() + "]");
    }
}