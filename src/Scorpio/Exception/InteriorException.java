package Scorpio.Exception;

import Scorpio.*;

//内部异常 标识是 脚本里面调用了 throw 函数
public class InteriorException extends RuntimeException {
    public ScriptObject obj;
    public InteriorException(ScriptObject obj) {
        this.obj = obj;
    }
}