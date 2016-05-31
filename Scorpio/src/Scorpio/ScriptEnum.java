package Scorpio;

public class ScriptEnum extends ScriptObject {
    private Object m_Value;
    private java.lang.Class<?> m_EnumType;
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
    public final java.lang.Class<?> getEnumType() {
        return m_EnumType;
    }
    public ScriptEnum(Script script, Object obj) {
        super(script);
        m_Value = obj;
        m_EnumType = m_Value.getClass();
    }
}