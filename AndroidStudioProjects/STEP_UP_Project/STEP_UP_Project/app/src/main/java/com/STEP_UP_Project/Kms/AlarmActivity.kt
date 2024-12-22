    package com.STEP_UP_Project.Kms

    import android.annotation.SuppressLint
    import android.app.AlarmManager
    import android.app.PendingIntent
    import android.content.Context
    import android.content.Intent
    import android.content.SharedPreferences
    import android.os.Bundle
    import android.util.Log
    import android.view.MenuInflater
    import android.view.MenuItem
    import android.view.View
    import android.widget.Button
    import android.widget.ImageButton
    import android.widget.PopupMenu
    import android.widget.Switch
    import android.widget.TextView
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.constraintlayout.widget.ConstraintLayout
    import androidx.core.view.GravityCompat
    import androidx.drawerlayout.widget.DrawerLayout
    import com.STEP_UP_Project.Kms.AddalarmActivity
    import com.STEP_UP_Project.Kms.R
    import com.google.android.material.navigation.NavigationView
    import java.util.Calendar

    class AlarmActivity : AppCompatActivity() {
        private var id = 0  // 클래스 레벨에서 선언하여 값을 유지
        private lateinit var alarmManager: AlarmManager
        private lateinit var drawerLayout: DrawerLayout

        @SuppressLint("MissingInflatedId", "CutPasteId")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.layout_alarm)
            enableEdgeToEdge()

            alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val sharedPreferences = getSharedPreferences("AlarmPreferences", MODE_PRIVATE)
            id = sharedPreferences.getInt("current_id", 0) // 'current_id' 키로 저장된 id를 가져옵니다.
            if (id > 5) {
                Toast.makeText(this,"알람은 최대 5개까지 설정 가능합니다!",Toast.LENGTH_SHORT).show()
            }
            val rootView = findViewById<View>(R.id.main) // 루트 레이아웃 ID
            if (rootView == null) {
                Log.e("MainActivity", "Root view is null! 레이아웃이 로드되지 않았습니다.")
            } else {
                Log.d("MainActivity", "Root view 로드 완료")
            }

            val AddButton : ImageButton = findViewById(R.id.AddButton)
            val menuButton : ImageButton = findViewById(R.id.menubutton)
            val mypageButton : ImageButton = findViewById(R.id.MyPageButton)
            val editor = sharedPreferences.edit()
            // val calendarButton : ImageButton = findViewById(R.id.calendarButton)
            if(sharedPreferences.getInt("save_id", 0) != 0) {
                id = sharedPreferences.getInt("save_id",0)
                Log.d("MainActivity", "save_id:$id")
                editor.remove("save_id")
            }
            for(i in 1..id) {
                var a = id
                var hour = sharedPreferences.getInt("hour_$i", -1)  // ID별 시간 정보 가져오기
                var minute = sharedPreferences.getInt("minute_$i", -1)  // ID별 분 정보 가져오기
                var selectedDays = sharedPreferences.getStringSet("selectedDays_$i", emptySet())
                    ?: emptySet()  // ID별 요일 정보 가져오기
                val textView = findViewById<TextView>(R.id.textView)
                val daysOfWeek = mapOf(
                    0 to "일",
                    1 to "월",
                    2 to "화",
                    3 to "수",
                    4 to "목",
                    5 to "금",
                    6 to "토"
                )
                fun formatTime(hour: Int, minute: Int): String {
                    // 12시간 형식으로 변환
                    val isPM = hour >= 12
                    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                    val amPm = if (isPM) "PM" else "AM"

                    // 분을 두 자리 수로 포맷팅
                    val displayMinute = String.format("%02d", minute)

                    return "$amPm $displayHour:$displayMinute"
                }
                if (i == 1) {
                    val btn1: Button = findViewById(R.id.button1)
                    val alarmSwitch1: Switch = findViewById(R.id.switch1)
                    val editTextView = findViewById<TextView>(R.id.editTextTime)
                    Log.d(
                        "MainActivity",
                        "받은 데이터: hour_$i:$hour, minute_$i:$minute, selectedDays_${selectedDays.map { it.toString() }}"
                    )
                    val formattedTime = formatTime(hour, minute)
                    //textView.text = selectedDays.joinToString(", ") { daysOfWeek[it] ?: "Unknown" }

                    val isAlarmOn1 = sharedPreferences.getBoolean("alarm_on_1", true) // 기본값 false
                    alarmSwitch1.isChecked = isAlarmOn1

                    editTextView.text = "$formattedTime"
                    btn1.setOnClickListener {
                        editor.putInt("save_id", id)
                        editor.putInt("current_id", i)
                        editor.apply()
                        val intent = Intent(this, AddalarmActivity::class.java)
                        startActivity(intent)
                    }
                    alarmSwitch1.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {

                        }
                    }
                }
                if (i == 2) {
                    val btn2: Button = findViewById(R.id.button2)
                    val editTextView2 = findViewById<TextView>(R.id.editTextTime2)
                    val formattedTime2 = formatTime(hour, minute)
                    Log.d(
                        "MainActivity",
                        "받은 데이터: hour_$i:$hour, minute_$i:$minute, selectedDays_${selectedDays.map { it.toString() }}"
                    )
                    editTextView2.text = "$formattedTime2"
                    btn2.setOnClickListener {
                        editor.putInt("save_id", id)
                        editor.putInt("current_id", i)
                        editor.apply()
                        val intent = Intent(this, AddalarmActivity::class.java)
                        startActivity(intent)
                    }
                }
                if (i == 3) {
                    val btn3: Button = findViewById(R.id.button3)
                    val editTextView3 = findViewById<TextView>(R.id.editTextTime3)
                    val formattedTime3 = formatTime(hour, minute)
                    Log.d(
                        "MainActivity",
                        "받은 데이터: hour_$i:$hour, minute_$i:$minute, selectedDays_${selectedDays.map { it.toString() }}"
                    )
                    editTextView3.text = "$formattedTime3"
                    btn3.setOnClickListener {
                        editor.putInt("save_id", id)
                        editor.putInt("current_id", i)
                        editor.apply()
                        val intent = Intent(this, AddalarmActivity::class.java)
                        startActivity(intent)
                    }
                }
                if (i == 4) {
                    val btn4: Button = findViewById(R.id.button4)
                    val editTextView4 = findViewById<TextView>(R.id.editTextTime4)
                    val formattedTime4 = formatTime(hour, minute)
                    Log.d(
                        "MainActivity",
                        "받은 데이터: hour_$i:$hour, minute_$i:$minute, selectedDays_${selectedDays.map { it.toString() }}"
                    )
                    editTextView4.text = "$formattedTime4"
                    btn4.setOnClickListener {
                        editor.putInt("save_id", id)
                        editor.putInt("current_id", i)
                        editor.apply()
                        val intent = Intent(this, AddalarmActivity::class.java)
                        startActivity(intent)
                    }
                }
                if (i == 5) {
                    val btn5: Button = findViewById(R.id.button5)
                    val editTextView5 = findViewById<TextView>(R.id.editTextTime5)
                    val formattedTime5 = formatTime(hour, minute)
                    Log.d(
                        "MainActivity",
                        "받은 데이터: hour_$i:$hour, minute_$i:$minute, selectedDays_${selectedDays.map { it.toString() }}"
                    )
                    editTextView5.text = "$formattedTime5"
                    btn5.setOnClickListener {
                        editor.putInt("save_id", id)
                        editor.putInt("current_id", i)
                        editor.apply()
                        val intent = Intent(this, AddalarmActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
            drawerLayout = findViewById(R.id.main)

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
            AddButton.setOnClickListener {
                if (id >= 5) {
                    Toast.makeText(this,"알람은 최대 5개까지 설정 가능합니다!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AlarmActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    id++
                    editor.putInt("current_id", id)
                    editor.apply()
                    val intent = Intent(this, AddalarmActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            mypageButton.setOnClickListener{
                val intent = Intent(this, MypageActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        // 알람 설정 함수
        @SuppressLint("ScheduleExactAlarm")
        fun setAlarm(context: Context, id: Int, hour: Int, minute: Int, selectedDays: List<Int>) {
            // 알람 시간 설정
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)

                // 설정 시간이 현재 시간보다 이전이면 다음 날로 설정
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

            // 알람 설정
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("ID", id)
                putExtra("hour", hour)
                putExtra("minute", minute)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

            // SharedPreferences에 저장
            val sharedPreferences = context.getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            // 알람 시간과 선택된 요일을 SharedPreferences에 저장
            editor.putInt("hour_$id", hour)
            editor.putInt("minute_$id", minute)
            editor.putStringSet("selectedDays_$id", selectedDays.map { it.toString() }.toSet())
            editor.apply()

         }

        // 알람 취소 함수
        private fun cancelAlarm(alarmId: Int) {
            val intent = Intent(this, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                alarmId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }
    }