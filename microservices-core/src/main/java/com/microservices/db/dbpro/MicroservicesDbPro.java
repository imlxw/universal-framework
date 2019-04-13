package com.microservices.db.dbpro;

import java.util.Arrays;
import java.util.List;

import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.DbPro;
import com.jfinal.plugin.activerecord.Record;

/**
 * @version V1.0
 * @Package com.microservices.db.dbpro
 */
public class MicroservicesDbPro extends DbPro {

	public MicroservicesDbPro() {
	}

	public MicroservicesDbPro(String configName) {
		super(configName);
	}

	@Override
	public List<Record> find(String sql, Object... paras) {
		debugPrintParas(paras);
		return super.find(sql, paras);

	}

	private void debugPrintParas(Object... objects) {
		if (JFinal.me().getConstants().getDevMode()) {
			System.out.println("\r\n---------------Paras: " + Arrays.toString(objects) + "----------------");
		}
	}

}
