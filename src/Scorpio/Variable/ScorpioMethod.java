package Scorpio.Variable;

import Scorpio.ScriptObject;
import Scorpio.Userdata.*;

public class ScorpioMethod {
    private UserdataMethod m_Method;
    private Object m_Object;
    private String privateMethodName;
    public final String getMethodName() {
        return privateMethodName;
    }
    private void setMethodName(String value) {
        privateMethodName = value;
    }
    public ScorpioMethod(Object obj, String name, UserdataMethod method) {
        m_Object = obj;
        m_Method = method;
        setMethodName(name);
    }
    public final Object Call(ScriptObject[] parameters) throws Exception {
        return m_Method.Call(m_Object, parameters);
    }
}