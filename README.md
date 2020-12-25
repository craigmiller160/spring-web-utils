# spring-web-utils

This is a collection of utility code for Spring Boot webapps.

## Setup

### Adding to Project

To add this library to a project, first (obviously) add the dependency. Since this will likely not be deployed to Maven Central, the repo will have to be pulled down and installed locally using `mvn clean install`. Then, simply add it to the `pom.xml` of the project that wants to use it.

After this, it is necessary to configure that project to pull in the Spring Beans from this library. This is done by adding a WebUtilsConfig class to that project, which should look something like this:

```
@Configuration
@ComponentScan(basePackages = [
    "io.craigmiller160.webutils"
])
class WebUtilsConfig
```

Keep in mind that the basePackages value can be more fine-tuned if you don't want to pull in all the classes from this library.

### Logging

Some of the features here provide log output. To make sure that output is visible, add the following to the `application.yml` of the project consuming this library. Be sure to set the level to what you want.

```
logging:
    level:
        io.craigmiller160.webutils: INFO
```

## Features

### Error Handling

The `ErrorControllerAdvice` class provides some very robust error handling for REST APIs. It returns an explicit JSON error response with details about what happened. It also provides support for any exception with the `@ResponseStatus` annotation, and will use that when defining the response.

### Request Logging

The `RequestLogger` will log all requests and responses to the API. These will all be logged at the `DEBUG` logging level.

To add the logger, it must be configured in the consuming project:

```
@Configuration
class WebConfig (
        private val requestLogger: RequestLogger
) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(requestLogger)
                .addPathPatterns("/**")
    }
}
```

### TLS Configuration

When needing to make API requests to other services that use unknown certificates, a new TrustStore will need to be added to the global SSLContext. The TlsConfigurer will do this by passing in some simple parameters:

```
TlsConfigurer.configureTlsTrustSTore(path, type, password)
```

### AuthEntryPoint

This is a simple tool to use when configuring application security. It handles login errors and returns a JSON 401 response. It is added to your Spring Security config like this:

```
@Configuration
@EnableWebSecurity
class WebSecurityConfig(
        private val authEntryPoint: AuthEntryPoint
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http?.let {
            it.exceptionHandling().authenticationEntryPoint(authEntryPoint)
        }
    }

}
```
