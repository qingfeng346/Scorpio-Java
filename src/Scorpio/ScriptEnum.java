package Scorpio;

public class ScriptEnum extends ScriptObject {
    @Override
    public ObjectType getType() {
        return ObjectType.Enum;
    }
    @Override
    public Object getObjectValue() {
        return m_Object;
    }
    private java.lang.Class privateEnumType;
    public final java.lang.Class getEnumType() {
        return privateEnumType;
    }
    private void setEnumType(java.lang.Class value) {
        privateEnumType = value;
    }
    public Object m_Object;
    public ScriptEnum(Script script, Object obj) {
        super(script);
        m_Object = obj;
        setEnumType(m_Object.getClass());
    }
}