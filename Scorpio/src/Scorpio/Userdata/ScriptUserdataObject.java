package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Variable.*;
import Scorpio.Exception.*;
import Scorpio.Compiler.*;

/**  普通Object类型 
*/
public class ScriptUserdataObject extends ScriptUserdata {
    protected UserdataType m_UserdataType;
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
        return m_Script.CreateObject(m_UserdataType.GetValue(m_Value, name));
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