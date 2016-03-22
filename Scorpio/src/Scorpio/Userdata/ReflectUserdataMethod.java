package Scorpio.Userdata;

import Scorpio.*;

public class ReflectUserdataMethod extends UserdataMethod {
    public ReflectUserdataMethod(Script script, java.lang.Class<?> type, String methodName, java.lang.reflect.Method[] methods) {
        this.Initialize(script, type, methodName, methods);
    }
    public ReflectUserdataMethod(Script script, java.lang.Class<?> type, String methodName, java.lang.reflect.Constructor<?>[] cons) {
        this.Initialize(script, type, methodName, cons);
    }
}