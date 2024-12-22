package com.STEP_UP_Project.Kms

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.STEP_UP_Project.Kms.R
import com.STEP_UP_Project.Kms.HomeActivity
import com.STEP_UP_Project.Kms.SettingsActivity
import com.google.android.material.navigation.NavigationView

class DetailActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_detail) // 연결된 레이아웃 파일

        val alarmbutton : ImageButton = findViewById(R.id.ivAlarm)
        val calendarbutton : ImageButton = findViewById(R.id.ivCalendar)
        val homebutton : ImageButton = findViewById(R.id.ivHome)
        val mypagebutton : ImageButton = findViewById(R.id.ivProfile)
        // 원형 ProgressBar에 애니메이션 적용
        val progressCircle = findViewById<ProgressBar>(R.id.progressCircle)
        animateProgress(progressCircle, 70) // 목표값 설정

        // 하단 Home 아이콘 클릭 이벤트
        mypagebutton.setOnClickListener{
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
            finish()
        }
        alarmbutton.setOnClickListener{
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
            finish()
        }
        calendarbutton.setOnClickListener{
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
            finish()
        }

        homebutton.setOnClickListener {
            // MainActivity로 이동
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // HomeActivity 종료 (필요에 따라 추가)
        }

        // 드롭다운 메뉴 설정
        val menuIcon = findViewById<ImageView>(R.id.ivMenu)
        drawerLayout = findViewById(R.id.main)

        // 메뉴 버튼 클릭 시 드로어 열기
        menuIcon.setOnClickListener {
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


    }

    private fun animateProgress(progressBar: ProgressBar, targetProgress: Int) {
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, targetProgress)
        animator.duration = 1000 // 1초 동안 애니메이션
        animator.start()
    }

    private fun showPopupMenu(anchor: ImageView) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_dropdown, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    Toast.makeText(this, "home selected", Toast.LENGTH_SHORT).show()
                    // SettingsActivity로 이동 (예시)
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_mypage -> {
                    Toast.makeText(this, "mypage selected", Toast.LENGTH_SHORT).show()
                    // AboutActivity로 이동 (예시)
                    val intent = Intent(this, MypageActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_alarm -> {
                    val intent = Intent(this, AlarmActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.menu_calendar -> {
                    val intent = Intent(this, CalendarActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
}
