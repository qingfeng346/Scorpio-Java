package Scorpio.Userdata;

import java.lang.reflect.Method;

import Scorpio.Script;
import Scorpio.ScriptEnum;
import Scorpio.ScriptObject;
import Scorpio.ScriptUserdata;
import Scorpio.Exception.*;

public class DefaultScriptUserdataEnum extends ScriptUserdata
{
	private java.util.HashMap<String, ScriptEnum> m_Enums; //如果是枚举的话 所有枚举的值
	public DefaultScriptUserdataEnum(Script script, Object value) throws Exception
	{
		this.m_Script = script;
		this.setValue(value);
		this.setValueType((value instanceof Class<?>) ? (Class<?>)value : value.getClass());
		m_Enums = new java.util.HashMap<String, ScriptEnum>();
		Method values = getValueType().getMethod("values");
		Object[] rets = (Object[]) values.invoke(null);
		for (Object v : rets)
		{
			m_Enums.put(v.toString(), script.CreateEnum(v));
		}
	}
	@Override
	public ScriptObject Call(ScriptObject[] parameters)
	{
		throw new ScriptException("枚举类型不支持实例化");
	}
	@Override
	public ScriptObject GetValue(String strName)
	{
		if (!m_Enums.containsKey(strName))
		{
			throw new ScriptException("枚举[" + getValueType().toString() + "] 元素[" + strName + "] 不存在");
		}
		return m_Enums.get(strName);
	}
}