package Scorpio.Userdata;

import Scorpio.Script;
import Scorpio.ScriptObject;
import Scorpio.ScriptUserdata;
import Scorpio.Util;
import Scorpio.Exception.*;
import Scorpio.Variable.*;

/**  保存一个类的所有元素 
*/
public class UserdataType {
    private static class UserdataField {
    	private Script m_Script;
        public String Name;
        public java.lang.Class FieldType;
        private java.lang.reflect.Field m_Field;
        public UserdataField(Script script, java.lang.reflect.Field info) {
        	m_Script = script;
            m_Field = info;
            Name = info.getName();
            FieldType = info.getType();
        }
        public final Object GetValue(Object obj) throws Exception {
            if (m_Field != null) {
                return m_Field.get(obj);
            }
            throw new ExecutionException(m_Script, "变量 [" + Name + "] 不支持GetValue");
        }
        public final void SetValue(Object obj, Object val) throws Exception {
            if (m_Field != null) {
                m_Field.set(obj, val);
            } else {
                throw new ExecutionException(m_Script, "变量 [" + Name + "] 不支持SetValue");
            }
        }
    }
    private Script m_Script; //脚本系统
    private java.lang.Class m_Type; //类型
    private boolean m_InitializeConstructor; //是否初始化过所有构造函数
    private boolean m_InitializeMethods; //是否初始化过所有函数
    private UserdataMethod m_Constructor; //所有构造函数
    private java.lang.reflect.Method[] m_Methods; //所有函数
    private java.util.HashMap<String, UserdataField> m_FieldInfos; //所有的变量 以及 get set函数
    private java.util.HashMap<String, ScriptUserdata> m_NestedTypes; //所有的类中类
    private java.util.HashMap<String, UserdataMethod> m_Functions; //所有的函数
    private java.util.HashMap<String, ScorpioMethod> m_ScorpioMethods; //所有的静态函数和类函数（不包含对象函数）
    public UserdataType(Script script, java.lang.Class type) {
        m_Script = script;
        m_Type = type;
        m_InitializeConstructor = false;
        m_InitializeMethods = false;
        m_FieldInfos = new java.util.HashMap<String, UserdataField>();
        m_NestedTypes = new java.util.HashMap<String, ScriptUserdata>();
        m_Functions = new java.util.HashMap<String, UserdataMethod>();
        m_ScorpioMethods = new java.util.HashMap<String, ScorpioMethod>();
    }
    private void InitializeConstructor() {
        if (m_InitializeConstructor == true) {
            return;
        }
        m_InitializeConstructor = true;
        m_Constructor = new UserdataMethod(m_Script, m_Type, m_Type.toString(), m_Type.getConstructors());
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
                UserdataMethod method = new UserdataMethod(m_Script, m_Type, name, m_Methods);
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
            ScorpioMethod ret = new ScorpioTypeMethod(m_Script, name, method);
            m_ScorpioMethods.put(name, ret);
            return ret;
        }
        return new ScorpioObjectMethod(obj, name, method);
    }
    private UserdataField GetField(String name) {
        if (m_FieldInfos.containsKey(name)) {
            return m_FieldInfos.get(name);
        }
        java.lang.reflect.Field fInfo = null;
		try { fInfo = m_Type.getField(name); } catch (Exception e) { }
        if (fInfo != null) {
            UserdataField info = new UserdataField(m_Script, fInfo);
            m_FieldInfos.put(name, info);
            return info;
        }
        return null;
    }
    /**  创建一个实例 
     * @throws Exception 
    */
    public final Object CreateInstance(ScriptObject[] parameters) throws Exception {
        InitializeConstructor();
        return m_Constructor.Call(null, parameters);
    }
    /**  获得一个类变量 
     * @throws Exception 
    */
    public final Object GetValue(Object obj, String name) throws Exception {
    	if (m_ScorpioMethods.containsKey(name)) {
            return m_ScorpioMethods.get(name);
        }
        if (m_Functions.containsKey(name)) {
            return GetMethod(obj, name, m_Functions.get(name));
        }
        if (m_NestedTypes.containsKey(name)) {
            return m_NestedTypes.get(name);
        }
        UserdataField field = GetField(name);
        if (field != null) {
            return m_Script.CreateObject(field.GetValue(obj));
        }
        Class<?>[] clazz = m_Type.getDeclaredClasses();
		for (Class<?> c : clazz)
		{
			if (c.getName().equals(name))
			{
				ScriptUserdata ret = m_Script.CreateUserdata(c);
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
     * @throws Exception 
    */
    public final void SetValue(Object obj, String name, ScriptObject value) throws Exception {
        UserdataField field = GetField(name);
        if (field == null) {
            throw new ExecutionException(m_Script, "SetValue Type[" + m_Type + "] 变量 [" + name + "] 不存在");
        }
        try {
        	field.SetValue(obj, Util.ChangeType(m_Script, value, field.FieldType));
        } catch (Exception e) {
            throw new ExecutionException(m_Script, "不能从源类型:" + (value.getIsNull() ? "null" : value.getObjectValue().getClass().getName()) + " 转换成目标类型:" + field.FieldType.getName());
        }
    }
}