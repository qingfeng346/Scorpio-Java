package Scorpio;

import java.nio.charset.Charset;
import java.util.List;

import Scorpio.Runtime.*;
import Scorpio.Serialize.ScorpioMaker;
import Scorpio.Compiler.*;
import Scorpio.Exception.*;
import Scorpio.Library.*;
import Scorpio.Userdata.*;
import Scorpio.Variable.*;
//脚本类
public class Script {
    public static final String DynamicDelegateName = "__DynamicDelegate__";
    public static final String Version = "master";
    private static final String GLOBAL_TABLE = "_G"; //全局table
    private static final String GLOBAL_VERSION = "_VERSION"; //版本号
    private static final String GLOBAL_SCRIPT = "_SCRIPT";         //Script对象
    private IScriptUserdataFactory m_UserdataFactory = null; //Userdata工厂
    private ScriptTable m_GlobalTable; //全局Table
    private java.util.ArrayList<StackInfo> m_StackInfoStack = new java.util.ArrayList<StackInfo>(); //堆栈数据
    private StackInfo m_StackInfo = new StackInfo(); //最近堆栈数据
    
    public ScriptNull Null;                            //null对象
    public ScriptBoolean True;                         //true对象
    public ScriptBoolean False;                        //false对象
    public ScriptBoolean GetBoolean(boolean value) {
        return value ? True : False; 
    }
    public Script()
    {
        Null = new ScriptNull(this);
        True = new ScriptBoolean(this, true);
        False = new ScriptBoolean(this, false);
        m_UserdataFactory = new DefaultScriptUserdataFactory(this);
        m_GlobalTable = CreateTable();
        m_GlobalTable.SetValue(GLOBAL_TABLE, m_GlobalTable);
        m_GlobalTable.SetValue(GLOBAL_VERSION, CreateString(Version));
        m_GlobalTable.SetValue(GLOBAL_SCRIPT, CreateObject(this));
    }
    public final ScriptObject LoadFile(String strFileName) throws Exception {
        return LoadFile(strFileName, "UTF8");
    }
    public final ScriptObject LoadFile(String fileName, String encoding) throws Exception {
        return LoadFile(fileName,Charset.forName(encoding));
    }
    public final ScriptObject LoadFile(String fileName, Charset encoding) throws Exception {
    	byte[] buffer = Util.GetFileBuffer(fileName);
    	if (buffer.length > 0 && buffer[0] == 0)
    		return LoadTokens(fileName, ScorpioMaker.Deserialize(buffer));
    	else
    		return LoadString(fileName, new String(buffer, encoding));
    }
    public final ScriptObject LoadString(String strBuffer) throws Exception {
        return LoadString("", strBuffer);
    }
    public final ScriptObject LoadString(String strBreviary, String strBuffer) throws Exception {
        return LoadString(strBreviary, strBuffer, null, true);
    }
    public final ScriptObject LoadString(String strBreviary, String strBuffer, ScriptContext context, boolean clearStack) throws Exception {
        if (Util.IsNullOrEmpty(strBuffer)) return Null;
    	if (clearStack) m_StackInfoStack.clear();
        ScriptLexer scriptLexer = new ScriptLexer(strBuffer, strBreviary);
        return Load(scriptLexer.GetBreviary(), scriptLexer.GetTokens(), context);
    }
    public final ScriptObject LoadTokens(String strBreviary, List<Token> tokens) throws Exception
    {
        if (tokens.size() == 0) return Null;
        m_StackInfoStack.clear();
        return Load(strBreviary, tokens, null);
    }
    private final ScriptObject Load(String strBreviary, List<Token> tokens, ScriptContext context) throws Exception
    {
        if (tokens.size() == 0) return Null;
        ScriptParser scriptParser = new ScriptParser(this, tokens, strBreviary);
        ScriptExecutable scriptExecutable = scriptParser.Parse();
        return new ScriptContext(this, scriptExecutable, context, Executable_Block.Context).Execute();
    }
    public final ScriptObject LoadType(String str) {
    	try {
            Class<?> type = java.lang.Class.forName(str);
            if (type != null) {
                return CreateUserdata(type);
            }
    	} catch (Exception e) {}
        return Null;
    }
    public final void SetStackInfo(StackInfo info) {
        m_StackInfo = info;
    }
    public final StackInfo GetCurrentStackInfo()
    {
        return m_StackInfo;
    }
    public final void PushStackInfo() {
        m_StackInfoStack.add(m_StackInfo);
    }
    public void ClearStackInfo() {
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
    public final Object Call(String strName, Object... args) throws Exception {
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
    public final ScriptObject CreateObject(Object value) {
        if (value == null) {
            return Null;
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
    public final ScriptFunction CreateFunction(String name, ScorpioScriptFunction value) {
        return new ScriptFunction(this, name, value);
    }
    public final ScriptFunction CreateFunction(ScorpioHandle value) {
        return new ScriptFunction(this, value);
    }
    public final ScriptFunction CreateFunction(ScorpioMethod value) {
        return new ScriptFunction(this, value);
    }
    public IScriptUserdataFactory GetUserdataFactory() {
        return m_UserdataFactory;
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
