package com.yetu.oauth2provider.signature.models

import java.util.Date

import net.adamcin.httpsig.api.Authorization

case class SignedRequestHeaders(auth: Authorization, email: String, date: Date)