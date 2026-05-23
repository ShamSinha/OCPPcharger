package Hardware;

import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class SerialRfidReader {
    private static final String TAG = "SerialRfidReader";
    private static final String DEFAULT_PATH = "/dev/ttyACM0";

    private final String path;
    private final Object readerLock = new Object();
    private BufferedReader reader;

    public SerialRfidReader() {
        this(DEFAULT_PATH);
    }

    public SerialRfidReader(String path) {
        this.path = path;
    }

    public boolean isConnected() {
        return new File(path).canRead();
    }

    public String readUid() throws IOException {
        BufferedReader activeReader = getReader();
        String line = activeReader.readLine();
        if (line == null) {
            close();
            return null;
        }

        String uid = line.trim().toUpperCase(Locale.US);
        if (uid.length() == 0) {
            return null;
        }
        return uid;
    }

    public void close() {
        BufferedReader readerToClose;
        synchronized (readerLock) {
            readerToClose = reader;
            reader = null;
        }
        closeQuietly(readerToClose);
    }

    private BufferedReader getReader() throws IOException {
        synchronized (readerLock) {
            if (reader == null) {
                reader = new BufferedReader(new FileReader(path));
            }
            return reader;
        }
    }

    private void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing serial reader: " + e.getMessage());
        }
    }
}
