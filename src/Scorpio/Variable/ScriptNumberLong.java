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
        m_Value = -m_Value;
        return this;
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
    public ScriptObject Compute(TokenType type, ScriptNumber obj) {
        switch (type) {
            case Plus:
                return getScript().CreateLong(m_Value + obj.ToLong());
            case Minus:
                return getScript().CreateLong(m_Value - obj.ToLong());
            case Multiply:
                return getScript().CreateLong(m_Value * obj.ToLong());
            case Divide:
                return getScript().CreateLong(m_Value / obj.ToLong());
            case Modulo:
                return getScript().CreateLong(m_Value % obj.ToLong());
            case InclusiveOr:
                return getScript().CreateLong(m_Value | obj.ToLong());
            case Combine:
                return getScript().CreateLong(m_Value & obj.ToLong());
            case XOR:
                return getScript().CreateLong(m_Value ^ obj.ToLong());
            case Shr:
                return getScript().CreateLong(m_Value >> obj.ToInt32());
            case Shi:
                return getScript().CreateLong(m_Value << obj.ToInt32());
            default:
                throw new ExecutionException("Long不支持的运算符 " + type);
        }
    }
    @Override
    public ScriptObject AssignCompute(TokenType type, ScriptNumber obj) {
        switch (type) {
            case AssignPlus:
                m_Value += obj.ToLong();
                return this;
            case AssignMinus:
                m_Value -= obj.ToLong();
                return this;
            case AssignMultiply:
                m_Value *= obj.ToLong();
                return this;
            case AssignDivide:
                m_Value /= obj.ToLong();
                return this;
            case AssignModulo:
                m_Value %= obj.ToLong();
                return this;
            case AssignInclusiveOr:
                m_Value |= obj.ToLong();
                return this;
            case AssignCombine:
                m_Value &= obj.ToLong();
                return this;
            case AssignXOR:
                m_Value ^= obj.ToLong();
                return this;
            case AssignShr:
                m_Value >>= obj.ToInt32();
                return this;
            case AssignShi:
                m_Value <<= obj.ToInt32();
                return this;
            default:
                throw new ExecutionException("Long不支持的运算符 " + type);
        }
    }
    @Override
    public boolean Compare(TokenType type, ScriptNumber num) {
        ScriptNumberLong val = (ScriptNumberLong)((num instanceof ScriptNumberLong) ? num : null);
        if (val == null) {
            throw new ExecutionException("数字比较 两边的数字类型不一致 请先转换再比较 ");
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
                throw new ExecutionException("Number类型 操作符[" + type + "]不支持");
        }
    }
    @Override
    public ScriptObject clone() {
        return getScript().CreateLong(m_Value);
    }
}