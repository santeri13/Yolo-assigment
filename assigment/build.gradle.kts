plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"

}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(24)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation ("org.springframework.boot:spring-boot-starter-websocket")
	implementation ("com.fasterxml.jackson.core:jackson-databind")
	testImplementation ("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Test> {
	jvmArgs("-XX:+EnableDynamicAgentLoading")
	useJUnitPlatform()
}
