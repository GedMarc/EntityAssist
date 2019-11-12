package com.entityassist.injections.longs;

import com.entityassist.services.EntityAssistIDMapping;

public class LongStringIDMapping
		implements EntityAssistIDMapping<Long, String>
{

	@Override
	public String toObject(Long dbReturned)
	{
		return dbReturned.toString();
	}
}
