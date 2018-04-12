package io.vertx.filesystem;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.service.ServiceVerticleFactory;

public class FilesystemVerticleFactory extends ServiceVerticleFactory {

	public static final String BASE_DIR_SYS_PROP = ResolverOptions.BASE_DIR_SYS_PROP;

	private static final String DEFAULT_BASE_DIR = ResolverOptions.DEFAULT_BASE_DIR;

	private Vertx vertx;
	private ResolverOptions resolverOptions;

	public FilesystemVerticleFactory() {
		this(new ResolverOptions().setBaseDirectory(System.getProperty(BASE_DIR_SYS_PROP, DEFAULT_BASE_DIR)));
	}

	public FilesystemVerticleFactory(ResolverOptions resolverOptions) {
		this.resolverOptions = resolverOptions;
	}

	@Override
	public void init(Vertx vertx) {
		this.vertx = vertx;
	}

	@Override
	public String prefix() {
		return "filesystem";
	}

	private File getModuleFile(String identifier) {
		return Paths.get(resolverOptions.getBaseDirectory(), identifier).toAbsolutePath().normalize().toFile();
	}

	@Override
	public void resolve(String identifier, DeploymentOptions deploymentOptions, ClassLoader classLoader, Future<String> resolution) {
		vertx.<Void>executeBlocking(fut -> {
			try {
				String identifierNoPrefix = VerticleFactory.removePrefix(identifier);
				String moduleName = identifierNoPrefix;
				String serviceName = null;
				int pos = identifierNoPrefix.lastIndexOf("::");
				if (pos != -1) {
					moduleName = identifierNoPrefix.substring(0, pos);
					serviceName = identifierNoPrefix.substring(pos + 2);
				}

				File moduleFile = getModuleFile(moduleName);
				if (moduleFile.exists() && moduleFile.isFile()) {

					// When service name is null we look at the Main-Verticle in META-INF/MANIFEST.MF
					String serviceIdentifer = null;
					if (serviceName != null) {
						serviceIdentifer = "service:" + serviceName;
					} else {
						try (JarFile jarFile = new JarFile(moduleFile)) {
							Manifest manifest = jarFile.getManifest();
							if (manifest != null) {
								serviceIdentifer = (String) manifest.getMainAttributes().get(new Attributes.Name("Main-Verticle"));
							}

							if (serviceIdentifer == null) {
								throw new IllegalArgumentException("Invalid service identifier, missing service name: " + identifierNoPrefix);
							}
						}
					}

					List<String> extraCP = Arrays.asList(moduleFile.getAbsolutePath());
					deploymentOptions.setExtraClasspath(extraCP);
					deploymentOptions.setIsolationGroup("__vertx_filesystem_" + moduleName);
					URL[] urls = new URL[] { moduleFile.toURI().toURL() };
					URLClassLoader urlc = new URLClassLoader(urls, classLoader);
					super.resolve(serviceIdentifer, deploymentOptions, urlc, resolution);
					fut.complete();

				} else {
					throw new IllegalArgumentException(
						"Cannot find module " + moduleName + ". Checked " + moduleFile.getAbsolutePath() + " Maybe baseDir is invalid?");
				}
			} catch (Exception e) {
				fut.fail(e);
				resolution.fail(e);
			}
		}, ar -> {
		});
	}

	public ResolverOptions getResolverOptions() {
		return resolverOptions;
	}

}
