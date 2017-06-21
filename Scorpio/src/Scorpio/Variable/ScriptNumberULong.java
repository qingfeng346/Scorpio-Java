package Scorpio.Variable;

import Scorpio.*;

public class ScriptNumberULong extends ScriptNumber {
    private long m_Value;
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    @Override
    public int getBranchType() {
        return NumberType.TypeULong;
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
    public ScriptNumberULong(Script script, long value) {
        super(script);
        m_Value = value;
    }
    @Override
    public ScriptObject clone() {
        return new ScriptNumberULong(m_Script, m_Value);
    }
}