package org.realityforge.guiceyloops.server.glassfish;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.jmq.jmsclient.runtime.BrokerInstance;
import com.sun.messaging.jmq.jmsclient.runtime.ClientRuntime;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;

public class OpenMQContainer
{
  private static final Logger LOG = Logger.getLogger( OpenMQContainer.class.getName() );

  private final int _port;
  private BrokerInstance _instance;
  private ConnectionFactory _connectionFactory;

  public OpenMQContainer()
    throws Exception
  {
    this( GlassFishContainerUtil.getRandomPort() );
  }

  public OpenMQContainer( final int port )
  {
    _port = port;
  }

  public Connection createConnection()
    throws Exception
  {
    return _connectionFactory.createConnection();
  }

  public void start()
    throws Exception
  {
    if ( null == _instance )
    {
      LOG.info( "Starting OpenMQ." );
      final File runtimeDir = createRuntimeDir();

      final String[] args = { "-port", String.valueOf( _port ),
                              "-name", "TestMessageBroker" + _port,
                              "-varhome", runtimeDir.getAbsolutePath(),
                              "-imqhome", runtimeDir.getAbsolutePath(),
                              "-libhome", runtimeDir.getAbsolutePath() + "/lib" };


      final BrokerInstance instance = ClientRuntime.getRuntime().createBrokerInstance();
      instance.init( instance.parseArgs( args ), null );

      // now start the embedded broker
      instance.start();

      final com.sun.messaging.ConnectionFactory connectionFactory = new com.sun.messaging.ConnectionFactory();

      final String address = "mq://" + InetAddress.getLocalHost().getHostAddress() + ":" + _port;
      connectionFactory.setProperty( ConnectionConfiguration.imqAddressList, address );

      _connectionFactory = connectionFactory;

      _instance = instance;
      LOG.info( "OpenMQ started." );
    }
    else
    {
      LOG.warning( "Attempted to start already started OpenMQ instance." );
    }
  }

  public void stop()
  {
    if ( null != _instance )
    {
      LOG.info( "Stopping OpenMQ." );
      try
      {
        _instance.stop();
        _instance.shutdown();
      }
      catch ( final Exception e )
      {
        // Ignored
      }
      _connectionFactory = null;
      _instance = null;
      LOG.info( "OpenMQ stopped." );
    }
    else
    {
      LOG.warning( "Attempted to stop already stopped OpenMQ instance." );
    }
  }

  private File createRuntimeDir()
    throws IOException
  {
    final File runtimeDir = File.createTempFile( "openmq", "rt" );
    if ( !runtimeDir.delete() || !runtimeDir.mkdirs() )
    {
      final String message = "Failed to setup base OpenMQ directory.";
      LOG.warning( message );
      throw new IllegalStateException( message );
    }

    final File propertiesDir = new File( runtimeDir, "lib/props/broker" );
    if ( !propertiesDir.mkdirs() )
    {
      final String message = "Failed to setup OpenMQ properties directory.";
      LOG.warning( message );
      throw new IllegalStateException( message );
    }

    final Properties properties = getDefaultProperties();
    properties.store( new FileOutputStream( new File( propertiesDir, "default.properties" ) ), "" );
    return runtimeDir;
  }

