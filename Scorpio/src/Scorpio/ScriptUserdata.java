package Scorpio;

//语言数据
public abstract class ScriptUserdata extends ScriptObject {
    protected Object m_Value;
    protected java.lang.Class<?> m_ValueType;
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
    public final java.lang.Class<?> getValueType() {
        return m_ValueType;
    }
    public ScriptUserdata(Script script) {
        super(script);
    }
}