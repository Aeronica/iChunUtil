package us.ichun.mods.ichunutil.common.core.updateChecker;

import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.util.Map;
import java.util.TreeMap;

public class ModVersionJsonGen
{
    public static void generate()
    {
        Map<String, Map<String, String>> map = new TreeMap<String, Map<String, String>>(Ordering.natural());

        Map<String, String> versions;

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("Grinder", versions);
        versions.put("1.8.0", "5.0.0");
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("BackTools", versions);
        versions.put("1.8.0", "5.1.0");
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.1");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("Doors", versions);
        versions.put("1.7.10", "4.0.1");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("Hats", versions);
        versions.put("1.7.10", "4.0.1");
        versions.put("1.7", "3.0.1");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("HatStand", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("GravityGun", versions);
        versions.put("1.8.0", "5.0.0");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("GuiltTrip", versions);
        versions.put("1.8.0", "5.0.0");
        versions.put("1.7.10", "4.0.0");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("iChunUtil", versions);
        versions.put("1.8.0", iChunUtil.version);
        versions.put("1.7.10", "4.2.2");
        versions.put("1.7", "3.3.0");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("ItFellFromTheSky", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("MobAmputation", versions);
        versions.put("1.8.0", "5.0.0");
        versions.put("1.7.10", "4.0.1");
        versions.put("1.7", "3.0.1");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("MobDismemberment", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.1");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("Morph", versions);
        versions.put("1.7.10", "0.9.2");
        versions.put("1.7", "0.8.1");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("PiP", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.4");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("Photoreal", versions);
        versions.put("1.8.0", "5.0.0");
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("Shatter", versions);
        versions.put("1.8.0", "5.0.0");
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("Streak", versions);
        versions.put("1.8.0", "5.0.1");
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.0");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("Sync", versions);
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.1");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("Tabula", versions);
        versions.put("1.8.0", "5.1.0");
        versions.put("1.7.10", "4.1.1");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("Torched", versions);
        versions.put("1.8.0", "5.0.0");
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.1");

        versions = new TreeMap<String, String>(Ordering.natural());
        map.put("TrailMix", versions);
        versions.put("1.8.0", "5.0.0");
        versions.put("1.7.10", "4.0.0");
        versions.put("1.7", "3.0.2");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = "\n" + gson.toJson(map);

        System.out.println(jsonOutput);
    }
}
