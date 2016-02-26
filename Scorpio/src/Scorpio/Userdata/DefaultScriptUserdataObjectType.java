package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Exception.ExecutionException;

/**  普通Object Type类型 
*/
public class DefaultScriptUserdataObjectType extends ScriptUserdata {
    private UserdataType m_Type;
    public DefaultScriptUserdataObjectType(Script script, java.lang.Class<?> value, UserdataType type) {
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
    public ScriptObject GetValue(Object key) throws Exception {
    	if (!(key instanceof String))
            throw new ExecutionException(getScript(), "ObjectType GetValue只支持String类型");
        return getScript().CreateObject(m_Type.GetValue(null, (String)key));
    }
    @Override
    public void SetValue(Object key, ScriptObject value) throws Exception {
    	if (!(key instanceof String))
            throw new ExecutionException(getScript(), "ObjectType GetValue只支持String类型");
        m_Type.SetValue(null, (String)key, value);
    }
}