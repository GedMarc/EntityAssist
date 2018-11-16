package com.jwebmp.entityassist.injections.integer;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

public class IntegerIDMapping
		extends EntityAssistIDMapping<Integer, Integer>
{

	@Override
	public Integer toObject(Integer dbReturned)
	{
		return dbReturned.intValue();
	}
}
