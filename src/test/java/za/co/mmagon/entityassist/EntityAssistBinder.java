package za.co.mmagon.entityassist;

import com.armineasy.injection.GuiceDefaultBinding;
import com.armineasy.injection.abstractions.GuiceInjectorModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class EntityAssistBinder extends GuiceDefaultBinding
{
	@Override
	public void onBind(GuiceInjectorModule module)
	{
		module.bind(EntityManager.class).toProvider(new Provider<EntityManager>()
		{
			@Override
			public EntityManager get()
			{
				return Persistence.createEntityManagerFactory("h2").createEntityManager();
			}
		}).in(Singleton.class);

		super.onBind(module);
	}
}