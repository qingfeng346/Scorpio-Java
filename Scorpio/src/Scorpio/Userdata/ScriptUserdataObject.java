package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Variable.*;
import Scorpio.Exception.*;
import Scorpio.Compiler.*;

/**  普通Object类型 
*/
public class ScriptUserdataObject extends ScriptUserdata {
    protected UserdataType m_UserdataType;
    protected java.util.HashMap<String, ScriptObject> m_Methods = new java.util.HashMap<String, ScriptObject>();
    public ScriptUserdataObject(Script script, Object value, UserdataType type) {
        super(script);
        this.m_Value = value;
        this.m_ValueType = value.getClass();
        this.m_UserdataType = type;
    }
    @Override
    public ScriptObject GetValue(Object key) {
        String name = (String)((key instanceof String) ? key : null);
        if (name == null) {
            throw new ExecutionException(m_Script, "Object GetValue只支持String类型");
        }
        if (m_Methods.containsKey(name)) {
            return m_Methods.get(name);
        }
        Object ret = m_UserdataType.GetValue(m_Value, name);
        if (ret instanceof UserdataMethod) {
            UserdataMethod method = (UserdataMethod)ret;
            ScriptObject value = m_Script.CreateObject(method.getIsStatic() ? (ScorpioMethod)new ScorpioStaticMethod(name, method) : (ScorpioMethod)new ScorpioObjectMethod(m_Value, name, method));
            m_Methods.put(name, value);
            return value;
        }
        return m_Script.CreateObject(ret);
    }
    @Override
    public void SetValue(Object key, ScriptObject value) {
        String name = (String)((key instanceof String) ? key : null);
        if (name == null) {
            throw new ExecutionException(m_Script, "Object SetValue只支持String类型");
        }
        m_UserdataType.SetValue(m_Value, name, value);
    }
    @Override
    public ScriptObject Compute(TokenType type, ScriptObject obj) {
        ScorpioMethod method = m_UserdataType.GetComputeMethod(type);
        if (method == null) {
            throw new ExecutionException(m_Script, "找不到运算符重载 " + type);
        }
        return m_Script.CreateObject(method.Call(new ScriptObject[] { this, obj }));
    }
}