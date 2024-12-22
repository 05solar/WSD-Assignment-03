package com.STEP_UP_Project.Kms

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.STEP_UP_Project.Kms.MainActivity
import com.google.android.material.navigation.NavigationView

class MypageActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_mypage)

        // TextViews for user details
        val tvUserID = findViewById<TextView>(R.id.userid)
        val tvUseremail = findViewById<TextView>(R.id.useremail)
        val tvUsername = findViewById<TextView>(R.id.username) // Assuming you use this to show the nickname

        // Load saved data from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userID", "N/A")
        val useremail = sharedPreferences.getString("useremail", "N/A")
        val username = sharedPreferences.getString("username", "N/A")

        // Display user data
        tvUserID.text = "ID: $userId"
        tvUseremail.text = "Email: $useremail"
        tvUsername.text = "$username"

        // Buttons to navigate to other activities
        val logout = findViewById<Button>(R.id.logout)
        val gotoalarm = findViewById<ImageButton>(R.id.gotoalarm)
        val gotorecord = findViewById<ImageButton>(R.id.gotorecord)
        val gotocalendar = findViewById<ImageButton>(R.id.gotocalendar)
        val gotohome = findViewById<ImageButton>(R.id.gotohome)

        val menuButton: ImageButton = findViewById(R.id.menu)

        // 메뉴 버튼 클릭 이벤트
        gotoalarm.setOnClickListener{
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
            finish()
        }
        gotorecord.setOnClickListener{
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
            finish()
        }
        gotocalendar.setOnClickListener{
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
            finish()
        }
        gotohome.setOnClickListener{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        drawerLayout = findViewById(R.id.main_layout)

        // 메뉴 버튼 클릭 시 드로어 열기
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START) // 왼쪽에서 열기
            val navigationView : NavigationView = findViewById(R.id.navigation_view)
            navigationView.setNavigationItemSelectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_home -> {
                        // 홈 화면으로 이동
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        drawerLayout.closeDrawers() // 네비게이션 뷰 닫기
                        finish()
                        true
                    }
                    R.id.menu_mypage -> {
                        // 홈 화면으로 이동
                        val intent = Intent(this, MypageActivity::class.java)
                        startActivity(intent)
                        drawerLayout.closeDrawers() // 네비게이션 뷰 닫기
                        finish()
                        true
                    }
                    R.id.menu_alarm -> {
                        // 홈 화면으로 이동
                        val intent = Intent(this, AlarmActivity::class.java)
                        startActivity(intent)
                        drawerLayout.closeDrawers() // 네비게이션 뷰 닫기
                        finish()
                        true
                    }
                    R.id.menu_calendar -> {
                        // 홈 화면으로 이동
                        val intent = Intent(this, CalendarActivity::class.java)
                        startActivity(intent)
                        drawerLayout.closeDrawers() // 네비게이션 뷰 닫기
                        finish()
                        true
                    }
                    else -> false // 처리되지 않은 메뉴 아이템
                }
            }
        }
        logout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }
}