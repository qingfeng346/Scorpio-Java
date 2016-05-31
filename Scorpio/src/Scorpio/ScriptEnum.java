package Scorpio;

public class ScriptEnum extends ScriptObject {
    @Override
    public ObjectType getType() {
        return ObjectType.Enum;
    }
    @Override
    public Object getObjectValue() {
        return m_Value;
    }
    @Override
    public Object getKeyValue() {
        return m_Value;
    }
    private java.lang.Class<?> privateEnumType;
    public final java.lang.Class<?> getEnumType() {
        return privateEnumType;
    }
    private void setEnumType(java.lang.Class<?> value) {
        privateEnumType = value;
    }
    public Object m_Value;
    public ScriptEnum(Script script, Object obj) {
        super(script);
        m_Value = obj;
        setEnumType(m_Value.getClass());
    }
}