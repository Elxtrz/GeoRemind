package com.tijil.reminder;

import com.codename1.db.Database;
import com.codename1.db.Cursor;
import com.codename1.db.Row;
import com.codename1.io.Log;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static DatabaseHelper instance;
    private Database db;

    private DatabaseHelper() {
        openDatabase();
        createTables();
    }

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null)
            instance = new DatabaseHelper();

        return instance;
    }

    private void openDatabase() {
        try {
            db = Database.openOrCreate("geofence_reminders.db");
        } catch (Exception e) {
            Log.e(e);
        }
    }

    private void createTables() {
        try {
            String createRemindersTable =
                    "CREATE TABLE IF NOT EXISTS reminders (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "title TEXT NOT NULL, " +
                            "description TEXT, " +
                            "latitude REAL NOT NULL, " +
                            "longitude REAL NOT NULL, " +
                            "address TEXT, " +
                            "radius INTEGER DEFAULT 100, " +
                            "is_active INTEGER DEFAULT 1, " +
                            "created_at INTEGER" +
                            ")";
            db.execute(createRemindersTable);
        } catch (Exception e) {
            Log.e(e);
        }
    }

    public long addReminder(String title, String description, double latitude, double longitude, String address, int radius) {
        try {
            long now = System.currentTimeMillis() / 1000;
            db.execute("INSERT INTO reminders (title, description, latitude, longitude, address, radius, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    title, description, latitude, longitude, address, radius, now);

            // Get the last inserted ID
            Cursor cursor = db.executeQuery("SELECT last_insert_rowid()");
            long id = -1;
            if (cursor.next()) {
                Row row = cursor.getRow();
                id = row.getLong(0);
            }
            cursor.close();
            return id;
        } catch (Exception e) {
            Log.e(e);
            return -1;
        }
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        try {
            Cursor cursor = db.executeQuery("SELECT * FROM reminders WHERE is_active = 1 ORDER BY created_at DESC");
            while (cursor.next()) {
                Row row = cursor.getRow();

                int id = (int) row.getLong(0);

                String title = row.getString(1);
                String description = row.getString(2);

                double latitude = row.getDouble(3);
                double longitude = row.getDouble(4);

                String address = row.getString(5);

                int radius = (int) row.getLong(6);
                int isActive = (int) row.getLong(7);

                reminders.add(new Reminder(id, title, description, latitude, longitude, address, radius, isActive));
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(e);
        }
        return reminders;
    }

    public void deleteReminder(long id) {
        try {
            db.execute("DELETE FROM reminders WHERE id = ?", id);
        } catch (Exception e) {
            Log.e(e);
        }
    }

    public void close() {
        try {
            if (db != null)
                db.close();
        } catch (Exception e) {
            Log.e(e);
        }
    }
}