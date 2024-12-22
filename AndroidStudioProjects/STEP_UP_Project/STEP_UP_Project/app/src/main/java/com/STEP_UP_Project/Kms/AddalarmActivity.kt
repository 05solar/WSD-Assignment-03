package com.STEP_UP_Project.Kms

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import java.util.Calendar

// BroadcastReceiver는 알람 수신 시 동작을 처리하는 클래스
// BroadcastReceiver를 상속받아 알람 발생 시 동작 정의
@SuppressLint("ScheduleExactAlarm")
class AlarmReceiver : BroadcastReceiver() {
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Notification_Channel_id"
            val channelName = "Alarm Notification"
            val descriptionText = "This channel is used for alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.getNotificationChannel(channelId) == null) {
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)
        Log.d("AlarmReceiver","알람리시버 접근 완료")
        // NotificationManager를 통해 알림을 생성 및 표시
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationManager = NotificationManagerCompat.from(context)
            // 인텐트에서 전달된 데이터를 가져옵니다.
            val sharedPreferences =
                context.getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
            val id = sharedPreferences.getInt("current_id", 0)
            val vibrate = sharedPreferences.getBoolean("vibrate_$id", false)
            val isFiveMinuteEnabled = sharedPreferences.getBoolean("5Route_$id",false)
            val hour = sharedPreferences.getInt("hour_$id", -1)     // 알람 발생 시간 (시)
            val minute = sharedPreferences.getInt("minute_$id", -1) // 알람 발생 시간 (분)

            if (vibrate) {
                // 진동 실행
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val vibrationEffect = VibrationEffect.createOneShot(
                        500, // 진동 시간
                        VibrationEffect.DEFAULT_AMPLITUDE // 진동 강도
                    )
                    vibrator.vibrate(vibrationEffect)
                } else {
                    vibrator.vibrate(500) // 진동 시간 (밀리초)
                }
            }

            // 알림 생성 및 표시
            val builder = NotificationCompat.Builder(context, "Notification_Channel_id")
                .setSmallIcon(R.drawable.logo)   // 알림 아이콘 설정
                .setContentTitle("STEP_UP")                  // 알림 제목
                .setContentText("설정된 알람 시간: $hour:$minute")  // 알림 내용
                .setPriority(NotificationCompat.PRIORITY_HIGH)  // 높은 우선순위
                .setAutoCancel(true)                        // 알림 클릭 시 자동 제거

            // 알림을 표시합니다.
            notificationManager.notify(id, builder.build())
            Log.d("AlarmReceiver", "Alarm triggered: $hour:$minute")
            if (isFiveMinuteEnabled) {
                val nextAlarmTime = System.currentTimeMillis() + 5 * 60 * 1000 // 5분 후

                val nextIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("ID", id)
                    putExtra("hour", hour)
                    putExtra("minute", minute)
                }
                val nextPendingIntent = PendingIntent.getBroadcast(
                    context,
                    id,
                    nextIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTime,
                    nextPendingIntent
                )
                Log.d("AlarmReceiver", "5분 주기 알람 예약됨 (ID: $id, 시간: ${nextAlarmTime})")
            }

            // 다음 알람 예약 (하루 후로 설정)
            val nextAlarmTime = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                add(Calendar.DAY_OF_MONTH, 1)  // 하루 후로 알람 예약
            }

            val nextIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("ID", id)
                putExtra("hour", hour)
                putExtra("minute", minute)
            }
            val nextPendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime.timeInMillis,
                nextPendingIntent
            )
            Log.d("AlarmReceiver", "Next alarm set for ${nextAlarmTime.time}")
        }
    }
}

