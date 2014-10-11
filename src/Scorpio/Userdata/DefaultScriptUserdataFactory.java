package Scorpio.Userdata;

import Scorpio.IScriptUserdataFactory;
import Scorpio.Script;
import Scorpio.ScriptUserdata;

public class DefaultScriptUserdataFactory implements IScriptUserdataFactory
{
	@Override
	public ScriptUserdata create(Script script, Object obj) throws Exception {
		if (obj instanceof java.lang.Class && ((java.lang.Class)obj).isEnum())
		{
			return new DefaultScriptUserdataEnum(script, obj);
		}
		return new DefaultScriptUserdataObject(script, obj);
	}
}