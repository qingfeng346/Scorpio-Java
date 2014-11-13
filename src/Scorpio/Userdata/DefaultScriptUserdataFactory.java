package Scorpio.Userdata;

import Scorpio.*;

public class DefaultScriptUserdataFactory implements IScriptUserdataFactory {
    public final ScriptUserdata create(Script script, Object obj) {
        java.lang.Class type = (java.lang.Class)((obj instanceof java.lang.Class) ? obj : null);
        if (type != null) {
            if (Util.IsEnum(type)) {
                return new DefaultScriptUserdataEnum(script, type);
            }
        }
        return new DefaultScriptUserdataObject(script, obj);
    }
}