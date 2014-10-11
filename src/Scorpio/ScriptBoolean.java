package Scorpio;

import Scorpio.Variable.*;

//脚本bool类型
public class ScriptBoolean extends ScriptPrimitiveObject<Boolean>
{
	public static final ScriptBoolean True = new ScriptBoolean(true);
	public static final ScriptBoolean False = new ScriptBoolean(false);
	@Override
	public ObjectType getType()
	{
		return ObjectType.Boolean;
	}
	private ScriptBoolean(boolean value)
	{
		super(value);
	}
	public final ScriptBoolean Inverse()
	{
		return getValue() ? False : True;
	}
	@Override
	public ScriptObject clone()
	{
		return this;
	}
	public static ScriptBoolean Get(boolean b)
	{
		return b ? True : False;
	}
}