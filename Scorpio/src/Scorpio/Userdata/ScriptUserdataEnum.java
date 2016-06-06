package Scorpio.Userdata;

import Scorpio.*;
import Scorpio.Exception.*;

/**  枚举类型 
*/
public class ScriptUserdataEnum extends ScriptUserdata {
    private java.util.HashMap<String, ScriptEnum> m_Enums; //如果是枚举的话 所有枚举的值
    public ScriptUserdataEnum(Script script, java.lang.Class<?> value) {
        super(script);
        this.m_Value = value;
        this.m_ValueType = value;
        m_Enums = new java.util.HashMap<String, ScriptEnum>();
        try {
        	java.lang.reflect.Method values = m_ValueType.getMethod("values");
    		Object[] rets = (Object[]) values.invoke(null);
    		for (Object v : rets) {
    			m_Enums.put(v.toString(), new ScriptEnum(script, v));
    		}
        } catch (Exception e) { }
    }
    @Override
    public Object Call(ScriptObject[] parameters) {
        throw new ExecutionException(m_Script, "枚举类型不支持实例化");
    }
    @Override
    public ScriptObject GetValue(Object key) {
        if (!(key instanceof String)) {
            throw new ExecutionException(m_Script, "Enum GetValue只支持String类型");
        }
        String name = (String)key;
        if (m_Enums.containsKey(name)) {
            return m_Enums.get(name);
        }
        throw new ExecutionException(m_Script, "枚举[" + getValueType().toString() + "] 元素[" + name + "] 不存在");
    }
}