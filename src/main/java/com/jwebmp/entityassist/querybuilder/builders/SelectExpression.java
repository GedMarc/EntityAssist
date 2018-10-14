package com.jwebmp.entityassist.querybuilder.builders;

import com.jwebmp.entityassist.enumerations.SelectAggregrate;

import javax.persistence.metamodel.Attribute;
import javax.validation.constraints.NotNull;

/**
 * A select clause expression
 */
@SuppressWarnings("unused")
final class SelectExpression
{
	/**
	 * The attribute to select
	 */
	private Attribute attribute;

	/**
	 * The select aggregate - none for default
	 */
	private SelectAggregrate aggregrate;

	/**
	 * A new select expression
	 */
	SelectExpression()
	{
		//No config required
	}

	/**
	 * A general select expression
	 *
	 * @param attribute
	 * 		The attribute to use
	 * @param aggregrate
	 * 		The aggregate to use - none for default
	 */
	SelectExpression(Attribute attribute, SelectAggregrate aggregrate)
	{
		this.attribute = attribute;
		this.aggregrate = aggregrate;
	}

	/**
	 * Method hashCode ...
	 *
	 * @return int
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	/**
	 * Method equals ...
	 *
	 * @param obj
	 * 		of type Object
	 *
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}

	/**
	 * Method getAttribute returns the attribute of this SelectExpression object.
	 * <p>
	 * The attribute to select
	 *
	 * @return the attribute (type Attribute) of this SelectExpression object.
	 */
	public Attribute getAttribute()
	{
		return attribute;
	}

	/**
	 * Method setAttribute sets the attribute of this SelectExpression object.
	 * <p>
	 * The attribute to select
	 *
	 * @param attribute
	 * 		the attribute of this SelectExpression object.
	 *
	 * @return SelectExpression
	 */
	@NotNull
	public SelectExpression setAttribute(Attribute attribute)
	{
		this.attribute = attribute;
		return this;
	}

	/**
	 * Method getAggregrate returns the aggregrate of this SelectExpression object.
	 * <p>
	 * The select aggregate - none for default
	 *
	 * @return the aggregrate (type SelectAggregrate) of this SelectExpression object.
	 */
	@SuppressWarnings("WeakerAccess")
	@NotNull
	public SelectAggregrate getAggregrate()
	{
		return aggregrate;
	}

	/**
	 * Method setAggregrate sets the aggregrate of this SelectExpression object.
	 * <p>
	 * The select aggregate - none for default
	 *
	 * @param aggregrate
	 * 		the aggregrate of this SelectExpression object.
	 */
	public void setAggregrate(SelectAggregrate aggregrate)
	{
		this.aggregrate = aggregrate;
	}
}
