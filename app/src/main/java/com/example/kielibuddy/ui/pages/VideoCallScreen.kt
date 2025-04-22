package com.example.kielibuddy.ui.pages

import android.Manifest
import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.kielibuddy.model.UserModel
import com.example.kielibuddy.repository.UserRepository
import com.example.kielibuddy.ui.components.ReviewForm
import com.example.kielibuddy.viewmodel.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCallScreen(navController: NavController, channelName: String, appId: String, remoteUserId: String) {
    val context = LocalContext.current
    val localSurfaceView = remember { SurfaceView(context) }
    val remoteSurfaceView = remember { SurfaceView(context) }
    var engine by remember { mutableStateOf<RtcEngine?>(null) }
    var joined by remember { mutableStateOf(false) }
    var localUid by remember { mutableStateOf<Int?>(null) }
    var remoteUid by remember { mutableStateOf<Int?>(null) }
    var isMicMuted by remember { mutableStateOf(false) }
    var callDuration by rememberSaveable { mutableStateOf(0L) }
    val maxCallDurationInMinutes = 20L
    var callActive by remember { mutableStateOf(false) }
    var isRemoteConnected by remember { mutableStateOf(false) }
    val view = LocalView.current
    var isSpeakerEnabled by remember { mutableStateOf(true) }
    var showReviewDialog by remember { mutableStateOf(false) }
    val isStudent = true
    val reviewViewModel = remember { ReviewViewModel() }

    val userRepo = remember { UserRepository() }
    var remoteUser by remember { mutableStateOf<UserModel?>(null) }

    LaunchedEffect(remoteUserId) {
        remoteUser = userRepo.getUserDetails(remoteUserId)
    }

    val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            joined = true
            localUid = uid

            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )

            Handler(Looper.getMainLooper()).postDelayed({
                audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
                audioManager.isSpeakerphoneOn = true

                engine?.setEnableSpeakerphone(true)
                engine?.setDefaultAudioRoutetoSpeakerphone(true)
            }, 500)
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("AGORA", "🔥 Remote user joined: $uid")
            remoteUid = uid
            engine?.setupRemoteVideo(VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
            isRemoteConnected = true
            callActive = true
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("AGORA", "❌ Remote user left: $uid")
            isRemoteConnected = false
            callActive = false
            if (isStudent) {
                showReviewDialog = true
            }
        }

        override fun onError(err: Int) {
            Log.e("AGORA", "❌ Error: $err")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions.values.all { it }
            if (granted) {
                val rtc = RtcEngine.create(context, appId, rtcEventHandler).apply {
                    setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
                    setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
                    enableVideo()
                    enableAudio()

                    setDefaultAudioRoutetoSpeakerphone(true)
                    setEnableSpeakerphone(true)

                    setVideoEncoderConfiguration(
                        VideoEncoderConfiguration(
                            VideoEncoderConfiguration.VD_640x360,
                            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                            VideoEncoderConfiguration.STANDARD_BITRATE,
                            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
                        )
                    )
                }

                rtc.setupLocalVideo(VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
                rtc.setDefaultAudioRoutetoSpeakerphone(true)
                rtc.setEnableSpeakerphone(true)
                rtc.muteLocalAudioStream(false)
                rtc.startPreview()

                rtc.joinChannel(null, channelName, null, 0)
                engine = rtc
                Log.d("AGORA", "Speaker enabled: $isSpeakerEnabled")
            } else {
                Log.e("AGORA", "❌ Permissions not granted")
            }
        }
    )

    LaunchedEffect(callActive) {
        if (callActive) {
            Log.d("AGORA", "⏳ Call active, starting shared timer")
            while (callActive && callDuration < maxCallDurationInMinutes * 60) {
                delay(1000)
                callDuration++
            }
            if (callDuration >= maxCallDurationInMinutes * 60) {
                Log.d("AGORA", "⏱ Call time limit reached")
                engine?.leaveChannel()
                engine?.stopPreview()
                RtcEngine.destroy()
                val window = (view.context as? android.app.Activity)?.window
                window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                engine = null
                if (isStudent) {
                    showReviewDialog = true
                } else {
                    navController.popBackStack()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    DisposableEffect(Unit) {
        val window = (view.context as? android.app.Activity)?.window
        window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            engine?.leaveChannel()
            engine?.stopPreview()
            RtcEngine.destroy()
            Log.d("AGORA", "✅ Agora engine destroyed")
            engine = null
        }
    }

    if (showReviewDialog) {
        Dialog(onDismissRequest = { showReviewDialog = false }) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ReviewForm(
                    tutorId = remoteUserId,
                    reviewViewModel = reviewViewModel,
                    onReviewSubmit = {
                        navController.navigate("studentHome")
                        showReviewDialog = false
                    }
                )
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = remoteUser?.profileImg,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = remoteUser?.firstName + " " + remoteUser?.lastName, color = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
        )
        Box(modifier = Modifier.weight(1f)) {
            if (joined) {
                AndroidView(factory = { remoteSurfaceView }, modifier = Modifier.fillMaxSize())
                AndroidView(
                    factory = { localSurfaceView },
                    modifier = Modifier
                        .size(130.dp)
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Waiting for other user to join...", color = Color.White)
                }
            }

            if (!isRemoteConnected && joined) {
                Text(
                    text = "🔁 Waiting for user to reconnect...",
                    color = Color.Yellow,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(8.dp)
                )
            }

            if (callActive) {
                Text(
                    text = String.format("⏱ %02d:%02d",
                        TimeUnit.SECONDS.toMinutes(callDuration),
                        callDuration % 60),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val buttonSize = 56.dp

            IconButton(
                onClick = {
                    isMicMuted = !isMicMuted
                    engine?.muteLocalAudioStream(isMicMuted)
                },
                modifier = Modifier
                    .size(buttonSize)
                    .background(
                        color = if (isMicMuted) Color.DarkGray else Color(0xFF4CAF50),
                        shape = MaterialTheme.shapes.extraLarge
                    )
            ) {
                Icon(
                    imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { engine?.switchCamera() },
                modifier = Modifier
                    .size(buttonSize)
                    .background(Color(0xFF2196F3), shape = MaterialTheme.shapes.extraLarge)
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch Camera",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {
                    isSpeakerEnabled = !isSpeakerEnabled
                    engine?.setEnableSpeakerphone(isSpeakerEnabled)

                    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    audioManager.isSpeakerphoneOn = isSpeakerEnabled
                },
                modifier = Modifier
                    .size(buttonSize)
                    .background(
                        color = if (isSpeakerEnabled) Color(0xFF00BCD4) else Color.DarkGray,
                        shape = MaterialTheme.shapes.extraLarge
                    )
            ) {
                Icon(
                    imageVector = if (isSpeakerEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Speaker",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {
                    engine?.leaveChannel()
                    engine?.stopPreview()
                    RtcEngine.destroy()
                    Log.d("AGORA", "✅ End call & destroy")
                    engine = null
                    if (isStudent) {
                        showReviewDialog = true
                    } else {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .size(buttonSize)
                    .background(Color.Red, shape = MaterialTheme.shapes.extraLarge)
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White
                )
            }
        }
    }
}