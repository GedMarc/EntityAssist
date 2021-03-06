package com.entityassist.injections.biginteger;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigInteger;

public class BigIntegerDoubleIDMapping
		implements EntityAssistIDMapping<BigInteger, Double>
{

	@Override
	public Double toObject(BigInteger dbReturned)
	{
		return dbReturned.doubleValue();
	}
}
