package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Variable.*;

/**  普通Object Type类型 
*/
public class DefaultScriptUserdataObjectType extends ScriptUserdata {
    private UserdataType m_Type;
    public DefaultScriptUserdataObjectType(Script script, java.lang.Class value, UserdataType type) {
        super(script);
        this.setValue(value);
        this.setValueType(value);
        this.m_Type = type;
    }
    @Override
    public Object Call(ScriptObject[] parameters) throws Exception {
        return m_Type.CreateInstance(parameters);
    }
    @Override
    public ScriptObject GetValue(String strName) throws Exception {
        return getScript().CreateObject(m_Type.GetValue(null, strName));
    }
    @Override
    public void SetValue(String strName, ScriptObject value) throws Exception {
        m_Type.SetValue(getValue(), strName, value);
    }
}