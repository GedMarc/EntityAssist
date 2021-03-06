package com.entityassist.services;

import com.guicedee.guicedinjection.interfaces.IDefaultService;
import com.guicedee.logger.LogFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;

/**
 * Maps an entity assist ID Mapping for db returned data from the query.
 * <p>
 * Is a class and not an interface to read the typed mappings
 *
 * @param <DB>
 * 		The Database Returned Type
 * @param <OBJECT>
 * 		The object to render
 */
public interface EntityAssistIDMapping<DB, OBJECT>
		extends IDefaultService<EntityAssistIDMapping<DB, OBJECT>>
{
	OBJECT toObject(DB dbReturned);

	@SuppressWarnings("unchecked")
	default Class<DB> getDBClassType()
	{
		try
		{
			for (Type genericInterface : getClass().getGenericInterfaces())
			{
				if(genericInterface.getTypeName().startsWith(EntityAssistIDMapping.class.getTypeName()))
				{
					ParameterizedType pp = (ParameterizedType) genericInterface;
					return (Class<DB>) pp.getActualTypeArguments()[0];
				}
			}
		}
		catch (Exception e)
		{
			LogFactory.getLog("EntityAssistIDMapping")
			          .log(Level.SEVERE,
			               "Cannot return the db or entity id class - config seems wrong. " +
			               "Check that a builder is attached to this entity as the second generic field type\n" +
			               e, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	default Class<OBJECT> getObjectClassType()
	{
		try
		{
			for (Type genericInterface : getClass().getGenericInterfaces())
			{
				if(genericInterface.getTypeName().startsWith(EntityAssistIDMapping.class.getTypeName()))
				{
					ParameterizedType pp = (ParameterizedType) genericInterface;
					return (Class<OBJECT>) pp.getActualTypeArguments()[1];
				}
			}
		}
		catch (Exception e)
		{
			LogFactory.getLog("EntityAssistIDMapping")
			          .log(Level.SEVERE,
			               "Cannot return the db or entity id class - config seems wrong. " +
			               "Check that a builder is attached to this entity as the second generic field type\n" +
			               e, e);
		}
		return null;
	}
}
