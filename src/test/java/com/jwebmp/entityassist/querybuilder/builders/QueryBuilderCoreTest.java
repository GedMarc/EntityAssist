package com.jwebmp.entityassist.querybuilder.builders;

import com.google.inject.Key;
import com.jwebmp.entityassist.TestEntityAssistCustomPersistenceLoader;
import com.jwebmp.entityassist.entities.EntityClass;
import com.jwebmp.entityassist.entities.EntityClassTwo;
import com.jwebmp.entityassist.entities.EntityClassTwo_;
import com.jwebmp.entityassist.entities.EntityClass_;
import com.jwebmp.entityassist.enumerations.ActiveFlag;
import com.jwebmp.guiceinjection.GuiceContext;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import java.util.List;

import static com.jwebmp.entityassist.enumerations.Operand.*;
import static org.junit.jupiter.api.Assertions.*;

public class QueryBuilderCoreTest
{


	@Test
	public void testVisibleRange()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .join(EntityClassTwo_.entityClass, new EntityClassTwo().builder()
		                                                                                                 .where(EntityClassTwo_.activeFlag, Equals, ActiveFlag.Active))
		                                          .inVisibleRange()
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}


	@Test
	public void testDateRange()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .inDateRange()
		                                          .join(EntityClassTwo_.entityClass)
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}

	@Test
	public void testDateAndVisibleRange()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .inDateRange()
		                                          .inVisibleRange()
		                                          .join(EntityClassTwo_.entityClass, null, JoinType.LEFT)
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}

	@Test
	public void testDateAndVisibleRange1()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		List<EntityClass> list = new EntityClass().builder()
		                                          .inDateRange()
		                                          .inVisibleRange()
		                                          .join(EntityClassTwo_.entityClass, new EntityClassTwo().builder()
		                                                                                                 .where(EntityClassTwo_.activeFlag, Equals, ActiveFlag.Active)
		                                                                                                 .inActiveRange(), JoinType.LEFT)
		                                          .getAll();
		if (!list.isEmpty())
		{
			fail("Rows not inserted?");
		}
	}

	@Test
	public void testBulkUpdate()
	{
		EntityManager em = GuiceContext.getInstance(Key.get(EntityManager.class, TestEntityAssistCustomPersistenceLoader.class));
		System.out.println("EM Open : " + em.isOpen());
		EntityClass updates = new EntityClass(true);
		updates.setActiveFlag(ActiveFlag.Archived);
		new EntityClass().builder()
		                 .where(EntityClass_.activeFlag, Equals, ActiveFlag.Invisible)
		                 .bulkUpdate(updates, true);
	}
}