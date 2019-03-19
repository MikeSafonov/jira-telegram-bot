package com.github.mikesafonov.jira.telegram.service.jira.oauth

import com.google.api.client.auth.oauth.OAuthGetTemporaryToken

class JiraOAuthGetTemporaryToken(authorizationUrl: String) : OAuthGetTemporaryToken(authorizationUrl) {
    init {
        usePost = true
    }
}