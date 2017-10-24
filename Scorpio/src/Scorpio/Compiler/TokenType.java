package Scorpio.Compiler;

//脚本的表征类型
public enum TokenType {
    /** 
     空类型（没有实际用途）
     
    */
    None(0),
    /** 
     var
     
    */
    Var(1),
    /** 
     {
     
    */
    LeftBrace(2),
    /** 
     }
     
    */
    RightBrace(3),
    /** 
     (
     
    */
    LeftPar(4),
    /** 
     )
     
    */
    RightPar(5),
    /** 
     [
     
    */
    LeftBracket(6),
    /** 
     ]
     
    */
    RightBracket(7),
    /** 
     .
     
    */
    Period(8),
    /** 
     ,
     
    */
    Comma(9),
    /** 
     :
     
    */
    Colon(10),
    /** 
     ;
     
    */
    SemiColon(11),
    /** 
     ?
     
    */
    QuestionMark(12),
    /** 
     #
     
    */
    Sharp(13),
    /** 
     +
     
    */
    Plus(14),
    /** 
     ++
     
    */
    Increment(15),
    /** 
     +=
     
    */
    AssignPlus(16),
    /** 
     -
     
    */
    Minus(17),
    /** 
     --
     
    */
    Decrement(18),
    /** 
     -=
     
    */
    AssignMinus(19),
    /** 
     *
     
    */
    Multiply(20),
    /** 
     *=
     
    */
    AssignMultiply(21),
    /** 
     /
     
    */
    Divide(22),
    /** 
     /=
     
    */
    AssignDivide(23),
    /** 
     % 模运算
     
    */
    Modulo(24),
    /** 
     %=
     
    */
    AssignModulo(25),
    /** 
     | 或运算
     
    */
    InclusiveOr(26),
    /** 
     |=
     
    */
    AssignInclusiveOr(27),
    /** 
     ||
     
    */
    Or(28),
    /** 
     & 并运算
     
    */
    Combine(29),
    /** 
     &=
     
    */
    AssignCombine(30),
    /** 
     &&
     
    */
    And(31),
    /** 
     ^ 异或
     
    */
    XOR(32),
    /** 
     ^=
     
    */
    AssignXOR(33),
    /**  
     ~ 取反操作
     
    */
    Negative(34),
    /** 
     <<左移
     
    */
    Shi(35),
    /** 
     <<=
     
    */
    AssignShi(36),
    /** 
     >> 右移
     
    */
    Shr(37),
    /** 
     >>=
     
    */
    AssignShr(38),
    /** 
     !
     
    */
    Not(39),
    /** 
     =
     
    */
    Assign(40),
    /** 
     ==
     
    */
    Equal(41),
    /** 
     !=
     
    */
    NotEqual(42),
    /** 
     >
     
    */
    Greater(43),
    /** 
     >=
     
    */
    GreaterOrEqual(44),
    /** 
      <
     
    */
    Less(45),
    /** 
     <=
     
    */
    LessOrEqual(46),
    /** 
     ...
     
    */
    Params(47),
    /** 
     if
     
    */
    If(48),
    /** 
     else
     
    */
    Else(49),
    /** 
     elif
     
    */
    ElseIf(50),
    /** 
     ifndef
     
    */
    Ifndef(51),
    /** 
     endif
     
    */
    Endif(52),
    /** 
     for
     
    */
    For(53),
    /** 
     foreach
     
    */
    Foreach(54),
    /** 
     in
     
    */
    In(55),
    /** 
     switch
     
    */
    Switch(56),
    /** 
     case
     
    */
    Case(57),
    /** 
     default
     
    */
    Default(58),
    /** 
     break
     
    */
    Break(59),
    /** 
     continue
     
    */
    Continue(60),
    /** 
     return
     
    */
    Return(61),
    /** 
     while
     
    */
    While(62),
    /** 
     function
     
    */
    Function(63),
    /** 
     try
     
    */
    Try(64),
    /** 
     catch
     
    */
    Catch(65),
    /** 
     throw
     
    */
    Throw(66),
    /** 
     finally
     
    */
    Finally(67),
    /** 
     define
     
    */
    Define(68),
    /** 
     bool true false
     
    */
    Boolean(69),
    /** 
     int float
     
    */
    Number(70),
    /** 
     string
     
    */
    String(71),
    /** 
     @"" @'' string
     
    */
    SimpleString(72),
    /** 
     null
     
    */
    Null(73),
    /** 
     eval
     
    */
    Eval(74),
    /** 
     说明符
     
    */
    Identifier(75),
    /** 
     结束
     
    */
    Finished(76);

    private int intValue;
    private static java.util.HashMap<Integer, TokenType> mappings;
    private synchronized static java.util.HashMap<Integer, TokenType> getMappings() {
        if (mappings == null) {
            mappings = new java.util.HashMap<Integer, TokenType>();
        }
        return mappings;
    }

    private TokenType(int value) {
        intValue = value;
        TokenType.getMappings().put(value, this);
    }

    public int getValue() {
        return intValue;
    }

    public static TokenType forValue(int value) {
        return getMappings().get(value);
    }
}