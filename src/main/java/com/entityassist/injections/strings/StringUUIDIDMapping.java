package com.entityassist.injections.strings;

import com.entityassist.services.EntityAssistIDMapping;

import java.util.UUID;

public class StringUUIDIDMapping
		implements EntityAssistIDMapping<String, UUID>
{

	@Override
	public UUID toObject(String dbReturned)
	{
		return UUID.fromString(dbReturned);
	}
}
