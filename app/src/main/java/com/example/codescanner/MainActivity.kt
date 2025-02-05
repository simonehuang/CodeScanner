package com.example.codescanner

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.example.codescanner.ui.theme.CodeScannerTheme
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.BarcodeCallback
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startScanning() // Start scanning when permission is granted
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    private var barcodeResult: String? by mutableStateOf(null)
    private var barcodeView: BarcodeView? = null
    private var showConfirmationDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeScannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        // Display the scanner view when scanning is active
                        ScannerView()

                        // Force recomposition when detecting a result
                        barcodeResult?.let {
                            LaunchedEffect(it) {
                                Log.d("Scanner", "Scanned code: $it") // Confirmation of scanned code
                                showConfirmationDialog = true // Show confirmation dialog
                            }
                        }
                    }
                }

                // Confirmation Dialog
                if (showConfirmationDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showConfirmationDialog = false
                        },
                        title = {
                            Text("Confirm Search")
                        },
                        text = {
                            Text("Do you want to search this code: $barcodeResult?")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    barcodeResult?.let { result ->
                                        // Search in the browser with the scanned code
                                        searchInBrowser(result)
                                    }
                                    showConfirmationDialog = false
                                }
                            ) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    showConfirmationDialog = false
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }

    // The function that will be called when checking camera permission
    private fun startScanning() {
        // Checks if BarcodeView was correctly initialized
        if (barcodeView == null) {
            Log.e("Scanner", "BarcodeView is null, initializing it now.")
            barcodeView = BarcodeView(this)
        } else {
            Log.d("Scanner", "BarcodeView is already initialized.")
        }

        // Configure the decoder for QR Codes and other formats
        barcodeView?.decoderFactory = DefaultDecoderFactory()

        // Add callback for scan result
        barcodeView?.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: com.journeyapps.barcodescanner.BarcodeResult?) {
                if (result != null) {
                    Log.d("Scanner", "Scanned code: ${result.text}") // Log to confirm that the code was read
                    barcodeResult = result.text // Updates the value of barcodeResult
                    showConfirmationDialog = true // Show the confirmation dialog again after barcode scan
                } else {
                    Log.d("Scanner", "No codes found.")
                }
            }

            override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {
                // It can be used to check whether the scanner is capturing points
                Log.d("Scanner", "Possible outcome points\n" +
                        "\n: $resultPoints")
            }
        })

        // Start scanning
        barcodeView?.resume()
        Log.d("Scanner", "Scanner started.")
        Toast.makeText(this, "Starting the scan", Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun ScannerView() {
        AndroidView(
            factory = { context ->
                barcodeView?.apply {
                    resume()
                    Log.d("Scanner", "BarcodeView started and running.")
                } ?: BarcodeView(context).apply {
                    barcodeView = this
                    resume()
                    Log.d("Scanner", "BarcodeView created and running.")
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }

    private fun searchInBrowser(result: String) {
        // If the code is not a direct URL, do a Google search with the code
        val searchQuery = if (result.startsWith("http://") || result.startsWith("https://")) {
            result // If it is already a valid URL, you do not need to modify it
        } else {
            "https://www.google.com/search?q=$result" // Perform a Google search with the code


        }

        val searchUri = Uri.parse(searchQuery)
        val searchIntent = Intent(Intent.ACTION_VIEW, searchUri)
        startActivity(searchIntent)
    }

    // Check permissions in onCreate
    override fun onStart() {
        super.onStart()
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startScanning() // If has permission
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA) // Request permission
            }
        }
    }

    // Ensures scanning restarts when activity resumes
    override fun onResume() {
        super.onResume()
        if (!showConfirmationDialog) {
            startScanning() // Restart scanning if not showing the confirmation dialog
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewMainActivity() {
        CodeScannerTheme {
            ScannerView()
        }
    }
}
