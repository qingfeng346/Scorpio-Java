package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Exception.*;
import Scorpio.Variable.*;

/**  普通Object Type类型 
*/
public class ScriptUserdataObjectType extends ScriptUserdata {
    protected UserdataType m_UserdataType;
    protected java.util.HashMap<String, ScriptObject> m_Methods = new java.util.HashMap<String, ScriptObject>();
    public ScriptUserdataObjectType(Script script, java.lang.Class<?> value, UserdataType type) {
        super(script);
        this.m_Value = value;
        this.m_ValueType = value;
        this.m_UserdataType = type;
    }
    @Override
    public Object Call(ScriptObject[] parameters) {
        return m_UserdataType.CreateInstance(parameters);
    }
    @Override
    public ScriptObject GetValue(Object key) {
        String name = (String)((key instanceof String) ? key : null);
        if (name == null) {
            throw new ExecutionException(m_Script, "ObjectType GetValue只支持String类型");
        }
        if (m_Methods.containsKey(name)) {
            return m_Methods.get(name);
        }
        Object ret = m_UserdataType.GetValue(null, name);
        if (ret instanceof UserdataMethod) {
            UserdataMethod method = (UserdataMethod)ret;
            ScriptObject value = m_Script.CreateObject(method.getIsStatic() ? (ScorpioMethod)new ScorpioStaticMethod(name, method) : (ScorpioMethod)new ScorpioTypeMethod(m_Script, name, method, m_ValueType));
            m_Methods.put(name, value);
            return value;
        }
        return m_Script.CreateObject(ret);
    }
    @Override
    public void SetValue(Object key, ScriptObject value) {
        String name = (String)((key instanceof String) ? key : null);
        if (name == null) {
            throw new ExecutionException(m_Script, "ObjectType SetValue只支持String类型");
        }
        m_UserdataType.SetValue(null, name, value);
    }
}