package Scorpio;

import Scorpio.Variable.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;

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
    public final boolean Compare(TokenType type, ScriptString str) {
        switch (type) {
            case Greater:
                return getValue().compareTo(str.getValue()) < 0;
            case GreaterOrEqual:
                return getValue().compareTo(str.getValue()) <= 0;
            case Less:
                return getValue().compareTo(str.getValue()) > 0;
            case LessOrEqual:
                return getValue().compareTo(str.getValue()) >= 0;
            default:
                throw new ExecutionException("String类型 操作符[" + type + "]不支持");
        }
    }
    @Override
    public ScriptObject clone() {
        return getScript().CreateString(getValue());
    }
    @Override
    public String ToJson() {
        return "\"" + getValue() + "\"";
    }
}