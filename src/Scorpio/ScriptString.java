package Scorpio;

import Scorpio.Variable.*;

//脚本字符串类型
public class ScriptString extends ScriptObject {
    @Override
    public ObjectType getType() {
        return ObjectType.String;
    }
    @Override
    public Object getObjectValue() {
        return getValue();
    }
    private String privateValue;
    public final String getValue() {
        return privateValue;
    }
    public final void setValue(String value) {
        privateValue = value;
    }
    public ScriptString(Script script, String value) {
        super(script);
        this.setValue(value);
    }
    @Override
    public ScriptObject Assign() {
        return getScript().CreateString(getValue());
    }
    public final ScriptObject AssignPlus(ScriptObject obj) {
        setValue(getValue() + obj.toString());
        return this;
    }
    @Override
    public ScriptObject clone() {
        return getScript().CreateString(getValue());
    }
}