package net.tactware.worldweaver.dal.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import net.tactware.worldweaver.db.NimbusDb
import org.koin.core.annotation.Single
import java.io.File
import kotlin.invoke

/**
 * Provider for the SQLDelight database.
 * Initializes and provides access to the database.
 */
@Single
class DatabaseProvider {

    // The database instance
    private val database: NimbusDb

    init {
        // Create the database directory if it doesn't exist
        val databaseDir = File(System.getProperty("user.home"), ".worldweaver")
        if (!databaseDir.exists()) {
            databaseDir.mkdirs()
        }

        // Create the database file path
        val databasePath = File(databaseDir, "nimbus.db").absolutePath

        // Create the database driver
        val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")

        val dbFile = File(databasePath)
        // Create the schema if the database file doesn't exist, or migrate if it exists
        if (!dbFile.exists()) {
            NimbusDb.Schema.create(driver)
        } else {
            // Ensure schema is up-to-date by migrating
            try {
                // Try to migrate from the current version to the latest version
                // If this fails, we'll catch the exception and create the schema
                NimbusDb.Schema.migrate(driver, 0, NimbusDb.Schema.version)
            } catch (e: Exception) {
                // If migration fails, log the error and try to create the schema
                println("Migration failed: ${e.message}")
                try {
                    // Try to create the schema
                    NimbusDb.Schema.create(driver)
                } catch (e2: Exception) {
                    // If creating the schema also fails, log the error and rethrow
                    println("Failed to create schema: ${e2.message}")
                    throw e2
                }
            }
        }

        database = NimbusDb.invoke(driver)

        // Create the schema if it doesn't exist
        NimbusDb.Schema.create(driver)
    }

    /**
     * Get the database instance
     */
    fun getDatabase(): NimbusDb {
        return database
    }
}
