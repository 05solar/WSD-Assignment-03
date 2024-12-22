package com.STEP_UP_Project.Kms

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class CalendarActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.layout_calander)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets
        }
        val AlarmButton : ImageButton = findViewById(R.id.alarmbutton)
        val RecordButton : ImageButton = findViewById(R.id.recordbutton)
        val HomeButton : ImageButton = findViewById(R.id.homebutton)
        val MenuButton : ImageButton = findViewById(R.id.menubutton2)
        val sharedPreferences = getSharedPreferences("AlarmPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendar_view)

        // 색상 설정 (진행도에 맞는 색상)
        val emeraldColor = ContextCompat.getColor(this, R.color.emerald)
        val beigeColor = ContextCompat.getColor(this, R.color.beige)

        // 완료된 날짜들
        val completedDates: List<CalendarDay> = fetchRoutineData()
        calendarView.addDecorator(RoutineDecorator(this, completedDates, emeraldColor, 0.4f)) // 70% 진행

        // 미완료 날짜들
        val incompleteDates: List<CalendarDay> = listOf(CalendarDay.from(2024, 12, 26)) // 40% 진행
        calendarView.addDecorator(RoutineDecorator(this, incompleteDates, beigeColor, 0.4f))

        // 아직 시작하지 않은 날짜들
        val notStartedDates: List<CalendarDay> = listOf(CalendarDay.from(2024, 12, 24)) // 10% 진행
        calendarView.addDecorator(RoutineDecorator(this, notStartedDates, Color.RED, 0.4f))

        // 날짜 선택 기능 비활성화
        calendarView.selectedDate = null // 날짜 선택 초기화
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE) // 날짜 선택 비활성화
        val mypagebutton : ImageButton = findViewById(R.id.MyPageButton)
        mypagebutton.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java)
            startActivity(intent)
            finish()
        }
        AlarmButton.setOnClickListener { //알람 페이지 이동
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
            finish()
        }
        RecordButton.setOnClickListener{ //루틴 상세보기 1번 페이지 이동
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
            finish()

        }
        HomeButton.setOnClickListener{ // 홈 페이지 이동
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        drawerLayout = findViewById(R.id.main)

        // 메뉴 버튼 클릭 시 드로어 열기
        MenuButton.setOnClickListener {
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

    private fun fetchRoutineData(): List<CalendarDay> {
        return listOf(
                CalendarDay.from(2024, 12, 21),
                CalendarDay.from(2024,12,25)
        )
    }
}

class RoutineDecorator(
    private val context: Context,
    private val dates: List<CalendarDay>,
    private val color: Int,
    private val alpha: Float // 투명도
) : DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        // 투명도를 적용한 원을 BackgroundSpan으로 표시
        val adjustedColor = adjustAlpha(color, alpha)
        view.addSpan(CustomBackgroundSpan(adjustedColor))
    }

    // 투명도 조정 함수
    private fun adjustAlpha(color: Int, alpha: Float): Int {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alphaInt = (255 * alpha).toInt()
        return Color.argb(alphaInt, red, green, blue)
    }
}
class CustomBackgroundSpan(private val color: Int) : DotSpan(0f, color) {
    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        charSequence: CharSequence,
        start: Int,
        end: Int,
        lineNum: Int
    ) {
        val paint = Paint()
        paint.color = color
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true

        val centerX = (left + right) / 2f
        val centerY = (top + bottom) / 2f

        // 여기서 원을 그립니다
        val radius = 30f // 원의 크기 (반지름)
        canvas.drawCircle(centerX, centerY, radius, paint)
    }
}