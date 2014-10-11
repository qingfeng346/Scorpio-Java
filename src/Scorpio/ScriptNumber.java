package Scorpio;

import Scorpio.CodeDom.*;
import Scorpio.Variable.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;

//脚本数字类型
public abstract class ScriptNumber extends ScriptObject
{
	@Override
	public ObjectType getType()
	{
		return ObjectType.Number;
	}
	protected Script m_Script;
	public abstract ScriptNumber Calc(CALC c);
	public abstract ScriptNumber Negative();
	public abstract boolean Compare(TokenType type, CodeOperator oper, ScriptNumber num);
	public final int ToInt32()
	{
		return Util.ToInt32(getObjectValue());
	}
	@Override
	public ScriptObject Assign()
	{
		return m_Script.CreateNumber(getObjectValue());
	}
	public double ToDouble()
	{
		return Util.ToDouble(getObjectValue());
	}
	public long ToLong()
	{
		return Util.ToInt64(getObjectValue());
	}
	@Override
	public String toString()
	{
		return getObjectValue().toString();
	}
}