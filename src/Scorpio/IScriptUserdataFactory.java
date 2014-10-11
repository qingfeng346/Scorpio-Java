package Scorpio;

public interface IScriptUserdataFactory
{
	ScriptUserdata create(Script script, Object obj) throws Exception;
}