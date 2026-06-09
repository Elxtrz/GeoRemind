package com.tijil.reminder;

import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Button;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;

public class GeoReminder {

    private Form current;
    private Resources theme;
    private DatabaseHelper db;
    private Form mainForm;
    private Container remindersContainer;

    public void init(Object context) {
        theme = UIManager.initFirstTheme("/theme");
        db = DatabaseHelper.getInstance();
    }

    public void start() {
        if (current != null) {
            current.show();
            return;
        }
        showMainScreen();
    }

    private void showMainScreen() {
        mainForm = new Form("My Reminders", new BorderLayout());

        // Lock to portrait mode only
        Display.getInstance().lockOrientation(true);

        // Reminders list area
        remindersContainer = new Container(BoxLayout.y());
        refreshReminderList();

        // Add button at bottom (simpler than toolbar)
        Button addButton = new Button("+ Add New Reminder");
        addButton.addActionListener(e -> showAddReminderScreen());

        mainForm.add(BorderLayout.CENTER, remindersContainer);
        mainForm.add(BorderLayout.SOUTH, addButton);

        mainForm.show();
    }

    private void refreshReminderList() {
        remindersContainer.removeAll();

        java.util.List<Reminder> reminders = db.getAllReminders();

        // Debug output
        System.out.println("=== Found " + reminders.size() + " reminders in database ===");
        for (Reminder r : reminders) {
            System.out.println("  - " + r.getTitle() + " @ " + r.getAddress());
        }

        if (reminders.isEmpty()) {
            Label emptyLabel = new Label("No reminders yet.\nTap + Add New Reminder to start.");
            remindersContainer.add(emptyLabel);
        } else {
            for (Reminder r : reminders) {
                Container card = new Container(BoxLayout.y());

                Label titleLabel = new Label(r.getTitle());
                titleLabel.setUIID("LargeLabel");

                Label addressLabel = new Label(r.getAddress());
                addressLabel.setUIID("SmallLabel");

                Button navButton = new Button("Open in Google Maps");
                navButton.addActionListener(e -> {
                    String address = r.getAddress();
                    if (address != null && !address.isEmpty()) {
                        String url = "https://maps.google.com/?q=" + address.replace(" ", "+");
                        Display.getInstance().execute(url);
                    } else {
                        Dialog.show("Error", "No address saved", "OK", null);
                    }
                });

                Button deleteButton = new Button("Delete");
                deleteButton.addActionListener(e -> {
                    db.deleteReminder(r.getId());
                    refreshReminderList();
                });

                card.add(titleLabel);
                card.add(addressLabel);
                card.add(navButton);
                card.add(deleteButton);
                remindersContainer.add(card);
            }
        }

        remindersContainer.revalidate();
        mainForm.revalidate();
    }

    private void showAddReminderScreen() {
        Form addForm = new Form("New Reminder", BoxLayout.y());

        TextField titleField = new TextField("", "Title (e.g., Buy milk)", 20, TextField.ANY);
        TextField addressField = new TextField("", "Address (e.g., 123 Main St)", 30, TextField.ANY);

        Button saveButton = new Button("Save Reminder");
        Button cancelButton = new Button("Cancel");

        saveButton.addActionListener(e -> {
            String title = titleField.getText();
            String address = addressField.getText();

            if (title.isEmpty()) {
                Dialog.show("Error", "Please enter a title", "OK", null);
                return;
            }

            if (address.isEmpty()) {
                Dialog.show("Error", "Please enter an address", "OK", null);
                return;
            }

            long id = db.addReminder(title, "", 0.0, 0.0, address, 100);

            System.out.println("Saved reminder: '" + title + "' at '" + address + "' got ID: " + id);

            if (id != -1) {
                Dialog.show("Success", "Reminder saved!", "OK", null);
                refreshReminderList();
                addForm.showBack();
            } else {
                Dialog.show("Error", "Failed to save reminder. Check console.", "OK", null);
            }
        });

        cancelButton.addActionListener(e -> addForm.showBack());

        addForm.add(titleField);
        addForm.add(addressField);
        addForm.add(saveButton);
        addForm.add(cancelButton);

        addForm.show();
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }

    public void destroy() {
        if (db != null) {
            db.close();
        }
    }
}