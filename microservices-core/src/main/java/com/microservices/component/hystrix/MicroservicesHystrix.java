package com.microservices.component.hystrix;

import com.microservices.utils.ClassKits;

public class MicroservicesHystrix {

	private static MicroservicesHystrix me;

	public static MicroservicesHystrix me() {
		if (me == null) {
			me = ClassKits.singleton(MicroservicesHystrix.class);
		}
		return me;
	}

}
