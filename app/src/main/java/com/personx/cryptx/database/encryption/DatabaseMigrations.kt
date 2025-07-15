package com.personx.cryptx.database.encryption

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1,2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS key_pairs (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                publicKey TEXT NOT NULL,
                privateKey TEXT NOT NULL,
                timestamp INTEGER NOT NULL
            )
        """.trimIndent())
    }
}