package Scorpio.Compiler;

//脚本的表征类型
public enum TokenType
{
	/** 
	 空类型（没有实际用途）
	 
	*/
	None(0),
	/** 
	 global
	 
	*/
	Global(1),
	/** 
	 var
	 
	*/
	Var(2),
	/** 
	 {
	 
	*/
	LeftBrace(3),
	/** 
	 }
	 
	*/
	RightBrace(4),
	/** 
	 (
	 
	*/
	LeftPar(5),
	/** 
	 )
	 
	*/
	RightPar(6),
	/** 
	 [
	 
	*/
	LeftBracket(7),
	/** 
	 ]
	 
	*/
	RightBracket(8),
	/** 
	 .
	 
	*/
	Period(9),
	/** 
	 ,
	 
	*/
	Comma(10),
	/** 
	 :
	 
	*/
	Colon(11),
	/** 
	 ;
	 
	*/
	SemiColon(12),
	/** 
	 ?
	 
	*/
	QuestionMark(13),
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
	 %
	 
	*/
	Modulo(24),
	/** 
	 %=
	 
	*/
	AssignModulo(25),
	/** 
	 
	 
	*/
	Power(26),
	/** 
	 !
	 
	*/
	Not(27),
	/** 
	 &&
	 
	*/
	And(28),
	/** 
	 ||
	 
	*/
	Or(29),
	/** 
	 =
	 
	*/
	Assign(30),
	/** 
	 ==
	 
	*/
	Equal(31),
	/** 
	 !=
	 
	*/
	NotEqual(32),
	/** 
	 >
	 
	*/
	Greater(33),
	/** 
	 >=
	 
	*/
	GreaterOrEqual(34),
	/** 
	  <
	 
	*/
	Less(35),
	/** 
	 <=
	 
	*/
	LessOrEqual(36),
	/** 
	 ...
	 
	*/
	Params(37),
	/** 
	 if
	 
	*/
	If(38),
	/** 
	 else
	 
	*/
	Else(39),
	/** 
	 elif
	 
	*/
	ElseIf(40),
	/** 
	 for
	 
	*/
	For(41),
	/** 
	 foreach
	 
	*/
	Foreach(42),
	/** 
	 in
	 
	*/
	In(43),
	/** 
	 break
	 
	*/
	Break(44),
	/** 
	 continue
	 
	*/
	Continue(45),
	/** 
	 return
	 
	*/
	Return(46),
	/** 
	 while
	 
	*/
	While(47),
	/** 
	 function
	 
	*/
	Function(48),
	/** 
	 bool true false
	 
	*/
	Boolean(49),
	/** 
	 int float
	 
	*/
	Number(50),
	/** 
	 string
	 
	*/
	String(51),
	/** 
	 null
	 
	*/
	Null(52),
	/** 
	 require,include,import,using
	 
	*/
	Require(53),
	/** 
	 eval
	 
	*/
	Eval(54),
	/** 
	 说明符
	 
	*/
	Identifier(55),
	/** 
	 结束
	 
	*/
	Finished(56);

	private int intValue;
	private static java.util.HashMap<Integer, TokenType> mappings;
	private synchronized static java.util.HashMap<Integer, TokenType> getMappings()
	{
		if (mappings == null)
		{
			mappings = new java.util.HashMap<Integer, TokenType>();
		}
		return mappings;
	}

	private TokenType(int value)
	{
		intValue = value;
		TokenType.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static TokenType forValue(int value)
	{
		return getMappings().get(value);
	}
}