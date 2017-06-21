package Scorpio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import Scorpio.Runtime.*;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;
import Scorpio.Library.*;
import Scorpio.Userdata.*;
import Scorpio.Variable.*;
import Scorpio.Serialize.*;
import Scorpio.Function.*;

//脚本类
public class Script {
    public static final String Version = "master";
    private static final String GLOBAL_TABLE = "_G"; //全局table
    private static final String GLOBAL_VERSION = "_VERSION"; //版本号
    private static final String GLOBAL_SCRIPT = "_SCRIPT"; //Script对象
    private static final Charset UTF8 = Charset.forName("UTF8");
    private ScriptTable m_GlobalTable; //全局Table
    private java.util.Stack<StackInfo> m_StackInfoStack = new java.util.Stack<StackInfo>(); //堆栈数据
    private java.util.ArrayList<String> m_SearchPath = new java.util.ArrayList<String>(); //request所有文件的路径集合
    private java.util.ArrayList<String> m_Defines = new java.util.ArrayList<String>(); //所有Define
    private java.util.HashMap<java.lang.Class<?>, IScorpioFastReflectClass> m_FastReflectClass = new java.util.HashMap<java.lang.Class<?>, IScorpioFastReflectClass>(); //快速反射集合
    private java.util.HashMap<java.lang.Class<?>, ScriptUserdataEnum> m_Enums = new java.util.HashMap<java.lang.Class<?>, ScriptUserdataEnum>(); //所有枚举集合
    private java.util.HashMap<java.lang.Class<?>, ScriptUserdataObjectType> m_Types = new java.util.HashMap<java.lang.Class<?>, ScriptUserdataObjectType>(); //所有的类集合
    private java.util.HashMap<java.lang.Class<?>, UserdataType> m_UserdataTypes = new java.util.HashMap<java.lang.Class<?>, UserdataType>(); //所有的类集合
    private StackInfo m_StackInfo = new StackInfo(); //最近堆栈数据
    private ScriptNull m_Null; //null对象
    private ScriptBoolean m_True; //true对象
    private ScriptBoolean m_False; //false对象
    public final ScriptNull getNull() {
        return m_Null;
    }
    public final ScriptBoolean getTrue() {
        return m_True;
    }
    public final ScriptBoolean getFalse() {
        return m_False;
    }
    public Script() {
        m_Null = new ScriptNull(this);
        m_True = new ScriptBoolean(this, true);
        m_False = new ScriptBoolean(this, false);
        m_GlobalTable = CreateTable();
        m_GlobalTable.SetValue(GLOBAL_TABLE, m_GlobalTable);
        m_GlobalTable.SetValue(GLOBAL_VERSION, CreateString(Version));
        m_GlobalTable.SetValue(GLOBAL_SCRIPT, CreateObject(this));
    }
    public final void LoadLibrary() {
        LibraryBasis.Load(this);
        LibraryArray.Load(this);
        LibraryString.Load(this);
        LibraryTable.Load(this);
        LibraryJson.Load(this);
        LibraryMath.Load(this);
        LibraryFunc.Load(this);
        LibraryUserdata.Load(this);
    }
    public final ScriptObject LoadFile(String strFileName) {
        return LoadFile(strFileName, UTF8);
    }
    public final ScriptObject LoadFile(String fileName, Charset encoding) {
    	ByteArrayOutputStream output = new ByteArrayOutputStream();
    	try {
    		FileInputStream stream = new FileInputStream(new File(fileName));
            int n = 0;
            byte[] buffer = new byte[4096];
            while (-1 != (n = stream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            stream.close();
    	} catch (Exception e) {
    		throw new ScriptException("load file [" + fileName + "] is error : " + e.toString());
    	}
        return LoadBuffer(fileName, output.toByteArray(), encoding);
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
                return m_Null;
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
                return m_Null;
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
            return m_Null;
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
            File f = new File(file);
            if (f.exists() && f.isFile()) {
                return LoadFile(file);
            }
        }
        throw new ExecutionException(this, "require 找不到文件 : " + fileName);
    }
    public final void PushDefine(String define) {
        if (!m_Defines.contains(define)) {
            m_Defines.add(define);
        }
    }
    public final boolean ContainDefine(String define) {
        return m_Defines.contains(define);
    }
    public final java.lang.Class<?> GetType(String str) {
    	try {
            return java.lang.Class.forName(str);
    	} catch (Exception e) {}
    	return null;
    }
    public final ScriptObject LoadType(String str) {
        java.lang.Class<?> type = GetType(str);
        if (type == null) {
            return m_Null;
        }
        return CreateUserdata(type);
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
        m_StackInfoStack.push(m_StackInfo);
    }
    public final void PopStackInfo() {
        if (m_StackInfoStack.size() > 0) {
            m_StackInfoStack.pop();
        }
    }
    public final void ClearStackInfo() {
        m_StackInfoStack.clear();
    }
    public final String GetStackInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("Source [" + m_StackInfo.Breviary + "] Line [" + m_StackInfo.Line + "]\n");
        for (StackInfo info : m_StackInfoStack) {
            builder.append("        Source [" + info.Breviary + "] Line [" + info.Line + "]\n");
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
    public final void SetValue(String key, Object value) {
        m_GlobalTable.SetValue(key, CreateObject(value));
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
        for (int i = 0; i < length; ++i) {
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
            return m_Null;
        }
        else if (value instanceof Boolean) {
            return CreateBool(((Boolean)value).booleanValue());
        }
        else if (value instanceof String) {
            return new ScriptString(this, (String)value);
        }
        else if (value instanceof Long) {
            return new ScriptNumberLong(this, ((Long)value).longValue());
        }
        else if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Float || value instanceof Double || value instanceof java.math.BigDecimal) {
            return new ScriptNumberDouble(this, Util.ToDouble(value));
        }
        else if (value instanceof ScriptObject) {
            return (ScriptObject)value;
        }
        else if (value instanceof ScorpioHandle) {
            return new ScriptHandleFunction(this, (ScorpioHandle)value);
        }
        else if (value instanceof ScorpioMethod) {
            return new ScriptMethodFunction(this, (ScorpioMethod)value);
        }
        else if (value.getClass().isEnum()) {
            return new ScriptEnum(this, value);
        }
        return CreateUserdata(value);
    }
    public final ScriptBoolean CreateBool(boolean value) {
        return value ? getTrue() : getFalse();
    }
    public final ScriptString CreateString(String value) {
        return new ScriptString(this, value);
    }
    public final ScriptNumber CreateDouble(double value) {
        return new ScriptNumberDouble(this, value);
    }
    public final ScriptArray CreateArray() {
        return new ScriptArray(this);
    }
    public final ScriptTable CreateTable() {
        return new ScriptTable(this);
    }
    public final ScriptFunction CreateFunction(ScorpioHandle value) {
        return new ScriptHandleFunction(this, value);
    }
    public final ScriptUserdata CreateUserdata(Object obj) {
        java.lang.Class<?> type = (java.lang.Class<?>)((obj instanceof java.lang.Class<?>) ? obj : null);
        if (type != null) {
            if (type.isEnum()) {
                return GetEnum(type);
            }
            else {
                return GetType(type);
            }
        }
        return new ScriptUserdataObject(this, obj, GetScorpioType(obj.getClass()));
    }
    public final ScriptUserdata GetEnum(java.lang.Class<?> type) {
        if (m_Enums.containsKey(type)) {
            return m_Enums.get(type);
        }
        ScriptUserdataEnum ret = new ScriptUserdataEnum(this, type);
        m_Enums.put(type, ret);
        return ret;
    }
    public final ScriptUserdataObjectType GetType(java.lang.Class<?> type) {
        if (m_Types.containsKey(type)) {
            return m_Types.get(type);
        }
        ScriptUserdataObjectType ret = new ScriptUserdataObjectType(this, type, GetScorpioType(type));
        m_Types.put(type, ret);
        return ret;
    }
    public final UserdataType GetScorpioType(java.lang.Class<?> type) {
        if (m_UserdataTypes.containsKey(type)) {
            return m_UserdataTypes.get(type);
        }
        UserdataType scorpioType = null;
        if (ContainsFastReflectClass(type)) {
            scorpioType = new FastReflectUserdataType(this, type, GetFastReflectClass(type));
        }
        else {
            scorpioType = new ReflectUserdataType(this, type);
        }
        m_UserdataTypes.put(type, scorpioType);
        return scorpioType;
    }
}