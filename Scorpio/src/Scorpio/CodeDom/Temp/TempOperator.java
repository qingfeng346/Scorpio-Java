package Scorpio.CodeDom.Temp;

import Scorpio.Compiler.*;

public class TempOperator {
    private static final int HighOperate = 6;
    private static final int LowOperate = 5;
    private static final int Compare = 4;
    private static final int BitOperate = 3;
    private static final int Logical = 2;
    //运算符优先级表 优先级高的 先执行
    private static java.util.HashMap<TokenType, TempOperator> Operators = new java.util.HashMap<TokenType, TempOperator>();
    static {
        Operators.put(TokenType.And, new TempOperator(TokenType.And, Logical));
        Operators.put(TokenType.Or, new TempOperator(TokenType.Or, Logical));

        Operators.put(TokenType.InclusiveOr, new TempOperator(TokenType.InclusiveOr, BitOperate));
        Operators.put(TokenType.Combine, new TempOperator(TokenType.Combine, BitOperate));
        Operators.put(TokenType.XOR, new TempOperator(TokenType.XOR, BitOperate));
        Operators.put(TokenType.Shi, new TempOperator(TokenType.Shi, BitOperate));
        Operators.put(TokenType.Shr, new TempOperator(TokenType.Shr, BitOperate));

        Operators.put(TokenType.Equal, new TempOperator(TokenType.Equal, Compare));
        Operators.put(TokenType.NotEqual, new TempOperator(TokenType.NotEqual, Compare));
        Operators.put(TokenType.Greater, new TempOperator(TokenType.Greater, Compare));
        Operators.put(TokenType.GreaterOrEqual, new TempOperator(TokenType.GreaterOrEqual, Compare));
        Operators.put(TokenType.Less, new TempOperator(TokenType.Less, Compare));
        Operators.put(TokenType.LessOrEqual, new TempOperator(TokenType.LessOrEqual, Compare));

        Operators.put(TokenType.Plus, new TempOperator(TokenType.Plus, LowOperate));
        Operators.put(TokenType.Minus, new TempOperator(TokenType.Minus, LowOperate));

        Operators.put(TokenType.Multiply, new TempOperator(TokenType.Multiply, HighOperate));
        Operators.put(TokenType.Divide, new TempOperator(TokenType.Divide, HighOperate));
        Operators.put(TokenType.Modulo, new TempOperator(TokenType.Modulo, HighOperate));
    }
    public TokenType Operator = TokenType.forValue(0); //符号类型
    public int Level; //优先级
    public TempOperator(TokenType oper, int level) {
        this.Operator = oper;
        this.Level = level;
    }
    //获得运算符
    public static TempOperator GetOper(TokenType oper) {
        if (Operators.containsKey(oper)) {
            return Operators.get(oper);
        }
        return null;
    }

}