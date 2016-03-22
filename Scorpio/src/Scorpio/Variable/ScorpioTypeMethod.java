package Scorpio.Variable;

import Scorpio.*;
import Scorpio.Userdata.*;

//类函数 c#类 类函数  直接获取类成员函数引用 然后调用时第一个参数传入实例 后面传参数
public class ScorpioTypeMethod extends ScorpioMethod {
    private Script m_script;
    //所在的类
    private java.lang.Class<?> m_Type;
    public ScorpioTypeMethod(Script script, String name, UserdataMethod method, java.lang.Class<?> type) {
        m_script = script;
        m_Type = type;
        setMethod(method);
        setMethodName(name);
    }
    @Override
    public Object Call(ScriptObject[] parameters) {
        int length = parameters.length;
        Util.Assert(length > 0, m_script, "length > 0");
        if (length > 1) {
            ScriptObject[] pars = new ScriptObject[parameters.length - 1];
            System.arraycopy(parameters, 1, pars, 0, pars.length);
            if (parameters[0] instanceof ScriptNumber) {
                return getMethod().Call(Util.ChangeType_impl(parameters[0].getObjectValue(), m_Type), pars);
            }
            else {
                return getMethod().Call(parameters[0].getObjectValue(), pars);
            }
        }
        else {
            return getMethod().Call(parameters[0].getObjectValue(), new ScriptObject[0]);
        }
    }
}