package com.jwebmp.entityassist.entities.builders;

import com.google.inject.Key;
import com.jwebmp.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.jwebmp.entityassist.entities.EntityClassTwo;
import com.jwebmp.entityassist.querybuilder.QueryBuilderCore;
import com.jwebmp.guiceinjection.GuiceContext;

import javax.persistence.EntityManager;

public class EntityClassTwoBuilder
		extends QueryBuilderCore<EntityClassTwoBuilder, EntityClassTwo, Long>
{
	@Override
	public EntityManager getEntityManager()
	{
		return GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
	}

	@Override
	protected void onCreate(EntityClassTwo entity)
	{

	}

	@Override
	protected boolean isIdGenerated()
	{
		return true;
	}

	@Override
	protected void onUpdate(EntityClassTwo entity)
	{

	}
}