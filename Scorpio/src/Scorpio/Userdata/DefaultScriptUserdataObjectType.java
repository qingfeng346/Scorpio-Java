package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Exception.*;

/**  普通Object Type类型 
*/
public class DefaultScriptUserdataObjectType extends ScriptUserdata {
    protected UserdataType m_UserdataType;
    public DefaultScriptUserdataObjectType(Script script, java.lang.Class<?> value, UserdataType type) {
        super(script);
        this.setValue(value);
        this.setValueType(value);
        this.m_UserdataType = type;
    }
    @Override
    public Object Call(ScriptObject[] parameters) {
        return m_UserdataType.CreateInstance(parameters);
    }
    @Override
    public ScriptObject GetValue(Object key) {
        if (!(key instanceof String)) {
            throw new ExecutionException(getScript(), "ObjectType GetValue只支持String类型");
        }
        return getScript().CreateObject(m_UserdataType.GetValue(null, (String)key));
    }
    @Override
    public void SetValue(Object key, ScriptObject value) {
        if (!(key instanceof String)) {
            throw new ExecutionException(getScript(), "ObjectType SetValue只支持String类型");
        }
        m_UserdataType.SetValue(null, (String)key, value);
    }
}