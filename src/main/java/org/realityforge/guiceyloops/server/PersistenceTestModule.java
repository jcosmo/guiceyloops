package org.realityforge.guiceyloops.server;

import com.google.inject.name.Names;
import java.util.Vector;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventManager;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListener;
import org.eclipse.persistence.sessions.Session;
import org.realityforge.guiceyloops.shared.AbstractModule;

public abstract class PersistenceTestModule
  extends AbstractModule
{
  private final String _persistenceUnitName;
  private final String[] _tablesToClean;
  private final String _databasePrefix;
  private EntityManager _entityManager;

  public PersistenceTestModule( @Nonnull final String persistenceUnitName, @Nonnull final String[] tablesToClean )
  {
    this( persistenceUnitName, tablesToClean, null );
  }

  public PersistenceTestModule( @Nonnull final String persistenceUnitName,
                                @Nonnull final String[] tablesToClean,
                                @Nullable final String databasePrefix )
  {
    _persistenceUnitName = persistenceUnitName;
    _tablesToClean = tablesToClean;
    _databasePrefix = databasePrefix;
  }

  protected final EntityManager getEntityManager()
  {
    return _entityManager;
  }

  /**
   * @return the prefix used to lookup database properties.
   */
  @Nullable
  protected final String getDatabasePrefix()
  {
    return _databasePrefix;
  }

  /**
   * Override this to further customize the persistence elements.
   */
  protected final void configure()
  {
    _entityManager = DatabaseUtil.createEntityManager( _persistenceUnitName, getDatabasePrefix() );
    bindResource( EntityManager.class, _persistenceUnitName, _entityManager );
    requestInjectionForAllEntityListeners();
    if ( 0 != _tablesToClean.length )
    {
      requestCleaningOfTables( _tablesToClean );
    }
  }

  private void requestCleaningOfTables( @Nonnull final String[] tables )
  {
    bind( DbCleaner.class ).
      annotatedWith( Names.named( _persistenceUnitName ) ).
      toInstance( new DbCleaner( tables, getEntityManager() ) );
  }

  /**
   * Request injection for entity listeners on all entities in persistence unit.
   */
  private void requestInjectionForAllEntityListeners()
  {
    final Session session = _entityManager.unwrap( Session.class );
    for ( final ClassDescriptor descriptor : session.getDescriptors().values() )
    {
      requestInjectionForEntityListeners( descriptor );
    }
  }

  private void requestInjectionForEntityListeners( final ClassDescriptor descriptor )
  {
    final DescriptorEventManager eventManager = descriptor.getEventManager();
    requestInjectionForEntityListeners( eventManager.getDefaultEventListeners() );
    requestInjectionForEntityListeners( eventManager.getEntityListenerEventListeners() );
  }

  private void requestInjectionForEntityListeners( final Vector eventListeners )
  {
    for ( final Object o : eventListeners )
    {
      final EntityListener listener = (EntityListener) o;
      requestInjection( listener.getListener( listener.getOwningSession() ) );
    }
  }
}
