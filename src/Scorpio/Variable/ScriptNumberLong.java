﻿package Scorpio.Variable;

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
    public long ToLong()
    {
    	return m_Value;
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
    public ScriptObject ComputePlus(ScriptNumber obj) {
        return getScript().CreateLong(m_Value + obj.ToLong());
    }
    @Override
    public ScriptObject ComputeMinus(ScriptNumber obj) {
        return getScript().CreateLong(m_Value - obj.ToLong());
    }
    @Override
    public ScriptObject ComputeMultiply(ScriptNumber obj) {
        return getScript().CreateLong(m_Value * obj.ToLong());
    }
    @Override
    public ScriptObject ComputeDivide(ScriptNumber obj) {
        return getScript().CreateLong(m_Value / obj.ToLong());
    }
    @Override
    public ScriptObject ComputeModulo(ScriptNumber obj) {
        return getScript().CreateLong(m_Value % obj.ToLong());
    }
    @Override
    public ScriptObject AssignPlus(ScriptNumber obj) {
        m_Value += obj.ToLong();
        return this;
    }
    @Override
    public ScriptObject AssignMinus(ScriptNumber obj) {
        m_Value -= obj.ToLong();
        return this;
    }
    @Override
    public ScriptObject AssignMultiply(ScriptNumber obj) {
        m_Value *= obj.ToLong();
        return this;
    }
    @Override
    public ScriptObject AssignDivide(ScriptNumber obj) {
        m_Value /= obj.ToLong();
        return this;
    }
    @Override
    public ScriptObject AssignModulo(ScriptNumber obj) {
        m_Value %= obj.ToLong();
        return this;
    }
    @Override
    public boolean Compare(TokenType type, CodeOperator oper, ScriptNumber num) {
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
        }
        return false;
    }
    @Override
    public ScriptObject clone() {
        return getScript().CreateLong(m_Value);
    }
}