package Scorpio.Variable;

import Scorpio.*;
import Scorpio.Userdata.*;

//静态函数 c#类静态函数
public class ScorpioStaticMethod extends ScorpioMethod {
    public ScorpioStaticMethod(String name, UserdataMethod method) {
        m_Method = method;
        m_MethodName = name;
    }
    @Override
    public Object Call(ScriptObject[] parameters) {
        return m_Method.Call(null, parameters);
    }
}