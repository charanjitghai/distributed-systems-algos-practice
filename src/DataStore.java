import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DataStore<K, V> {
    private final Map<K, V> map;
    private final int dataStoreId;

    DataStore(int dataStoreId) {
        this.map = new HashMap<>();
        this.dataStoreId = dataStoreId;
    }

    void put(K key, V val) {
        map.put(key, val);
    }

    V get(K key) {
        return map.get(key);
    }

    void remove(K key) {
        this.map.remove(key);
    }

    Set<K> keySet() {
        return this.map.keySet();
    }

    int getDataStoreId() {
        return dataStoreId;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n---------------\n");
        stringBuilder.append(String.format("DataStoreId: %s\n", dataStoreId));
        for (Map.Entry<K, V> entry : this.map.entrySet()) {
            stringBuilder.append(String.format("\t%s : %s\n",entry.getKey(), entry.getValue()));
        }
        stringBuilder.append("\n---------------\n");
        return stringBuilder.toString();
    }
}
