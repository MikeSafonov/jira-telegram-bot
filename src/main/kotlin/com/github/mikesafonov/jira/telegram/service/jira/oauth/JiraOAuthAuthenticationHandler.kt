package com.github.mikesafonov.jira.telegram.service.jira.oauth

import com.atlassian.httpclient.api.Request
import com.atlassian.jira.rest.client.api.AuthenticationHandler
import com.google.api.client.auth.oauth.OAuthParameters
import com.google.api.client.http.GenericUrl

/**
 * This class add __Authorization__ header for request authenticated by Jira oauth
 * @author Mike Safonov
 */
class JiraOAuthAuthenticationHandler(private val oAuthParameters: OAuthParameters) : AuthenticationHandler {

    override fun configure(builder: Request.Builder?) {
        if (builder != null) {
            val request = builder.build()
            val methodName = request.method.name
            val genericUrl = GenericUrl(request.uri)
            builder.setHeader("Authorization", computeOAuthHeader(methodName, genericUrl))
        }
    }

    private fun computeOAuthHeader(methodName: String, genericUrl : GenericUrl) : String{
        oAuthParameters.computeNonce()
        oAuthParameters.computeTimestamp()
        oAuthParameters.computeSignature(methodName, genericUrl)
        return oAuthParameters.authorizationHeader
    }
}