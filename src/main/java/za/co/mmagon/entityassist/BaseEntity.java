package za.co.mmagon.entityassist;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import za.co.mmagon.entityassist.querybuilder.EntityAssistStrings;
import za.co.mmagon.entityassist.querybuilder.builders.QueryBuilderBase;
import za.co.mmagon.entityassist.querybuilder.statements.InsertStatement;

import javax.persistence.EntityManager;
import javax.persistence.MappedSuperclass;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.armineasy.injection.GuiceContext.getInstance;

@MappedSuperclass()
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseEntity<J extends BaseEntity<J, Q, I>, Q extends QueryBuilderBase<Q, J, I>, I extends Serializable>
		implements EntityAssistStrings
{
	private static final Logger log = Logger.getLogger(BaseEntity.class.getName());

	@Transient
	@JsonIgnore
	@SuppressWarnings("all")
	private transient Class<J> myClass;
	@Transient
	@JsonIgnore
	@SuppressWarnings("all")
	private transient Class<Q> queryBuilderClass;
	@Transient
	@JsonIgnore
	@SuppressWarnings("all")
	private transient Class<I> idTypeClass;
	@Transient
	@JsonIgnore
	private Map<Serializable, Serializable> properties;

	/**
	 * Whether or not to run these queries as detached objects or within the entity managers scope
	 */
	@JsonIgnore
	@Transient
	private boolean runDetached;

	/**
	 * Constructs a new base entity type
	 */
	public BaseEntity()
	{
		//No configuration needed
	}

	/**
	 * Returns if this entity is operating as a fake or not (testing or dto)
	 *
	 * @return
	 */
	@NotNull
	public boolean isFake()
	{
		return getProperties().containsKey(FAKE_KEY) && Boolean.parseBoolean(getProperties().get(FAKE_KEY).toString());
	}

	/**
	 * Any DB Transient Maps
	 * <p>
	 * Sets any custom properties for this core entity.
	 * Dto Read only structure. Not for storage unless mapped as such in a sub-method
	 *
	 * @return
	 */
	@NotNull
	public Map<Serializable, Serializable> getProperties()
	{
		if (properties == null)
		{
			properties = new HashMap<>();
		}
		return properties;
	}

	/**
	 * Sets any custom properties for this core entity.
	 * Dto Read only structure. Not for storage unless mapped as such in a sub-method
	 *
	 * @param properties
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public J setProperties(@NotNull Map<Serializable, Serializable> properties)
	{
		this.properties = properties;
		return (J) this;
	}

	/**
	 * Sets the fake property
	 *
	 * @param fake
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setFake(boolean fake)
	{
		if (fake)
		{
			getProperties().put(FAKE_KEY, fake);
		}
		else
		{
			getProperties().remove(FAKE_KEY);
		}
		return (J) this;
	}

	/**
	 * Returns the id of the given type in the generic decleration
	 *
	 * @return Returns the ID
	 */
	@NotNull
	public abstract I getId();

	/**
	 * Returns the id of the given type in the generic decleration
	 *
	 * @param id
	 *
	 * @return
	 */
	@SuppressWarnings("all")
	@NotNull
	public abstract J setId(@NotNull I id);

	/**
	 * Returns this classes specific entity type
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Class<J> getClassEntityType()
	{
		if (myClass == null)
		{
			try
			{
				this.myClass = (Class<J>) ((ParameterizedType) getClass()
						                                               .getGenericSuperclass()).getActualTypeArguments()[0];
			}
			catch (Exception e)
			{
				this.myClass = null;
				log.log(Level.SEVERE, "Cannot return the my class generic type? this class is not extended?", e);
			}
		}
		return myClass;
	}

	/**
	 * Persist and Flush
	 *
	 * @return
	 */
	@SuppressWarnings("all")
	@NotNull
	public J persistNow()
	{
		persist();
		getEntityManager().flush();
		return (J) this;
	}

	/**
	 * Persists this entity. Uses the get instance entity manager to operate.
	 *
	 * @return
	 *
	 * @throws za.co.mmagon.entityassist.exceptions.QueryNotValidException
	 * @throws za.co.mmagon.entityassist.exceptions.EntityNotValidException
	 * @throws za.co.mmagon.entityassist.exceptions.ConstraintsNotMetException
	 */
	@NotNull
	@SuppressWarnings("all")
	public J persist()
	{
		try
		{
			onCreate();
			List<String> errors = validateEntity();
			if (!errors.isEmpty())
			{
				log.warning("Constraint Errors Occured, Validation might fail : " + errors);
			}
			String insertString = InsertStatement.buildInsertString(this);
			log.info(insertString);
			EntityManager entityManager = getInstance(EntityManager.class);
			if (!entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().begin();
			}
			if (!isRunDetached())
			{
				entityManager.createNativeQuery(insertString).executeUpdate();
				if (isIdGenerated())
				{
					//Query statmentSelectId = entityManager.createNativeQuery("CALL SCOPE_IDENTITY();"); //h2
					//Query statmentSelectId = entityManager.createNativeQuery("SELECT @@SCOPE_INDENTITY"); //mysql
					Query statmentSelectId = entityManager.createNativeQuery("SELECT @@IDENTITY"); //microsoft sql
					BigDecimal generatedId = ((BigDecimal) statmentSelectId.getSingleResult());
					setId((I) (Long) generatedId.longValue());
					entityManager.getTransaction().commit();
				}
			}
			else
			{
				log.warning("Still doing detached inserts, you can use the performInsert() method currently to generate the sql statement.");
			}
			setFake(false);
		}
		catch (IllegalStateException ise)
		{
			log.log(Level.SEVERE, "This entity is not in a state to be persisted. Perhaps an update merge remove or refresh?", ise);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to persist, exception occured\n", e);
		}
		return (J) this;
	}

	/**
	 * Returns the assigned entity manager
	 *
	 * @return
	 */
	@NotNull
	@Transient
	protected EntityManager getEntityManager()
	{
		return getInstance(EntityManager.class);
	}

	protected abstract void onCreate();

	/**
	 * Performs the constraint validation and returns a list of all constraint errors.
	 * <p>
	 * <b>Great for form checking</b>
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unused")
	public List<String> validateEntity()
	{
		List<String> errors = new ArrayList<>();
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set constraintViolations = validator.validate(this);

		if (!constraintViolations.isEmpty())
		{
			for (Object constraintViolation : constraintViolations)
			{
				ConstraintViolation contraints = (ConstraintViolation) constraintViolation;
				String error = contraints.getRootBeanClass().getSimpleName() + "." + contraints.getPropertyPath() + " " + contraints.getMessage();
				errors.add(error);
			}
		}
		return errors;
	}

	/**
	 * If this entity should run in a detached and separate to the entity manager
	 *
	 * @return
	 */
	public boolean isRunDetached()
	{
		return runDetached;
	}

	/**
	 * If this ID is generated from the source and which form to use
	 * Default is Generated
	 *
	 * @return Returns if the id column is a generated type
	 */
	protected abstract boolean isIdGenerated();

	/**
	 * If this entity should run in a detached and separate to the entity manager
	 *
	 * @param runDetached
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setRunDetached(boolean runDetached)
	{
		this.runDetached = runDetached;
		return (J) this;
	}

	@NotNull
	/**
	 * Returns the builder associated with this entity
	 *
	 * @return
	 */
	public Q builder()
	{
		Class<Q> foundQueryBuilderClass = getClassQueryBuilderClass();
		return getInstance(foundQueryBuilderClass);

	}

	/**
	 * Returns this classes associated query builder class
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	protected Class<Q> getClassQueryBuilderClass()
	{
		if (queryBuilderClass == null)
		{
			try
			{
				this.queryBuilderClass = (Class<Q>) ((ParameterizedType) getClass()
						                                                         .getGenericSuperclass()).getActualTypeArguments()[1];
			}
			catch (Exception e)
			{
				this.queryBuilderClass = null;
				log.log(Level.SEVERE, "Cannot return the my query builder class - config seems wrong. Check that a builder is attached to this entity as the second generic field type e.g. \n" +
						                      "public class EntityClass extends CoreEntity<EntityClass, EntityClassBuilder, Long>\n\n" +
						                      "You can view the test class in the sources or at https://github.com/GedMarc/EntityAssist/tree/master/test/za/co/mmagon/entityassist/entities", e);
			}
		}
		return queryBuilderClass;
	}

	/**
	 * Performs the actual insert
	 *
	 * @param connection
	 * @param insertString
	 */
	public void performInsert(Connection connection, String insertString)
	{
		String escaped = StringUtils.replace(insertString, STRING_SINGLE_QUOTES, STRING_SINGLE_QUOTES_TWICE);
		try (PreparedStatement statement = connection.prepareStatement(escaped, Statement.RETURN_GENERATED_KEYS))
		{
			int affectedRows = statement.executeUpdate();
			if (affectedRows == 0)
			{
				throw new SQLException("Insert Failed, no rows affected.");
			}
			try (ResultSet generatedKeys = statement.getGeneratedKeys())
			{
				iterateThroughResultSetForGeneratedIDs(generatedKeys);
			}
		}
		catch (SQLException sql)
		{
			log.log(Level.SEVERE, "Fix the query....", sql);
		}
	}

	private void iterateThroughResultSetForGeneratedIDs(ResultSet generatedKeys) throws SQLException
	{
		if (generatedKeys.next())
		{
			Object o = generatedKeys.getObject(1);
			processId(generatedKeys, o);
		}
		else
		{
			throw new SQLException("Creating user failed, no ID obtained.");
		}
	}

	@SuppressWarnings("unchecked")
	private void processId(ResultSet generatedKeys, Object o) throws SQLException
	{
		if (o instanceof BigDecimal)
		{
			if (getClassIDType().isAssignableFrom(Long.class))
			{
				setId((I) (Long) BigDecimal.class.cast(o).longValue());
			}
			else if (getClassIDType().isAssignableFrom(Integer.class))
			{
				setId((I) (Integer) BigDecimal.class.cast(o).intValue());
			}
			else
			{
				setId((I) generatedKeys.getObject(1));
			}
		}
		else
		{
			setId((I) generatedKeys.getObject(1));
		}
	}

	/**
	 * Returns this classes associated id class type
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	protected Class<I> getClassIDType()
	{
		if (idTypeClass == null)
		{
			try
			{
				this.idTypeClass = (Class<I>) ((ParameterizedType) getClass()
						                                                   .getGenericSuperclass()).getActualTypeArguments()[2];
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, "Cannot return the class for uncheckeds. Embeddables are allowed. Config seems wrong. Check that a builder is attached to this entity as the second generic field type e.g. \n" +
						                      "public class EntityClass extends CoreEntity<EntityClass, EntityClassBuilder, Long>\n\n" +
						                      "You can view the test class in the sources or at https://github.com/GedMarc/EntityAssist/tree/master/test/za/co/mmagon/entityassist/entities", e);
				this.idTypeClass = null;
			}
		}
		return idTypeClass;
	}

	/**
	 * Merges this entity with the database copy. Uses getInstance(EntityManager.class)
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("all")
	public J update()
	{
		try
		{
			onUpdate();

			List<String> errors = validateEntity();
			if (!errors.isEmpty())
			{
				throw new SQLException("Constraint Violations in Update");
			}

			if (isRunDetached())
			{
				if (!(getEntityManager().getTransaction().isActive()))
				{
					getEntityManager().getTransaction().begin();
				}
				getEntityManager().merge(this);
				getEntityManager().flush();
				getEntityManager().getTransaction().commit();
			}
			else
			{
				getEntityManager().merge(this);
			}
		}
		catch (IllegalStateException ise)
		{
			log.log(Level.SEVERE, "Cannot update this entity the state of this object is not ready : \n", ise);
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, "Cannot update this entity the state of this object is not ready : \n", e);
		}
		return (J) this;
	}

	protected abstract void onUpdate();
}
