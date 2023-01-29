plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.1.0"
}
val mapstructVersion = "1.5.3.Final"

dependencies{

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("co.elastic.clients:elasticsearch-java:8.6.0")
    implementation("jakarta.json:jakarta.json-api:2.0.1")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.1")
    implementation("org.springframework.boot:spring-boot-starter-log4j2:3.0.2")
    implementation("org.apache.logging.log4j:log4j-spring-boot:2.17.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    configurations.implementation {
        exclude(group = "org.slf4j", module = "slf4j-simple")
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}
