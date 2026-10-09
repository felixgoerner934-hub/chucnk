package dev.spawnerhl.spawner;

import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Remembers every spawner block entity the client currently knows about, grouped by chunk.
 * All methods are called on the render/main thread only.
 */
public final class SpawnerTracker {
    /** chunk key -> packed block positions of the spawners in that chunk. */
    private static final Map<Long, Set<Long>> CHUNKS = new HashMap<>();
    private static Object lastLevel;

    private static int pendingFound;
    private static int pendingCx;
    private static int pendingCz;

    private SpawnerTracker() {}

    public static long key(int cx, int cz) {
        return ((long) cx << 32) | (cz & 0xFFFFFFFFL);
    }

    public static int keyX(long key) { return (int) (key >> 32); }

    public static int keyZ(long key) { return (int) key; }

    /** Call with the level of every loaded block entity; wipes the data when the level changes (dimension switch). */
    public static void add(BlockPos pos, Object level) {
        if (level != lastLevel) {
            CHUNKS.clear();
            lastLevel = level;
        }
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        long k = key(cx, cz);
        Set<Long> set = CHUNKS.get(k);
        if (set == null) {
            set = new HashSet<>();
            CHUNKS.put(k, set);
            pendingFound++;
            pendingCx = cx;
            pendingCz = cz;
        }
        set.add(pos.asLong());
    }

    public static void remove(BlockPos pos) {
        long k = key(pos.getX() >> 4, pos.getZ() >> 4);
        Set<Long> set = CHUNKS.get(k);
        if (set == null) return;
        set.remove(pos.asLong());
        if (set.isEmpty()) CHUNKS.remove(k);
    }

    public static void clear() {
        CHUNKS.clear();
        lastLevel = null;
        pendingFound = 0;
    }

    public static Iterable<Long> chunkKeys() { return CHUNKS.keySet(); }

    public static int chunkCount() { return CHUNKS.size(); }

    public static int spawnerCount() {
        int n = 0;
        for (Set<Long> s : CHUNKS.values()) n += s.size();
        return n;
    }

    public static int spawnersIn(long chunkKey) {
        Set<Long> s = CHUNKS.get(chunkKey);
        return s == null ? 0 : s.size();
    }

    /** Returns the number of chunks discovered since the last call (0 if none). */
    public static int takeFound() {
        int n = pendingFound;
        pendingFound = 0;
        return n;
    }

    public static int lastFoundX() { return pendingCx; }

    public static int lastFoundZ() { return pendingCz; }

    /** Packed position of the spawner closest to (x, z) in the horizontal plane, or Long.MIN_VALUE if none. */
    public static long nearestSpawner(double x, double z) {
        long best = Long.MIN_VALUE;
        double bestD = Double.MAX_VALUE;
        for (Set<Long> set : CHUNKS.values()) {
            for (long p : set) {
                double dx = BlockPos.getX(p) + 0.5 - x;
                double dz = BlockPos.getZ(p) + 0.5 - z;
                double d = dx * dx + dz * dz;
                if (d < bestD) {
                    bestD = d;
                    best = p;
                }
            }
        }
        return best;
    }
}
