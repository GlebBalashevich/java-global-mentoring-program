plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.1.0"
}

val mapstructVersion = "1.5.3.Final"

dependencies{
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-solr:2.4.13")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("com.positiondev.epublib:epublib-core:3.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.codehaus.woodstox:stax2-api:4.2.1")

}
