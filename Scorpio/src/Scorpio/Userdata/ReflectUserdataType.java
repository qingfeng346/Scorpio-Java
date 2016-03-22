package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Exception.*;
import Scorpio.Variable.*;
import Scorpio.Compiler.*;

public class ReflectUserdataType extends UserdataType {
    private boolean m_InitializeConstructor; //是否初始化过所有构造函数
    private boolean m_InitializeMethods; //是否初始化过所有函数
    private UserdataMethod m_Constructor; //所有构造函数
    private java.lang.reflect.Method[] m_Methods; //所有函数
    private java.util.HashMap<String, UserdataVariable> m_Variables; //所有的变量 FieldInfo,PropertyInfo,EventInfo
    private java.util.HashMap<String, ScriptUserdata> m_NestedTypes; //所有的类中类
    private java.util.HashMap<String, UserdataMethod> m_Functions; //所有的函数
    private java.util.HashMap<String, ScorpioMethod> m_ScorpioMethods; //所有的静态函数和类函数（不包含对象函数）
    public ReflectUserdataType(Script script, java.lang.Class<?> type) {
        super(script, type);
        m_InitializeConstructor = false;
        m_InitializeMethods = false;
        m_Variables = new java.util.HashMap<String, UserdataVariable>();
        m_NestedTypes = new java.util.HashMap<String, ScriptUserdata>();
        m_Functions = new java.util.HashMap<String, UserdataMethod>();
        m_ScorpioMethods = new java.util.HashMap<String, ScorpioMethod>();
    }
    private void InitializeConstructor() {
        if (m_InitializeConstructor == true) {
            return;
        }
        m_InitializeConstructor = true;
        m_Constructor = new ReflectUserdataMethod(m_Script, m_Type, m_Type.toString(), m_Type.getConstructors());
    }
    private void InitializeMethods() {
        if (m_InitializeMethods == true) {
            return;
        }
        m_InitializeMethods = true;
        m_Methods = m_Type.getMethods();
    }
    private UserdataMethod GetMethod(String name) {
        InitializeMethods();
        for (int i = 0; i < m_Methods.length; ++i) {
            if (m_Methods[i].getName().equals(name)) {
                UserdataMethod method = new ReflectUserdataMethod(m_Script, m_Type, name, m_Methods);
                m_Functions.put(name, method);
                return method;
            }
        }
        return null;
    }
    private ScorpioMethod GetMethod(Object obj, String name, UserdataMethod method) {
        if (method.getIsStatic()) {
        	ScorpioMethod ret = new ScorpioStaticMethod(name, method);
            m_ScorpioMethods.put(name, ret);
            return ret;
        }
        else if (obj == null) {
        	ScorpioMethod ret = new ScorpioTypeMethod(m_Script, name, method, m_Type);
            m_ScorpioMethods.put(name, ret);
            return ret;
        }
        return new ScorpioObjectMethod(obj, name, method);
    }
    private UserdataVariable GetVariable(String name) {
        if (m_Variables.containsKey(name)) {
            return m_Variables.get(name);
        }
        java.lang.reflect.Field fInfo = null;
		try { fInfo = m_Type.getField(name); } catch (Exception e) { }
        if (fInfo != null) {
            UserdataField info = new UserdataField(m_Script, fInfo);
            m_Variables.put(name, info);
            return info;
        }
        return null;
    }
    /**  创建一个实例 
    */
    @Override
    public Object CreateInstance(ScriptObject[] parameters) {
        InitializeConstructor();
        return m_Constructor.Call(null, parameters);
    }
    /**  获得运算符重载的函数 
    */
    @Override
    public UserdataMethod GetComputeMethod(TokenType type) {
        switch (type) {
        case Plus:
            return GetMethod("op_Addition");
        case Minus:
            return GetMethod("op_Subtraction");
        case Multiply:
            return GetMethod("op_Multiply");
        case Divide:
            return GetMethod("op_Division");
        default:
            return null;
        }
    }
    /**  获得一个类变量 
    */
    @Override
    public Object GetValue(Object obj, String name) {
        if (m_ScorpioMethods.containsKey(name)) {
            return m_ScorpioMethods.get(name);
        }
        if (m_Functions.containsKey(name)) {
            return GetMethod(obj, name, m_Functions.get(name));
        }
        if (m_NestedTypes.containsKey(name)) {
            return m_NestedTypes.get(name);
        }
        UserdataVariable variable = GetVariable(name);
        if (variable != null) {
            return variable.GetValue(obj);
        }
        Class<?>[] classes = m_Type.getDeclaredClasses();
		for (Class<?> clazz : classes) {
			if (clazz.getName().equals(name)) {
				ScriptUserdata ret = m_Script.CreateUserdata(clazz);
				m_NestedTypes.put(name, ret);
				return ret;
			}
		}
        UserdataMethod func = GetMethod(name);
        if (func != null) {
            return GetMethod(obj, name, func);
        }
        throw new ExecutionException(m_Script, "GetValue Type[" + m_Type.toString() + "] 变量 [" + name + "] 不存在");
    }
    /**  设置一个类变量 
    */
    @Override
    public void SetValue(Object obj, String name, ScriptObject value) {
        UserdataVariable variable = GetVariable(name);
        if (variable == null) {
            throw new ExecutionException(m_Script, "SetValue Type[" + m_Type + "] 变量 [" + name + "] 不存在");
        }
        try {
            variable.SetValue(obj, Util.ChangeType(m_Script, value, variable.FieldType));
        }
        catch (RuntimeException e) {
            throw new ExecutionException(m_Script, "SetValue 出错 源类型:" + (value == null || value.getIsNull() ? "null" : value.getObjectValue().getClass().getName()) + " 目标类型:" + variable.FieldType.getName() + " : " + e.toString());
        }
    }
}