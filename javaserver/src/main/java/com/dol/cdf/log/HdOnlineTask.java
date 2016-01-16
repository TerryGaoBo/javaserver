package com.dol.cdf.log;

import io.nadron.app.Task;
import io.nadron.context.AppContext;
import io.nadron.example.lostdecade.LDRoom;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dol.cdf.common.ContextConfig;
import com.dol.cdf.common.ContextConfig.RuntimeEnv;
import com.dol.cdf.common.config.AllGameConfig;
import com.dol.cdf.log.msghd.HdOnline;

public class HdOnlineTask implements Task {
	private static final Logger LOG = LoggerFactory
			.getLogger(HdOnlineTask.class);

	private final LDRoom bean = AppContext.getBean(LDRoom.class);

	@Override
	public void run() {
		int online = 0;
		if (bean != null) {
			online = bean.getOnlineCount();
		} else {
			LOG.error("LDRoom is null");
		}
		LOG.info("当前在线人数:" + online);
		if (ContextConfig.isJointServer()) {
			List<Integer> sids = ContextConfig.getServerIds();
			for (int sid : sids) {
				if (AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE) {
					sid = 100000 + sid;
				}
				HdOnline log = new HdOnline(sid + "", online);
				LogUtil.sendLog(log);
			}
		} else {
			int sid = ContextConfig.getFirstServerId();
			if (AllGameConfig.getInstance().env == RuntimeEnv.IOS_STAGE) {
				sid = 100000 + sid;
			}
			HdOnline log = new HdOnline(sid + "", online);
			LOG.info(log.toString());
			LogUtil.sendLog(log);
		}

	}

	@Override
	public Object getId() {
		return null;
	}

	@Override
	public void setId(Object id) {

	}

}
