package Scorpio.Variable;

import Scorpio.*;
import Scorpio.Userdata.*;

//类函数
public class ScorpioTypeMethod extends ScorpioMethod {
    public ScorpioTypeMethod(String name, UserdataMethod method) {
        setMethod(method);
        setMethodName(name);
    }
    @Override
    public Object Call(ScriptObject[] parameters) throws Exception {
        int length = parameters.length;
        Util.Assert(length > 0, "length > 0");
        Util.Assert(parameters[0] instanceof ScriptUserdata, "parameters[0] is ScriptUserdata");
        if (length > 1) {
            ScriptObject[] pars = new ScriptObject[parameters.length - 1];
            System.arraycopy(parameters, 1, pars, 0, pars.length);
            return getMethod().Call(parameters[0].getObjectValue(), pars);
        }
        else {
            return getMethod().Call(parameters[0].getObjectValue(), new ScriptObject[0]);
        }
    }
}