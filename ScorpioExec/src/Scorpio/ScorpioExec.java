package Scorpio;
import java.io.File;
import java.util.Scanner;
public class ScorpioExec {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		Script script = new Script();
		System.out.println("开始执行，当前版本:" + Script.Version);
		script.LoadLibrary();
		if (args.length >= 1) {
            try {
            	long watch = System.currentTimeMillis();
            	script.PushSearchPath(new File(args[0]).getParent());
            	System.out.println("返回值为:" + script.LoadFile(args[0]));
            	System.out.println("运行时间:" + (System.currentTimeMillis() - watch) + " ms");
            } catch (Exception ex) {
            	System.out.println(script.GetStackInfo());
            	System.out.println(ex.toString());
            }
		} else {
			Scanner sc = new Scanner(System.in); 
			while (true) {
				try {
					String str = sc.nextLine();
					if (str.equals("exit"))  { 
	                    break;
	                } else if (str.equals("version")) {
	                	System.out.println(Script.Version);
	                } else {
	                    script.LoadString(str);
	                }
				} catch (Exception ex) {
					System.out.println(script.GetStackInfo());
	            	System.out.println(ex.toString());
				}
			}
		}
	}
}
