# OAuth2 Provider and Authentication system

[![Build Status](https://travis-ci.org/yetu/oauth2-provider.svg?branch=master)](https://travis-ci.org/yetu/oauth2-provider)
[![Coverage Status](https://coveralls.io/repos/yetu/oauth2-provider/badge.svg)](https://coveralls.io/r/yetu/oauth2-provider)

This project is yetu's central authentication and authorization system.

**Please note that this project may contain security bugs as it has not been reviewed or audited by security experts. Use at your own risk.**

## Notable dependencies

This Scala project is built with the Play Framework and uses [Securesocial](http://securesocial.ws) for authentication and [scala-oauth2-provider](https://github.com/nulab/scala-oauth2-provider) as a starting point for the OAuth2 flow.


## API documentation

See [API.md](API.md) for more information.

## Set up your own oauth2-provider

Execute the following script to have a working oauth2provider set up with your own pair of RSA keys (The oauth2-provider creates access_tokens based on JSON Web Tokens (JWT) which it signs using RSA keys)

```
# sets up rsa keys and creates a fresh copy of conf/application.conf for you to adapt if needed
./setupLocalDev.sh
```

Have a look at the `conf/application-conf` file and fill in sections for
 - **LDAP** (if using; defaulting to in-memory), 
 - **SMTP** (if using; defaulting to a mocked smtp that does not send any emails.)

Run the app with `sbt run`, run tests with `sbt test` and integration tests with `sbt it:test`

## Functionality status

#### General

- [ ] persistence of user sessions
    - benefit: user will no longer lose their active login session when updates are deployed or the server is restarted
- [ ] persistence of auth_codes and access_tokens
    - benefit: apps will no longer lose their ability to talk to APIs when updates are deployed or the server is restarted
- [x] JSON web token extension
    - benefit: reduces amount of validation requests to auth server
- [ ] OpenID Connect extension
    - benefit: improves security and ensures the authorization_code flow cannot be tampered with by a malicious app

#### Authentication

- [x] signup
- [x] login
- [x] reset password
- [ ] delete account


#### Authorization

- [x] authorization code flow
     - used by any app with a server backend
- [x] resource owner password flow
- [x] signature flow (non-standard flow using signed HTTP headers)
     - used by gateway/TV
- [x] implicit flow (uses special API end-point)
    - can be used by browser-only third party apps
- [ ] client credentials flow
    - (low priority) unclear, to be investigated
- [x] handle multiple scopes
- [ ] grant app permissions page (note: partial work is done on this)
    - !MISSING! - required to support ANY third party apps
- [ ] distinguish app permissions via scopes
    - !MISSING! - required to support third party apps that can have different permissions
    - involvement of infrastructure team required also; each service needs to check the validation response.


#### security features

- [x] checking client_id/client_secret
- [x] checking scopes match
- [x] checking redirectURIs match
- [ ] handle refresh tokens
    - reduces validity time of access_tokens, thereby reducing the time window for abuse of access_tokens.
- [ ] make sure auth_codes can only be used once to prevent replay-attacks.
- [ ] make sure refresh_tokens can only be used once to prevent replay-attacks.


## Contributing

Feel free to submit issues or pull requests!

### Additional information

#### JSON Web Token (JWT)

Please also see official specification on [ietf.org](https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-32) and easier-to-read information on [openid.net]( http://openid.net/specs/draft-jones-json-web-token-07.html)

This little [online tool](http://jwt.io/) might be useful for quickly seeing the content of a JSON Web Token.

Please note that all datetime values (e.g. `exp`, `iat`) are in seconds since 1970 UTC ignoring leap seconds, see [Terminology -> `NumericDate`](https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-32#section-2)

## License

MIT. See [LICENSE](LICENSE) for details.


