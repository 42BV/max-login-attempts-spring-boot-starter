# max-login-attempts-spring-boot-starter

Spring boot starter project that can be used to put a maximum attempt on
the number of login failures a user can make from a specific IP-address. 
The use-case of this is to prevent hackers from brute-forcing an account.

After the maximum amount of failed attempts, the user is blocked for a certain
amount of time. During this time, an error will be presented when the user attempts
to login, also if the credentials are correct. The waiting time and amount of tries
are all configurable.

## Registering the filter

From this starter a LoginAttemptFilter bean is exposed, all you need to do to get
this to work is add it to your spring security filter chain somewhere that you want.
We use the [rest-secure-spring-boot-starter](https://github.com/42BV/rest-secure-spring-boot-starter),
but you don't have to. If you do, you can add the filter like this in your client application:

```java
@Configuration
public class CustomSecurityConfig {
    
    @Autowired
    private LoginAttemptFilter loginAttemptFilter;

    @Bean
    public HttpSecurityCustomizer httpSecurityCustomizer() {
        return http -> http.addFilterBefore(loginAttemptFilter, RestAuthenticationFilter.class);
    }
}
```

## Enabling/disabling the login attempt limiter

When you have not provided any settings, the login limiter is enabled by default. You can
disable this by setting the following property:

```yaml
max-login-attempts-starter:
  enabled: false
```

## Overriding the default authentication endpoint

It will operate on the POST "/authentication" endpoint only by default. You can change this
in the following way:

```yaml
max-login-attempts-starter:
  authentication-endpoints:
    - path: '/authentication'
      method: POST
```

You can also define multiple endpoints:

 ```yaml
 max-login-attempts-starter:
   authentication-endpoints:
     - path: '/authentication'
       method: POST
     - path: '/saml-authentication'
       method: PUT
 ```

## Overriding the default maximum number of failed login attempts

By default the user has 5 attempts before he is blocked. This means that on the 5th failed
attempt the user is logged out. You can change this number the following way:

```yaml
max-login-attempts-starter:
  max-attempts: 10
```

## Overriding the default after-blocking cooldown

After the user is blocked due to too many failed login attempts, the user is by default blocked
for 60_000 milliseconds (1 minute). You can change this value by defining the following property:

```yaml
max-login-attempts-starter:
  cooldown-in-ms: 90000 # (1,5 minute)
``` 

## Overriding the default CRON that resets the counters

Because it is probably not desirable that failed login attempts are counted "over multiple days",
by default all failed attempt counters are reset at 00:00. You can change this by setting the following
CRON to your desired interval:

```yaml
max-login-attempts-starter:
  clear-all-attempts-cron: '0 0 0 25 12 *' # Christmas time at 00:00
```

## Enabling debug logging

If you want more specific information on failed login attempts, enable debug logging as follows:

```yaml
logging:
  level:
    nl._42.max_login_attempts_spring_boot_starter: debug
```

## Overriding the default error handler

By default, the starter will return a JSON object with a key 'errorCode' and a value 'TOO_MANY_LOGIN_ATTEMPTS' when the max login attempt
has been reached. You can override this behaviour by exposing a bean that implements the TooManyLoginAttemptsErrorHandler
interface:

```java
@Component
public class CustomTooManyLoginAttemptsErrorHandler implements TooManyLoginAttemptsErrorHandler {
       
   @Override
   public void handle(HttpServletResponse response) throws IOException {
      // Define your response here
   }
}
```