package com.entityassist.injections.bigdecimal;

import com.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToStringIDMapping
		implements EntityAssistIDMapping<BigDecimal, String>
{
	@Override
	public String toObject(BigDecimal dbReturned)
	{
		return dbReturned.toPlainString();
	}
}
