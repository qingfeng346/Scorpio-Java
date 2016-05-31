package Scorpio;

//语言数据
public abstract class ScriptUserdata extends ScriptObject {
    protected Object m_Value;
    @Override
    public Object getObjectValue() {
        return m_Value;
    }
    @Override
    public Object getKeyValue() {
        return m_Value;
    }
    @Override
    public ObjectType getType() {
        return ObjectType.UserData;
    }
    public final Object getValue() {
        return m_Value;
    }
    private java.lang.Class<?> privateValueType;
    public final java.lang.Class<?> getValueType() {
        return privateValueType;
    }
    protected final void setValueType(java.lang.Class<?> value) {
        privateValueType = value;
    }
    public ScriptUserdata(Script script) {
        super(script);
    }
}