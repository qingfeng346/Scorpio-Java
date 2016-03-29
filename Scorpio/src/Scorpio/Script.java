package Scorpio;

import java.nio.charset.Charset;

import Scorpio.Runtime.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;
import Scorpio.Function.*;
import Scorpio.Library.*;
import Scorpio.Userdata.*;
import Scorpio.Variable.*;
import Scorpio.Serialize.*;
//脚本类
public class Script {
    public static final String DynamicDelegateName = "__DynamicDelegate__";
    public static final String Version = "master";
    public static final Charset UTF8 = Charset.forName("UTF8");
    private static final String GLOBAL_TABLE = "_G"; //全局table
    private static final String GLOBAL_VERSION = "_VERSION"; //版本号
    private static final String GLOBAL_SCRIPT = "_SCRIPT"; //Script对象
    private IScriptUserdataFactory m_UserdataFactory = null; //Userdata工厂
    private ScriptTable m_GlobalTable; //全局Table
    private java.util.ArrayList<StackInfo> m_StackInfoStack = new java.util.ArrayList<StackInfo>(); //堆栈数据
    private java.util.ArrayList<String> m_SearchPath = new java.util.ArrayList<String>(); //request所有文件的路径集合
    private java.util.HashMap<java.lang.Class<?>, IScorpioFastReflectClass> m_FastReflectClass = new java.util.HashMap<java.lang.Class<?>, IScorpioFastReflectClass>();
    private StackInfo m_StackInfo = new StackInfo(); //最近堆栈数据
    private ScriptNull privateNull;
    public final ScriptNull getNull() {
        return privateNull;
    }
    private void setNull(ScriptNull value) {
        privateNull = value;
    }
    private ScriptBoolean privateTrue;
    public final ScriptBoolean getTrue() {
        return privateTrue;
    }
    private void setTrue(ScriptBoolean value) {
        privateTrue = value;
    }
    private ScriptBoolean privateFalse;
    public final ScriptBoolean getFalse() {
        return privateFalse;
    }
    private void setFalse(ScriptBoolean value) {
        privateFalse = value;
    }
    public final ScriptBoolean GetBoolean(boolean value) {
        return value ? getTrue() : getFalse();
    }
    public Script() {
        setNull(new ScriptNull(this));
        setTrue(new ScriptBoolean(this, true));
        setFalse(new ScriptBoolean(this, false));
        m_UserdataFactory = new DefaultScriptUserdataFactory(this);
        m_GlobalTable = CreateTable();
        m_GlobalTable.SetValue(GLOBAL_TABLE, m_GlobalTable);
        m_GlobalTable.SetValue(GLOBAL_VERSION, CreateString(Version));
        m_GlobalTable.SetValue(GLOBAL_SCRIPT, CreateObject(this));
    }
    public final ScriptObject LoadFile(String strFileName) {
        return LoadFile(strFileName, UTF8);
    }
    public final ScriptObject LoadFile(String fileName, Charset encoding) {
        return LoadBuffer(fileName, ScriptExtensions.GetFileBuffer(fileName), encoding);
    }
    public final ScriptObject LoadBuffer(byte[] buffer) {
        return LoadBuffer("Undefined", buffer, UTF8);
    }
    public final ScriptObject LoadBuffer(String strBreviary, byte[] buffer) {
        return LoadBuffer(strBreviary, buffer, UTF8);
    }
    public final ScriptObject LoadBuffer(String strBreviary, byte[] buffer, Charset encoding) {
        if (buffer == null || buffer.length == 0) {
            return null;
        }
        try {
            if (buffer[0] == 0) {
                return LoadTokens(strBreviary, ScorpioMaker.Deserialize(buffer));
            }
            else {
                return LoadString(strBreviary, new String(buffer, encoding));
            }
        }
        catch (RuntimeException e) {
            throw new ScriptException("load buffer [" + strBreviary + "] is error : " + e.toString());
        }
    }
    public final ScriptObject LoadString(String strBuffer) {
        return LoadString("", strBuffer);
    }
    public final ScriptObject LoadString(String strBreviary, String strBuffer) {
        return LoadString(strBreviary, strBuffer, null, true);
    }
    public final ScriptObject LoadString(String strBreviary, String strBuffer, ScriptContext context, boolean clearStack) {
        try {
            if (Util.IsNullOrEmpty(strBuffer)) {
                return getNull();
            }
            if (clearStack) {
                m_StackInfoStack.clear();
            }
            ScriptLexer scriptLexer = new ScriptLexer(strBuffer, strBreviary);
            return Load(scriptLexer.GetBreviary(), scriptLexer.GetTokens(), context);
        }
        catch (RuntimeException e) {
            throw new ScriptException("load buffer [" + strBreviary + "] is error : " + e.toString());
        }
    }
    public final ScriptObject LoadTokens(java.util.ArrayList<Token> tokens) {
        return LoadTokens("Undefined", tokens);
    }
    public final ScriptObject LoadTokens(String strBreviary, java.util.ArrayList<Token> tokens) {
        try {
            if (tokens.isEmpty()) {
                return getNull();
            }
            m_StackInfoStack.clear();
            return Load(strBreviary, tokens, null);
        }
        catch (RuntimeException e) {
            throw new ScriptException("load tokens [" + strBreviary + "] is error : " + e.toString());
        }
    }
    private ScriptObject Load(String strBreviary, java.util.ArrayList<Token> tokens, ScriptContext context) {
        if (tokens.isEmpty()) {
            return getNull();
        }
        ScriptParser scriptParser = new ScriptParser(this, tokens, strBreviary);
        ScriptExecutable scriptExecutable = scriptParser.Parse();
        return new ScriptContext(this, scriptExecutable, context, Executable_Block.Context).Execute();
    }
    public final void PushSearchPath(String path) {
        if (!m_SearchPath.contains(path)) {
            m_SearchPath.add(path);
        }
    }
    public final ScriptObject LoadSearchPathFile(String fileName) {
        for (int i = 0; i < m_SearchPath.size(); ++i) {
            String file = m_SearchPath.get(i) + "/" + fileName;
            if (ScriptExtensions.FileExist(file)) {
                return LoadFile(file);
            }
        }
        throw new ExecutionException(this, "require 找不到文件 : " + fileName);
    }
    public final ScriptObject LoadType(String str) {
    	try {
            Class<?> type = java.lang.Class.forName(str);
            if (type != null) {
                return CreateUserdata(type);
            }
    	} catch (Exception e) {}
        return getNull();
    }
    public final void PushFastReflectClass(java.lang.Class<?> type, IScorpioFastReflectClass value) {
        m_FastReflectClass.put(type, value);
    }
    public final boolean ContainsFastReflectClass(java.lang.Class<?> type) {
        return m_FastReflectClass.containsKey(type);
    }
    public final IScorpioFastReflectClass GetFastReflectClass(java.lang.Class<?> type) {
        return m_FastReflectClass.get(type);
    }
    public final void SetStackInfo(StackInfo info) {
        m_StackInfo = info;
    }
    public final StackInfo GetCurrentStackInfo() {
        return m_StackInfo;
    }
    public final void PushStackInfo() {
        m_StackInfoStack.add(m_StackInfo);
    }
    public final void ClearStackInfo() {
        m_StackInfoStack.clear();
    }
    public final String GetStackInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("Source [ " + m_StackInfo.Breviary + "] Line [" + m_StackInfo.Line + "]\n");
        for (int i = m_StackInfoStack.size() - 1; i >= 0;--i) {
            builder.append("        Source [" + m_StackInfoStack.get(i).Breviary + "] Line [" + m_StackInfoStack.get(i).Line + "]\n");
        }
        return builder.toString();
    }
    public final ScriptTable GetGlobalTable() {
        return m_GlobalTable;
    }
    public final boolean HasValue(String key) {
        return m_GlobalTable.HasValue(key);
    }
    public final ScriptObject GetValue(String key) {
        return m_GlobalTable.GetValue(key);
    }
    public final void SetObject(String key, Object value) {
        m_GlobalTable.SetValue(key, CreateObject(value));
    }
    public final void SetObjectInternal(String key, ScriptObject value) {
        m_GlobalTable.SetValue(key, value);
    }
    public final Object Call(String strName, Object... args) {
        ScriptObject obj = m_GlobalTable.GetValue(strName);
        if (obj instanceof ScriptNull) {
            throw new ScriptException("找不到变量[" + strName + "]");
        }
        int length = args.length;
        ScriptObject[] parameters = new ScriptObject[length];
        for (int i = 0; i < length;++i) {
            parameters[i] = CreateObject(args[i]);
        }
        m_StackInfoStack.clear();
        return obj.Call(parameters);
    }
    public final Object Call(String strName, ScriptObject[] args) {
        ScriptObject obj = m_GlobalTable.GetValue(strName);
        if (obj instanceof ScriptNull) {
            throw new ScriptException("找不到变量[" + strName + "]");
        }
        m_StackInfoStack.clear();
        return obj.Call(args);
    }
    public final ScriptObject CreateObject(Object value) {
        if (value == null) {
            return getNull();
        }
        else if (value instanceof ScriptObject) {
            return (ScriptObject)value;
        }
        else if (value instanceof ScorpioHandle) {
            return CreateFunction((ScorpioHandle)value);
        }
        else if (value instanceof ScorpioMethod) {
            return CreateFunction((ScorpioMethod)value);
        }
        else if (Util.IsBoolObject(value)) {
            return CreateBool(((Boolean)value).booleanValue());
        }
        else if (Util.IsStringObject(value)) {
            return CreateString((String)value);
        }
        else if (Util.IsNumberObject(value)) {
            return CreateNumber(value);
        }
        else if (Util.IsEnumObject(value)) {
            return CreateEnum(value);
        }
        return CreateUserdata(value);
    }
    public final ScriptBoolean CreateBool(boolean value) {
        return GetBoolean(value);
    }
    public final ScriptString CreateString(String value) {
        return new ScriptString(this, value);
    }
    public final ScriptNumber CreateNumber(Object value) {
        return Util.IsLongObject(value) ? CreateLong(((Long)value).longValue()) : CreateDouble(Util.ToDouble(value));
    }
    public final ScriptNumber CreateDouble(double value) {
        return new ScriptNumberDouble(this, value);
    }
    public final ScriptNumber CreateLong(long value) {
        return new ScriptNumberLong(this, value);
    }
    public final ScriptNumber CreateInt(int value) {
        return new ScriptNumberInt(this, value);
    }
    public final ScriptEnum CreateEnum(Object value) {
        return new ScriptEnum(this, value);
    }
    public final ScriptUserdata CreateUserdata(Object value) {
        return m_UserdataFactory.create(this, value);
    }
    public final ScriptArray CreateArray() {
        return new ScriptArray(this);
    }
    public final ScriptTable CreateTable() {
        return new ScriptTable(this);
    }
    public final ScriptScriptFunction CreateFunction(String name, ScorpioScriptFunction value) {
        return new ScriptScriptFunction(this, name, value);
    }
    public final ScriptFunction CreateFunction(ScorpioHandle value) {
        return new ScriptHandleFunction(this, value);
    }
    public final ScriptFunction CreateFunction(ScorpioMethod value) {
        return new ScriptMethodFunction(this, value);
    }
    public final IScriptUserdataFactory GetUserdataFactory() {
        return m_UserdataFactory;
    }
    public final void SetUserdataFactory(IScriptUserdataFactory value) {
        m_UserdataFactory = value;
    }
    public final void LoadLibrary() {
        LibraryBasis.Load(this);
        LibraryArray.Load(this);
        LibraryString.Load(this);
        LibraryTable.Load(this);
        LibraryJson.Load(this);
        LibraryMath.Load(this);
    }
}
