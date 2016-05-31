package Scorpio.Variable;

import Scorpio.*;
import Scorpio.Userdata.*;

public abstract class ScorpioMethod {
    protected UserdataMethod m_Method;
    protected String m_MethodName;
    public final UserdataMethod getMethod() {
        return m_Method;
    }
    public final String getMethodName() {
        return m_MethodName;
    }
    public abstract Object Call(ScriptObject[] parameters); //调用函数
}