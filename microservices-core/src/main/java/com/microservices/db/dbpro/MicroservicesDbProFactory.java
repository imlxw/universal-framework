package com.microservices.db.dbpro;

import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.IDbProFactory;

/**
 * @version V1.0
 * @Package com.microservices.db
 */
public class MicroservicesDbProFactory implements IDbProFactory {

	@Override
	public DbPro getDbPro(String configName) {
		return new MicroservicesDbPro(configName);
	}

}
