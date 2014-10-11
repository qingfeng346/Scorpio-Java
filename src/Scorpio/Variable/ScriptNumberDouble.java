package Scorpio.Variable;

import Scorpio.*;
import Scorpio.CodeDom.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;

public class ScriptNumberDouble extends ScriptNumber
{
	@Override
	public ObjectType getType()
	{
		return ObjectType.Number;
	}
	@Override
	public int getBranchType()
	{
		return 0;
	}
	private double privateValue;
	public final double getValue()
	{
		return privateValue;
	}
	private void setValue(double value)
	{
		privateValue = value;
	}
	public ScriptNumberDouble(Script script, double value)
	{
		m_Script = script;
		setValue(value);
	}
	@Override
	public Object getObjectValue()
	{
		return getValue();
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
		}
		return this;
	}
	@Override
	public ScriptNumber Negative()
	{
		setValue(-getValue());
		return this;
	}
	@Override
	public double ToDouble()
	{
		return getValue();
	}
	@Override
	public ScriptObject Plus(ScriptObject obj)
	{
		return new ScriptNumberDouble(m_Script, getValue() + ((ScriptNumber)obj).ToDouble());
	}
	@Override
	public ScriptObject Minus(ScriptObject obj)
	{
		return new ScriptNumberDouble(m_Script, getValue() - ((ScriptNumber)obj).ToDouble());
	}
	@Override
	public ScriptObject Multiply(ScriptObject obj)
	{
		return new ScriptNumberDouble(m_Script, getValue() * ((ScriptNumber)obj).ToDouble());
	}
	@Override
	public ScriptObject Divide(ScriptObject obj)
	{
		return new ScriptNumberDouble(m_Script, getValue() / ((ScriptNumber)obj).ToDouble());
	}
	@Override
	public ScriptObject Modulo(ScriptObject obj)
	{
		return new ScriptNumberDouble(m_Script, getValue() % ((ScriptNumber)obj).ToDouble());
	}
	@Override
	public boolean Compare(TokenType type, CodeOperator oper, ScriptNumber num)
	{
		ScriptNumberDouble val = (ScriptNumberDouble)((num instanceof ScriptNumberDouble) ? num : null);
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
		return new ScriptNumberDouble(m_Script, getValue());
	}
}