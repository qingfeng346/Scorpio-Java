package Scorpio.Variable;

import Scorpio.*;
import Scorpio.Userdata.*;

public abstract class ScorpioMethod {
    private UserdataMethod privateMethod;
    public final UserdataMethod getMethod() {
        return privateMethod;
    }
    protected final void setMethod(UserdataMethod value) {
        privateMethod = value;
    }
    private String privateMethodName;
    public final String getMethodName() {
        return privateMethodName;
    }
    protected final void setMethodName(String value) {
        privateMethodName = value;
    }
    public abstract Object Call(ScriptObject[] parameters) throws Exception; //调用函数
}