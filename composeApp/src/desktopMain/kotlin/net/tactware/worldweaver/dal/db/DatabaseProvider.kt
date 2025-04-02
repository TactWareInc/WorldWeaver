package net.tactware.worldweaver.dal.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import net.tactware.worldweaver.db.NimbusDb
import org.koin.core.annotation.Single
import java.io.File

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

        // Create the schema if it doesn't exist
        NimbusDb.Schema.create(driver)

        // Create the database instance
        database = NimbusDb(driver)
    }

    /**
     * Get the database instance
     */
    fun getDatabase(): NimbusDb {
        return database
    }
}
