package io.vertx.mymodule;

import io.vertx.core.AbstractVerticle;
import io.vertx.mydep.DummyClass;

public class MyVerticleDepB extends AbstractVerticle {

	DummyClass map = null;

	@Override
	public void start() throws Exception {
		if (DummyClass.class.getClassLoader() == MyVerticleDepB.class.getClassLoader().getParent()) {
			throw new Exception("Dependency not loaded by the correct loader");
		}
		vertx.eventBus().publish("mymodule", "whatever2");
	}

	@Override
	public void stop() throws Exception {
	}
}
