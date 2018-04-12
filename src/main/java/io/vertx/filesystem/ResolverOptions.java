package io.vertx.filesystem;

public class ResolverOptions {

	private String baseDirectory;

	public String getBaseDirectory() {
		return baseDirectory;
	}

	public ResolverOptions setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
		return this;
	}
}
