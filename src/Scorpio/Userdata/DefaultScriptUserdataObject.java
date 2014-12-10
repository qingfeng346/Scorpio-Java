package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Variable.*;
import Scorpio.Exception.*;

/**  普通Object类型 
*/
public class DefaultScriptUserdataObject extends ScriptUserdata {
    private UserdataType m_Type;
    public DefaultScriptUserdataObject(Script script, Object value, UserdataType type) {
        super(script);
        this.setValue(value);
        this.setValueType(value.getClass());
        this.m_Type = type;
    }
    @Override
    public ScriptObject GetValue(String strName) throws Exception {
        return getScript().CreateObject(m_Type.GetValue(getValue(), strName));
    }
    @Override
    public void SetValue(String strName, ScriptObject value) throws Exception {
        m_Type.SetValue(getValue(), strName, value);
    }
}