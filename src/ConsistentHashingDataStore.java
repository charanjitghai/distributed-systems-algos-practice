import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashingDataStore<K, V> implements  DistributedDataStore<K, V> {

    TreeMap<Integer, DataStore<K, V>> treeMap;
    int hashRingSize;
    HashFunction<K, V> hashFunction;

    ConsistentHashingDataStore(int hashRingSize, HashFunction<K, V> hashFunction) {
        this.hashRingSize = hashRingSize;
        this.treeMap = new TreeMap<>();
        this.hashFunction = hashFunction;
    }

    public void put(K key, V value) throws NoDataStoreFoundException {
        DataStore<K, V> dataStore = getDataStoreForKey(key);
        dataStore.put(key, value);
    }

    public V get(K key) throws NoDataStoreFoundException {
        DataStore<K, V> dataStore = getDataStoreForKey(key);
        return dataStore.get(key);
    }

    public void addDataStore(DataStore<K, V> dataStore) throws DataStoreHashCollisionException {
        int dataStoreHash = this.hashFunction.hashDataStore(dataStore);
        if (treeMap.containsKey(dataStoreHash)) {
            throw new DataStoreHashCollisionException();
        }
        treeMap.put(dataStoreHash, dataStore);
        DataStore<K, V> nextDataStoreInRing = getDataStoreForKeyHash(dataStoreHash + 1);
        moveKeys(nextDataStoreInRing, dataStore);
    }

    public void removeDataStore(DataStore<K, V> dataStore) throws InsufficientDataStoresException {
        if (treeMap.size() == 1 || !treeMap.containsKey(hashFunction.hashDataStore(dataStore))) {
            throw new InsufficientDataStoresException();
        }
        dataStore = treeMap.get(hashFunction.hashDataStore(dataStore));
        int dataStoreHash = this.hashFunction.hashDataStore(dataStore);
        DataStore<K, V> nextDataStoreInRing = getDataStoreForKeyHash(dataStoreHash + 1);
        treeMap.remove(dataStoreHash);
        moveKeys(dataStore, nextDataStoreInRing);
    }

    public List<DataStore<K, V>> listDataStores() {
        List<DataStore<K, V>> dataStores = new ArrayList<>();
        for (Map.Entry<Integer, DataStore<K, V>> entry : treeMap.entrySet()) {
            dataStores.add(entry.getValue());
        }
        return dataStores;
    }

    private void moveKeys(DataStore<K, V> fromDataStore, DataStore<K, V> toDataStore) {
        List<K> allKeysInNextDataStore = new ArrayList<>(fromDataStore.keySet());
        for (K key : allKeysInNextDataStore) {
            try {
                DataStore<K, V> newDataStoreForKey = getDataStoreForKey(key);
                if (newDataStoreForKey.equals(toDataStore)) {
                    V value = fromDataStore.get(key);
                    toDataStore.put(key, value);
                    fromDataStore.remove(key);
                }
            } catch (NoDataStoreFoundException noDataStoreFoundException) {
                throw new RuntimeException();
            }
        }
    }

    private DataStore<K, V> getDataStoreForKey(K key) throws NoDataStoreFoundException {
        if (treeMap.isEmpty()) {
            throw new NoDataStoreFoundException();
        }
        int keyHash = this.hashFunction.hashKey(key);
        return getDataStoreForKeyHash(keyHash);
    }

    private DataStore<K, V> getDataStoreForKeyHash(int keyHash) {
        Integer serverHash =  this.treeMap.ceilingKey(keyHash);
        if (serverHash == null) {
            serverHash = this.treeMap.firstKey();
        }
        return this.treeMap.get(serverHash);
    }
}