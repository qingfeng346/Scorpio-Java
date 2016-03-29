package Scorpio.Function;

import Scorpio.*;
import Scorpio.Exception.*;

public class ScriptHandleFunction extends ScriptFunction {
    private ScorpioHandle m_Handle; //程序函数执行类
    public ScriptHandleFunction(Script script, ScorpioHandle handle) {
        this(script, handle.getClass().getName(), handle);
    }
    public ScriptHandleFunction(Script script, String name, ScorpioHandle handle) {
        super(script, name);
        this.m_Handle = handle;
    }
    @Override
    public Object Call(ScriptObject[] parameters) {
        try {
            return m_Handle.Call(parameters);
        }
        catch (RuntimeException ex) {
            throw new ExecutionException(getScript(), "CallFunction [" + getName() + "] is error : " + ex.toString());
        }
    }
}