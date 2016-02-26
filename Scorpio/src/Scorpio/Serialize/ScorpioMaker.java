package Scorpio.Serialize;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import Scorpio.Util;
import Scorpio.Compiler.*;
public class ScorpioMaker
{
	private static byte LineFlag = Byte.MAX_VALUE;
	public static java.util.ArrayList<Token> Deserialize(byte[] data)
	{
		ByteBuffer reader = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		reader.get(); //取出第一个null字符
		java.util.ArrayList<Token> tokens = new java.util.ArrayList<Token>();
		int count = reader.getInt();
		int sourceLine = 0;
		for (int i = 0; i < count; ++i)
		{
			byte flag = reader.get();
			if (flag == LineFlag)
			{
				sourceLine = reader.getInt();
				flag = reader.get();
			}
			TokenType type = TokenType.forValue(flag);
			Object value = null;
			switch (type)
			{
				case Boolean:
					value = (reader.get() == 1);
					break;
				case String:
				case SimpleString:
					value = Util.ReadString(reader);
					break;
				case Identifier:
					value = Util.ReadString(reader);
					break;
				case Number:
					if (reader.get() == 1)
						value = reader.getDouble();
					else
						value = reader.getLong();
					break;
				default:
					value = type.toString();
					break;
			}
			tokens.add(new Token(type, value, sourceLine - 1, 0));
		}
		return tokens;
	}
}