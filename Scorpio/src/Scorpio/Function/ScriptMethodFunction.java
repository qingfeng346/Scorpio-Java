package Scorpio.Function;

import Scorpio.*;
import Scorpio.Variable.*;
import Scorpio.Exception.*;

public class ScriptMethodFunction extends ScriptFunction {
    private ScorpioMethod m_Method; //程序函数
    public final ScorpioMethod getMethod() {
        return m_Method;
    }
    public ScriptMethodFunction(Script script, ScorpioMethod method) {
        this(script, method.getMethodName(), method);
    }
    public ScriptMethodFunction(Script script, String name, ScorpioMethod method) {
        super(script, name);
        this.m_Method = method;
    }
    @Override
    public Object Call(ScriptObject[] parameters) {
        try {
            return m_Method.Call(parameters);
        }
        catch (RuntimeException ex) {
            throw new ExecutionException(getScript(), "CallFunction [" + getName() + "] is error : " + ex.toString());
        }
    }
}