package com.github.mikesafonov.jira.telegram.service.jira

import com.google.api.client.auth.oauth.OAuthGetAccessToken

class JiraOAuthGetAccessToken : OAuthGetAccessToken {
    constructor(authorizationServerUrl : String) : super(authorizationServerUrl){
        usePost = true
    }
}