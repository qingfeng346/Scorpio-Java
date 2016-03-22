package Scorpio;

import Scorpio.Extensions.*;

public class ScriptExtensions {
    private static IScriptExtensions m_Extensions = null;
    static {
        SetExtensions(new DefaultScriptExtensions());
    }
    public static void SetExtensions(IScriptExtensions extensions) {
        m_Extensions = extensions;
    }
    public static void print(String str) {
        m_Extensions.print(str);
    }
    public static boolean IsEnum(java.lang.Class<?> type) {
        return m_Extensions.IsEnum(type);
    }
    public static boolean FileExist(String file) {
        return m_Extensions.FileExist(file);
    }
    public static byte[] GetFileBuffer(String file) {
        return m_Extensions.GetFileBuffer(file);
    }
}