# Internal API

The following is currently in use internally, however the API might change.

Any of the following API calls may be restricted to internal yetu applications.


## Signature Flow

This is a custom flow not mentioned in the official OAuth2 specification.

* `POST /oauth2/access_token`
* the body must include, form-url-encoded (JSON is currently NOT supported), these fields:
    - `grant_type=signature`
* add headers as specified by [this specification](https://github.com/joyent/node-http-signature/blob/master/http_signing.md)
* see also [the nodejs library](https://github.com/joyent/node-http-signature), the [java library](https://github.com/adamcin/httpsig-java) or the unit test helpers under [test/oauth2/SignatureFlow.scala](test/oauth2/SignatureFlow.scala)



## login API via username/password

Warning: might not be supported in the future due to CSRF token checks.

While the process of logging in is supposed to be done by a user filling out the login form, it can also be, for the purposes of integration tests or command line interaction, be done via:

Request:

* `POST /authenticate/userpass`
* the body must include, form-url-encoded (JSON is currently NOT supported), these fields:
    - `username`
    - `password`

Response:

* success will be a 303 redirect, and the headers will include "Set-Cookie: id=....". This "id" Cookie is your session.
* 400 is given if the request is unsuccessful.

Example request:

* cURL syntax:
  - `curl -s -i -d "username=${username}" -d "password=${password}" "https://auth.${domain}/authenticate/userpass"`
* HTTPie syntax:
  - `http -f POST https://auth.${domain}/authenticate/userpass username="${username}" password="${password}"`


## login API via signed http headers

Request:

* `POST /authenticate/SignatureAuthentication`
* add headers as specified by [this specification](https://github.com/joyent/node-http-signature/blob/master/http_signing.md)
* see also [the nodejs library](https://github.com/joyent/node-http-signature), the [java library](https://github.com/adamcin/httpsig-java) or the unit test helpers under [test/oauth2/SignatureFlow.scala](test/oauth2/SignatureFlow.scala)

Response:

* success will be a 303 redirect, and the headers will include "Set-Cookie: id=....". This "id" Cookie is your session.
* 400 is given if the request is unsuccessful.


## Validation API

Request:

* `GET /oauth2/validate?access_token=....`

Response:

* invalid access token will return a 401 unauthorized.
* success will be a 200 with a json object. The minimum object to expect is:

```json
{
    "userEmail": "a3246725@trbvm.com",
    "userId": "a3246725@trbvm.com",
    "userUUID": "2d217536-aa04-498f-ac70-3bfdcb6d9e32"
}
```

But other fields may be included.
*Please note that `userId` is deprecated and will be removed soon. Please use either `userEmail` or `userUUID`.*

Most apps, when using the authorization_grant flow, will ask for a certain scope. In this case you will get e.g.

```json
{
    "scope": "basic",
    "userEmail": "a3246725@trbvm.com",
    "userId": "a3246725@trbvm.com",
    "userUUID": "2d217536-aa04-498f-ac70-3bfdcb6d9e32"
}
```
It is up to the requesting service to check whether the scope in the json matches the scope for your service and the action to be performed.

Example Request:

* HTTPie syntax:
    - `http "https://auth.yetudev.com/oauth2/validate?access_token=nDFPRcNPCHhdk3APVp85JR4yh6lYtxeV"`


