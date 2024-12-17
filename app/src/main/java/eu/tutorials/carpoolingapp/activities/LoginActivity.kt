package eu.tutorials.carpoolingapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import eu.tutorials.carpoolingapp.R
import eu.tutorials.carpoolingapp.data.DatabaseHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        etEmail = findViewById(R.id.et_email_login)
        etPassword = findViewById(R.id.et_password_login)
        btnLogin = findViewById(R.id.btn_login_user)
        btnRegister = findViewById(R.id.btn_register_user)

        dbHelper = DatabaseHelper(this)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                handleLogin(email, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    @SuppressLint("Range")
    private fun handleLogin(email: String, password: String) {
        val cursor = dbHelper.getUserByEmail(email)
        if (cursor != null && cursor.moveToFirst()) {
            val storedPassword = cursor.getString(cursor.getColumnIndex("password"))
            val userType = cursor.getString(cursor.getColumnIndex("user_type"))

            if (storedPassword == password) {
                saveLoggedInUser(email)
                navigateToDashboard(email, userType)
            } else {
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No account found with this email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLoggedInUser(email: String) {
        val sharedPreferences = getSharedPreferences("carpooling_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("current_user_email", email)
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }

    private fun navigateToDashboard(email: String, userType: String) {
        val intent =  Intent(this, RidePreferenceActivity::class.java)


        // Pass the email and userType as extras
        intent.putExtra("USER_EMAIL", email)  // Pass the user email
        intent.putExtra("USER_TYPE", userType) // Pass the user type (optional if needed)

        startActivity(intent)
        finish()
    }
}
