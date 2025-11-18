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
    //(Developer Android, 2025).
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)
        //(Developer Android, 2025).
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
        //(Developer Android, 2025).
        navigationView.setNavigationItemSelectedListener(this)

        onBackPressedDispatcher.addCallback(this) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
        //(Developer Android, 2025).
        if (savedInstanceState == null) {
            routeByAuth()
        }
        //(Developer Android, 2025).
        createNotificationChannel(this)

        registerNetworkCallback(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        //(Developer Android, 2025).
        scheduleDailyPrompt(this)
    }

    override fun onStart() {
        super.onStart()
        routeByAuth()
    }
    //(Developer Android, 2025).
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
    //(Developer Android, 2025).
    fun setDrawerLocked(locked: Boolean) {
        drawerLayout.setDrawerLockMode(
            if (locked) DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            else DrawerLayout.LOCK_MODE_UNLOCKED
        )
    }
    //(Developer Android, 2025).
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
    //(Developer Android, 2025).
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
    //(Developer Android, 2025).
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
    //(Developer Android, 2025).
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

                    NoteSyncManager.syncNow(context)

                    sendNetworkRestoredNotification(context)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                }
            }
        )
    }
    //(Developer Android, 2025).
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
    //(Developer Android, 2025).
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
    //(Developer Android, 2025).
}
/*
Reference List

Developer Android. 2025. Fragments, 10 February 2025. [Online]. Available at: https://developer.android.com/guide/fragments [Accessed 15 November 2025].

Developer Android. 2025. Save data in a local database using Room, 29 October 2025. [Online]. Available at: https://developer.android.com/training/data-storage/room [Accessed 15 November 2025].

Developer Android. 2025. Accessing data using Room DAOs, 10 February 2025. [Online]. Available at: https://developer.android.com/training/data-storage/room/accessing-data [Accessed 15 November 2025].

Developer Android. 2025. ViewModel overview, 3 September 2025. [Online]. Available at: https://developer.android.com/topic/libraries/architecture/viewmodel [Accessed 15 November 2025].

Developer Android. 2025. LiveData overview, 10 February 2025. [Online]. Available at: https://developer.android.com/topic/libraries/architecture/livedata#observe_livedata_objects [Accessed 15 November 2025].

Developer Android. 2025. Task scheduling, 8 September 2025. [Online]. Available at: https://developer.android.com/develop/background-work/background-tasks/persistent [Accessed 15 November 2025].

Developer Android. 2025. Navigation, 5 November 2025. [Online]. Available at: https://developer.android.com/guide/navigation [Accessed 15 November 2025].

Developer Android. 2025. ConstraintLayout, 17 July 2025. [Online]. Available at: https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout [Accessed 15 November 2025].

Developer Android. 2025. Spinner, 17 September 2025. [Online]. Available at: https://developer.android.com/reference/android/widget/Spinner [Accessed 15 November 2025].

Developer Android. 2025. RecyclerView.Adapter, 15 May 2025. [Online]. Available at: https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter [Accessed 15 November 2025].

Developer Android. 2025. Add a floating action button, 30 October 2025. [Online]. Available at: https://developer.android.com/develop/ui/views/components/floating-action-button [Accessed 15 November 2025].

Developer Android. 2025. Better performance through threading, 3 January 2024. [Online]. Available at: https://developer.android.com/topic/performance/threads [Accessed 15 November 2025].

Developer Android. 2025. Kotlin coroutines on Android, 6 July 2024. [Online]. Available at: https://developer.android.com/kotlin/coroutines [Accessed 15 November 2025].

Firebase. 2025. Firebase Authentication, 20 October 2025. [Online]. Available at: https://firebase.google.com/docs/auth [Accessed 15 November 2025].

Firebase. 2025. Get Started with Firebase Authentication on Android, 14 November 2025. [Online]. Available at: https://firebase.google.com/docs/auth/android/start [Accessed 15 November 2025].

Firebase. 2025. Cloud Firestore, 14 November 2025. [Online]. Available at: https://firebase.google.com/docs/firestore [Accessed 15 November 2025].

Client authentication. 2025. 14 November 2025. [Online]. Available at: https://developers.google.com/android/guides/client-auth [Accessed 15 November 2025].
 */