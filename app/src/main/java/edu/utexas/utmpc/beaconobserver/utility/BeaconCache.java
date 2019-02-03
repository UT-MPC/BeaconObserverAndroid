package edu.utexas.utmpc.beaconobserver.utility;


import android.support.annotation.NonNull;

import org.apache.commons.collections4.map.PassiveExpiringMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static edu.utexas.utmpc.beaconobserver.utility.Constant.BEACON_VALID_DURATION_MS;

public class BeaconCache implements Map<String, Beacon> {
    /* Synchronized singleton. */
    private static BeaconCache instance;

    private Map<String, Beacon> cache;

    private BeaconCache() {
        PassiveExpiringMap.ConstantTimeToLiveExpirationPolicy<String, Beacon> policy =
                new PassiveExpiringMap.ConstantTimeToLiveExpirationPolicy<>(
                        BEACON_VALID_DURATION_MS, TimeUnit.MILLISECONDS);
        cache = new PassiveExpiringMap<>(policy, new HashMap<String, Beacon>());
    }

    public static synchronized BeaconCache getInstance() {
        if (instance == null) {
            instance = new BeaconCache();
        }
        return instance;
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return cache.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return cache.containsValue(o);
    }

    @Override
    public Beacon get(Object o) {
        return cache.get(o);
    }

    @Override
    public Beacon put(String s, Beacon beacon) {
        return cache.put(s, beacon);
    }

    @Override
    public Beacon remove(Object o) {
        return cache.remove(o);
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ? extends Beacon> map) {
        cache.putAll(map);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @NonNull
    @Override
    public Set<String> keySet() {
        return cache.keySet();
    }

    @NonNull
    @Override
    public Collection<Beacon> values() {
        return cache.values();
    }

    @NonNull
    @Override
    public Set<Entry<String, Beacon>> entrySet() {
        return cache.entrySet();
    }
}
