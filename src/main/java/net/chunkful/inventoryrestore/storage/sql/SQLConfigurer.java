package net.chunkful.inventoryrestore.storage.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.JDBCUtils;

import javax.sql.DataSource;

public final class SQLConfigurer {

    SQLDialect dialect(String connectionString) {
        return JDBCUtils.dialect(connectionString);
    }

    DataSource dataSource(String connectionString, SQLDialect dialect) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(connectionString);
        hikariDataSource.setDriverClassName(JDBCUtils.driver(dialect));
        return hikariDataSource;
    }

    DSLContext dslContext(DataSource dataSource, SQLDialect dialect) {
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");

        return DSL.using(dataSource, dialect, new Settings().withRenderSchema(false));
    }
}
