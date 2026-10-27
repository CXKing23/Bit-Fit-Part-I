package com.example.bitfit

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate
import java.time.YearMonth

/** Stores one water-intake total per calendar day on the device. */
class WaterDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE $TABLE_ENTRIES (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_DATE TEXT NOT NULL UNIQUE,
            $COLUMN_OUNCES INTEGER NOT NULL CHECK ($COLUMN_OUNCES > 0),
            $COLUMN_IS_DEMO INTEGER NOT NULL DEFAULT 0
        )""".trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_ENTRIES ADD COLUMN $COLUMN_IS_DEMO INTEGER NOT NULL DEFAULT 0")
        }
    }

    fun saveEntry(date: String, ounces: Int, isDemo: Boolean = false) {
        val values = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_OUNCES, ounces)
            put(COLUMN_IS_DEMO, if (isDemo) 1 else 0)
        }
        writableDatabase.insertWithOnConflict(TABLE_ENTRIES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getEntriesForMonth(month: YearMonth): List<WaterEntry> {
        val start = month.atDay(1).toString()
        val end = month.atEndOfMonth().toString()
        val entries = mutableListOf<WaterEntry>()
        readableDatabase.query(
            TABLE_ENTRIES,
            arrayOf(COLUMN_ID, COLUMN_DATE, COLUMN_OUNCES, COLUMN_IS_DEMO),
            "$COLUMN_DATE BETWEEN ? AND ?", arrayOf(start, end), null, null,
            "$COLUMN_DATE DESC"
        ).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
            val dateIndex = cursor.getColumnIndexOrThrow(COLUMN_DATE)
            val ouncesIndex = cursor.getColumnIndexOrThrow(COLUMN_OUNCES)
            val demoIndex = cursor.getColumnIndexOrThrow(COLUMN_IS_DEMO)
            while (cursor.moveToNext()) {
                entries += WaterEntry(
                    cursor.getLong(idIndex), cursor.getString(dateIndex), cursor.getInt(ouncesIndex),
                    cursor.getInt(demoIndex) == 1
                )
            }
        }
        return entries
    }

    fun getEntry(date: String): WaterEntry? {
        readableDatabase.query(
            TABLE_ENTRIES,
            arrayOf(COLUMN_ID, COLUMN_DATE, COLUMN_OUNCES, COLUMN_IS_DEMO),
            "$COLUMN_DATE = ?", arrayOf(date), null, null, null
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null
            return WaterEntry(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_OUNCES)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_DEMO)) == 1
            )
        }
    }

    /** Adds a month of clearly marked sample values on first launch, leaving existing logs intact. */
    fun seedDemoMonthIfEmpty() {
        val count = readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_ENTRIES", null).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
        if (count != 0) return

        val currentMonth = YearMonth.now()
        val lastDay = minOf(LocalDate.now().dayOfMonth, currentMonth.lengthOfMonth())
        for (day in 1..lastDay) {
            // A repeatable, gently varying pattern makes the monthly chart useful in the demo.
            val ounces = 48 + ((day * 11 + day / 2) % 31)
            saveEntry(currentMonth.atDay(day).toString(), ounces, isDemo = true)
        }
    }

    companion object {
        private const val DATABASE_NAME = "bitfit.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_ENTRIES = "water_entries"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DATE = "entry_date"
        private const val COLUMN_OUNCES = "ounces"
        private const val COLUMN_IS_DEMO = "is_demo"
    }
}

data class WaterEntry(val id: Long, val date: String, val ounces: Int, val isDemo: Boolean)
