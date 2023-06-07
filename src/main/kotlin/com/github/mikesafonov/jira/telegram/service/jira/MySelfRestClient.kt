package com.github.mikesafonov.jira.telegram.service.jira

import com.atlassian.httpclient.api.HttpClient
import com.atlassian.jira.rest.client.api.domain.User
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient
import com.atlassian.jira.rest.client.internal.json.UserJsonParser
import io.atlassian.util.concurrent.Promise
import java.net.URI
import javax.ws.rs.core.UriBuilder


class MySelfRestClient(httpClient: HttpClient, serverUri: URI) : AbstractAsynchronousRestClient(httpClient) {

    private val baseUri: URI
    private val userJsonParser = UserJsonParser()

    init {
        baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build()
    }

    fun getMySelf(): Promise<User> {
        val userUri = UriBuilder.fromUri(baseUri).path("myself").build()
        return getAndParse(userUri, userJsonParser)
    }
}
