package Scorpio.Variable;

import Scorpio.*;

public class ScriptNumberUShort extends ScriptNumber {
    private short m_Value;
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    @Override
    public int getBranchType() {
        return NumberType.TypeUShort;
    }
    @Override
    public Object getObjectValue() {
        return m_Value;
    }
    @Override
    public Object getKeyValue() {
        return m_Value;
    }
    public final short getValue() {
        return m_Value;
    }
    public ScriptNumberUShort(Script script, short value) {
        super(script);
        m_Value = value;
    }
    @Override
    public ScriptObject clone() {
        return new ScriptNumberUShort(m_Script, m_Value);
    }
}