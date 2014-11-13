package Scorpio;

import Scorpio.Variable.*;

//脚本bool类型
public class ScriptBoolean extends ScriptObject {
    public static final ScriptBoolean True = new ScriptBoolean(true);
    public static final ScriptBoolean False = new ScriptBoolean(false);
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
    public final void setValue(boolean value) {
        privateValue = value;
    }
    public ScriptBoolean(boolean value) {
        super(null);
        this.setValue(value);
    }
    public final ScriptBoolean Inverse() {
        return getValue() ? False : True;
    }
    public static ScriptBoolean Get(boolean b) {
        return b ? True : False;
    }
}