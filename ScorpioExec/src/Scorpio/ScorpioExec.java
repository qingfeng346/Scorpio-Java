package Scorpio;

public class ScorpioExec {
	public static void main(String[] args) {
		Script script = new Script();
		System.out.println("开始执行，当前版本:" + Script.Version);
		script.LoadLibrary();
		if (args.length >= 1) {
            try {
            	long watch = System.currentTimeMillis();
            	System.out.println("返回值为:" + script.LoadFile(args[0]));
            	System.out.println("运行时间:" + (System.currentTimeMillis() - watch) + " ms");
            } catch (Exception ex) {
            	System.out.println(script.GetStackInfo());
            	System.out.println(ex.toString());
            }
		}
	}
}
