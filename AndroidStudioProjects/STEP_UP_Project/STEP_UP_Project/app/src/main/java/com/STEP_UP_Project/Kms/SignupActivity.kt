package com.STEP_UP_Project.Kms

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.STEP_UP_Project.Kms.MainActivity

class SignupActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_signup)

        // EditText와 Button 참조
        val etName = findViewById<EditText>(R.id.etname)
        val etID = findViewById<EditText>(R.id.etID_sup)
        val etEmail = findViewById<EditText>(R.id.etemail)
        val etPassword = findViewById<EditText>(R.id.etPD_sup)
        val etRePassword = findViewById<EditText>(R.id.etrePD_sup)
        val signInButton = findViewById<Button>(R.id.signin_sup)
        val backToSignInButton = findViewById<Button>(R.id.backtosignin)

        val htmlText = "<font color='#000000'>ALREADY HAVE ACCOUNT?</font> <font color='#00CCCC'>SIGNIN</font>"
        backToSignInButton.text = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)


        signInButton.setOnClickListener {
            val name = etName.text.toString()
            val id = etID.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val rePassword = etRePassword.text.toString()

            if (name.isEmpty() || id.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != rePassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()

            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("username",name)//이름
            editor.putString("userID", id) // ID
            editor.putString("useremail", email)//메일
            editor.putString("userpassword",password)
            editor.apply()

            finish()
        }

        // MainActivity로 돌아가기
        backToSignInButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}