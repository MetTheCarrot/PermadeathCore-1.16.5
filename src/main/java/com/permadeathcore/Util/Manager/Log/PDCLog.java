package com.permadeathcore.Util.Manager.Log;

import com.permadeathcore.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PDCLog {

    private static PDCLog logs;
    private Main instance;

    private File file;

    public PDCLog() {
        this.instance = Main.getInstance();
        this.file = new File(instance.getDataFolder(), "logs.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disable(String reason) {
        log("El plugin ha sido apagado: " + reason);
    }

    public void log(String log) {

        LocalDate date = LocalDate.now();
        LocalDateTime time = LocalDateTime.now();

        String msg = String.format("[%02d/%02d/%02d] ", date.getDayOfMonth(), date.getMonthValue(), date.getYear()) + String.format("%02d:%02d:%02d ", time.getHour(), time.getMinute(), time.getSecond()) + log;
        add(msg);
    }

    private void add(String msg) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.append(msg);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PDCLog getInstance() {
        if (logs == null) logs = new PDCLog();
        return logs;
    }
}
