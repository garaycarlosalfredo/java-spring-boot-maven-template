---
name: java-spring-development
description: Design, implement, review, or explain Java and Spring Boot development using good practices grounded in official documentation and compatible with the project's versions. Use for Java code, configuration, dependencies, APIs, persistence, security, and testing; does not require Spring for plain Java projects.
license: MIT
---

# Java and Spring Boot development

Complete the requested task with proportional changes, preserving the project's behavior, conventions, and architecture. Respond in the user's language. Use official sources as a foundation; distinguish provider requirements or recommendations from project or agent design decisions. Do not attribute an undocumented practice to a provider.

## Understand the project and consult sources

1. Read repository instructions and relevant files. Identify the target Java version and, where applicable, Spring Boot, Spring Framework, and involved component versions; inspect Maven or Gradle, wrappers, configuration, and tests. Distinguish compilation, runtime, and effective dependency versions. If they cannot be determined, say so and avoid assuming the latest version.
2. Consult current official documentation for **those versions** and technologies involved. Start from the indexes below, identify and read pages relevant to every part of the request, and follow links to official references and guides. For compatibility or changes, consult version-specific and migration documentation. Do not claim that unread pages substantiate a conclusion.
3. Continue research until each part is supported by evidence or relevant accessible sources are exhausted. If something cannot be verified, explain what was consulted, give the best supported answer, and state the limitation. Cite specific sources when explaining recommendations or technical decisions; distinguish documented facts, observed results, and preferences.

## Practical application

- Examine the affected flow and behavior before changing code. Design for the actual problem without imposing package organization, architecture, patterns, or dependencies the task does not require. Spring Boot does not prescribe a single internal layout.
- Apply Java features available in the target version; consider readability, contracts, errors, mutability, concurrency, and resources as appropriate. Do not implicitly upgrade the JDK or introduce incompatible features.
- In Spring, verify configuration, lifecycle, injection, HTTP boundaries, validation, persistence, and transactions as applicable. For security, authentication, and authorization, consult the relevant Spring Security version and verify actual behavior; do not turn example values into production configuration.
- Use the project's dependencies and tools, checking compatibility before adding or changing versions. Do not impose libraries or infrastructure by habit.
- Choose tests proportional to risk and behavior: unit tests for isolated logic and integration tests for contracts or real infrastructure interaction when they provide evidence. Run relevant checks and communicate results and limitations; do not claim tests were run when they were not.
- In a review, identify file and line, evidence, impact, and correction. Distinguish observable errors and demonstrable risks from preferences. In an explanation, answer concretely and use examples when they clarify the decision.
- If the task explicitly concerns hexagonal architecture, DDD, or another specialty, also use its corresponding skill when available, without treating its patterns as general Spring requirements.

## Official starting points

- [Java SE documentation by version](https://docs.oracle.com/en/java/javase/) and [Java tutorials and specifications](https://dev.java/learn/).
- [Spring Boot reference](https://docs.spring.io/spring-boot/reference/), including [structure](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html) and [testing](https://docs.spring.io/spring-boot/reference/testing/index.html). Select the project's version in the documentation.
- [Spring Framework](https://docs.spring.io/spring-framework/reference/), [Spring Security](https://docs.spring.io/spring-security/reference/), and [Spring Data](https://spring.io/projects/spring-data): select the relevant module and version.
- For additional tools and libraries, consult their specific official guides, such as [Maven](https://maven.apache.org/guides/), [Gradle](https://docs.gradle.org/), [JUnit](https://docs.junit.org/), and [Hibernate ORM](https://hibernate.org/orm/documentation/), when relevant.
