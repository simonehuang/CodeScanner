# CodeScanner

This is an Android application to scan barcodes and QR codes using the device's camera. The project was developed with Jetpack Compose and the `journeyapps` library to facilitate code scanning.

## Features
* **Continuous Code Scanning**: The device's camera is used to continuously scan barcodes or QR codes.
* **Camera Perission**: The app requests permission to access the device's camera.
* **Automatic Search**: After scanning a code, the app displays a confirmation and, if the code is a URL, performs a browser search.

## Requirements
* Camera permission granted
* Android 5.0 (Lollipop) or higher

## How to Use
1. **Scanner Initialization**: When the app is opened, the camera will automatically activate to start scanning codes.
2. **Code Detection**: The app will continuously scan until a code is detected.
3. **Confirmation**: After detecting a code, a pop-up will appear to confirm whether the user wants to perform a search with the code.
4. **Code Action**: If the code is a URL, the browser will open. Otherwise, a Google search will be performed with the code.

## Instalation
1. Clone the repository:
`git clone https://github.com/your-username/codescanner.git`
2. Open the project in Android Studio.
3. Run the project on a physical device or emulator.

## Dependencies
* `com.journeyapps:barcodescanner` for code scanning.
* `androidx.activity:activity-compose` for permission management and navigation.
* `androidx.compose` for UI based on Compose.

## Contributing
Feel free to contribute improvements or bug fixes. Submit pull requests or open issues to report problems.
