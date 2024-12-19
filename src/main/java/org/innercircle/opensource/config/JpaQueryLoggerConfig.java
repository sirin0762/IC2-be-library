package org.innercircle.opensource.config;

import org.hibernate.cfg.AvailableSettings;
import org.innercircle.opensource.inspector.JpaQueryInspector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.innercircle.opensource")
@ConditionalOnProperty(name = "my.jpa.query.logger.enabled", havingValue = "true")
public class JpaQueryLoggerConfig {

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties ->
            hibernateProperties.put(AvailableSettings.STATEMENT_INSPECTOR, jpaQueryInspector());
    }

    @Bean
    public JpaQueryInspector jpaQueryInspector() {
        return new JpaQueryInspector();
    }

}