class AddalarmActivity : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager

    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.layout_addalarm)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets
        }

        // 시간 선택을 위한 TimePicker 초기화
        val timePicker = findViewById<TimePicker>(R.id.timepicker)
        timePicker.setIs24HourView(false)  // 12시간 형식으로 시간 표시
        val sharedPreferences = getSharedPreferences("AlarmPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val id = sharedPreferences.getInt("current_id", -1)  // 기본값 -1로 설정
        if (id == -1) {
            Log.e("MainActivity2", "SharedPreferences에서 current_id를 찾을 수 없습니다.")
        } else {
            Log.d("MainActivity2", "SharedPreferences에서 가져온 id: $id")
        }

        // 알람 매니저 초기화
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 버튼과 스위치 초기화
        val Completebutton: Button = findViewById<Button>(R.id.CompleteButton)
        val vibrateButton: Switch = findViewById<Switch>(R.id.viberateButton)
        val sendButton: Switch = findViewById<Switch>(R.id.sendbutton)

        val isVibrateEnable= sharedPreferences.getBoolean("vibrate_$id",false)
        val isRouteEnable = sharedPreferences.getBoolean("5Route_$id",false)
        val hour = sharedPreferences.getInt("hour_$id", -1)
        val minute = sharedPreferences.getInt("minute_$id", -1)
        val selectedDaysSet =
            sharedPreferences.getStringSet("selectedDays_$id", emptySet()) ?: emptySet()
        val selectedDays = selectedDaysSet.map { it.toInt() } // String -> Int 변환

        // TimePicker에서 선택한 시간 가져오기
        timePicker.hour
        timePicker.minute
        Log.d("CompleteButton", "TimePicker 시간: $hour, 분: $minute")

        // 요일 체크박스 초기화
        val cbSun = findViewById<CheckBox>(R.id.cb_sun)
        val cbMon = findViewById<CheckBox>(R.id.cb_mon)
        val cbTue = findViewById<CheckBox>(R.id.cb_tue)
        val cbWed = findViewById<CheckBox>(R.id.cb_wed)
        val cbThr = findViewById<CheckBox>(R.id.cb_thr)
        val cbFri = findViewById<CheckBox>(R.id.cb_fri)
        val cbSat = findViewById<CheckBox>(R.id.cb_sat)

        // 요일과 체크 여부를 매핑한 리스트
        val checkBoxes = listOf(cbSun, cbMon, cbTue, cbWed, cbThr, cbFri, cbSat)

        // 전달된 selectedDays에 맞게 체크박스 상태 설정
        selectedDays.forEach { day ->
            checkBoxes.getOrNull(day)?.isChecked = true
        }
        Log.d("CompleteButton", "요일 선택 상태: $checkBoxes")  // 선택 상태 로그 출력

        // 선택된 요일이 없으면 토스트 메시지 출력
        if (selectedDays.isEmpty()) {
            Toast.makeText(this, "요일을 선택해 주세요.", Toast.LENGTH_SHORT).show()
        }

        // 선택된 요일별로 알람 설정
        for (day in selectedDays) {
            setAlarmForDay(this, day, hour, minute)
        }

        // 버튼 클릭 리스너 설정 (추후 기능 추가 예정)
        sendButton.isChecked= isRouteEnable
        sendButton.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("5Route_$id", isChecked)
            editor.apply()
            sendButton.isChecked = isChecked
            Log.d("sendButton", "5분 주기 알람 활성화 : 5Route_$id")

            // 기능 추가 예정
        }
        vibrateButton.isChecked = isVibrateEnable
        vibrateButton.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("vibrate_$id", isChecked) // 진동 설정 저장
            editor.apply()
            vibrateButton.isChecked = isChecked // UI 동기화
            Log.d("vibrateButton", "진동 활성화 : vibrate_$id")

            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (!vibrator.hasVibrator()) {
                Log.d("vibrateButton", "진동을 지원하지 않는 디바이스입니다.")
                Toast.makeText(this, "이 디바이스는 진동을 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnCheckedChangeListener
            }

            if (isChecked) {
                Toast.makeText(this, "진동 설정이 활성화되었습니다.", Toast.LENGTH_SHORT).show()
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val vibrationEffect = VibrationEffect.createOneShot(
                        500, // 진동 시간 (밀리초)
                        VibrationEffect.DEFAULT_AMPLITUDE // 진동 강도
                    )
                    vibrator.vibrate(vibrationEffect)
                } else {
                    vibrator.vibrate(500) // 진동 시간 (밀리초)
                }
            } else {
                Toast.makeText(this, "진동 설정이 비활성화되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        Completebutton.setOnClickListener {
            val newHour = timePicker.hour
            val newMinute = timePicker.minute
            Log.d("CompleteButton", "TimePicker 시간: $newHour, 분: $newMinute")

            // 요일 데이터 필터링
            val checkBoxes = listOf(cbSun, cbMon, cbTue, cbWed, cbThr, cbFri, cbSat)

            val updatedSelectedDays = checkBoxes.mapIndexedNotNull { index, checkBox ->
                if (checkBox.isChecked) index else null
            }

            if (updatedSelectedDays.isEmpty()) {
                Toast.makeText(this, "요일을 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // SharedPreferences에 수정된 알람 정보 저장
            saveAlarmSettings(
                sharedPreferences.getInt("id", 0),
                updatedSelectedDays,
                newHour,
                newMinute
            )
            Log.d(
                "last Check",
                "저장된 요일: ${updatedSelectedDays.map { it.toString() }}, 시간: $newHour, 분: $newMinute"
            )
            // 수정 완료 후 MainActivity로 돌아가기
            val returnIntent = Intent(this, AlarmActivity::class.java)
            startActivity(returnIntent)
            finish()
        }
    }

    // 알람 설정 함수 (요일별로 알람 설정)
    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarmForDay(context: Context, dayOfWeek: Int, hour: Int, minute: Int) {
        // Calendar 객체를 사용해 알람 시간을 설정합니다.
        // 알람 발생 시 실행할 Intent 및 PendingIntent 생성
        val alarmintent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ID", dayOfWeek)            // 알람 ID (요일 기반)
            putExtra("hour", hour)              // 설정한 시간 정보 전달
            putExtra("minute", minute)          // 설정한 분 정보 전달
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            dayOfWeek,  // 요일별로 고유 PendingIntent 사용
            alarmintent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek + 1)  // Calendar.DAY_OF_WEEK는 일요일을 1로 시작
            set(Calendar.HOUR_OF_DAY, hour)          // 설정할 시간 (시)
            set(Calendar.MINUTE, minute)            // 설정할 시간 (분)
            set(Calendar.SECOND, 0)                 // 초는 0으로 설정

            // 설정 시간이 현재 시간보다 이전이면 다음 주로 설정
            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,             // 알람 발생 시 기기를 깨우도록 설정
            calendar.timeInMillis,              // 설정한 시간 (밀리초)
            pendingIntent                       // 알람 발생 시 실행될 PendingIntent
        )

        // AlarmManager를 통해 정확한 알람을 설정

        Log.d("setAlarmForDay", "Alarm set for day: $dayOfWeek at ${calendar.time}")
    }

    // 알람 설정값을 SharedPreferences에 저장
    private fun saveAlarmSettings(id: Int, selectedDays: List<Int>, hour: Int, minute: Int) {
        val sharedPreferences = getSharedPreferences("AlarmPreferences", MODE_PRIVATE)
        val id = sharedPreferences.getInt("current_id", -1)
        val editor = sharedPreferences.edit()
        Log.d("sharedAlarmSettings", "Id Level:$id")

        // 선택된 요일을 Set으로 저장
        editor.putStringSet("selectedDays_$id", selectedDays.map { it.toString() }.toSet())
        editor.putInt("hour_$id", hour)  // 수정된 시간 저장
        editor.putInt("minute_$id", minute)  // 수정된 분 저장
        editor.apply()  // 저장 완료

        Log.d(
            "saveAlarmSettings",
            "저장된 요일: ${selectedDays.map { it.toString() }}, 시간: $hour, 분: $minute"
        )
    }

    // 알림 음향 선택을 위한 팝업 메뉴
    private fun showPopupMenu(anchor: android.view.View) {
        val popupMenu = PopupMenu(this, anchor)

        // 팝업 메뉴에 항목 추가
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        //asdf asdf asdf asdf
        // 메뉴 항목 클릭 리스너 설정
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.option1 -> {
                    showToast("옵션 1 선택됨")
                    true
                }

                R.id.option2 -> {
                    showToast("옵션 2 선택됨")
                    true
                }

                R.id.option3 -> {
                    showToast("옵션 3 선택됨")
                    true
                }

                else -> false
            }
        }
        popupMenu.show()  // 팝업 메뉴 표시
    }

    // 토스트 메시지 표시 함수
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}