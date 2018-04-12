package io.vertx.filesystem;

public class ResolverOptions {

	public static final String BASE_DIR_SYS_PROP = "vertx.filesystem.baseDir";

	public static final String DEFAULT_BASE_DIR = ".";

	private String baseDirectory = System.getProperty(BASE_DIR_SYS_PROP, DEFAULT_BASE_DIR);

	public String getBaseDirectory() {
		return baseDirectory;
	}

	public ResolverOptions setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
		return this;
	}
}
