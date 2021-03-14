/*
 * Copyright (c) 2021 Richard Allwood
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.ricall.rocksdb.rocksdbdemo.repository;

import io.ricall.rocksdb.rocksdbdemo.configuration.RocksDBProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.SerializationUtils.deserialize;
import static org.springframework.util.SerializationUtils.serialize;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RocksDBRepository implements KVRepository<String, Object> {

    private final RocksDBProperties properties;
    private transient RocksDB db;

    @PostConstruct
    public void init() throws IOException, RocksDBException {
        RocksDB.loadLibrary();
        Options options = new Options();
        options.setCreateIfMissing(true);

        Path folder = properties.getDatabaseFolder();
        Files.createDirectories(folder);
        db = RocksDB.open(options, folder.toString());
        log.info("RocksDB initialised");
    }

    @PreDestroy
    public void cleanup() {
        db.close();
    }

    @Override
    public synchronized boolean save(String key, Object value) {
        log.info("saving key {} -> value {}", key, value);

        try {
            db.put(key.getBytes(UTF_8), serialize(value));
            return true;
        } catch (RocksDBException e) {
            log.error("failed to save key {}", key, e);
            return false;
        }
    }

    @Override
    public synchronized Optional<Object> find(String key) {
        log.info("finding value by key {}", key);
        try {
            val bytes = db.get(key.getBytes(UTF_8));
            if (bytes != null) {
                return Optional.ofNullable(deserialize(bytes));
            }
        } catch (Exception e) {
            log.error("Failed to retrieve entry with key {}", key, e);
        }
        return Optional.empty();
    }

    @Override
    public synchronized boolean delete(String key) {
        log.info("deleting key {}", key);
        try {
            db.delete(key.getBytes(UTF_8));
            return true;
        } catch (RocksDBException e) {
            log.error("Failed to delete entry with key {}", key, e);
            return false;
        }
    }

}
