package com.esteel.framework.mybatis;

import com.esteel.framework.mybatis.dialect.Dialect;
import com.esteel.framework.mybatis.dialect.MySql5Dialect;
import com.esteel.framework.mybatis.dialect.OracleDialect;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;
import java.util.Properties;
/**
 * @version 1.0.0
 * @ClassName Sort.java
 * @author: liu Jie
 * @Description: TODO
 * @createTime: 2020年-05月-19日
 */
@SuppressWarnings("all")
@Intercepts({ @Signature(type = StatementHandler.class, method = "update", args = { Statement.class }) })
public class SqlInterceptor implements Interceptor {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private static ThreadLocal<RowBounds> rowBounds = new ThreadLocal<RowBounds>();
	
	public static RowBounds getRowBounds() {
		RowBounds rowBounds = SqlInterceptor.rowBounds.get();
		SqlInterceptor.rowBounds.remove();
		return rowBounds;
	}

	public static void setRowBounds(RowBounds rowBounds) {
		SqlInterceptor.rowBounds.set(rowBounds);
	}
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		BoundSql boundSql = statementHandler.getBoundSql();
//		MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, new DefaultObjectFactory(), new DefaultObjectWrapperFactory());
		MetaObject metaStatementHandler = MetaObject.forObject(invocation.getTarget(), new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());

		RowBounds rowBounds = getRowBounds();
		if (rowBounds == null) {
			rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");
		}
		Configuration configuration = (Configuration) metaStatementHandler.getValue("delegate.configuration");
		Dialect.Type databaseType = null;
		try {
			databaseType = Dialect.Type.valueOf(configuration.getVariables().getProperty("dialect").toUpperCase());
		} catch (Exception e) {
			// ignore
		}
		if (databaseType == null) {
			throw new RuntimeException("the value of the dialect property in configuration.xml is not defined : " + configuration.getVariables().getProperty("dialect"));
		}
		Dialect dialect = null;
		switch (databaseType) {
		case MYSQL:
			dialect = new MySql5Dialect();
			break;
		case ORACLE:
			dialect = new OracleDialect();
			break;
		}

		String sql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
		if ((rowBounds != null) && (rowBounds != RowBounds.DEFAULT)) {
			sql = dialect.getLimitString(sql, rowBounds.getOffset(), rowBounds.getLimit());
		}
		
		metaStatementHandler.setValue("delegate.boundSql.sql", sql);
		metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
		metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
		//logger.debug("SQL : " + boundSql.getSql());
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {}

}