package Scorpio;

import Scorpio.CodeDom.*;

//脚本数字类型
public abstract class ScriptNumber extends ScriptObject {
    protected ScriptNumber(Script script) {
        super(script);
    }
    @Override
    public ObjectType getType() {
        return ObjectType.Number;
    }
    public abstract ScriptNumber Calc(CALC c);
    public abstract ScriptNumber Negative(); //取相反值
    public abstract ScriptNumber Abs(); //取绝对值
    public abstract ScriptNumber Floor(); //取数的整数
    public abstract ScriptNumber Clamp(ScriptNumber min, ScriptNumber max); //取值的区间
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