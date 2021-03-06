package com.entityassist.injections.longs;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

public class LongBigDecimalIDMapping
		implements EntityAssistIDMapping<Long, BigDecimal>
{

	@Override
	public BigDecimal toObject(Long dbReturned)
	{
		return new BigDecimal(dbReturned);
	}
}
