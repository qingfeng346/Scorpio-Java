package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Variable.*;
import Scorpio.Exception.*;
import Scorpio.Compiler.*;

/**  普通Object类型 
*/
public class DefaultScriptUserdataObject extends ScriptUserdata {
    protected UserdataType m_UserdataType;
    public DefaultScriptUserdataObject(Script script, Object value, UserdataType type) {
        super(script);
        this.m_Value = value;
        this.m_ValueType = value.getClass();
        this.m_UserdataType = type;
    }
    @Override
    public ScriptObject GetValue(Object key) {
        if (!(key instanceof String)) {
            throw new ExecutionException(m_Script, "Object GetValue只支持String类型");
        }
        return m_Script.CreateObject(m_UserdataType.GetValue(m_Value, (String)key));
    }
    @Override
    public void SetValue(Object key, ScriptObject value) {
        if (!(key instanceof String)) {
            throw new ExecutionException(m_Script, "Object SetValue只支持String类型");
        }
        m_UserdataType.SetValue(m_Value, (String)key, value);
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