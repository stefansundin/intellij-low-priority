package com.stefansundin.intellij.low_priority;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;

import com.sun.jna.*;
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
        boolean SetPriorityClass(WinNT.HANDLE hProcess, int dwPriorityClass);
        WinNT.HANDLE GetCurrentProcess();
        int GetCurrentProcessId();
        int GetLastError();
    }

    public void actionPerformed(AnActionEvent e) {
        Kernel32 lib = Kernel32.INSTANCE;
        WinNT.HANDLE handle = lib.GetCurrentProcess();
        int pid = lib.GetCurrentProcessId();
        boolean success = lib.SetPriorityClass(handle, lib.IDLE_PRIORITY_CLASS);

        if (success) {
            JOptionPane.showMessageDialog(null, "Set pid " + Integer.toString(pid) + " to low priority.", "Low Priority Plugin", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(null, "Error setting pid " + Integer.toString(pid) + " to low priority.", "Low Priority Plugin", JOptionPane.ERROR_MESSAGE);
        }
    }
}
