package Scorpio.Exception;

public class StackInfo {
    public String Breviary = ""; // 文件摘要
    public int Line = 1; // 起始关键字所在行数
    public StackInfo() {
    }
    public StackInfo(String breviary, int line) {
        Breviary = breviary;
        Line = line;
    }
}