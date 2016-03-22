package Scorpio;

import Scorpio.Compiler.*;
import Scorpio.Exception.*;

//脚本字符串类型
public class ScriptString extends ScriptObject {
    public ScriptString(Script script, String value) {
        super(script);
        this.setValue(value);
    }
    private String privateValue;
    public final String getValue() {
        return privateValue;
    }
    public final void setValue(String value) {
        privateValue = value;
    }
    @Override
    public ObjectType getType() {
        return ObjectType.String;
    }
    @Override
    public Object getObjectValue() {
        return getValue();
    }
    @Override
    public ScriptObject Assign() {
        return getScript().CreateString(getValue());
    }
    @Override
    public ScriptObject GetValue(Object index) {
        if (!(index instanceof Double || index instanceof Integer || index instanceof Long)) {
            throw new ExecutionException(getScript(), "String GetValue只支持Number类型");
        }
        return getScript().CreateString(getValue().charAt(Util.ToInt32(index)) + "");
    }
    @Override
    public boolean Compare(TokenType type, ScriptObject obj) {
        ScriptString val = (ScriptString)((obj instanceof ScriptString) ? obj : null);
        if (val == null) {
            throw new ExecutionException(getScript(), "字符串比较 右边必须为字符串类型");
        }
        switch (type) {
            case Greater:
                return getValue().compareTo(val.getValue()) < 0;
            case GreaterOrEqual:
                return getValue().compareTo(val.getValue()) <= 0;
            case Less:
                return getValue().compareTo(val.getValue()) > 0;
            case LessOrEqual:
                return getValue().compareTo(val.getValue()) >= 0;
            default:
                throw new ExecutionException(getScript(), "String类型 操作符[" + type + "]不支持");
        }
    }
    @Override
    public ScriptObject AssignCompute(TokenType type, ScriptObject obj) {
        if (type == TokenType.AssignPlus) {
            setValue(getValue() + obj.toString());
            return this;
        }
        throw new ExecutionException(getScript(), "String类型 操作符[" + type + "]不支持");
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