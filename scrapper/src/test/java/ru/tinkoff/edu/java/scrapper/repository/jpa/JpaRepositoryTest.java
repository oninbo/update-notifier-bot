package ru.tinkoff.edu.java.scrapper.repository.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import ru.tinkoff.edu.java.scrapper.configuration.TestDataSourceConfig;

@DataJpaTest(includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        TestDataSourceConfig.class
                })
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class JpaRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
}
