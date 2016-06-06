package Scorpio.Library;

import Scorpio.*;

public class LibraryMath {
    public static final float PI = 3.14159274f;
    public static final float Deg2Rad = 0.0174532924f;
    public static final float Rad2Deg = 57.29578f;
    public static final float Epsilon = 1.401298E-45f;
    public static void Load(Script script) {
        ScriptTable Table = script.CreateTable();
        Table.SetValue("PI", script.CreateDouble(PI));
        Table.SetValue("Deg2Rad", script.CreateDouble(Deg2Rad));
        Table.SetValue("Rad2Deg", script.CreateDouble(Rad2Deg));
        Table.SetValue("Epsilon", script.CreateDouble(Epsilon));
        Table.SetValue("min", script.CreateFunction(new min()));
        Table.SetValue("max", script.CreateFunction(new max()));
        Table.SetValue("abs", script.CreateFunction(new abs()));
        Table.SetValue("floor", script.CreateFunction(new floor()));
        Table.SetValue("clamp", script.CreateFunction(new clamp()));
        Table.SetValue("sqrt", script.CreateFunction(new sqrt()));
        Table.SetValue("pow", script.CreateFunction(new pow()));
        script.SetObjectInternal("math", Table);
    }
    private static class min implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            int num = args.length;
            if (num == 0) {
                return 0f;
            }
            double num2 = ((ScriptNumber)((args[0] instanceof ScriptNumber) ? args[0] : null)).ToDouble();
            for (int i = 1; i < num; i++) {
                double num3 = ((ScriptNumber)((args[i] instanceof ScriptNumber) ? args[i] : null)).ToDouble();
                if (num3 < num2) {
                    num2 = num3;
                }
            }
            return num2;
        }
    }
    private static class max implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            int num = args.length;
            if (num == 0) {
                return 0f;
            }
            double num2 = ((ScriptNumber)((args[0] instanceof ScriptNumber) ? args[0] : null)).ToDouble();
            for (int i = 1; i < num; i++) {
                double num3 = ((ScriptNumber)((args[i] instanceof ScriptNumber) ? args[i] : null)).ToDouble();
                if (num3 > num2) {
                    num2 = num3;
                }
            }
            return num2;
        }
    }
    private static class abs implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptNumber)((args [0] instanceof ScriptNumber) ? args [0] : null)).Abs();
        }
    }
    private static class floor implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptNumber)((args [0] instanceof ScriptNumber) ? args [0] : null)).Floor();
        }
    }
    private static class clamp implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptNumber)((args [0] instanceof ScriptNumber) ? args [0] : null)).Clamp((ScriptNumber)((args [1] instanceof ScriptNumber) ? args [1] : null), (ScriptNumber)((args [2] instanceof ScriptNumber) ? args [2] : null));
        }
    }
    private static class sqrt implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptNumber)((args [0] instanceof ScriptNumber) ? args [0] : null)).Sqrt();
        }
    }
    private static class pow implements ScorpioHandle {
        public final Object Call(ScriptObject[] args) {
            return ((ScriptNumber)((args [0] instanceof ScriptNumber) ? args [0] : null)).Pow((ScriptNumber)((args [1] instanceof ScriptNumber) ? args [1] : null));
        }
    }
}