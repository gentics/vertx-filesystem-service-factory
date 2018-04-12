package examples;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.filesystem.FilesystemVerticleFactory;
import io.vertx.filesystem.ResolverOptions;

public class Examples {

	public void example1(Vertx vertx, DeploymentOptions options) {
		vertx.deployVerticle("filesystem:my-service-module::my-service", options);
	}

	public void example2(Vertx vertx, DeploymentOptions options) {
		vertx.deployVerticle("filesystem:my-service-module::my-service", options);
	}

	public void example3(Vertx vertx) {
		vertx.registerVerticleFactory(new FilesystemVerticleFactory());
	}

	public void example4(Vertx vertx) {
		vertx.registerVerticleFactory(new FilesystemVerticleFactory(new ResolverOptions().setBaseDirectory("/tmp/")));
	}
}
