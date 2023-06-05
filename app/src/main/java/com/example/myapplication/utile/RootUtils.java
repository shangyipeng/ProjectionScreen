package com.example.myapplication.utile;

import android.util.Log;

import com.example.myapplication.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class RootUtils {
    private static RootUtils instance;
    public static DataInputStream localDataInputStream;
    public static DataOutputStream localDataOutputStream;
    public static InputStream localInputStream;
    public static OutputStream localOutputStream;
    public static Process localProcess;
    public DataOutputStream dos = null;

    public static boolean isAccessGiven() {
        return false;
    }

    public RootUtils() {
        try {
            localProcess = Runtime.getRuntime().exec("su\n");
            localOutputStream = localProcess.getOutputStream();
            localInputStream = localProcess.getInputStream();
            localOutputStream.write("echo \"executed su command.\" \n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RootUtils getInstance() {
        try {
            if (instance == null) {
                instance = new RootUtils();
            }
            return instance;
        } catch (Exception unused) {
            return null;
        }
    }

    public boolean haveRoot() {
        try {
            return a() || b();
        } catch (Exception unused) {
            return false;
        }
    }

    private static boolean a() {
        boolean z = false;
        try {
            DataInputStream a2 = a("ls /data/");
            if (a2.readLine() != null) {
                z = true;
            }
            a2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return z;
    }

    private static DataInputStream a(String str) throws IOException {
        Process exec = Runtime.getRuntime().exec("su");
        DataOutputStream dataOutputStream = new DataOutputStream(exec.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(exec.getInputStream());
        dataOutputStream.writeBytes(str + "\n");
        dataOutputStream.flush();
        dataOutputStream.writeBytes("exit\n");
        dataOutputStream.flush();
        return dataInputStream;
    }

    private static boolean b() {
        try {
            Runtime.getRuntime().exec("").getOutputStream().write("mount -t yaffs2 -o remount,rw,noatime,nodiratime /dev/mtdblock3 /system\nchmod 777 /system/app".getBytes());
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public void execRootCmdSilent1(String str) throws Exception {
        if (str != null) {
            Log.e("TAG","123   "+str.getBytes()+"   "+str);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(str.getBytes());
            outputStream.write("echo \"\"\n".getBytes());
            outputStream.flush();

//            localOutputStream.write(str.getBytes());
//            localOutputStream.write("echo \"\"\n".getBytes());
//            localOutputStream.flush();
        }
    }

    public boolean uninstallApk(String str) {
        try {
            String[] strArr = new String[6];
            strArr[0] = "pm uninstall";
            strArr[1] = " ";
            strArr[2] = str;
            strArr[3] = " \n";
            execRootCmdSilent1(uniteString(strArr));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String uniteString(String[] strArr) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static void chmod(String str, String str2) {
        try {
            Runtime.getRuntime().exec("chmod " + str + " " + str2);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public static boolean isRoot() {
        if (new File("/system/bin/su").exists() && b("/system/bin/su")) {
            return true;
        }
        if (!new File("/system/xbin/su").exists() || b("/system/xbin/su")) {
            return false;
        }
        return true;
    }

    private static boolean b(String str) {
        Process process = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec("ls -l " + str);
            String readLine = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();
            if (readLine != null && readLine.length() >= 4) {
                char charAt = readLine.charAt(3);
                if (charAt == 's' || charAt == 'x') {
                    if (process != null) {
                        process.destroy();
                    }
                    return true;
                }
            }
            if (process == null) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (0 == 0) {
                return false;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                process.destroy();
            }
            throw th;
        }
        process.destroy();
        return false;
    }

    public static boolean isRootAvailable() {
        return RootTools.isRootAvailable();
    }
}
