package com.dol.cdf.common.entities;

import com.dol.cdf.common.crypto.Guid;
import com.jelly.node.cache.ObjectCacheService;

public class LineProps extends Entity {
	
	public static Guid theGuid = Guid.SHA1("iKWIr8/+h26q/3GYEkeosbs1M3GMyABWoServerLineProps");
	
	private ObjectCacheService objectCache;
	
	public LineProps(){
		super(theGuid.toString());
	}
	
	public void setObjectCache(ObjectCacheService objectCache) {
		this.objectCache = objectCache;
	}
}
