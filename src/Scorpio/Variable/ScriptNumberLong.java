package Scorpio.Variable;

import Scorpio.*;
import Scorpio.CodeDom.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;

public class ScriptNumberLong extends ScriptNumber
{
	@Override
	public ObjectType getType()
	{
		return ObjectType.Number;
	}
	@Override
	public int getBranchType()
	{
		return 1;
	}
	@Override
	public Object getObjectValue()
	{
		return getValue();
	}
	private long privateValue;
	public final long getValue()
	{
		return privateValue;
	}
	private void setValue(long value)
	{
		privateValue = value;
	}
	public ScriptNumberLong(Script script, long value)
	{
		m_Script = script;
		setValue(value);
	}
	@Override
	public ScriptNumber Calc(CALC c)
	{
		switch (c)
		{
			case PRE_INCREMENT:
				++privateValue;
				break;
			case PRE_DECREMENT:
				--privateValue;
				break;
			case POST_INCREMENT:
				return m_Script.CreateNumber(privateValue++);
			case POST_DECREMENT:
				return m_Script.CreateNumber(privateValue--);
		default:
			break;
		}
		return this;
	}
	@Override
	public ScriptNumber Negative()
	{
		privateValue = -privateValue;
		return this;
	}
	@Override
	public long ToLong()
	{
		return getValue();
	}
	@Override
	public ScriptObject Plus(ScriptObject obj)
	{
		return new ScriptNumberLong(m_Script, getValue() + ((ScriptNumber)obj).ToLong());
	}
	@Override
	public ScriptObject Minus(ScriptObject obj)
	{
		return new ScriptNumberLong(m_Script, getValue() - ((ScriptNumber)obj).ToLong());
	}
	@Override
	public ScriptObject Multiply(ScriptObject obj)
	{
		return new ScriptNumberLong(m_Script, getValue() * ((ScriptNumber)obj).ToLong());
	}
	@Override
	public ScriptObject Divide(ScriptObject obj)
	{
		return new ScriptNumberLong(m_Script, getValue() / ((ScriptNumber)obj).ToLong());
	}
	@Override
	public ScriptObject Modulo(ScriptObject obj)
	{
		return new ScriptNumberLong(m_Script, getValue() % ((ScriptNumber)obj).ToLong());
	}
	@Override
	public boolean Compare(TokenType type, CodeOperator oper, ScriptNumber num)
	{
		ScriptNumberLong val = (ScriptNumberLong)((num instanceof ScriptNumberLong) ? num : null);
		if (val == null)
		{
			throw new ExecutionException("数字比较 两边的数字类型不一致 请先转换再比较 ");
		}
		switch (type)
		{
			case Equal:
				return getValue() == val.getValue();
			case NotEqual:
				return getValue() != val.getValue();
			case Greater:
				return getValue() > val.getValue();
			case GreaterOrEqual:
				return getValue() >= val.getValue();
			case Less:
				return getValue() < val.getValue();
			case LessOrEqual:
				return getValue() <= val.getValue();
		}
		return false;
	}
	@Override
	public ScriptObject clone()
	{
		return new ScriptNumberLong(m_Script, getValue());
	}
}