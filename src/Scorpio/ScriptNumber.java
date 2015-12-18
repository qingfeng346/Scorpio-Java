package Scorpio;

import Scorpio.CodeDom.*;
import Scorpio.Compiler.*;

//脚本数字类型
public abstract class ScriptNumber extends ScriptObject {
    protected ScriptNumber(Script script) {
        super(script);
    }
    @Override
    public ObjectType getType() { return ObjectType.Number; }
    public abstract ScriptNumber Calc(CALC c);
    public abstract ScriptNumber Negative();
    public abstract ScriptNumber Abs();
    public abstract ScriptNumber Floor();
    public abstract ScriptNumber Clamp(ScriptNumber min, ScriptNumber max);
	public ScriptNumber Sqrt () {													//取平方根
		return getScript().CreateDouble (Math.sqrt(ToDouble()));
	}
	public ScriptNumber Pow (ScriptNumber value) {									//取几次方
		return getScript().CreateDouble (Math.pow(ToDouble(), value.ToDouble()));
	}
    public abstract boolean Compare(TokenType type, ScriptNumber num); //两个数值比较 > >= < <=
    public abstract ScriptObject Compute(TokenType type, ScriptNumber obj); //位运算或者运算符 + - * / % | & ^ >> <<
    public abstract ScriptObject AssignCompute(TokenType type, ScriptNumber obj); //位运算或者运算符复制运算 += -= *= /= %= |= &= ^= >>= <<=
    
    public int ToInt32() { return Util.ToInt32(getObjectValue()); }
    public double ToDouble() { return Util.ToDouble(getObjectValue()); }
    public long ToLong() { return Util.ToInt64(getObjectValue()); }
}