package org.springframework.jdbc.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.rowmapper.RowMapper;
import org.springframework.jdbc.core.statementexecutor.StatementExecutor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.springframework.jdbc.core.rowmapper.RowMapperGenerator.generateRowMapperOf;
import static org.springframework.jdbc.core.statementexecutor.QueryForObjectStatementExecutor.QUERY_FOR_OBJECT_EXECUTOR;
import static org.springframework.jdbc.core.statementexecutor.QueryStatementExecutor.QUERY_EXECUTOR;

public class JdbcTemplate {

    private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);

    private final DataSource dataSource;
    private final StatementGenerator statementGenerator;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
        this.statementGenerator = new StatementGenerator();
    }

    public void update(final Connection conn, final String sql, final Object... params) {
        execute(conn, sql, params);
    }

    public <T> T queryForObject(final Connection conn, final String sql, final RowMapper<T> rowMapper, final Object... params) {
        return execute(conn, sql, rowMapper, QUERY_FOR_OBJECT_EXECUTOR, params);
    }

    public <T> T queryForObject(final Connection conn, final String sql, final Class<T> requiredType, final Object... params) {
        return queryForObject(
                conn,
                sql,
                generateRowMapperOf(requiredType),
                params
        );
    }

    public <T> List<T> query(final Connection conn, final String sql, final RowMapper<T> rowMapper, final Object... params) {
        return (List<T>) execute(conn, sql, rowMapper, QUERY_EXECUTOR, params);
    }

    public <T> List<T> query(final Connection conn, final String sql, final Class<T> requiredType, final Object... params) {
        return query(
                conn,
                sql,
                generateRowMapperOf(requiredType),
                params
        );
    }

    public void execute(final Connection conn, final String sql, final Object... params) {
        try {
            PreparedStatement pstmt = statementGenerator.prepareStatement(sql, conn, params);
            log.debug("query : {}", sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new DataAccessException(e);
        }
    }

    private <T> T execute(
            final Connection conn,
            final String sql,
            final RowMapper<T> rowMapper,
            final StatementExecutor executor,
            final Object... params
    ) {
        try {
            PreparedStatement pstmt = statementGenerator.prepareStatement(sql, conn, params);
            log.debug("query : {}", sql);
            return (T) executor.execute(pstmt, rowMapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
