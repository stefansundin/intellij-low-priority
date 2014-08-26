package com.stefansundin.intellij.low_priority;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

import com.sun.jna.*;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

public class LowPriority extends AnAction {

    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
        public static final int ABOVE_NORMAL_PRIORITY_CLASS = 0x00008000,
                BELOW_NORMAL_PRIORITY_CLASS = 0x00004000,
                HIGH_PRIORITY_CLASS = 0x00000080,
                IDLE_PRIORITY_CLASS = 0x00000040,
                NORMAL_PRIORITY_CLASS = 0x00000020,
                PROCESS_MODE_BACKGROUND_BEGIN = 0x00100000,
                PROCESS_MODE_BACKGROUND_END = 0x00200000,
                REALTIME_PRIORITY_CLASS = 0x00000100;
        int TH32CS_SNAPPROCESS  = 0x00000002;

        public static final int PROCESS_QUERY_INFORMATION = 0x0400,
                PROCESS_SET_INFORMATION = 0x0200,
                SYNCHRONIZE = 0x00100000;

        public WinNT.HANDLE GetCurrentProcess();
        public int GetPriorityClass(WinNT.HANDLE hProcess);
        public boolean SetPriorityClass(WinNT.HANDLE hProcess, int dwPriorityClass);
        public int GetCurrentProcessId();
        public int GetLastError();
        public WinNT.HANDLE OpenProcess(int fdwAccess, boolean fInherit, int IDProcess);

        public WinNT.HANDLE CreateToolhelp32Snapshot(int dwFlags, int th32ProcessID);
        public boolean Process32First(WinNT.HANDLE hSnapshot, Tlhelp32.PROCESSENTRY32.ByReference lppe);
        public boolean Process32Next(WinNT.HANDLE hSnapshot, Tlhelp32.PROCESSENTRY32.ByReference lppe);
        public boolean CloseHandle(WinNT.HANDLE hObject);
    }

    static final int targetPriority = Kernel32.IDLE_PRIORITY_CLASS;

    public void actionPerformed(AnActionEvent e) {
        Kernel32 lib = Kernel32.INSTANCE;
        int pid = lib.GetCurrentProcessId();
        WinNT.HANDLE handle = lib.GetCurrentProcess();
        /*
        int currentPriority = lib.GetPriorityClass(handle);
        if (currentPriority == targetPriority) {
            JOptionPane.showMessageDialog(null, "Process with pid " + Integer.toString(pid) + " already set to low priority.", "Low Priority Plugin", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        */

        // set child processes
        int numChildProcesses = 0;
        WinNT.HANDLE snapshot = lib.CreateToolhelp32Snapshot(Kernel32.TH32CS_SNAPPROCESS, 0);
        Tlhelp32.PROCESSENTRY32.ByReference ent = new Tlhelp32.PROCESSENTRY32.ByReference();
        if (lib.Process32First(snapshot, ent)) {
            do {
                int parentPid = ent.th32ParentProcessID.intValue();
                int childPid = ent.th32ProcessID.intValue();
                if (parentPid == pid) {
                    WinNT.HANDLE childHandle = lib.OpenProcess(Kernel32.SYNCHRONIZE | Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_SET_INFORMATION, false, childPid);
                    if (lib.SetPriorityClass(childHandle, targetPriority)) {
                        numChildProcesses++;
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Error setting child pid " + Integer.toString(childPid) + " to low priority.", "Low Priority Plugin", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } while (lib.Process32Next(snapshot, ent));
        }
        lib.CloseHandle(snapshot);

        // set current process
        if (lib.SetPriorityClass(handle, targetPriority)) {
            JOptionPane.showMessageDialog(null, "Set pid " + Integer.toString(pid) + " and " + Integer.toString(numChildProcesses) + " child processes to low priority.", "Low Priority Plugin", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(null, "Error setting pid " + Integer.toString(pid) + " to low priority.", "Low Priority Plugin", JOptionPane.ERROR_MESSAGE);
        }
    }
}
