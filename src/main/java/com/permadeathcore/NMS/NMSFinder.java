package com.permadeathcore.NMS;

import com.permadeathcore.Main;
import com.permadeathcore.NMS.Versions.ClassFinder.*;

public class NMSFinder {

    private Main instance;

    public NMSFinder(Main instance) {

        this.instance = instance;
    }

    public Object getNMSHandler() {

        if (VersionManager.getVersion().equalsIgnoreCase("1_16_R3")) {
            return new ClassFinder_1_16_R3().findNmsHandler();
        }

        return null;
    }

    public Object getNMSAccesor() {

        if (VersionManager.getVersion().equalsIgnoreCase("1_16_R3")) {
            return new ClassFinder_1_16_R3().findNmsAccesor();
        }

        return null;
    }

    public Object getCustomBlock() {

        if (VersionManager.getVersion().equalsIgnoreCase("1_16_R3")) {

            return new ClassFinder_1_16_R3().findCustomBlock();
        }

        return null;
    }
}
