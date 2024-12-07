import java.util.List;

public interface DistributedDataStore<K, V> {

    void put(K key, V value) throws NoDataStoreFoundException;

    V get(K key) throws NoDataStoreFoundException;

    void addDataStore(DataStore<K, V> dataStore) throws DataStoreHashCollisionException;

    void removeDataStore(DataStore<K, V> dataStore) throws InsufficientDataStoresException;

    List<DataStore<K, V>> listDataStores();
}
