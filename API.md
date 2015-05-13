# API documentation

All applications follow a basic pattern when accessing a yetu API using OAuth 2.0. These are:

1. Obtain OAuth 2.0 credentials (currently there is no developer-console, please contact to : dev-support@yetu.com)
2. Obtain an access token from the yetu OAuth2 Provider/Authorization Server. (this codebase, hosted at https://auth.yetu.me)
3. Send the access token to a resource-server API to retrieve information or make requests.

#### 1. Obtain OAuth 2.0 credentials

Before you can use these API calls, you need to register your app with the following information:

* `client_id`
* `client_secret`
* list of allowed `scope` this app can request
    * example: `basic`
    * example: `basic events`
* list of allowed `redirect_uri`s (where the browser should be redirected to after contacting the yetu auth server)
    * **Important: The redirect URI must match exactly, the base domain is not sufficient.**
    * example: `http://localhost:8080/authenticate/yetu`
    * example: `http://home.yetu.me/authenticate/yetu`

Please contact `dev-support@yetu.com`

#### 2. Obtain an access token from the yetu OAuth2 Provider/Authorization Server

The easiest way is to use a library, see *Client libraries* under [oauth2.net](http://oauth.net/2/)

If you're using Scala and the Play framework, you can use our [yetu-play-authenticator](https://github.com/yetu/yetu-play-authenticator), which is a thin wrapper with the right settings around [Mohiva Silhouette](http://silhouette.mohiva.com/)

In any case, you will need to provide the following settings:

```python
authorizationURL = "https://auth.yetu.me/oauth2/authorize"
accessTokenURL = "https://auth.yetu.me/oauth2/access_token"
profileURL = "https://auth.yetu.me/oauth2/info"

scope = "" # space separated, as registered with yetu
clientId = "" # as registered with yetu
clientSecret = "" # as registered with yetu
redirectURL = "" # full URL including protocol, port, and url path as registered with yetu

```

## 3. Send the access token to a resource-server API to retrieve information or make requests

For example you can retrieve profile information: (usually your OAuth2 library already does this however)

```
https://auth.yetu.me/oauth2/info?access_token=YOUR_PREVIOUSLY_RETRIEVED_ACCESS_TOKEN
```

Please see the respective API documentation you wish to make use of. Some may accept query parameters, others require the access token to be specified in the http headers.


## Authorization Code flow

This section should only be necessary if you need to implement your own oauth2 flow and cannot make use of an existing library.

See also [this blog post](labs.hybris.com/2012/06/01/oauth2-authorization-code-flow/) for more information about OAuth2 and the authorization code flow.

### Part one: get an auth_code (done by browser)

Request:

* `GET /oauth2/authorize`
* the query parameters must include:
    - `scope`
    - `redirect_uri` (where you want to be redirected to; make sure this parameter is urlencoded)
    - `client_id`
    - `response_type=code`
    - `state` (any random string)

Response:

* success will be a 303 redirect, and the headers will include "Location: ...&code=<AUTH_CODE>&...". This "AUTH_CODE" can be used by the next request (done from server side)
* 400 is given if the request is unsuccessful.

Example request:

* cURL syntax (requires you to have a session cookie)
   - `curl -s -i --cookie "id=$id" "https://auth.${domain}/oauth2/authorize?scope=${scope}&client_id=${client_id}&redirect_uri=${redirect_uri}&response_type=code&state=7c63796d-3ef4-4add-8282-f47004430ebc"`

### Part two: Get an access token, if you have an auth code (done by app server side):

* `POST /oauth2/access_token`
* the body must include, form-url-encoded (JSON is currently NOT supported), these fields (or write them as query parameters as part of the url):
    - `auth_code`
    - `redirect_uri`
    - `client_id`
    - `client_secret`
    - `grant_type=authorization_code`

Example Request:

* cURL syntax:
    - `curl -s -i -d '@/dev/null' "https://auth.${domain}/oauth2/access_token?grant_type=authorization_code&client_id=${client_id}&client_secret=${client_secret}&redirect_uri=${redirect_uri}&code=${auth_code}"`


## Implicit Grant Flow
For implicit grant flow to work, user must be logged in ouath2provider server.

The client initiates the flow by directing the resource owner’s user-agent to the authorization endpoint(`/oauth2/access_token_implicit`). parameters required:

    - `redirect_uri`
    - `client_id`
    - `response_type=token`
    - `scope`
    - `state` (any random string)

to which the authorization server will send the user-agent back once access is granted (or denied).

The authorization server authenticates the resource owner (via the user-agent) and establishes whether the resource owner grants or denies the client’s access request.

Assuming the resource owner grants access, the authorization server redirects the user-agent back to the client using the redirection URI provided earlier. The redirection URI includes the access token in the URI fragment.


## Resource Owner Password flow

See also [this blog post](http://labs.hybris.com/2012/06/11/oauth2-resource-owner-password-flow/) for more information about OAuth2 and the resource owner flow.


* `POST /oauth2/access_token`
* the body must include, form-url-encoded (JSON is currently NOT supported), these fields:
    - `username`
    - `password`
    - `client_id`
    - `client_secret`
    - `grant_type=password`

Example request:

* cURL syntax:
    - `curl -s -i -d "username=${username}" -d "password=${password}" -d "client_id=${client_id}" -d "client_secret=${client_secret}" -d "grant_type=password" "https://auth.${domain}/oauth2/access_token"`



## Resource server API

Request:

* `GET /oauth2/info?access_token=....`

Response:

* success will be a 200 with a json object. The minimum object to expect is:

scope `id` (default scope):

```json
{
    "userId": "2d117536-aa04-497f-ac70-3bfdcf2d9e32"
}
```
but this can include other fields, depending on the scope:

scope `basic`:

```json
{
    "email": "john.smith@johnsmithexample.com",
    "firstName": "John",
    "lastName": "Smith",
    "userId": "2d117536-aa04-497f-ac70-3bfdcf2d9e32"
}
```

Example Request:

* HTTPie syntax:
    - `http "https://auth.yetu.me/oauth2/info?access_token=nDFPRcNPCHhdk3APVp85JR4yh6lYtxeV"`

