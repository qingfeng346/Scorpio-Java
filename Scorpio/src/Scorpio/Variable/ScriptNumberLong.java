package Scorpio.Variable;

import Scorpio.*;
import Scorpio.CodeDom.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;

public class ScriptNumberLong extends ScriptNumber {
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    @Override
    public int getBranchType() {
        return 1;
    }
    @Override
    public Object getObjectValue() {
        return m_Value;
    }
    @Override
    public Object getKeyValue() {
        return m_Value;
    }
    public final long getValue() {
        return m_Value;
    }
    public long m_Value;
    public ScriptNumberLong(Script script, long value) {
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
                return getScript().CreateLong(m_Value++);
            case POST_DECREMENT:
                return getScript().CreateLong(m_Value--);
            default:
                return this;
        }
        return this;
    }
    @Override
    public ScriptNumber Negative() {
        return getScript().CreateLong(-m_Value);
    }
    @Override
    public ScriptNumber Abs() {
        if (m_Value >= 0) {
            return getScript().CreateLong(m_Value);
        }
        return getScript().CreateLong(-m_Value);
    }
    @Override
    public ScriptNumber Floor() {
        return getScript().CreateLong(m_Value);
    }
    @Override
    public ScriptNumber Clamp(ScriptNumber min, ScriptNumber max) {
        if (m_Value < min.ToLong()) {
            return getScript().CreateLong(min.ToLong());
        }
        if (m_Value > max.ToLong()) {
            return getScript().CreateLong(max.ToLong());
        }
        return getScript().CreateLong(m_Value);
    }
    @Override
    public ScriptObject Assign() {
        return getScript().CreateLong(m_Value);
    }
    @Override
    public long ToLong() {
        return m_Value;
    }
    @Override
    public boolean Compare(TokenType type, ScriptObject num) {
        ScriptNumberLong val = (ScriptNumberLong)((num instanceof ScriptNumberLong) ? num : null);
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
                throw new ExecutionException(getScript(), "Long类型 操作符[" + type + "]不支持");
        }
    }
    @Override
    public ScriptObject Compute(TokenType type, ScriptObject obj) {
        ScriptNumber val = (ScriptNumber)((obj instanceof ScriptNumber) ? obj : null);
        if (val == null) {
            throw new ExecutionException(getScript(), "赋值逻辑计算 右边值必须为数字类型");
        }
        switch (type) {
            case Plus:
                return getScript().CreateLong(m_Value + val.ToLong());
            case Minus:
                return getScript().CreateLong(m_Value - val.ToLong());
            case Multiply:
                return getScript().CreateLong(m_Value * val.ToLong());
            case Divide:
                return getScript().CreateLong(m_Value / val.ToLong());
            case Modulo:
                return getScript().CreateLong(m_Value % val.ToLong());
            case InclusiveOr:
                return getScript().CreateLong(m_Value | val.ToLong());
            case Combine:
                return getScript().CreateLong(m_Value & val.ToLong());
            case XOR:
                return getScript().CreateLong(m_Value ^ val.ToLong());
            case Shr:
                return getScript().CreateLong(m_Value >> val.ToInt32());
            case Shi:
                return getScript().CreateLong(m_Value << val.ToInt32());
            default:
                throw new ExecutionException(getScript(), "Long不支持的运算符 " + type);
        }
    }
    @Override
    public ScriptObject AssignCompute(TokenType type, ScriptObject obj) {
        ScriptNumber val = (ScriptNumber)((obj instanceof ScriptNumber) ? obj : null);
        if (val == null) {
            throw new ExecutionException(getScript(), "赋值逻辑计算 右边值必须为数字类型");
        }
        switch (type) {
            case AssignPlus:
                m_Value += val.ToLong();
                return this;
            case AssignMinus:
                m_Value -= val.ToLong();
                return this;
            case AssignMultiply:
                m_Value *= val.ToLong();
                return this;
            case AssignDivide:
                m_Value /= val.ToLong();
                return this;
            case AssignModulo:
                m_Value %= val.ToLong();
                return this;
            case AssignInclusiveOr:
                m_Value |= val.ToLong();
                return this;
            case AssignCombine:
                m_Value &= val.ToLong();
                return this;
            case AssignXOR:
                m_Value ^= val.ToLong();
                return this;
            case AssignShr:
                m_Value >>= val.ToInt32();
                return this;
            case AssignShi:
                m_Value <<= val.ToInt32();
                return this;
            default:
                throw new ExecutionException(getScript(), "Long不支持的运算符 " + type);
        }
    }
    @Override
    public ScriptObject clone() {
        return getScript().CreateLong(m_Value);
    }
}