package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Exception.*;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if SCORPIO_UWP && !UNITY_EDITOR
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#define UWP
//#endif
/**  一个类的同名函数 
*/
public class UserdataMethod {
    private abstract static class FunctionBase {
        public java.lang.Class<?>[] ParameterType; //所有参数类型
        public boolean Params; //是否是变长参数
        public java.lang.Class<?> ParamType; //变长参数类型
        public String ParameterTypes; //传递参数的类型
        public Object[] Args; //参数数组（预创建 可以共用）
        private boolean privateIsValid;
        public final boolean getIsValid() {
            return privateIsValid;
        }
        protected final void setIsValid(boolean value) {
            privateIsValid = value;
        }
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
            setIsValid(true);
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
            this.Constructor = Constructor;
            setIsValid(true);
        }
        @Override
        public Object invoke(Object obj, java.lang.Class<?> type) throws Exception {
            return Constructor.newInstance(obj, Args);
        }
    }
    private static class FunctionFastMethod extends FunctionBase {
        public IScorpioFastReflectMethod Method;
        public FunctionFastMethod(IScorpioFastReflectMethod Method, java.lang.Class<?>[] ParameterType, java.lang.Class<?> ParamType, boolean Params, String ParameterTypes) {
            super(ParameterType, ParamType, Params, ParameterTypes);
            this.Method = Method;
            setIsValid(true);
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
    private String privateMethodName;
    public final String getMethodName() {
        return privateMethodName;
    }
    private void setMethodName(String value) {
        privateMethodName = value;
    }
    private boolean privateIsStatic;
    public final boolean getIsStatic() {
        return privateIsStatic;
    }
    private void setIsStatic(boolean value) {
        privateIsStatic = value;
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
        setIsStatic(methodBases.size() > 0 ? methodBases.get(0).isStatic() : false);
        Initialize_impl(type, methodName, methodBases, null);
    }
    protected final void Initialize(Script script, java.lang.Class<?> type, String methodName, java.lang.reflect.Constructor<?>[] cons) {
        m_Script = script;
        setIsStatic(false);
        java.util.ArrayList<MethodInfo> methodBases = new java.util.ArrayList<MethodInfo>(); 
    	for (java.lang.reflect.Constructor<?> con : cons) {
    		methodBases.add(new MethodInfo(con));
    	}
        Initialize_impl(type, methodName, methodBases, null);
    }
    protected final void Initialize(int t, boolean isStatic, Script script, java.lang.Class<?> type, String methodName, java.lang.reflect.Method[] methods, IScorpioFastReflectMethod fastMethod) {
        m_Script = script;
        setIsStatic(isStatic);
//        java.util.ArrayList<MethodBase> methodBases = new java.util.ArrayList<MethodBase>();
//        if (t == 0) {
//            methodBases.addAll(type.getConstructors());
//        }
//        else {
//            for (java.lang.reflect.Method method : methods) {
//                if (method.getName().equals(methodName)) {
//                    methodBases.add(method);
//                }
//            }
//        }
//        Initialize_impl(type, methodName, methodBases, fastMethod);
    }
    private void Initialize_impl(java.lang.Class<?> type, String methodName, java.util.ArrayList<MethodInfo> methods, IScorpioFastReflectMethod fastMethod) {
        m_Type = type;
        setMethodName(methodName);
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
            if (fastMethod != null) {
                functionMethod.add(new FunctionFastMethod(fastMethod, parameters.toArray(new java.lang.Class[]{}), ParamType, Params, ParameterTypes));
            }
            else if (method.GetMethod() != null) {
                functionMethod.add(new FunctionMethod(method.GetMethod(), parameters.toArray(new java.lang.Class[]{}), ParamType, Params, ParameterTypes));
            }
            else {
                functionMethod.add(new FunctionConstructor(method.GetConstructor(), parameters.toArray(new java.lang.Class[]{}), ParamType, Params, ParameterTypes));
            }
        }
        m_Methods = functionMethod.toArray(new FunctionBase[]{});
        m_Count = m_Methods.length;
    }
    public final Object Call(Object obj, ScriptObject[] parameters) {
        if (m_Count == 0) {
            throw new ExecutionException(m_Script, "找不到函数 [" + getMethodName() + "]");
        }
        FunctionBase methodInfo = null;
        if (m_Count == 1) {
            methodInfo = m_Methods[0];
            if (!methodInfo.getIsValid()) {
                throw new ExecutionException(m_Script, "Type[" + m_Type.toString() + "] 找不到合适的函数 [" + getMethodName() + "]");
            }
        }
        else {
            for (FunctionBase method : m_Methods) {
                if (method.getIsValid() && Util.CanChangeType(parameters, method.ParameterType)) {
                    methodInfo = method;
                    break;
                }
            }
        }
        try {
            if (methodInfo != null && !methodInfo.Params) {
                int length = methodInfo.ParameterType.length;
                Object[] objs = methodInfo.Args;
                for (int i = 0; i < length; i++) {
                    objs[i] = Util.ChangeType(m_Script, parameters[i], methodInfo.ParameterType[i]);
                }
                return methodInfo.invoke(obj, m_Type);
            }
            else {
                for (FunctionBase method : m_Methods) {
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
                                objs[i] = Util.ChangeType(m_Script, parameters[i], method.ParameterType[i]);
                            }
                            Object array = java.lang.reflect.Array.newInstance(method.ParamType, parameters.length - length + 1);
	                        for (int i = length - 1; i < parameters.length; ++i)
	                        	java.lang.reflect.Array.set(array, i - length + 1, Util.ChangeType(m_Script, parameters[i], method.ParamType));
                            objs[length - 1] = array;
                            return method.invoke(obj, m_Type);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new ExecutionException(m_Script, "Type[" + m_Type.toString() + "] 调用函数出错 [" + getMethodName() + "] : " + e.toString());
        }
        throw new ExecutionException(m_Script, "Type[" + m_Type.toString() + "] 找不到合适的函数 [" + getMethodName() + "]");
    }
}