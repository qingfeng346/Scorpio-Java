package Scorpio.Variable;

import Scorpio.*;

public class ScriptNumberUInt extends ScriptNumber {
    private int m_Value;
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    @Override
    public int getBranchType() {
        return NumberType.TypeUInt;
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
    public ScriptNumberUInt(Script script, int value) {
        super(script);
        m_Value = value;
    }
    @Override
    public ScriptObject clone() {
        return new ScriptNumberUInt(m_Script, m_Value);
    }
}