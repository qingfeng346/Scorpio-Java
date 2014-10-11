package Scorpio.Variable;

import Scorpio.*;

public abstract class ScriptPrimitiveObject<T> extends ScriptObject
{
	private T privateValue;
	public final T getValue()
	{
		return privateValue;
	}
	public final void setValue(T value)
	{
		privateValue = value;
	}
	@Override
	public Object getObjectValue()
	{
		return getValue();
	}
	public ScriptPrimitiveObject()
	{
		this.setValue(null);
	}
	public ScriptPrimitiveObject(T value)
	{
		this.setValue(value);
	}
	@Override
	public String toString()
	{
		return getValue().toString();
	}
}