package main;

import Scorpio.Script;
import Scorpio.ScriptObject;

public class Test {
	public static void Hello()
	{
		System.out.println("hello world");
	}
	public static void main(String[] args)
	{
		try {
			Script script = new Script();
			script.LoadLibrary();
			script.SetObject("Test", Test.class);
			ScriptObject obj = script.LoadFile("C:/Users/while/Desktop/a.sco");
			System.out.println(obj == null ? "null" : obj.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
