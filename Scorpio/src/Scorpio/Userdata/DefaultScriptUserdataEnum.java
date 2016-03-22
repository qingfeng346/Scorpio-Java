package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Exception.*;

/**  枚举类型 
*/
public class DefaultScriptUserdataEnum extends ScriptUserdata {
    private java.util.HashMap<String, ScriptEnum> m_Enums; //如果是枚举的话 所有枚举的值
    public DefaultScriptUserdataEnum(Script script, java.lang.Class<?> value) {
        super(script);
        this.setValue(value);
        this.setValueType(value);
        m_Enums = new java.util.HashMap<String, ScriptEnum>();
        try {
        	java.lang.reflect.Method values = getValueType().getMethod("values");
    		Object[] rets = (Object[]) values.invoke(null);
    		for (Object v : rets) {
    			m_Enums.put(v.toString(), script.CreateEnum(v));
    		}
        } catch (Exception e) { }
    }
    @Override
    public Object Call(ScriptObject[] parameters) {
        throw new ExecutionException(getScript(), "枚举类型不支持实例化");
    }
    @Override
    public ScriptObject GetValue(Object key) {
        if (!(key instanceof String)) {
            throw new ExecutionException(getScript(), "Enum GetValue只支持String类型");
        }
        String name = (String)key;
        if (m_Enums.containsKey(name)) {
            return m_Enums.get(name);
        }
        throw new ExecutionException(getScript(), "枚举[" + getValueType().toString() + "] 元素[" + name + "] 不存在");
    }
}