package processing.app;
import processing.app.helpers.PreferencesMap;

import cc.arduino.Constants;
import cc.arduino.i18n.Languages;
import org.apache.commons.compress.utils.IOUtils;
import processing.app.helpers.PreferencesHelper;
import processing.app.helpers.PreferencesMap;
import processing.app.legacy.PApplet;
import processing.app.legacy.PConstants;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static processing.app.I18n.format;
import static processing.app.I18n.tr;

public class PreferencesProxy
{
    PreferencesMap prefs = new PreferencesMap();
    String LOCAL_PREFS_FILE = "preferences.txt";
    String Directory = ".";
    File preferencesFile;

    public boolean has(String key)
    {
        return prefs.containsKey(key);
    }

    public String get(String key)
    {
        if (!has(key))
            return PreferencesData.get(key);

        String val = prefs.get(key);
        set(key, val);
        return val;
    }

    public void set(String attribute, String value) {
        prefs.put(attribute, value);
    }

    public void setDirectory(String dir)
    {
        Directory = dir;
    }

    public void init(String dir)
    {
        Directory = dir + ".d/";

        try {
            Path path = Paths.get(Directory);

            //java.nio.file.Files;
            Files.createDirectories(path);

            System.out.println("Directory is created!");


        } catch (IOException e) {
            
        }
        
        try {
            preferencesFile = new File(Directory , LOCAL_PREFS_FILE);
            preferencesFile.createNewFile();

        } catch (IOException e) {
            
        }
        
        try {
            prefs.load(preferencesFile);
        } catch (IOException e) {
            BaseNoGui.showError(null, tr("Could not read default settings.\n" +
                "You'll need to reinstall Arduino."), e);
        }
    }

    public void save()
    {
        /*if (!doSave)
        return;*/

        /*if (getBoolean("preferences.readonly"))
        return;*/

        // on startup, don't worry about it
        // this is trying to update the prefs for who is open
        // before Preferences.init() has been called.
        //if (preferencesFile == null) return;

        // Fix for 0163 to properly use Unicode when writing preferences.txt
        PrintWriter writer = null;
        try {
        writer = PApplet.createWriter(preferencesFile);

        String[] keys = prefs.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        for (String key : keys) {
            if (key.startsWith("runtime."))
            continue;
            writer.println(key + "=" + prefs.get(key));
        }

        writer.flush();
        } catch (Throwable e) {
        System.err.println(format(tr("Could not write preferences file: {0}"), e.getMessage()));
        return;
        } finally {
        IOUtils.closeQuietly(writer);
        }

        try {
        BaseNoGui.getPlatform().fixPrefsFilePermissions(preferencesFile);
        } catch (Exception e) {
        //ignore
        }
    }

    public PreferencesProxy()
    {
        
    }

    public PreferencesProxy(String dir)
    {
        init(dir);
    }
}

