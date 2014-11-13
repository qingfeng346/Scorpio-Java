package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Variable.*;
import Scorpio.Exception.*;

//语言数据
public class DefaultScriptUserdataObject extends ScriptUserdata {
    private static class Field {
        public String name;
        public java.lang.Class<?> fieldType;
        public java.lang.reflect.Field field;
        public final Object GetValue(Object obj) throws Exception {
            if (field != null) {
                return field.get(obj);
            }
            throw new ScriptException("变量 [" + name + "] 不支持GetValue");
        }
        public final void SetValue(Object obj, Object val) throws Exception {
            if (field != null) {
                field.set(obj, val);
            } else {
                throw new ScriptException("变量 [" + name + "] 不支持SetValue");
            }
        }
    }
    private ScorpioMethod m_Constructor;
    private java.util.HashMap<String, Field> m_FieldInfos; //所有的变量 以及 get set函数
    private java.util.HashMap<String, ScriptUserdata> m_NestedTypes; //所有的类中类
    private java.util.HashMap<String, ScriptFunction> m_Functions; //所有的函数
    public DefaultScriptUserdataObject(Script script, Object value) {
        super(script);
        this.setValue(value);
        this.setValueType((value instanceof Class) ? (Class)value : value.getClass());
        m_FieldInfos = new java.util.HashMap<String, Field>();
        m_NestedTypes = new java.util.HashMap<String, ScriptUserdata>();
        m_Functions = new java.util.HashMap<String, ScriptFunction>();
        m_Constructor = new ScorpioMethod(getValueType().toString(), getValueType().getConstructors());
        java.lang.reflect.Method[] methods = getValueType().getMethods();
        for (int i = 0; i < methods.length;++i) {
            String name = methods[i].getName();
            if (!m_Functions.containsKey(name)) {
                m_Functions.put(name, getScript().CreateFunction(new ScorpioMethod(getValueType(), name, getValue())));
            }
        }
    }
    private Field GetField(String strName) {
        if (m_FieldInfos.containsKey(strName)) {
            return m_FieldInfos.get(strName);
        }
        try {
            java.lang.reflect.Field info = getValueType().getField(strName);
            if (info != null) {
            	Field field = new Field();
                field.name = strName;
                field.field = info;
                field.fieldType = info.getType();
                m_FieldInfos.put(strName, field);
                return field;
            }
        } catch (Exception e) { }
        return null;
    }
    @Override
    public ScriptObject Call(ScriptObject[] parameters) throws Exception {
        return getScript().CreateObject(m_Constructor.Call(parameters));
    }
    @Override
    public ScriptObject GetValue(String strName) throws Exception {
        if (m_Functions.containsKey(strName)) {
            return m_Functions.get(strName);
        }
        if (m_NestedTypes.containsKey(strName)) {
            return m_NestedTypes.get(strName);
        }
        Field field = GetField(strName);
        if (field != null) {
            return getScript().CreateObject(field.GetValue(getValue()));
        }
        Class<?>[] clazz = getValueType().getDeclaredClasses();
		for (Class<?> c : clazz)
		{
			if (c.getName().equals(strName))
			{
				ScriptUserdata ret = getScript().CreateUserdata(c);
				m_NestedTypes.put(strName, ret);
				return ret;
			}
		}
        throw new ScriptException("Type[" + getValueType().toString() + "] Variable[" + strName + "] 不存在");
    }
    @Override
    public void SetValue(String strName, ScriptObject value) throws Exception {
        Field field = GetField(strName);
        if (field == null) {
            throw new ScriptException("Type[" + getValueType() + "] 变量 [" + strName + "] 不存在");
        }
        field.SetValue(getValue(), Util.ChangeType(value, field.fieldType));
    }
}