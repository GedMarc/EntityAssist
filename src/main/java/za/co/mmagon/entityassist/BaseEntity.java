package za.co.mmagon.entityassist;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.inject.Inject;
import com.google.inject.Injector;
import za.co.mmagon.entityassist.querybuilder.EntityAssistStrings;
import za.co.mmagon.entityassist.querybuilder.builders.QueryBuilderBase;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	@Transient
	@Inject
	private Injector injector;

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

	@NotNull
	/**
	 * Returns the builder associated with this entity
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Q builder()
	{
		Class<Q> foundQueryBuilderClass = getClassQueryBuilderClass();
		QueryBuilderBase<?, ?, ?> instance = injector.getInstance(foundQueryBuilderClass);
		instance.setEntity(this);
		return (Q) instance;

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
	 * Returns this classes associated id class type
	 *
	 * @return
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public Class<I> getClassIDType()
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

	public abstract void onUpdate();
}
