package com.STEP_UP_Project.Kms

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.cardview.widget.CardView
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import com.STEP_UP_Project.Kms.R
import com.STEP_UP_Project.Kms.RoutineDetailActivity
import com.STEP_UP_Project.Kms.SettingsActivity
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_home)

        val Profilebtn : ImageButton = findViewById(R.id.btnProfile)
        // 툴바 설정
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu) // 커스텀 아이콘 설정

        // ActionBar 타이틀 제거
        supportActionBar?.setDisplayShowTitleEnabled(false)

        Profilebtn.setOnClickListener{
            val intent = Intent(this,MypageActivity::class.java)
            startActivity(intent)
            finish()
        }

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

        // DrawerLayout 설정
        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.black) // 원하는 색상 설정

/*
        // 드롭다운 메뉴 설정
        val menuIcon = findViewById<ImageView>(R.id.ivMenu)
        menuIcon.setOnClickListener {
            showPopupMenu(menuIcon)
        }
*/

        // 새 루틴 추가 버튼 클릭 이벤트
        findViewById<Button>(R.id.btnAddRoutine).setOnClickListener {
            startActivity(Intent(this, AddRoutineActivity::class.java))
        }

        // 루틴 1 카드 클릭 이벤트
        findViewById<CardView>(R.id.cardRoutine1).setOnClickListener {
            // Intent를 사용하여 HomeActivity로 이동
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        // 루틴 2 카드 클릭 이벤트
        findViewById<CardView>(R.id.cardRoutine2).setOnClickListener {
            val intent = Intent(this, RoutineDetailActivity::class.java)
            intent.putExtra("routineId", 2)
            startActivity(intent)
        }
        // OnBackPressedDispatcher 설정
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            private var backPressedTime: Long = 0
            private lateinit var backToast: Toast

            override fun handleOnBackPressed() {
                val currentTime = System.currentTimeMillis()

                // 2초 이내에 뒤로가기를 두 번 누른 경우 종료
                if (currentTime - backPressedTime < 2000) {
                    backToast.cancel()
                    isEnabled = false // 기본 뒤로가기 동작 활성화
                    onBackPressedDispatcher.onBackPressed() // 앱 종료
                } else {
                    // "한번 더 누르면 종료됩니다." 메시지 표시
                    backToast = Toast.makeText(this@HomeActivity, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT)
                    backToast.show()
                    backPressedTime = currentTime
                }
            }
        })
        setupBackPressedHandling()
    }

    private fun showPopupMenu(anchor: ImageView) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.menu_dropdown, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.menu_mypage -> {
                    val intent = Intent(this, MypageActivity::class.java)
                    startActivity(intent)
                    finish()
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

    // OnBackPressedDispatcher를 사용하여 뒤로가기 처리
    private fun setupBackPressedHandling() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START) // 네비게이션 닫기
                } else {
                    finish() // 액티비티 종료
                }
            }
        })
    }
}
