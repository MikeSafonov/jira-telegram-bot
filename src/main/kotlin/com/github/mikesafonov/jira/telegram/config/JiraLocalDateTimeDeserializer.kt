package com.github.mikesafonov.jira.telegram.config

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

/**
 * @author Mike Safonov
 */
@Component
class JiraLocalDateTimeDeserializer : LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"))