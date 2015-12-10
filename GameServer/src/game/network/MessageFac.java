package game.network;

import game.base.BaseAction;
import game.config.MessageKey;
import game.modules.login.LoginAction;
import game.modules.test.TestAction;

public class MessageFac {
	
	public static BaseAction getActionFac(String actionID)
	{
		BaseAction action = null;
		if (actionID.equals(MessageKey.LOGINS)) {
			action = new LoginAction();
		}else if(actionID.equals(MessageKey.TEST)){
			action = new TestAction();
		}
		return action;
	}
}
