package Scorpio;
//脚本bool类型
public class ScriptBoolean extends ScriptObject {
    public ScriptBoolean(Script script, boolean value) {
        super(script);
        this.setValue(value);
    }
    private boolean privateValue;
    public final boolean getValue() {
        return privateValue;
    }
    private void setValue(boolean value) {
        privateValue = value;
    }
    @Override
    public ObjectType getType() {
        return ObjectType.Boolean;
    }
    @Override
    public Object getObjectValue() {
        return getValue();
    }
    @Override
    public boolean LogicOperation() {
        return getValue();
    }
    @Override
    public String ToJson() {
        return getValue() ? "true" : "false";
    }
    public final ScriptBoolean Inverse() {
        return getValue() ? getScript().getFalse() : getScript().getTrue();
    }
}