package Scorpio;

import Scorpio.CodeDom.*;
import Scorpio.Exception.*;

//脚本数字类型
public abstract class ScriptNumber extends ScriptObject {
    protected ScriptNumber(Script script) {
        super(script);
    }
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    public ScriptNumber Calc(CALC c) { //数字计算
        throw new ExecutionException(m_Script, "数字类型不支持Calc函数");
    }
    public ScriptNumber Minus() { //取相反值 -
        throw new ExecutionException(m_Script, "数字类型不支持Minus函数");
    }
    public ScriptNumber Negative() { //取反操作 ~
        throw new ExecutionException(m_Script, "数字类型不支持Negative函数");
    }
    public ScriptNumber Abs() { //取绝对值
        throw new ExecutionException(m_Script, "数字类型不支持Abs函数");
    }
    public ScriptNumber Floor() { //取数的整数
        throw new ExecutionException(m_Script, "数字类型不支持Floor函数");
    }
    public ScriptNumber Clamp(ScriptNumber min, ScriptNumber max) { //取值的区间
        throw new ExecutionException(m_Script, "数字类型不支持Clamp函数");
    }
    public final ScriptNumber Sqrt() { //取平方根
        return m_Script.CreateDouble(Math.sqrt (ToDouble()));
    }
    public final ScriptNumber Pow(ScriptNumber value) { //取几次方
        return m_Script.CreateDouble(Math.pow (ToDouble(), value.ToDouble()));
    }
    public int ToInt32() {
        return Util.ToInt32(getObjectValue());
    }
    public double ToDouble() {
        return Util.ToDouble(getObjectValue());
    }
    public long ToLong() {
        return Util.ToInt64(getObjectValue());
    }
}