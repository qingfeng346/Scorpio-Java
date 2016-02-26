package Scorpio.Variable;

import Scorpio.*;
import Scorpio.Userdata.*;

//静态函数
public class ScorpioStaticMethod extends ScorpioMethod {
    public ScorpioStaticMethod(String name, UserdataMethod method) {
        setMethod(method);
        setMethodName(name);
    }
    @Override
    public Object Call(ScriptObject[] parameters) throws Exception {
        return getMethod().Call(null, parameters);
    }
}