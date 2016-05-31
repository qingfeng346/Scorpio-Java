package Scorpio.Variable;

import java.text.DecimalFormat;

import Scorpio.*;
import Scorpio.CodeDom.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;

public class ScriptNumberDouble extends ScriptNumber {
	private static DecimalFormat DoubleFormat = new DecimalFormat("#.############");
    public double m_Value;
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
    @Override
    public Object getKeyValue() {
        return m_Value;
    }
    public final double getValue() {
        return m_Value;
    }
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
                return m_Script.CreateDouble(m_Value++);
            case POST_DECREMENT:
                return m_Script.CreateDouble(m_Value--);
            default:
                return this;
        }
        return this;
    }
    @Override
    public ScriptNumber Negative() {
        return m_Script.CreateDouble(-m_Value);
    }
    @Override
    public ScriptNumber Abs() {
        if (m_Value >= 0) {
            return m_Script.CreateDouble(m_Value);
        }
        return m_Script.CreateDouble(-m_Value);
    }
    @Override
    public ScriptNumber Floor() {
        return m_Script.CreateDouble(Math.floor (m_Value));
    }
    @Override
    public ScriptNumber Clamp(ScriptNumber min, ScriptNumber max) {
        if (m_Value < min.ToDouble()) {
            return m_Script.CreateDouble(min.ToDouble());
        }
        if (m_Value > max.ToDouble()) {
            return m_Script.CreateDouble(max.ToDouble());
        }
        return m_Script.CreateDouble(m_Value);
    }
    @Override
    public ScriptObject Assign() {
        return m_Script.CreateDouble(m_Value);
    }
    @Override
    public double ToDouble() {
        return m_Value;
    }
    @Override
    public boolean Compare(TokenType type, ScriptObject obj) {
        ScriptNumberDouble val = (ScriptNumberDouble)((obj instanceof ScriptNumberDouble) ? obj : null);
        if (val == null) {
            throw new ExecutionException(m_Script, "数字比较 两边的数字类型不一致 请先转换再比较");
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
                throw new ExecutionException(m_Script, "Double类型 操作符[" + type + "]不支持");
        }
    }
    @Override
    public ScriptObject Compute(TokenType type, ScriptObject obj) {
        ScriptNumber val = (ScriptNumber)((obj instanceof ScriptNumber) ? obj : null);
        if (val == null) {
            throw new ExecutionException(m_Script, "逻辑计算 右边值必须为数字类型");
        }
        switch (type) {
            case Plus:
                return m_Script.CreateDouble(m_Value + val.ToDouble());
            case Minus:
                return m_Script.CreateDouble(m_Value - val.ToDouble());
            case Multiply:
                return m_Script.CreateDouble(m_Value * val.ToDouble());
            case Divide:
                return m_Script.CreateDouble(m_Value / val.ToDouble());
            case Modulo:
                return m_Script.CreateDouble(m_Value % val.ToDouble());
            default:
                throw new ExecutionException(m_Script, "Double不支持的运算符 " + type);
        }
    }
    @Override
    public ScriptObject AssignCompute(TokenType type, ScriptObject obj) {
        ScriptNumber val = (ScriptNumber)((obj instanceof ScriptNumber) ? obj : null);
        if (val == null) {
            throw new ExecutionException(m_Script, "赋值逻辑计算 右边值必须为数字类型");
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
                throw new ExecutionException(m_Script, "Double不支持的运算符 " + type);
        }
    }
    @Override
    public String toString() {
    	return DoubleFormat.format(m_Value);
    }
    @Override
    public ScriptObject clone() {
        return m_Script.CreateDouble(m_Value);
    }
}