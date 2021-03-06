package com.entityassist.injections.biginteger;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerFloatIDMapping
		implements EntityAssistIDMapping<BigInteger, Float>
{

	@Override
	public Float toObject(BigInteger dbReturned)
	{
		return dbReturned.floatValue();
	}
}
