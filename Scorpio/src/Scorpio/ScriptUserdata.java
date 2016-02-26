package Scorpio;
//语言数据
public abstract class ScriptUserdata extends ScriptObject {
    @Override
    public Object getObjectValue() {
        return getValue();
    }
    @Override
    public ObjectType getType() {
        return ObjectType.UserData;
    }
    private Object privateValue;
    public final Object getValue() {
        return privateValue;
    }
    protected final void setValue(Object value) {
        privateValue = value;
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