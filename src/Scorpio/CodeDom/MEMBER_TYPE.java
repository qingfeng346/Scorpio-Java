package Scorpio.CodeDom;

import Scorpio.*;

//成员类型
public enum MEMBER_TYPE
{
	STRING,
	NUMBER,
	OBJECT;

	public int getValue()
	{
		return this.ordinal();
	}

	public static MEMBER_TYPE forValue(int value)
	{
		return values()[value];
	}
}