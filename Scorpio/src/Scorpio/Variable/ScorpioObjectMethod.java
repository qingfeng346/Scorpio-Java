package Scorpio.Variable;

import Scorpio.*;
import Scorpio.Userdata.*;

//实例函数
public class ScorpioObjectMethod extends ScorpioMethod {
    private Object m_Object;
    public ScorpioObjectMethod(Object obj, String name, UserdataMethod method) {
        m_Object = obj;
        setMethod(method);
        setMethodName(name);
    }
    @Override
    public Object Call(ScriptObject[] parameters) {
        return getMethod().Call(m_Object, parameters);
    }
}