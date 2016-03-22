package Scorpio.CodeDom;

//++或者--标识
public enum CALC {
    NONE,
    PRE_INCREMENT, //前置++
    POST_INCREMENT, //后置++
    PRE_DECREMENT, //前置--
    POST_DECREMENT; //后置--

    public int getValue() {
        return this.ordinal();
    }

    public static CALC forValue(int value) {
        return values()[value];
    }
}