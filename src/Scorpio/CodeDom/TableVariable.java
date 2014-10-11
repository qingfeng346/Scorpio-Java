package Scorpio.CodeDom;

import Scorpio.*;

public class TableVariable
{
	public String Key;
	public CodeObject Value;
	public TableVariable(String key, CodeObject value)
	{
		this.Key = key;
		this.Value = value;
	}
}