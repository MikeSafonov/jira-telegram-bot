package com.github.mikesafonov.jira.telegram.service.jira.oauth

data class JiraTempTokenAndAuthorizeUrl(val token : String, val secret : String, val url : String)