# Dependency baseline

Status: T01 proposed baseline.

## Selected versions

| Component | Version | Reason |
| --- | --- | --- |
| Java | 17 | Required project baseline; Spring Boot 3.4 supports Java 17. |
| Spring Boot | 3.4.13 | Stable Spring Boot 3.4 maintenance release and compatible with the Java 17 baseline. |
| Gradle | 8.14.3 | Stable Gradle 8 release; Spring Boot 3.4.13 requires Gradle 8.4 or later in the 8.x line. |
| Gradle distribution | `gradle-8.14.3-bin.zip` | Binary-only distribution is sufficient for this build and avoids shipping documentation/source artifacts. |

The three application/test projects use the Spring Boot dependency BOM at `3.4.13`; individual dependency versions are intentionally managed by that BOM. No Chaos Monkey dependency or configuration is included.

## Wrapper integrity

`gradle/wrapper/gradle-wrapper.properties` pins the Gradle distribution SHA-256 checksum:

`bd71102213493060956ec229d946beee57158dbd89d0e62b91bca0fa2c5f3531`

The checksum is the official binary-only checksum for Gradle 8.14.3. The wrapper JAR is also checked against Gradle's published wrapper checksum during T01 verification.

## Sources

- [Spring Boot 3.4 system requirements](https://docs.spring.io/spring-boot/3.4/system-requirements.html) — Java 17 minimum and supported Java range.
- [Spring Boot 3.4 Gradle plugin](https://docs.spring.io/spring-boot/3.4/gradle-plugin/index.html) — Gradle compatibility for Spring Boot 3.4.13.
- [Gradle compatibility matrix](https://docs.gradle.org/current/userguide/compatibility.html) — Java and Gradle compatibility.
- [Gradle release checksums](https://gradle.org/release-checksums/) — Gradle 8.14.3 distribution and wrapper checksums.
- [Gradle Wrapper documentation](https://docs.gradle.org/current/userguide/gradle_wrapper.html) — wrapper pinning and distribution checksum verification.
