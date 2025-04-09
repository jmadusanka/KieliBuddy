package com.example.kielibuddy.ui.pages

import android.Manifest
import android.view.TextureView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoCallScreen(navController: NavController, channelName: String, appId: String) {
    val context = LocalContext.current
    var engine by remember { mutableStateOf<RtcEngine?>(null) }
    var localView by remember { mutableStateOf<TextureView?>(null) }
    var remoteView by remember { mutableStateOf<TextureView?>(null) }
    var remoteUid by remember { mutableStateOf<Int?>(null) }

    // Agora event handler
    val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            remoteUid = uid
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions.values.all { it }
            if (granted) {
                val rtc = RtcEngine.create(context, appId, rtcEventHandler).apply {
                    enableVideo()
                    setVideoEncoderConfiguration(
                        VideoEncoderConfiguration(
                            VideoEncoderConfiguration.VD_640x360,
                            VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                            VideoEncoderConfiguration.STANDARD_BITRATE,
                            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
                        )
                    )
                }

                val local = TextureView(context)
                rtc.setupLocalVideo(VideoCanvas(local, VideoCanvas.RENDER_MODE_HIDDEN, 0))
                rtc.startPreview()
                rtc.joinChannel(null, channelName, null, 0)

                engine = rtc
                localView = local
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            engine?.leaveChannel()
            engine?.stopPreview()
            RtcEngine.destroy()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f)) {
            remoteUid?.let { uid ->
                val remote = TextureView(context)
                engine?.setupRemoteVideo(VideoCanvas(remote, VideoCanvas.RENDER_MODE_HIDDEN, uid))
                remoteView = remote

                AndroidView(factory = { remote }, modifier = Modifier.fillMaxSize())
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Waiting for the other user to join...", color = Color.White)
                }
            }

            localView?.let { view ->
                AndroidView(factory = { view }, modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopEnd)
                    .padding(12.dp))
            }

        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("End Call", color = Color.White)
        }
    }
}
