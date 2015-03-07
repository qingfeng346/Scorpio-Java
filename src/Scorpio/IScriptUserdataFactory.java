package Scorpio;

public interface IScriptUserdataFactory {
	ScriptUserdata GetEnum(Class<?> type);
    ScriptUserdata create(Script script, Object obj);
}