package Scorpio.CodeDom.Temp;

import Scorpio.Compiler.*;
import Scorpio.CodeDom.*;

public class TempOperator {
    //运算符优先级表，
    private static java.util.HashMap<TokenType, TempOperator> Operators = new java.util.HashMap<TokenType, TempOperator>();
    static {
        Operators.put(TokenType.InclusiveOr, new TempOperator(TokenType.InclusiveOr, 1));
        Operators.put(TokenType.Combine, new TempOperator(TokenType.Combine, 1));
        Operators.put(TokenType.XOR, new TempOperator(TokenType.XOR, 1));
        Operators.put(TokenType.Shi, new TempOperator(TokenType.Shi, 1));
        Operators.put(TokenType.Shr, new TempOperator(TokenType.Shr, 1));
        Operators.put(TokenType.And, new TempOperator(TokenType.And, 1));
        Operators.put(TokenType.Or, new TempOperator(TokenType.Or, 1));

        Operators.put(TokenType.Equal, new TempOperator(TokenType.Equal, 2));
        Operators.put(TokenType.NotEqual, new TempOperator(TokenType.NotEqual, 2));
        Operators.put(TokenType.Greater, new TempOperator(TokenType.Greater, 2));
        Operators.put(TokenType.GreaterOrEqual, new TempOperator(TokenType.GreaterOrEqual, 2));
        Operators.put(TokenType.Less, new TempOperator(TokenType.Less, 2));
        Operators.put(TokenType.LessOrEqual, new TempOperator(TokenType.LessOrEqual, 2));

        Operators.put(TokenType.Plus, new TempOperator(TokenType.Plus, 3));
        Operators.put(TokenType.Minus, new TempOperator(TokenType.Minus, 3));

        Operators.put(TokenType.Multiply, new TempOperator(TokenType.Multiply, 4));
        Operators.put(TokenType.Divide, new TempOperator(TokenType.Divide, 4));
        Operators.put(TokenType.Modulo, new TempOperator(TokenType.Modulo, 4));
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