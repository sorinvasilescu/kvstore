package com.sorinvasilescu.kvstore.service;

import com.sorinvasilescu.kvstore.data.Item;
import com.sorinvasilescu.kvstore.exceptions.DuplicateItemException;
import com.sorinvasilescu.kvstore.exceptions.ItemNotFoundException;
import com.sorinvasilescu.kvstore.exceptions.ItemWriteFailedException;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

// TODO: actually code this class
public class FileStorage implements StorageService {

    @Value("${kvstore.location}")
    private String basePath;

    @Override
    public void put(Item item) throws ItemWriteFailedException, DuplicateItemException {
        File file = new File(basePath + File.separator + item.getKey() + ".ser");
        // check if file exists
        if (file.exists()) {
            throw new DuplicateItemException("Item already exists in storage", item.getKey());
        }
        // initialise the file channel
        FileChannel channel;
        FileLock lock;
        try {
            channel = new FileOutputStream(file, false).getChannel();
        } catch (FileNotFoundException e) {
            throw new ItemWriteFailedException("Write failed - check if folder exists", item.getKey(), e);
        }
        // get file lock
        try {
            lock = channel.lock();
        } catch (IOException e) {
            try { channel.close(); } catch (IOException ex) { /* do nothing, we already have enought IOExceptions */ }
            throw new ItemWriteFailedException("Write failed - could not get file lock", item.getKey(), e);
        }
        // write
        try {
            ByteBuffer buff = ByteBuffer.wrap(itemToBytes(item));
            channel.write(buff);
            channel.close();
        } catch (IOException e) {
            try { channel.close(); } catch (IOException ex) { /* do nothing, we already have enought IOExceptions */ }
            throw new ItemWriteFailedException("Item write failed", item.getKey(), e);
        }
    }

    @Override
    public Item get(String key) throws ItemNotFoundException {
        File file = new File(basePath + File.separator + key + ".ser");
        // check if file exists
        if (!file.exists()) {
            throw new ItemNotFoundException("Item already exists in storage", key);
        }
        // initialise the file channel
        FileChannel channel;
        try {
            channel = new FileInputStream(file).getChannel();
        } catch (FileNotFoundException e) {
            throw new ItemNotFoundException("Read failed - could not initialise filechannel", key);
        }
        // read
        try {
            ByteBuffer buff = ByteBuffer.allocate(2500);
            channel.read(buff);
            channel.close();
            buff.flip();
            return bytesToItem(buff.array());
        } catch (Exception e) {
            try {
                channel.close();
            } catch (IOException ex) { /* do nothing, we already have enought exceptions */ }
            throw new ItemNotFoundException("Item read failed", key); // TODO: make a particular exception for this?
        }
    }

    @Override
    public void delete(String key) throws ItemNotFoundException, IOException {
        File file = new File(basePath + File.separator + key + ".ser");
        // check if file exists
        if (!file.exists()) {
            throw new ItemNotFoundException("Item already exists in storage", key);
        }
        // delete file
        if (!file.delete()) {
            throw new IOException("Could not delete file");
        }
    }

    @Override
    public long size() throws NullPointerException {
        return new File(basePath).list().length;
    }

    private byte[] itemToBytes(Item item) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(item);
        return bos.toByteArray();
    }

    private Item bytesToItem(byte[] bytes) throws ClassNotFoundException, IOException{
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = new ObjectInputStream(bis);
        return (Item)in.readObject();
    }
}
