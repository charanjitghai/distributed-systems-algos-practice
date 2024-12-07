public interface HashFunction<K, V> {
    int hashKey(K key);
    int hashDataStore(DataStore<K, V> dataStore);
}
