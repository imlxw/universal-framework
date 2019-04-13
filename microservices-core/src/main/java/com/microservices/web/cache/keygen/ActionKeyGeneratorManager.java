package com.microservices.web.cache.keygen;

import com.microservices.Microservices;
import com.microservices.core.spi.MicroservicesSpiLoader;
import com.microservices.exception.MicroservicesIllegalConfigException;
import com.microservices.utils.StringUtils;
import com.microservices.web.MicroservicesWebConfig;

/**
 * @version V1.0
 * @Package com.microservices.web.cache.keygen
 */
public class ActionKeyGeneratorManager {

	private final static ActionKeyGeneratorManager me = new ActionKeyGeneratorManager();

	public static ActionKeyGeneratorManager me() {
		return me;
	}

	private MicroservicesWebConfig webConfig = Microservices.config(MicroservicesWebConfig.class);

	private IActionKeyGenerator generator;

	public IActionKeyGenerator getGenerator() {

		if (generator == null) {
			generator = createGenerator();
		}
		return generator;

	}

	private IActionKeyGenerator createGenerator() {

		String type = webConfig.getActionCacheKeyGeneratorType();

		if (StringUtils.isBlank(type)) {
			return new DefaultActionKeyGeneratorImpl();
		}

		if (MicroservicesWebConfig.ACTION_CACHE_KEYGENERATOR_TYPE_DEFAULT.equals(type)) {
			return new DefaultActionKeyGeneratorImpl();
		}

		IActionKeyGenerator generator = MicroservicesSpiLoader.load(IActionKeyGenerator.class, type);
		if (generator == null) {
			throw new MicroservicesIllegalConfigException("can not load [" + IActionKeyGenerator.class.getName() + "] from spi with type:" + type);
		}

		return generator;
	}

}
