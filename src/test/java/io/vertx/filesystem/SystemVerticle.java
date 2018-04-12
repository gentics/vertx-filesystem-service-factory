package io.vertx.filesystem;

import io.vertx.core.AbstractVerticle;

public class SystemVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		vertx.eventBus().publish("mymodule", "system");
	}

	@Override
	public void stop() throws Exception {
		vertx.eventBus().publish("mymoduleStopped", "system");
	}

}
