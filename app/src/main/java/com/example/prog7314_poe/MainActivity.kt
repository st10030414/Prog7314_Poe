package com.example.prog7314_poe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import androidx.work.*
import com.example.prog7314_poe.Notification.DailyPromptWorker
import com.example.prog7314_poe.sync.NoteSyncManager // NEW IMPORT
import java.util.concurrent.TimeUnit
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }

        if (savedInstanceState == null) {
            routeByAuth()
        }

        createNotificationChannel(this)

        // 🔥 NEW — make the network callback active
        registerNetworkCallback(this)

        // Notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        scheduleDailyPrompt(this)
    }

    override fun onStart() {
        super.onStart()
        routeByAuth()
    }

    private fun routeByAuth() {
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        if (isLoggedIn) {
            setDrawerLocked(false)
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, HomeFragment())
                .commit()
        } else {
            setDrawerLocked(true)
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, LoginFragment())
                .commit()
        }
        applyNavMenuFontSize()
    }

    fun setDrawerLocked(locked: Boolean) {
        drawerLayout.setDrawerLockMode(
            if (locked) DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            else DrawerLayout.LOCK_MODE_UNLOCKED
        )
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, HomeFragment()).commit()
            R.id.nav_profile -> supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, ProfileFragment()).commit()
            R.id.nav_vault -> supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, VaultFragment()).commit()
            R.id.nav_settings -> supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, SettingsFragment()).commit()
            R.id.menu_logout -> {
                FirebaseAuth.getInstance().signOut()
                setDrawerLocked(true)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_frame, LoginFragment())
                    .commit()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun applyNavMenuFontSize() {
        val menu = navigationView.menu
        val fontPx = Utility.getFontDimen(this, "body")
        for (i in 0 until menu.size()) {
            val mi = menu.getItem(i)
            val span = SpannableString(mi.title)
            span.setSpan(AbsoluteSizeSpan(fontPx.toInt()), 0, span.length, 0)
            mi.title = span
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "NetworkChannel"
            val descriptionText = "Notifies when network is restored"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("network_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // ✅ UPDATED — Now triggers NoteSyncManager on network regained
    fun registerNetworkCallback(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    // 🔥 NEW: Trigger offline → cloud sync immediately
                    NoteSyncManager.syncNow(context)

                    // (Optional) Keep your notification
                    sendNetworkRestoredNotification(context)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                }
            }
        )
    }

    fun sendNetworkRestoredNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val builder = NotificationCompat.Builder(context, "network_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Internet Connection")
            .setContentText("Internet connection restored!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1001, builder.build())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun scheduleDailyPrompt(context: Context) {
        val currentTime = Calendar.getInstance()
        val dueTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 13)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (dueTime.before(currentTime)) {
            dueTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = dueTime.timeInMillis - currentTime.timeInMillis

        val dailyWork = PeriodicWorkRequestBuilder<DailyPromptWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_prompt_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyWork
        )
    }
}
