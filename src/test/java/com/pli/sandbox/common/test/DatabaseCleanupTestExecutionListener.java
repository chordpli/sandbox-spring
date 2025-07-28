package com.pli.sandbox.common.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class DatabaseCleanupTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        DataSource dataSource = testContext.getApplicationContext().getBean(DataSource.class);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        try (Connection connection = dataSource.getConnection()) {
            List<String> tableNames = new ArrayList<>();
            try (ResultSet rs = connection.getMetaData().getTables(null, null, null, new String[] {"TABLE"})) {
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
            }

            if (!tableNames.isEmpty()) {
                // Disable referential integrity for H2
                jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");

                for (String tableName : tableNames) {
                    jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
                }

                // Re-enable referential integrity
                jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }
        }
    }
}
