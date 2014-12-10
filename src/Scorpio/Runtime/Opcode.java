package Scorpio.Runtime;

//指令类型
public enum Opcode {
    /**  赋值操作 
    */
    MOV,
    /**  申请一个局部变量 
    */
    VAR,
    /**  执行普通代码块 
    */
    CALL_BLOCK,
    /**  执行If语句 
    */
    CALL_IF,
    /**  执行For语句 
    */
    CALL_FOR,
    /**  执行For语句 
    */
    CALL_FORSIMPLE,
    /**  执行Foreach语句 
    */
    CALL_FOREACH,
    /**  执行While语句 
    */
    CALL_WHILE,
    /**  执行switch语句 
    */
    CALL_SWITCH,
    /**  执行try catch语句 
    */
    CALL_TRY,
    /**  调用一个函数 
    */
    CALL_FUNCTION,
    /**  throw 
    */
    THROW,
    /**  解析一个变量 
    */
    RESOLVE,
    /**  返回值 
    */
    RET,
    /**  break跳出 for foreach while 
    */
    BREAK,
    /**  continue跳出本次 for foreach while 
    */
    CONTINUE;

    public int getValue() {
        return this.ordinal();
    }

    public static Opcode forValue(int value) {
        return values()[value];
    }
}