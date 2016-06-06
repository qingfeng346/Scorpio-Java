package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Exception.*;

/**  普通Object Type类型 
*/
public class ScriptUserdataObjectType extends ScriptUserdata {
    protected UserdataType m_UserdataType;
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
        return m_Script.CreateObject(m_UserdataType.GetValue(null, name));
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