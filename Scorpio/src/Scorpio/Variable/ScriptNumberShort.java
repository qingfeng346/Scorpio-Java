package Scorpio.Variable;

import Scorpio.*;

public class ScriptNumberShort extends ScriptNumber {
    private short m_Value;
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    @Override
    public int getBranchType() {
        return NumberType.TypeShort;
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
    public ScriptNumberShort(Script script, short value) {
        super(script);
        m_Value = value;
    }
    @Override
    public ScriptObject clone() {
        return new ScriptNumberShort(m_Script, m_Value);
    }
}