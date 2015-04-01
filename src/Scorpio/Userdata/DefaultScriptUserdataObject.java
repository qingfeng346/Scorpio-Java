package Scorpio.Userdata;

import Scorpio.*;
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
    public ScriptObject GetValue(Object key) throws Exception {
    	if (!(key instanceof String))
            throw new ExecutionException(getScript(), "Object GetValue只支持String类型");
        return getScript().CreateObject(m_Type.GetValue(getValue(), (String)key));
    }
    @Override
    public void SetValue(Object key, ScriptObject value) throws Exception {
    	if (!(key instanceof String))
            throw new ExecutionException(getScript(), "Object GetValue只支持String类型");
        m_Type.SetValue(getValue(), (String)key, value);
    }
}