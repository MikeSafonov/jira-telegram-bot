package com.github.mikesafonov.jira.telegram.service.jira

import com.atlassian.jira.rest.client.api.domain.User
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient
import com.atlassian.jira.rest.client.internal.json.UserJsonParser
import io.atlassian.util.concurrent.Promise
import java.io.Closeable
import java.io.IOException
import java.net.URI
import javax.ws.rs.core.UriBuilder


class MySelfRestClient(private val httpClient: DisposableHttpClient, serverUri: URI) :
    AbstractAsynchronousRestClient(httpClient),
    Closeable {

    private val baseUri: URI
    private val userJsonParser = UserJsonParser()

    init {
        baseUri = UriBuilder.fromUri(serverUri).path("/rest/api/latest").build()
    }

    fun getMySelf(): Promise<User> {
        val userUri = UriBuilder.fromUri(baseUri).path("myself").build()
        return getAndParse(userUri, userJsonParser)
    }

    override fun close() {
        try {
            httpClient.destroy()
        } catch (e: Exception) {
            throw if (e is IOException) e else IOException(e)
        }
    }
}
