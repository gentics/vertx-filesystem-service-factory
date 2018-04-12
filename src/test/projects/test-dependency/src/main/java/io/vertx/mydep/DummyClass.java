package io.vertx.mydep;

public class DummyClass {
	static {
		System.out.println("Loaded {" + DummyClass.class.getName() + "}");
	}
}
