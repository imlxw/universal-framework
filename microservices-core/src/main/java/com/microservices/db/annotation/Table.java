package com.microservices.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.shardingjdbc.core.api.config.strategy.ShardingStrategyConfiguration;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Table {

	String tableName();

	String primaryKey() default "";

	Class<? extends ShardingStrategyConfiguration> databaseShardingStrategyConfig() default ShardingStrategyConfiguration.class;

	Class<? extends ShardingStrategyConfiguration> tableShardingStrategyConfig() default ShardingStrategyConfiguration.class;

	String actualDataNodes() default "";

	String keyGeneratorColumnName() default "";

	Class keyGeneratorClass() default Void.class;

	String datasource() default "";

}