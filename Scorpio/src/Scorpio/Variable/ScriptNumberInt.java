package Scorpio.Variable;

import Scorpio.*;
import Scorpio.CodeDom.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;

public class ScriptNumberInt extends ScriptNumber {
    private int m_Value;
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    @Override
    public int getBranchType() {
        return NumberType.TypeInt;
    }
    @Override
    public Object getObjectValue() {
        return m_Value;
    }
    @Override
    public Object getKeyValue() {
        return m_Value;
    }
    public final int getValue() {
        return m_Value;
    }
    public ScriptNumberInt(Script script, int value) {
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
            return new ScriptNumberInt(m_Script, m_Value++);
        case POST_DECREMENT:
            return new ScriptNumberInt(m_Script, m_Value--);
        default:
            return this;
        }
        return this;
    }
    @Override
    public ScriptNumber Minus() {
        return new ScriptNumberInt(m_Script, -m_Value);
    }
    @Override
    public ScriptNumber Negative() {
        return new ScriptNumberInt(m_Script, ~m_Value);
    }
    @Override
    public ScriptNumber Abs() {
        if (m_Value >= 0) {
            return new ScriptNumberInt(m_Script, m_Value);
        }
        return new ScriptNumberInt(m_Script, -m_Value);
    }
    @Override
    public ScriptNumber Floor() {
        return new ScriptNumberInt(m_Script, m_Value);
    }
    @Override
    public ScriptNumber Clamp(ScriptNumber min, ScriptNumber max) {
        if (m_Value < min.ToInt32()) {
            return new ScriptNumberInt(m_Script, min.ToInt32());
        }
        if (m_Value > max.ToInt32()) {
            return new ScriptNumberInt(m_Script, max.ToInt32());
        }
        return new ScriptNumberInt(m_Script, m_Value);
    }
    @Override
    public ScriptObject Assign() {
        return new ScriptNumberInt(m_Script, m_Value);
    }
    @Override
    public int ToInt32() {
        return m_Value;
    }
    @Override
    public boolean Compare(TokenType type, ScriptObject num) {
        ScriptNumberInt val = (ScriptNumberInt)((num instanceof ScriptNumberInt) ? num : null);
        if (val == null) {
            throw new ExecutionException(m_Script, this, "数字比较 两边的数字类型不一致 请先转换再比较 ");
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
            throw new ExecutionException(m_Script, this, "Int类型 操作符[" + type + "]不支持");
        }
    }
    @Override
    public ScriptObject Compute(TokenType type, ScriptObject obj) {
        ScriptNumber val = (ScriptNumber)((obj instanceof ScriptNumber) ? obj : null);
        if (val == null) {
            throw new ExecutionException(m_Script, this, "逻辑计算 右边值必须为数字类型");
        }
        switch (type) {
        case Plus:
            return new ScriptNumberInt(m_Script, m_Value + val.ToInt32());
        case Minus:
            return new ScriptNumberInt(m_Script, m_Value - val.ToInt32());
        case Multiply:
            return new ScriptNumberInt(m_Script, m_Value * val.ToInt32());
        case Divide:
            return new ScriptNumberInt(m_Script, m_Value / val.ToInt32());
        case Modulo:
            return new ScriptNumberInt(m_Script, m_Value % val.ToInt32());
        case InclusiveOr:
            return new ScriptNumberInt(m_Script, m_Value | val.ToInt32());
        case Combine:
            return new ScriptNumberInt(m_Script, m_Value & val.ToInt32());
        case XOR:
            return new ScriptNumberInt(m_Script, m_Value ^ val.ToInt32());
        case Shr:
            return new ScriptNumberInt(m_Script, m_Value >> val.ToInt32());
        case Shi:
            return new ScriptNumberInt(m_Script, m_Value << val.ToInt32());
        default:
            throw new ExecutionException(m_Script, this, "Int不支持的运算符 " + type);
        }
    }
    @Override
    public ScriptObject AssignCompute(TokenType type, ScriptObject obj) {
        ScriptNumber val = (ScriptNumber)((obj instanceof ScriptNumber) ? obj : null);
        if (val == null) {
            throw new ExecutionException(m_Script, this, "赋值逻辑计算 右边值必须为数字类型");
        }
        switch (type) {
        case AssignPlus:
            m_Value += val.ToInt32();
            return this;
        case AssignMinus:
            m_Value -= val.ToInt32();
            return this;
        case AssignMultiply:
            m_Value *= val.ToInt32();
            return this;
        case AssignDivide:
            m_Value /= val.ToInt32();
            return this;
        case AssignModulo:
            m_Value %= val.ToInt32();
            return this;
        case AssignInclusiveOr:
            m_Value |= val.ToInt32();
            return this;
        case AssignCombine:
            m_Value &= val.ToInt32();
            return this;
        case AssignXOR:
            m_Value ^= val.ToInt32();
            return this;
        case AssignShr:
            m_Value >>= val.ToInt32();
            return this;
        case AssignShi:
            m_Value <<= val.ToInt32();
            return this;
        default:
            throw new ExecutionException(m_Script, this, "Int不支持的运算符 " + type);
        }
    }

    @Override
    public ScriptObject clone() {
        return new ScriptNumberInt(m_Script, m_Value);
    }
}