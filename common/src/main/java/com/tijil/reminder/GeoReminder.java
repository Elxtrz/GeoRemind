package com.tijil.reminder;

import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;

public class GeoReminder {

    private Form current;
    private Resources theme;

    public void init(Object context) {
        theme = UIManager.initFirstTheme("/theme");
    }

    public void start() {
        if (current != null) {
            current.show();
            return;
        }

        Form hi = new Form("GeoReminder", BoxLayout.y());
        hi.add(new Label("Your reminders will appear here"));
        hi.add(new Label("Tap + to add a new reminder"));
        hi.show();
    }

    public void stop() {
        current = getCurrentForm();
    }

    public void destroy() {
    }

    private Form getCurrentForm() {
        return com.codename1.ui.Display.getInstance().getCurrent();
    }
}