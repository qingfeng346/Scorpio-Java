package Scorpio;

import Scorpio.CodeDom.*;
import Scorpio.Variable.*;
import Scorpio.Compiler.*;
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
    public abstract ScriptNumber Calc(CALC c);
    public abstract ScriptNumber Negative();
    public abstract boolean Compare(TokenType type, ScriptNumber num); //两个数值比较 > >= < <=
    public abstract ScriptObject Compute(TokenType type, ScriptNumber obj); //位运算或者运算符 + - * / % | & ^ >> <<
    public abstract ScriptObject AssignCompute(TokenType type, ScriptNumber obj); //位运算或者运算符复制运算 += -= *= /= %= |= &= ^= >>= <<=
    //public abstract ScriptObject ComputePlus(ScriptNumber obj);             // +
    //public abstract ScriptObject ComputeMinus(ScriptNumber obj);            // -
    //public abstract ScriptObject ComputeMultiply(ScriptNumber obj);         // *
    //public abstract ScriptObject ComputeDivide(ScriptNumber obj);           // /
    //public abstract ScriptObject ComputeModulo(ScriptNumber obj);           // %

    //public abstract ScriptObject AssignPlus(ScriptNumber obj);             // +=
    //public abstract ScriptObject AssignMinus(ScriptNumber obj);            // -=
    //public abstract ScriptObject AssignMultiply(ScriptNumber obj);         // *=
    //public abstract ScriptObject AssignDivide(ScriptNumber obj);           // /=
    //public abstract ScriptObject AssignModulo(ScriptNumber obj);           // %=
    public final int ToInt32() {
        return Util.ToInt32(getObjectValue());
    }
    public double ToDouble() {
        return Util.ToDouble(getObjectValue());
    }
    public long ToLong() {
        return Util.ToInt64(getObjectValue());
    }
}