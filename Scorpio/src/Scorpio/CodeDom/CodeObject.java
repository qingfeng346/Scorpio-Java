package Scorpio.CodeDom;

import Scorpio.Exception.*;

//一个需要解析的Object
public class CodeObject {
    public boolean Not; // ! 标识（非xxx）
    public boolean Minus; // - 标识（负数）
    public boolean Negative; // ~ 标识（取反操作）
    public StackInfo StackInfo; // 堆栈数据
    public CodeObject() {
    }
    public CodeObject(String breviary, int line) {
        StackInfo = new StackInfo(breviary, line);
    }
}