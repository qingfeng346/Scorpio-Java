package Scorpio;

import Scorpio.Variable.*;

//脚本字符串类型
public class ScriptString extends ScriptPrimitiveObject<String>
{
	@Override
	public ObjectType getType()
	{
		return ObjectType.String;
	}
	private Script m_Script;
	public ScriptString(Script script, String value)
	{
		super(value);
		m_Script = script;
	}
	@Override
	public ScriptObject Assign()
	{
		return m_Script.CreateString(getValue());
	}
	@Override
	public ScriptObject Plus(ScriptObject obj)
	{
		return m_Script.CreateString(getValue() + obj.toString());
	}
	@Override
	public ScriptObject clone()
	{
		return m_Script.CreateString(getValue());
	}
}