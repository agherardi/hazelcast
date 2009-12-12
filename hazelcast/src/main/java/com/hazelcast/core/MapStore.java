/* 
 * Copyright (c) 2008-2009, Hazel Ltd. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hazelcast.core;

import java.util.Collection;
import java.util.Map;

/**
 * Hazelcast distributed map implementation is an in-memory data store but
 * it can be backed by any type of data store such as RDBMS, OODBMS, or simply
 * a file based data store.
 * <p/>
 * IMap.put(key, value) normally stores the entry into JVM's memory. If MapStore
 * implementation is provided then Hazelcast can also call the MapStore
 * implementation to store the entry into a user defined storage such as
 * RDBMS or some other external storage system. It is completely up to the user
 * how the key-value will be stored or deleted.
 * <p/>
 * Same goes for IMap.remove(key)
 * <p/>
 * Store implementation can be called synchronously (write-through)
 * or asynchronously (write-behind) depending on the configuration.
 */
public interface MapStore<K, V> {
    /**
     * Stores the key-value pair.
     *
     * @param key   key of the entry to store
     * @param value value of the entry to store
     */
    void store(K key, V value);

    /**
     * Stores multiple entries. Implementation of this method can optimize the
     * store operation by storing all entries in one database connection for instance.
     *
     * @param map map of entries to store
     */
    void storeAll(Map<K, V> map);

    /**
     * Deletes the entry with a given key from the store.
     *
     * @param key key to delete from the store.
     */
    void delete(K key);

    /**
     * Deletes multiple entries from the store.
     *
     * @param keys keys of the entries to delete.
     */
    void deleteAll(Collection<K> keys);
}
