module buckpal.adapter.out.main {
	requires static lombok;
	requires jakarta.persistence;
	requires spring.context;
	requires spring.data.commons;
	requires spring.data.jpa;

	requires buckpal.application.main;
	requires buckpal.common.main;
}
