package org.realityforge.guiceyloops.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.File;
import java.lang.reflect.Field;
import javax.persistence.EntityManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.realityforge.guiceyloops.JEETestingModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class EntityManagerTestingModuleTest
{
  private File _databaseFile;

  @BeforeMethod
  public final void setupEntityManager()
    throws Exception
  {
    _databaseFile = TestUtil.setupDatabase();
  }

  @AfterMethod
  public final void tearDownEntityManager()
    throws Exception
  {
    if ( null != _databaseFile )
    {
      if ( !_databaseFile.delete() )
      {
        _databaseFile.deleteOnExit();
      }
      _databaseFile = null;
    }
  }

  @Test
  public void basicOperation()
    throws Throwable
  {
    final Injector injector =
      Guice.createInjector( new TestEntityManagerTestingModule(), new JEETestingModule() );

    assertNotNull( injector.getInstance( EntityManager.class ) );

    final TransactionSynchronizationRegistry registry =
      injector.getInstance( TransactionSynchronizationRegistry.class );
    assertTrue( registry instanceof TestTransactionSynchronizationRegistry );

    final DbCleaner cleaner = injector.getInstance( DbCleaner.class );
    assertNotNull( cleaner );

    final Field field = cleaner.getClass().getDeclaredField( "_tableNames" );
    field.setAccessible( true );
    final String[] tableNames = (String[]) field.get( cleaner );
    assertEquals( tableNames.length, 2 );
    assertEquals( tableNames[ 0 ], "Test.tblTestEntity1" );
    assertEquals( tableNames[ 1 ], "Test.tblTestEntity2" );
  }
}