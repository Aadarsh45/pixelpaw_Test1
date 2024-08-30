package com.example.test1

import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

data class Session(val timestamp: String, val detail: String)

class MainActivity : AppCompatActivity() {

    private lateinit var liquidLevel: View
    private lateinit var increaseButton: Button
    private var currentHeight = 0 // Initial height of the liquid level
    private val increaseStep = 5 // Height increase step in pixels
    private val delayMillis: Long = 50 // Delay between each height increase
    private val handler = Handler(Looper.getMainLooper())
    private var isButtonPressed = false

    private lateinit var sessionRecyclerView: RecyclerView
    private lateinit var sessionAdapter: SessionAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val sessionList = mutableListOf<Session>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("SessionPrefs", Context.MODE_PRIVATE)

        // Load session history from SharedPreferences
        loadSessionHistory()

        // Add current session
        addCurrentSession()

        // Initialize RecyclerView
        sessionRecyclerView = findViewById(R.id.sessionRecyclerView)
        sessionRecyclerView.layoutManager = LinearLayoutManager(this)
        sessionAdapter = SessionAdapter(sessionList)
        sessionRecyclerView.adapter = sessionAdapter

        liquidLevel = findViewById(R.id.liquidLevel)
        increaseButton = findViewById(R.id.increaseButton)

        increaseButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isButtonPressed = true
                    handler.post(increaseRunnable)
                    increaseButton.isPressed = true
                    Log.d("MainActivity", "Button Pressed")
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isButtonPressed = false
                    handler.removeCallbacks(increaseRunnable)
                    increaseButton.isPressed = false
                    Log.d("MainActivity", "Button Released")
                }
            }
            true
        }
    }

    private val increaseRunnable = object : Runnable {
        override fun run() {
            if (isButtonPressed) {
                increaseLiquidLevel()
                handler.postDelayed(this, delayMillis)
            }
        }
    }

    private fun increaseLiquidLevel() {
        val containerHeight = findViewById<FrameLayout>(R.id.liquidContainer).height

        Log.d("MainActivity", "Container Height: $containerHeight, Current Height: $currentHeight")

        // Calculate the new height for the liquid level
        val targetHeight = currentHeight + increaseStep

        // Ensure the new height does not exceed the container height
        if (targetHeight <= containerHeight) {
            currentHeight = targetHeight

            // Animate the change in height for a smooth transition
            val animator = ValueAnimator.ofInt(liquidLevel.height, currentHeight)
            animator.duration = 50 // Quick animation duration
            animator.addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                liquidLevel.layoutParams.height = value
                liquidLevel.requestLayout()
            }
            animator.start()
        } else {
            Log.d("MainActivity", "Reached max container height. Cannot increase further.")
        }
    }

    private fun addCurrentSession() {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val session = Session(timestamp, "App opened")
        sessionList.add(0, session) // Add at the beginning of the list
        saveSessionHistory() // Save updated list to SharedPreferences
        Log.d("MainActivity", "Session added: $session")
    }

    private fun loadSessionHistory() {
        val sessionSet = sharedPreferences.getStringSet("sessions", emptySet())
        sessionSet?.forEach { sessionString ->
            val parts = sessionString.split("|")
            if (parts.size == 2) {
                sessionList.add(Session(parts[0], parts[1]))
                Log.d("MainActivity", "Session loaded: ${parts[0]}, ${parts[1]}")
            }
        }
    }

    private fun saveSessionHistory() {
        val editor = sharedPreferences.edit()
        val sessionSet = sessionList.map { "${it.timestamp}|${it.detail}" }.toSet()
        editor.putStringSet("sessions", sessionSet)
        editor.apply()
        Log.d("MainActivity", "Session history saved.")
    }
}
