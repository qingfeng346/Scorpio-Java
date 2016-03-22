package Scorpio;

public interface IScriptExtensions {
    void print(String str);
    boolean IsEnum(java.lang.Class<?> type);
    boolean FileExist(String file);
    byte[] GetFileBuffer(String file);
}