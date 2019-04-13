package com.microservices.server.warmboot;

import java.io.File;

/**
 * @version V1.0
 * @Package com.microservices.config.server
 */
class TimeSize {

	final long time;
	final long size;

	public TimeSize(File file) {
		this.time = file.lastModified();
		this.size = file.length();
	}

	@Override
	public int hashCode() {
		return (int) (time ^ size);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TimeSize) {
			TimeSize ts = (TimeSize) o;
			return ts.time == this.time && ts.size == this.size;
		}
		return false;
	}

	@Override
	public String toString() {
		return "TimeSize{" + "time=" + time + ", size=" + size + '}';
	}
}
