package tech.mindstructs.hikary;

import intellispaces.ixora.mindstructs.rdb.ConnectionHandle;
import intellispaces.ixora.mindstructs.rdb.hikary.HikariDataSourcePropertiesHandle;
import intellispaces.ixora.mindstructs.rdb.hikary.MovableHikariDataSourceHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.intellispaces.framework.core.annotation.Mapper;
import tech.intellispaces.framework.core.annotation.Mover;
import tech.intellispaces.framework.core.annotation.ObjectHandle;
import tech.intellispaces.framework.core.exception.TraverseException;
import tech.mindstructs.rdb.BasicConnection;

import java.sql.Connection;
import java.sql.SQLException;

@ObjectHandle("HikariDataSource")
public abstract class AbstractHikariDataSource implements MovableHikariDataSourceHandle {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractHikariDataSource.class);

  private final HikariDataSourcePropertiesHandle dataSourceProperties;
  private final com.zaxxer.hikari.HikariDataSource dataSource;

  public AbstractHikariDataSource(
      com.zaxxer.hikari.HikariDataSource dataSource,
      HikariDataSourcePropertiesHandle dataSourceProperties
  ) {
    this.dataSource = dataSource;
    this.dataSourceProperties = dataSourceProperties;
  }

  @Mapper
  @Override
  public HikariDataSourcePropertiesHandle properties() {
    return dataSourceProperties;
  }

  @Mover
  @Override
  public ConnectionHandle getConnection() {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Get JDBC connection from Hikari data source. URL '{}', username '{}'", url(), username());
    }
    try {
      Connection connection = dataSource.getConnection();
      return new BasicConnection(connection);
    } catch (SQLException e) {
      throw TraverseException.withCauseAndMessage(e, "Could not get JDBC connection from Hikari data source. " +
          "URL '{}', username '{}'");
    }
  }

  private String url() {
    return dataSourceProperties.url().trim();
  }

  private String username() {
    return dataSourceProperties.username().trim();
  }
}
