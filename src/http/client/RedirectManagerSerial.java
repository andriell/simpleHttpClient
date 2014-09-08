package http.client;

import http.datatypes.HttpUrl;

import java.io.*;
import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by arybalko on 08.09.14.
 */
public class RedirectManagerSerial implements RedirectManager {
    protected boolean findLoop = true;
    protected File file;

    private TreeMap<HttpUrl, HttpUrl> data = new TreeMap<HttpUrl, HttpUrl>();

    public RedirectManagerSerial(File file) {
        this.file = file;
    }

    public RedirectManagerSerial(String fileName) {
        this.file = new File(fileName);
    }

    public void load() throws ParseException, IOException, ClassNotFoundException, IndexOutOfBoundsException {
        if (! file.isFile()) {
            throw new IOException("This is not file " + file.getName());
        }

        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        byte[][][] dataLoad = (byte[][][]) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();

        for (byte[][] bytes: dataLoad) {
            if (bytes.length != 2) {
                throw new IndexOutOfBoundsException("byte[][] Length != 2. Length = " + bytes.length);
            }
            try {
                set(new HttpUrl(bytes[0]), new HttpUrl(bytes[1]));
            } catch (LoopException e) {}
        }
    }

    public synchronized int save() throws IOException {
        int l = data.size();
        byte[][][] dataToSave = new byte[l][][];

        int i = 0;
        for(Map.Entry<HttpUrl, HttpUrl> entry: data.entrySet()) {
            dataToSave[i] = new byte[2][];
            dataToSave[i][0] = entry.getKey().getBytes();
            dataToSave[i][1] = entry.getValue().getBytes();
            i++;
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(dataToSave);
        objectOutputStream.flush();
        objectOutputStream.close();
        fileOutputStream.flush();
        fileOutputStream.close();
        return i;
    }

    public void printAll() {
        for(Map.Entry<HttpUrl, HttpUrl> entry: data.entrySet()) {
            System.out.println(entry.getKey() + " > " + entry.getValue());
        }
    }

    @Override
    public HttpUrl get(HttpUrl url) {
        HttpUrl newUrl = data.get(url);
        if (newUrl == null) {
            return url;
        }
        return newUrl;
    }

    @Override
    public void set(HttpUrl from, HttpUrl to) throws LoopException {
        if (from.equals(to)) {
            return;
        }
        if (findLoop && findLoop(from, to)) {
            throw new LoopException(from, to);
        }
        data.put(from, to);
    }

    private boolean findLoop(HttpUrl start, HttpUrl to) {
        HttpUrl toCurrent = data.get(to);
        if (toCurrent == null) {
            return false;
        }
        if (toCurrent.equals(start)) {
            return true;
        }
        return findLoop(start, toCurrent);
    }

    //<editor-fold desc="Getters and Setters">
    public boolean isFindLoop() {
        return findLoop;
    }

    public void setFindLoop(boolean findLoop) {
        this.findLoop = findLoop;
    }

    public File getFile() {
        return file;
    }

    public TreeMap<HttpUrl, HttpUrl> getData() {
        return data;
    }
    //</editor-fold>
}
