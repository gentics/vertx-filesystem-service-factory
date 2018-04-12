package io.vertx.mymodule;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.mydep.DummyClass;

public class MyVerticleDepA extends AbstractVerticle {

	DummyClass map = null;

	@Override
	public void start() throws Exception {
		Boolean flag = Vertx.currentContext().config().getBoolean("loaded_globally");
		if (flag != null) {
			if (flag) {
				if (DummyClass.class.getClassLoader() != MyVerticleDepA.class.getClassLoader()) {
					throw new Exception(
						"Dependency not loaded by the correct loader. It should be loaded with the same loader as the verticle. Class was loaded using {"
							+ DummyClass.class.getClassLoader() + "} and verticle using: {" + MyVerticleDepA.class.getClassLoader() + "}");
				}
			} else {
				if (DummyClass.class.getClassLoader() == MyVerticleDepA.class.getClassLoader()) {
					throw new Exception(
						"Dependency not loaded by the correct loader. It should be loaded with a different loader as the verticle. Class was loaded using {"
							+ DummyClass.class.getClassLoader() + "} and verticle using: {" + MyVerticleDepA.class.getClassLoader() + "}");
				}
			}
		}

		vertx.eventBus().publish("mymodule", "whatever");
	}

	@Override
	public void stop() throws Exception {
	}
}
