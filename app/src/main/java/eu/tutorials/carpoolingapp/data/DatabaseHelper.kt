package eu.tutorials.carpoolingapp.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import eu.tutorials.carpoolingapp.models.Location

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "carpooling_app.db"
        private const val DATABASE_VERSION = 6
        private const val TABLE_NAME = "users"
        private const val COL_ID = "id"
        const val COL_EMAIL = "email"
        private const val COL_PASSWORD = "password"
        private const val COL_USER_TYPE = "user_type"
        private const val COL_IS_SEEKING_RIDE = "isSeekingRide"
        const val COL_ADDRESS = "address"
        const val COL_NAME = "name"
        private const val COL_RATING = "rating"
        private const val COL_CAR_TYPE = "car_type"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EMAIL TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_USER_TYPE TEXT NOT NULL DEFAULT 'Passenger',
                $COL_IS_SEEKING_RIDE INTEGER NOT NULL DEFAULT 0,
                $COL_ADDRESS TEXT,
                $COL_NAME TEXT NOT NULL,
                $COL_RATING REAL NOT NULL DEFAULT 0.0,
                $COL_CAR_TYPE TEXT
            )
        """
        db.execSQL(createUsersTableQuery)

        val createLocationsTableQuery = """
            CREATE TABLE IF NOT EXISTS passenger_locations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT NOT NULL,
                address TEXT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                FOREIGN KEY(email) REFERENCES $TABLE_NAME($COL_EMAIL) ON DELETE CASCADE
            )
        """
        db.execSQL(createLocationsTableQuery)

        val createRatingsTableQuery = """
            CREATE TABLE IF NOT EXISTS ratings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                driver_email TEXT NOT NULL,
                passenger_email TEXT NOT NULL,
                rating REAL NOT NULL,
                FOREIGN KEY(driver_email) REFERENCES $TABLE_NAME($COL_EMAIL),
                FOREIGN KEY(passenger_email) REFERENCES $TABLE_NAME($COL_EMAIL)
            )
        """
        db.execSQL(createRatingsTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_NAME TEXT NOT NULL DEFAULT 'Unknown'")
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_RATING REAL NOT NULL DEFAULT 0.0")
        }
    }

    fun addUser(email: String, password: String, name: String, userType: String = "Passenger", isSeekingRide: Boolean = false): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_EMAIL, email)
            put(COL_PASSWORD, password)
            put(COL_NAME, name)
            put(COL_USER_TYPE, userType)
            put(COL_IS_SEEKING_RIDE, if (isSeekingRide) 1 else 0)
            put(COL_RATING, 0.0f)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    @SuppressLint("Range")
    fun getAllDrivers(): List<String> {
        val drivers = mutableListOf<String>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COL_USER_TYPE = 'Driver'"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow(COL_NAME))
                drivers.add(name)
            }
        }
        return drivers
    }

    fun updateCarType(email: String, carType: String): Int {
        val values = ContentValues().apply {
            put(COL_CAR_TYPE, carType)
        }
        return writableDatabase.update(TABLE_NAME, values, "$COL_EMAIL=?", arrayOf(email))
    }


    @SuppressLint("Range")
    fun getDriverAverageRating(email: String): Float {
        val query = "SELECT AVG(rating) as avg_rating FROM ratings WHERE driver_email=?"
        val cursor = readableDatabase.rawQuery(query, arrayOf(email))

        cursor.use {
            return if (it.moveToFirst()) it.getFloat(it.getColumnIndex("avg_rating")) else 0f
        }
    }

    fun addRating(driverEmail: String, passengerEmail: String, rating: Float): Boolean {
        val values = ContentValues().apply {
            put("driver_email", driverEmail)
            put("passenger_email", passengerEmail)
            put("rating", rating)
        }
        return writableDatabase.insert("ratings", null, values) != -1L
    }

    @SuppressLint("Range")
    fun getPassengerAverageRating(passengerEmail: String): Float {
        val query = "SELECT AVG(rating) as avg_rating FROM ratings WHERE passenger_email=?"
        val cursor = readableDatabase.rawQuery(query, arrayOf(passengerEmail))

        cursor.use {
            return if (it.moveToFirst()) it.getFloat(it.getColumnIndex("avg_rating")) else 0f
        }
    }

    fun getUserByEmail(email: String): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_EMAIL=?", arrayOf(email))
    }

    @SuppressLint("Range")
    fun getUserTypeByEmail(email: String): String? {
        val query = "SELECT $COL_USER_TYPE FROM $TABLE_NAME WHERE $COL_EMAIL=?"
        val cursor = readableDatabase.rawQuery(query, arrayOf(email))

        cursor.use {
            return if (it.moveToFirst()) it.getString(it.getColumnIndex(COL_USER_TYPE)) else null
        }
    }

    @SuppressLint("Range")
    fun getCurrentUserEmail(): String? {
        val query = "SELECT $COL_EMAIL FROM $TABLE_NAME WHERE $COL_IS_SEEKING_RIDE=1 LIMIT 1"
        val cursor = readableDatabase.rawQuery(query, null)

        cursor.use {
            return if (it.moveToFirst()) it.getString(it.getColumnIndex(COL_EMAIL)) else null
        }
    }

    fun savePassengerLocation(email: String, location: Location): Boolean {
        val values = ContentValues().apply {
            put("email", email)
            put("address", location.address)
            put("latitude", location.latitude)
            put("longitude", location.longitude)
        }
        return writableDatabase.insert("passenger_locations", null, values) != -1L
    }

    fun updateRidePreference(email: String, isSeekingRide: Boolean): Int {
        val values = ContentValues().apply {
            put(COL_IS_SEEKING_RIDE, if (isSeekingRide) 1 else 0)
        }

        // Log the email and preference being updated
        Log.d("DatabaseHelper", "Updating ride preference for $email: isSeekingRide = $isSeekingRide")

        val result = writableDatabase.update(TABLE_NAME, values, "$COL_EMAIL=?", arrayOf(email))

        // Log the result
        Log.d("DatabaseHelper", "Rows affected: $result")

        return result
    }
    @SuppressLint("Range")
    fun getDrivers(): List<String> {
        val drivers = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.query(
            "users", // Assuming "users" table holds user data
            arrayOf("name"), // Assuming the "name" column stores the driver names
            "user_type = ?", // Filter by user type (assuming "user_type" column exists)
            arrayOf("Driver"), // Only fetch users with "Driver" type
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val driverName = cursor.getString(cursor.getColumnIndex("name"))
                drivers.add(driverName)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return drivers
    }

    fun getPassengersInNeed(): Cursor? {
        val query = """
            SELECT u.$COL_EMAIL, u.$COL_NAME, p.$COL_ADDRESS 
            FROM $TABLE_NAME u 
            JOIN passenger_locations p ON u.$COL_EMAIL = p.$COL_EMAIL 
            WHERE u.$COL_IS_SEEKING_RIDE = 1
        """
        return readableDatabase.rawQuery(query, null)
    }

}
