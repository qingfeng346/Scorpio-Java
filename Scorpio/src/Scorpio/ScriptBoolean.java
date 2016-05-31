package Scorpio;

//脚本bool类型
public class ScriptBoolean extends ScriptObject {
    private boolean m_Value;
    public ScriptBoolean(Script script, boolean value) {
        super(script);
        this.m_Value = value;
    }
    public final boolean getValue() {
        return m_Value;
    }
    @Override
    public ObjectType getType() {
        return ObjectType.Boolean;
    }
    @Override
    public Object getObjectValue() {
        return m_Value;
    }
    @Override
    public Object getKeyValue() {
        return m_Value;
    }
    @Override
    public boolean LogicOperation() {
        return m_Value;
    }
    @Override
    public String ToJson() {
        return m_Value ? "true" : "false";
    }
    @Override
    public String toString() {
        return m_Value ? "true" : "false";
    }
    public final ScriptBoolean Inverse() {
        return m_Value ? m_Script.getFalse() : m_Script.getTrue();
    }
}