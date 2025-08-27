plugins {
    java

    id("com.diffplug.spotless") version "7.2.1"

    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "fr.redstom"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")

    implementation("net.dv8tion:JDA:5.0.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat()
            .reorderImports(true)
            .formatJavadoc(true)
            .reflowLongStrings()
            .aosp()
    }
}
