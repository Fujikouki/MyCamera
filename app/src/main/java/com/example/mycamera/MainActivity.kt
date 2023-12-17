package com.example.mycamera

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mycamera.ui.theme.MyCameraTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyCameraTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainCamera()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainCamera() {
    val permissionState: PermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    if(permissionState.status.isGranted){
        CametaStert()
    }else{
        NoPermission(onRequestPermission = permissionState::launchPermissionRequest)
    }
}

@Composable
fun NoPermission(onRequestPermission:() -> Unit){
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Button(onClick =  onRequestPermission ) {
            Text(text = "カメラの許可を与えてください")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CametaStert(){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture =  remember {
        ImageCapture.Builder()
            .setFlashMode(ImageCapture.FLASH_MODE_AUTO) // フラッシュモードを設定
            .setTargetAspectRatio(AspectRatio.RATIO_4_3) // ターゲットのアスペクト比を設定
            .build()
    }
    val previewView = PreviewView(context).apply {
        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        scaleType = PreviewView.ScaleType.FILL_CENTER
    }
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    DisposableEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        Log.d("cameraProvider",cameraProvider.toString())
        val preview = androidx.camera.core.Preview.Builder().build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        onDispose {
            cameraProvider.unbindAll()
        }
    }
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Clothesadd")
        }
    }) {paddingValues: PaddingValues ->
     AndroidView(factory = {previewView},
         modifier = Modifier.padding(paddingValues))
    }
}
