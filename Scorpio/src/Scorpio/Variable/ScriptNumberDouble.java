package Scorpio.Variable;

import Scorpio.*;
import Scorpio.CodeDom.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;

public class ScriptNumberDouble extends ScriptNumber {
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    @Override
    public int getBranchType() {
        return 0;
    }
    @Override
    public Object getObjectValue() {
        return m_Value;
    }
    public final double getValue() {
        return m_Value;
    }
    public double m_Value;
    public ScriptNumberDouble(Script script, double value) {
        super(script);
        m_Value = value;
    }
    @Override
    public ScriptNumber Calc(CALC c) {
        switch (c) {
            case PRE_INCREMENT:
                ++m_Value;
                break;
            case PRE_DECREMENT:
                --m_Value;
                break;
            case POST_INCREMENT:
                return getScript().CreateDouble(m_Value++);
            case POST_DECREMENT:
                return getScript().CreateDouble(m_Value--);
            default:
                return this;
        }
        return this;
    }
    @Override
    public ScriptNumber Negative() {
    	return getScript().CreateDouble(-m_Value);
    }
    @Override
	public ScriptNumber Abs () {
		if (m_Value >= 0)
			return getScript().CreateDouble(m_Value);
		return getScript().CreateDouble(-m_Value);
	}
    @Override
	public ScriptNumber Floor () {
		return getScript().CreateDouble (Math.floor (m_Value));
	}
    @Override
    public ScriptNumber Clamp (ScriptNumber min, ScriptNumber max) {
		if (m_Value < min.ToDouble ())
			return getScript().CreateDouble (min.ToDouble());
		if (m_Value > max.ToDouble ())
			return getScript().CreateDouble (max.ToDouble ());
		return getScript().CreateDouble (m_Value);
	}
    @Override
    public ScriptObject Assign() {
        return getScript().CreateDouble(m_Value);
    }
    @Override
    public double ToDouble() {
        return m_Value;
    }
    @Override
    public boolean Compare(TokenType type, ScriptObject obj) {
        ScriptNumberDouble val = (ScriptNumberDouble)((obj instanceof ScriptNumberDouble) ? obj : null);
        if (val == null) {
            throw new ExecutionException(getScript(), "数字比较 两边的数字类型不一致 请先转换再比较 ");
        }
        switch (type) {
            case Greater:
                return m_Value > val.m_Value;
            case GreaterOrEqual:
                return m_Value >= val.m_Value;
            case Less:
                return m_Value < val.m_Value;
            case LessOrEqual:
                return m_Value <= val.m_Value;
            default:
                throw new ExecutionException(getScript(), "Double类型 操作符[" + type + "]不支持");
        }
    }
    @Override
    public ScriptObject Compute(TokenType type, ScriptObject obj) {
    	ScriptNumber val = (ScriptNumber)((obj instanceof ScriptNumber) ? obj : null);
        if (val == null) {
            throw new ExecutionException(getScript(), "数字比较 两边的数字类型不一致 请先转换再比较 ");
        }
        switch (type) {
            case Plus:
                return getScript().CreateDouble(m_Value + val.ToDouble());
            case Minus:
                return getScript().CreateDouble(m_Value - val.ToDouble());
            case Multiply:
                return getScript().CreateDouble(m_Value * val.ToDouble());
            case Divide:
                return getScript().CreateDouble(m_Value / val.ToDouble());
            case Modulo:
                return getScript().CreateDouble(m_Value % val.ToDouble());
            default:
                throw new ExecutionException(getScript(), "Double不支持的运算符 " + type);
        }
    }
    @Override
    public ScriptObject AssignCompute(TokenType type, ScriptObject obj) {
    	ScriptNumber val = (ScriptNumber)((obj instanceof ScriptNumber) ? obj : null);
        if (val == null) {
            throw new ExecutionException(getScript(), "数字比较 两边的数字类型不一致 请先转换再比较 ");
        }
        switch (type) {
            case AssignPlus:
                m_Value += val.ToDouble();
                return this;
            case AssignMinus:
                m_Value -= val.ToDouble();
                return this;
            case AssignMultiply:
                m_Value *= val.ToDouble();
                return this;
            case AssignDivide:
                m_Value /= val.ToDouble();
                return this;
            case AssignModulo:
                m_Value %= val.ToDouble();
                return this;
            default:
                throw new ExecutionException(getScript(), "Double不支持的运算符 " + type);
        }
    }
    @Override
    public ScriptObject clone() {
        return getScript().CreateDouble(m_Value);
    }
}