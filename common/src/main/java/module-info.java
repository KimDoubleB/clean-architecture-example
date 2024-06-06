module buckpal.common.main {
	requires jakarta.validation;
	requires spring.context;
	requires spring.core;

	exports io.reflectoring.buckpal.common;
	exports io.reflectoring.buckpal.common.validation;
}
