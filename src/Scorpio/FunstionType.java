package Scorpio;

import Scorpio.Runtime.*;
import Scorpio.Variable.*;

/**  函数类型 
*/
public enum FunstionType {
    //脚本函数
    Script,
    //注册的C函数
    Function,
    //注册的C函数
    Handle,
    //函数
    Method;

    public int getValue() {
        return this.ordinal();
    }

    public static FunstionType forValue(int value) {
        return values()[value];
    }
}