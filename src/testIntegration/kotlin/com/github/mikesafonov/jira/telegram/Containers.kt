package com.github.mikesafonov.jira.telegram

import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.PostgreSQLContainer

/**
 * @author Mike Safonov
 */

class MyPostgreSQLContainer(imageName: String) : PostgreSQLContainer<MyPostgreSQLContainer>(imageName)

class MyMySQLContainer (imageName : String) : MySQLContainer<MyMySQLContainer>(imageName)