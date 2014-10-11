package Scorpio;

import Scorpio.Variable.*;
import Scorpio.Exception.*;

//语言数据
public abstract class ScriptUserdata extends ScriptObject
{
	@Override
	public Object getObjectValue()
	{
		return getValue();
	}
	@Override
	public ObjectType getType()
	{
		return ObjectType.UserData;
	}

	protected Script m_Script;
	private Object privateValue;
	public Object getValue()
	{
		return privateValue;
	}
	protected void setValue(Object value)
	{
		privateValue = value;
	}
	private java.lang.Class privateValueType;
	public java.lang.Class getValueType()
	{
		return privateValueType;
	}
	protected void setValueType(java.lang.Class value)
	{
		privateValueType = value;
	}
	@Override
	public String toString()
	{
		return getValue().toString();
	}
}