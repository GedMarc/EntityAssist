package com.jwebmp.entityassist.injections.bigdecimal;

import com.jwebmp.entityassist.services.EntityAssistIDMapping;

import java.math.BigDecimal;

/**
 * Maps Big Decimals ID Returned Types
 */
public class BigDecimalToDoubleIDMapping
		extends EntityAssistIDMapping<BigDecimal, Double>
{
	@Override
	public Double toObject(BigDecimal dbReturned)
	{
		return dbReturned.doubleValue();
	}
}
