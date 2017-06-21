package Scorpio.Variable;

import Scorpio.*;

public class ScriptNumberByte extends ScriptNumber {
    private byte m_Value;
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    @Override
    public int getBranchType() {
        return NumberType.TypeByte;
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
    public ScriptNumberByte(Script script, byte value) {
        super(script);
        m_Value = value;
    }
    @Override
    public ScriptObject clone() {
        return new ScriptNumberByte(m_Script, m_Value);
    }
}