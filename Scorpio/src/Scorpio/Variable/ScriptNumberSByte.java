package Scorpio.Variable;

import Scorpio.*;

public class ScriptNumberSByte extends ScriptNumber {
    private byte m_Value;
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    @Override
    public int getBranchType() {
        return NumberType.TypeSByte;
    }
    @Override
    public Object getObjectValue() {
        return m_Value;
    }
    @Override
    public Object getKeyValue() {
        return m_Value;
    }
    public final byte getValue() {
        return m_Value;
    }
    public ScriptNumberSByte(Script script, byte value) {
        super(script);
        m_Value = value;
    }
    @Override
    public ScriptObject clone() {
        return new ScriptNumberSByte(m_Script, m_Value);
    }
}