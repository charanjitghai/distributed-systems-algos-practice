public class Main {
    public static void main(String[] args) throws Exception {
        DistributedDataStore<Integer, String> distributedDataStore =
                new ConsistentHashingDataStore<>(100, new SimpleHashFunction());

        distributedDataStore.addDataStore(new DataStore<>(30));
        distributedDataStore.addDataStore(new DataStore<>(60));
        distributedDataStore.addDataStore(new DataStore<>(90));
        distributedDataStore.put(10, "key1");
        distributedDataStore.put(20, "key2");
        distributedDataStore.put(40, "key3");
        distributedDataStore.put(70, "key4");
        distributedDataStore.put(80, "key5");
        distributedDataStore.listDataStores().forEach(dataStore -> System.out.println(dataStore.toString()));
        distributedDataStore.removeDataStore(new DataStore<>(60));
        distributedDataStore.listDataStores().forEach(dataStore -> System.out.println(dataStore.toString()));
        distributedDataStore.addDataStore(new DataStore<>(60));
        distributedDataStore.listDataStores().forEach(dataStore -> System.out.println(dataStore.toString()));
    }
}


class SimpleHashFunction implements HashFunction<Integer, String> {
    public int hashKey(Integer key) {
        return key;
    }

    public int hashDataStore(DataStore<Integer, String> dataStore) {
        return dataStore.getDataStoreId();
    }
}