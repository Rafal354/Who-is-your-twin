# Who-is-your-twin

## Application Setup Guide

### Backend

1. **Clone the repository**

```bash
git clone https://github.com/Rafal354/Who-is-your-twin.git
```

```bash
cd Who-is-your-twin
```
   
2. **Build the Docker image**

```bash
docker build -t flask-app .
```
   
3. **Run the backend application**

   ```bash
    docker run -p 5000:5000 flask-app
    ```

The application will be available at `http://localhost:5000`.

### Deploy the mobile application to the phone via terminal

#### Prerequisites

1. Installed  **Android Studio**.
2. Configured **Android SDK**.
3. Installed  **ADB** (Android Debug Bridge).
4. Connected mobile device or running emulator.

### Krok 1: Ensure your device is connected

Connect your physical device to the computer via USB, and make sure Developer mode and USB debugging are enabled on the device. You can check if the device is properly recognized by ADB by running the following command:

```bash
adb devices
```

### Krok 2: Build the application in debug mode

```bash
./gradlew assembleDebug
```

### Krok 3: Install the application on the device

```bash
./gradlew installDebug
```

### Krok 4: Launch the application on the device

```bash
adb shell am start -n com/example/whosyourtwin/.MainActivity
```

### Krok 5: (Optional) Check application logs

```bash
adb logcat
```
