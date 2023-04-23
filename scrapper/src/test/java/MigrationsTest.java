import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.tinkoff.edu.java.scrapper.environment.IntegrationEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class MigrationsTest {
    @Test
    @SneakyThrows
    public void shouldCreateTables() {
        try (
            var databaseConnection = IntegrationEnvironment.getDatabaseConnection();
            var statement = databaseConnection.createStatement()
        ) {
            String sql = """
                    SELECT tablename
                    FROM pg_catalog.pg_tables
                    WHERE schemaname = 'public'
                    ORDER BY tablename;
                    """;
            var resultSet = statement.executeQuery(sql);

            List<String> tableNames = new ArrayList<>();

            while (resultSet.next()) {
                tableNames.add(resultSet.getString("tablename"));
            }

            var expected = List.of(
                    "databasechangelog",
                    "databasechangeloglock",
                    "github_repositories",
                    "links",
                    "stackoverflow_questions",
                    "tg_chats"
            );
            assertIterableEquals(expected, tableNames);
        }
    }
}
