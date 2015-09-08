package Scorpio.Library;
import Scorpio.*;

public class LibraryMath {
	public static final float PI = 3.14159274f;
	public static final float Deg2Rad = 0.0174532924f;
	public static final float Rad2Deg = 57.29578f;
	public static final float Epsilon = 1.401298E-45f;
    public static void Load(Script script) {
        ScriptTable Table = script.CreateTable();
		Table.SetValue("PI", script.CreateNumber(PI));
		Table.SetValue("Deg2Rad", script.CreateNumber(Deg2Rad));
		Table.SetValue("Rad2Deg", script.CreateNumber(Rad2Deg));
		Table.SetValue("Epsilon", script.CreateNumber(Epsilon));
		Table.SetValue("min", script.CreateFunction(new min()));
		Table.SetValue("max", script.CreateFunction(new max()));
		Table.SetValue("abs", script.CreateFunction(new abs()));
        script.SetObjectInternal("math", Table);
    }
    private static class min implements ScorpioHandle {
		public final Object Call(ScriptObject[] args) {
			int num = args.length;
			if (num == 0) return 0f;
			double num2 = ((ScriptNumber)args[0]).ToDouble();
			for (int i = 1; i < num; i++) {
				double num3 = ((ScriptNumber)args[i]).ToDouble();
				if (num3 < num2)
					num2 = num3;
			}
			return num2;
		}
	}
	private static class max implements ScorpioHandle {
		public final Object Call(ScriptObject[] args) {
			int num = args.length;
			if (num == 0) return 0f;
			double num2 = ((ScriptNumber)args[0]).ToDouble();
			for (int i = 1; i < num; i++) {
				double num3 = ((ScriptNumber)args[i]).ToDouble();
				if (num3 > num2)
					num2 = num3;
			}
			return num2;
		}
	}
	private static class abs implements ScorpioHandle {
		public final Object Call(ScriptObject[] args) {
			return ((ScriptNumber)args[0]).Abs ();
		}
	}
}
