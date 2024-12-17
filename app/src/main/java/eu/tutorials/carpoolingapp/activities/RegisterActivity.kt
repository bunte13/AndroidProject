package eu.tutorials.carpoolingapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import eu.tutorials.carpoolingapp.R
import eu.tutorials.carpoolingapp.data.DatabaseHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var rgUserType: RadioGroup
    private lateinit var btnRegister: Button

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        etName = findViewById(R.id.et_name_register)
        etEmail = findViewById(R.id.et_email_register)
        etPassword = findViewById(R.id.et_password_register)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        rgUserType = findViewById(R.id.rg_user_type)
        btnRegister = findViewById(R.id.btn_register_user)

        dbHelper = DatabaseHelper(this)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val userType =
                if (rgUserType.checkedRadioButtonId == R.id.rb_driver) "Driver" else "Passenger"

            if (name.isNotEmpty() && email.isNotEmpty() && password == confirmPassword) {
                registerUser(name, email, password, userType)
            } else {
                Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String, userType: String) {
        val result = dbHelper.addUser(email, password, name, userType)
        if (result != -1L) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
