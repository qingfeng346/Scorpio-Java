package Scorpio;

import Scorpio.Userdata.*;

public interface IScriptUserdataFactory {
    ScriptUserdata GetEnum(java.lang.Class type);
    ScriptUserdata GetDelegate(java.lang.Class type);
    UserdataType GetScorpioType(java.lang.Class type);
    ScriptUserdata create(Script script, Object obj);
}