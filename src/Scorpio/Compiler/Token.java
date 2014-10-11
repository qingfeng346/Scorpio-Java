package Scorpio.Compiler;

//脚本的表征
public class Token
{
	private TokenType privateType = TokenType.forValue(0);
	public final TokenType getType()
	{
		return privateType;
	}
	private void setType(TokenType value)
	{
		privateType = value;
	}
	private Object privateLexeme;
	public final Object getLexeme()
	{
		return privateLexeme;
	}
	private void setLexeme(Object value)
	{
		privateLexeme = value;
	}
	private int privateSourceLine;
	public final int getSourceLine()
	{
		return privateSourceLine;
	}
	private void setSourceLine(int value)
	{
		privateSourceLine = value;
	}
	private int privateSourceChar;
	public final int getSourceChar()
	{
		return privateSourceChar;
	}
	private void setSourceChar(int value)
	{
		privateSourceChar = value;
	}
	public Token(TokenType tokenType, Object lexeme, int sourceLine, int sourceChar)
	{
		this.setType(tokenType);
		this.setLexeme(lexeme);
		this.setSourceLine(sourceLine + 1);
		this.setSourceChar(sourceChar);
	}
	@Override
	public String toString()
	{
		return getType().toString() + "(" + getLexeme().toString() + ")";
	}
}