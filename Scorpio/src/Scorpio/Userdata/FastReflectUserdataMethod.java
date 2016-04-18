package Scorpio.Userdata;

import Scorpio.*;

public class FastReflectUserdataMethod extends UserdataMethod {
    public FastReflectUserdataMethod(boolean isStatic, Script script, java.lang.Class<?> type, String methodName, ScorpioMethodInfo[] methods, IScorpioFastReflectMethod fastMethod) {
        this.Initialize(isStatic, script, type, methodName, methods, fastMethod);
    }
}