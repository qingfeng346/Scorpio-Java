package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Exception.*;
import Scorpio.Compiler.*;

/**  普通Object类型 
*/
public class DefaultScriptUserdataObject extends ScriptUserdata {
    protected UserdataType m_UserdataType;
    public DefaultScriptUserdataObject(Script script, Object value, UserdataType type) {
        super(script);
        this.setValue(value);
        this.setValueType(value.getClass());
        this.m_UserdataType = type;
    }
    @Override
    public ScriptObject GetValue(Object key) {
        if (!(key instanceof String)) {
            throw new ExecutionException(getScript(), "Object GetValue只支持String类型");
        }
        return getScript().CreateObject(m_UserdataType.GetValue(getValue(), (String)key));
    }
    @Override
    public void SetValue(Object key, ScriptObject value) {
        if (!(key instanceof String)) {
            throw new ExecutionException(getScript(), "Object SetValue只支持String类型");
        }
        m_UserdataType.SetValue(getValue(), (String)key, value);
    }
    @Override
    public ScriptObject Compute(TokenType type, ScriptObject obj) {
        UserdataMethod method = m_UserdataType.GetComputeMethod(type);
        if (method == null) {
            throw new ExecutionException(getScript(), "找不到运算符重载 " + type);
        }
        return getScript().CreateObject(method.Call(null, new ScriptObject[] { this, obj }));
    }
}