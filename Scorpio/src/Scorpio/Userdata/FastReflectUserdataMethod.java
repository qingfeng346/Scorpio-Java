package Scorpio.Userdata;

import Scorpio.*;

public class FastReflectUserdataMethod extends UserdataMethod {
    public FastReflectUserdataMethod(int t, boolean isStatic, Script script, java.lang.Class<?> type, String methodName, java.lang.reflect.Method[] methods, IScorpioFastReflectMethod fastMethod) {
        this.Initialize(t, isStatic, script, type, methodName, methods, fastMethod);
    }
}