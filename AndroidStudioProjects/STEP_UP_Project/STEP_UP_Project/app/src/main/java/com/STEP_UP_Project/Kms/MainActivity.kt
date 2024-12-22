package com.STEP_UP_Project.Kms

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.STEP_UP_Project.Kms.MainActivity
import com.STEP_UP_Project.Kms.SignupActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_main)

        // EditText와 Button 참조
        val etID = findViewById<EditText>(R.id.etID)
        val etPD = findViewById<EditText>(R.id.etPD)
        val signInButton = findViewById<Button>(R.id.signin)
        val signUpButton = findViewById<Button>(R.id.signup)
        val htmlText = "<font color='#000000'>DONT'T HAVE ACCOUNT?</font> <font color='#00CCCC'>SIGNUP</font>"
        signUpButton.text = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // SignUp 버튼 클릭 시 SignupActivity로 이동
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // 로그인 버튼 클릭 시 SharedPreferences에서 ID와 비밀번호 확인
        signInButton.setOnClickListener {
            val inputID = etID.text.toString()
            val inputPassword = etPD.text.toString()

            if (inputID.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both ID and Password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // SharedPreferences에서 저장된 값 가져오기
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val savedID = sharedPreferences.getString("userID", null)
            val savedPassword = sharedPreferences.getString("userpassword", null)

            if (inputID == savedID && inputPassword == savedPassword) {
                Toast.makeText(this, "$savedID 님, 환영합니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 확인 해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
