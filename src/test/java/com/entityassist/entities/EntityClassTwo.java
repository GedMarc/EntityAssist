package com.entityassist.entities;

import com.entityassist.CoreEntity;
import com.entityassist.entities.builders.EntityClassTwoBuilder;

import javax.persistence.*;

@Entity
@Table
public class EntityClassTwo
		extends CoreEntity<EntityClassTwo, EntityClassTwoBuilder, Long>
{
	@Id
	@Column(name = "id")
	private Long id;

	@OneToOne
	@JoinColumn(referencedColumnName = "entityClass",
			name = "entityClass")
	private EntityClass entityClass;

	public EntityClass getEntityClass()
	{
		return entityClass;
	}

	public void setEntityClass(EntityClass entityClass)
	{
		this.entityClass = entityClass;
	}

	@Override
	public String toString()
	{
		return "EntityClassTwoTest : " + getId();
	}

	@Override
	public Long getId()
	{
		return id;
	}

	@Override
	public EntityClassTwo setId(Long id)
	{
		this.id = id;
		return this;
	}
}
