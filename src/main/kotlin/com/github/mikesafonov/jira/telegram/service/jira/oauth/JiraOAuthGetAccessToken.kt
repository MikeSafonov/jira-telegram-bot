package com.github.mikesafonov.jira.telegram.service.jira.oauth

import com.google.api.client.auth.oauth.OAuthGetAccessToken

class JiraOAuthGetAccessToken(authorizationServerUrl: String) : OAuthGetAccessToken(authorizationServerUrl) {
    init {
        usePost = true
    }
}