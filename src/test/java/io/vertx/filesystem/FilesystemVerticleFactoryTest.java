package io.vertx.filesystem;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.VertxTestBase;

public class FilesystemVerticleFactoryTest extends VertxTestBase {

	@Before
	public void setup() {
		System.clearProperty(ResolverOptions.BASE_DIR_SYS_PROP);
	}

	@Test
	public void testDeploymentMain() {
		setBaseDir("target/test-projects/test-module-main-verticle/target");
		assertDeployment("mymodule", "whatever");
		vertx.deployVerticle("filesystem:mymodule.jar");
		await();
	}

	@Test
	public void testDeploymentMainWithFolder() {
		setBaseDir("target/test-projects/test-module-main-verticle");
		assertDeployment("mymodule", "whatever");
		vertx.deployVerticle("filesystem:target/mymodule.jar");
		await();
	}

	@Test
	public void testDeploymentServiceA() {
		setBaseDir("target/test-projects/test-module/target");
		assertDeployment("mymodule", "whatever");
		vertx.deployVerticle("filesystem:mymodule.jar::my.serviceA");
		await();
	}

	@Test
	public void testDeploymentServiceB() {
		setBaseDir("target/test-projects/test-module/target");
		assertDeployment("mymodule", "whatever2");
		vertx.deployVerticle("filesystem:mymodule.jar::my.serviceB");
		await();
	}

	@Test
	public void testDeploymentServiceZip() {
		setBaseDir("target/test-projects/test-module/target");
		assertDeployment("mymodule", "whateverZip");
		vertx.deployVerticle("filesystem:mymodule.zip::my.serviceZip");
		await();
	}

	@Test
	public void testDeploymentServiceClassifier() {
		setBaseDir("target/test-projects/test-module/target");
		assertDeployment("mymodule", "whateverClassifier");
		vertx.deployVerticle("filesystem:mymodule-the_classifier.jar::my.serviceClassifier");
		await();
	}

	@Test
	public void testClassLoaderDep() {
		setBaseDir("target/test-projects/test-module-dep/target");
		assertDeployment("mymodule", "whatever");
		vertx.deployVerticle("filesystem:mymodule.jar::my.serviceA");
		await();
	}

	@Test
	public void testClassLoaderDep2() {
		setBaseDir("target/test-projects/test-module-dep/target");
		assertDeployment("mymodule", "whatever2");
		vertx.deployVerticle("filesystem:mymodule.jar::my.serviceB");
		await();
	}

	@Test
	public void testLoadDependencyFromParentLoaderWhenAbsent() {
		setBaseDir("target/test-projects/test-module-dep/target");
		assertDeployment("mymodule", "whatever");
		vertx.deployVerticle("filesystem:mymodule.jar::my.serviceA",
			new DeploymentOptions().setConfig(new JsonObject().put("loaded_globally", true)));
		await();
	}

	@Test
	public void testLoadDependencyFromParentLoaderWhenPresent() throws ClassNotFoundException, MalformedURLException {
		setBaseDir("target/test-projects/test-module-dep/target");
		URLClassLoader loader = new URLClassLoader(new URL[] { Paths.get("target/test-projects/test-dependency/target/mydep.jar").toUri().toURL() },
			FilesystemVerticleFactoryTest.class.getClassLoader());
		loader.loadClass("io.vertx.mydep.DummyClass");

		ClassLoader prev = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(loader);
		try {
			assertDeployment("mymodule", "whatever");
			vertx.deployVerticle("filesystem:mymodule.jar::my.serviceA",
				new DeploymentOptions().setConfig(new JsonObject().put("loaded_globally", false)));
			await();
		} finally {
			Thread.currentThread().setContextClassLoader(prev);
		}
	}

	/**
	 * Asserts that the correct module has been deployed by sending an event to the deployed verticle.
	 * 
	 * @param address
	 * @param expectedReply
	 */
	private void assertDeployment(String address, String expectedReply) {
		vertx.eventBus().localConsumer(address).handler(message -> {
			assertEquals(expectedReply, message.body());
			testComplete();
		});
	}

	private void setBaseDir(String path) {
		System.setProperty(ResolverOptions.BASE_DIR_SYS_PROP, path);
		vertx.close();
		vertx = Vertx.vertx();
	}
}
