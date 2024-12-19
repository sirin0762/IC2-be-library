package org.innercircle.opensource.inspector;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.innercircle.opensource.context.LocalJpaQueryContext;
import org.innercircle.opensource.context.dto.LocalQueryCounter;

import java.util.Objects;

public class JpaQueryInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        LocalQueryCounter localQueryCounter = LocalJpaQueryContext.getLocalQueryCounter();

        if (Objects.nonNull(localQueryCounter)) {
            localQueryCounter.appendQuery(sql);
        }
        return sql;
    }

}

