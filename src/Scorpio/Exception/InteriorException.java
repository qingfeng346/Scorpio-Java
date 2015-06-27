package Scorpio.Exception;

import Scorpio.*;

//内部异常 标识是 脚本里面调用了 throw 函数
public class InteriorException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ScriptObject obj;
    public InteriorException(ScriptObject obj) {
        this.obj = obj;
    }
}