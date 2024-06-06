module buckpal.application.main {
	requires static lombok;
	requires spring.context;
	requires spring.tx;
	requires jakarta.validation;
	requires buckpal.common.main;

	exports io.reflectoring.buckpal.application.domain.model;
	exports io.reflectoring.buckpal.application.port.in;
	exports io.reflectoring.buckpal.application.port.out;
}

