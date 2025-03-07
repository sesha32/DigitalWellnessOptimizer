package com.example.digitalwellness

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.digitalwellness.data.AppUsageTracker
import com.example.digitalwellness.data.SleepTracker
import com.example.digitalwellness.mooddetection.FaceAnalyzer
import com.example.digitalwellness.mooddetection.VoiceAnalyzer
import com.example.digitalwellness.ui.theme.DigitalWellnessOptimizerTheme

class MainActivity : ComponentActivity() {

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private val audioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Audio permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Audio permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            DigitalWellnessOptimizerTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val usageAccessGranted by remember { mutableStateOf(checkForUsageStatsPermission(context)) }
    var appUsageStats by remember { mutableStateOf<List<UsageStats>>(emptyList()) }
    var sleepData by remember { mutableStateOf<String>("") }

    val appUsageTracker = remember { AppUsageTracker(context) }
    val sleepTracker = remember { SleepTracker(context) }

    LaunchedEffect(usageAccessGranted) {
        if (usageAccessGranted) {
            appUsageStats = appUsageTracker.getAppUsageStats()
        }
        sleepData = sleepTracker.getSleepData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        MoodDetectionSection()

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Digital Wellness Optimizer", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            Toast.makeText(context, "Grant usage access permission", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Check Usage Permission")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (usageAccessGranted) "Usage access granted!" else "Usage access not granted.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (usageAccessGranted) {
            if (appUsageStats.isNotEmpty()) {
                Text(
                    text = "App Usage Data:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(appUsageStats) { usageStats ->
                        AppUsageItem(usageStats)
                    }
                }
            } else {
                Text(
                    text = "No app usage data found.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sleep Data: $sleepData",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun MoodDetectionSection() {
    val context = LocalContext.current
    val faceAnalyzer = remember { FaceAnalyzer() }
    val voiceAnalyzer = remember { VoiceAnalyzer() }
    var mood by remember { mutableStateOf("") }

    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.example_face)

    LaunchedEffect(Unit) {
        faceAnalyzer.analyzeFace(bitmap,
            onSuccess = { detectedMood ->
                mood = detectedMood
            },
            onFailure = { e ->
                mood = "Error: ${e.message}"
            }
        )

        voiceAnalyzer.analyzeVoice(
            onSuccess = { detectedMood ->
                mood = detectedMood
            },
            onFailure = { e ->
                mood = "Error: ${e.message}"
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Mood Detection", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Detected Mood: $mood", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun AppUsageItem(usageStats: UsageStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "App: ${usageStats.packageName}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Usage: ${usageStats.totalTimeInForeground / 1000} seconds",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

fun checkForUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
    } else {
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    DigitalWellnessOptimizerTheme {
        MainScreen()
    }
}