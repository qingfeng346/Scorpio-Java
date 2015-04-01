package Scorpio;
//脚本bool类型
public class ScriptBoolean extends ScriptObject {
    @Override
    public ObjectType getType() {
        return ObjectType.Boolean;
    }
    @Override
    public Object getObjectValue() {
        return getValue();
    }
    private boolean privateValue;
    public final boolean getValue() {
        return privateValue;
    }
    public ScriptBoolean(Script script, boolean value) {
        super(script);
        this.privateValue = value;
    }
    public final ScriptBoolean Inverse() {
        return getValue() ? getScript().False : getScript().True;
    }
    @Override
    public String ToJson() {
        return getValue() ? "true" : "false";
    }
}