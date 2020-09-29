package com.example.demo.configuration.database;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HibernateNamingStrategy extends PhysicalNamingStrategyStandardImpl {
	private static final long serialVersionUID = -3483207004213841702L;

	//	Используем префиксы для таблиц, поскольку в OracleDB для обьектов создали глобальные синонимы c данным префиксом
	@Value("${hibernate.naming.prefix}")
	private String TABLE_NAME_PREFIX;

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		Identifier newIdentifier = new Identifier(TABLE_NAME_PREFIX + name.getText(), name.isQuoted());
		return super.toPhysicalTableName(newIdentifier, context);
	}
}