  protected Properties getDefaultProperties()
    throws IOException
  {
    final Properties properties = new Properties();
    properties.setProperty( "imq.config.version", "200" );
    properties.setProperty( "imq.service.list", "jms" );
    properties.setProperty( "imq.service.activelist", "jms" );
    properties.setProperty( "imq.shared.connectionMonitor_limit", "512" );
    properties.setProperty( "imq.jms.protocoltype", "tcp" );
    properties.setProperty( "imq.jms.servicetype", "NORMAL" );
    properties.setProperty( "imq.jms.tcp.port", "0" );
    properties.setProperty( "imq.jms.tcp.backlog", "100" );
    properties.setProperty( "imq.jms.min_threads", "10" );
    properties.setProperty( "imq.jms.max_threads", "1000" );
    properties.setProperty( "imq.jms.threadpool_model", "dedicated" );
    properties.setProperty( "imq.keystore.file.dirpath", "${imq.etchome}" );
    properties.setProperty( "imq.keystore.file.name", "keystore" );
    properties.setProperty( "imq.passfile.enabled", "false" );
    properties.setProperty( "imq.passfile.dirpath", "${imq.etchome}" );
    properties.setProperty( "imq.passfile.name", "passfile" );
    properties.setProperty( "imq.protocol.tcp.inbufsz", "2048" );
    properties.setProperty( "imq.protocol.tcp.outbufsz", "2048" );
    properties.setProperty( "imq.protocol.tls.inbufsz", "2048" );
    properties.setProperty( "imq.protocol.tls.outbufsz", "2048" );
    properties.setProperty( "imq.protocol.http.inbufsz", "2048" );
    properties.setProperty( "imq.protocol.http.outbufsz", "2048" );
    properties.setProperty( "imq.protocol.https.inbufsz", "2048" );
    properties.setProperty( "imq.protocol.https.outbufsz", "2048" );
    properties.setProperty( "imq.protocol.tcp.nodelay", "true" );
    properties.setProperty( "imq.protocol.tls.nodelay", "true" );
    properties.setProperty( "imq.protocol.http.nodelay", "true" );
    properties.setProperty( "imq.protocol.https.nodelay", "true" );
    properties.setProperty( "imq.portmapper.port", "7676" );
    properties.setProperty( "imq.portmapper.backlog", "50" );
    properties.setProperty( "imq.portmapper.sotimeout", "500" );
    properties.setProperty( "imq.portmapper.bind", "true" );
    properties.setProperty( "imq.message.expiration.interval", "60" );
    properties.setProperty( "imq.system.max_count", "-1" );
    properties.setProperty( "imq.system.max_size", "-1" );
    properties.setProperty( "imq.message.max_size", "70m" );
    properties.setProperty( "imq.persist.store", "file" );
    properties.setProperty( "imq.persist.file.message.vrfile.initial_size", "1m" );
    properties.setProperty( "imq.persist.file.message.vrfile.block_size", "256" );
    properties.setProperty( "imq.persist.file.message.max_record_size", "1m" );
    properties.setProperty( "imq.persist.file.message.filepool.cleanratio", "0" );
    properties.setProperty( "imq.persist.file.destination.message.filepool.limit", "100" );
    properties.setProperty( "imq.persist.file.message.cleanup", "false" );
    properties.setProperty( "imq.persist.file.transaction.memorymappedfile.enabled", "true" );
    properties.setProperty( "imq.persist.file.sync.enabled", "false" );
    properties.setProperty( "imq.persist.file.txnLog.enabled", "false" );
    properties.setProperty( "imq.persist.file.txnLog.nonTransactedMsgSend.enabled", "false" );
    properties.setProperty( "imq.persist.file.txnLog.file.size", "10m" );
    properties.setProperty( "imq.persist.storecreate.all", "true" );
    properties.setProperty( "imq.authentication.basic.user_repository", "file" );
    properties.setProperty( "imq.authentication.digest.user_repository", "file" );
    properties.setProperty( "imq.authentication.type", "digest" );
    properties.setProperty( "imq.authentication.client.response.timeout", "180" );
    properties.setProperty( "imq.user_repository.file.filename", "passwd" );
    properties.setProperty( "imq.accesscontrol.enabled", "false" );
    properties.setProperty( "imq.log.level", "WARNING" );
    properties.setProperty( "imq.log.timezone", "" );
    properties.setProperty( "imq.log.handlers", "console" );
    properties.setProperty( "imq.log.console.stream", "ERR" );
    properties.setProperty( "imq.log.console.output", "ERROR|WARNING" );
    properties.setProperty( "imq.metrics.enabled", "false" );
    properties.setProperty( "imq.metrics.topic.enabled", "true" );
    properties.setProperty( "imq.metrics.topic.interval", "60" );
    properties.setProperty( "imq.metrics.topic.persist", "false" );
    properties.setProperty( "imq.metrics.topic.timetolive", "300" );
    properties.setProperty( "imq.autocreate.topic", "true" );
    properties.setProperty( "imq.autocreate.queue", "true" );
    properties.setProperty( "imq.autocreate.reaptime", "120" );
    properties.setProperty( "imq.transaction.autorollback", "false" );
    properties.setProperty( "imq.transaction.detachedTimeout", "0" );
    properties.setProperty( "imq.transaction.producer.maxNumMsgs", "1000" );
    properties.setProperty( "imq.transaction.consumer.maxNumMsgs", "100" );
    properties.setProperty( "imq.restart.code", "255" );
    properties.setProperty( "imq.packet.read.override", "heap" );
    properties.setProperty( "imq.diag.all", "false" );
    properties.setProperty( "imq.memory.levels", "green,yellow,orange,red" );
    properties.setProperty( "imq.memory.overhead", "10240" );
    properties.setProperty( "imq.green.threshold", "0" );
    properties.setProperty( "imq.yellow.threshold", "80" );
    properties.setProperty( "imq.orange.threshold", "90" );
    properties.setProperty( "imq.red.threshold", "98" );
    properties.setProperty( "imq.green.count", "5000" );
    properties.setProperty( "imq.yellow.count", "500" );
    properties.setProperty( "imq.orange.count", "50" );
    properties.setProperty( "imq.red.count", "0" );
    properties.setProperty( "imq.destination.DMQ.truncateBody", "false" );
    properties.setProperty( "imq.destination.logDeadMsgs", "false" );
    properties.setProperty( "imq.fix.JMSMessageID", "true" );

    return properties;
  }
}
