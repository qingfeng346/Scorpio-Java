package Scorpio.Variable;

import Scorpio.*;

public class ScriptNumberFloat extends ScriptNumber {
    private float m_Value;
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    @Override
    public int getBranchType() {
        return NumberType.TypeFloat;
    }
    @Override
    public Object getObjectValue() {
        return m_Value;
    }
    @Override
    public Object getKeyValue() {
        return m_Value;
    }
    public final float getValue() {
        return m_Value;
    }
    public ScriptNumberFloat(Script script, float value) {
        super(script);
        m_Value = value;
    }
    @Override
    public ScriptObject clone() {
        return new ScriptNumberFloat(m_Script, m_Value);
    }
}