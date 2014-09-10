package http.client;

import http.datatypes.HttpRequestMethod;
import http.datatypes.HttpUrl;
import http.helper.ArrayHelper;
import http.helper.C;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by arybalko on 10.09.14.
 */
public class HttpHardCache {
    private boolean replaceSeparator = false;
    private String dirForDefaultUser = "dafault";
    File dir;
    HashMap<String, Integer> dataTime = new HashMap<String, Integer>();

    public HttpHardCache(String dir) throws IOException {
        this(new File(dir));
    }

    public HttpHardCache(File dir) throws IOException {
        this.dir = dir;
        if (!this.dir.isDirectory()) {
            throw new IOException("Is not directory " + this.dir);
        }
        if (!this.dir.canWrite()) {
            throw new IOException("Cannot write to a directory " + this.dir);
        }
        replaceSeparator = File.separator.equals("\\");
    }

    public CacheFile fileCache(HttpRequestProcess client) {
        HttpUrl url = client.getUrl();
        if (url == null) {
            return null;
        }

        String user = client.getUser();
        if (user == null || user.isEmpty()) {
            user = dirForDefaultUser;
        }

        byte[] path = client.getUrl().getPath().getBytes();
        if (replaceSeparator) {
            for (int i = 0; i < path.length; i++) {
                if (path[i] == C.SOLIDUS) {
                    path[i] = C.REVERSE_SOLIDUS;
                }
            }
        }

        String dir = user + File.separator + url.getDomain() + "." + url.getPort() + File.separator + new String(path);
        return new CacheFile(dir, Arrays.hashCode(url.getQuery()) + ".tmp");
    }

    public byte[] get(HttpRequestProcess client) throws IOException {
        if (!client.getMethod().equals(HttpRequestMethod.GET)) {
            return null;
        }
        CacheFile cache = fileCache(client);
        File fileCache = new File(cache.fullPath);
        if (fileCache == null) {
            return null;
        }
        if (!fileCache.isFile()) {
            return null;
        }
        byte[] data = Files.readAllBytes(fileCache.toPath());
        /*int time = ArrayHelper.parseInt(data, 0, 7, 10, 0);
        System.out.println(time);
        System.out.println(time());
        if (time < time()) {
            fileCache.delete();
            return null;
        }*/
        return data;
    }

    public void save(HttpRequestProcess client, byte[] data, int timeMin) throws IOException {
        if (!client.getMethod().equals(HttpRequestMethod.GET)) {
            return;
        }

        File fileCache = fileCache(client);
        if (fileCache == null) {
            return;
        }
        if (fileCache.isFile()) {
            return;
        }
        fileCache.getParentFile().mkdirs();

        byte[] time = ArrayHelper.intToArry((time() + timeMin));

        FileOutputStream output = new FileOutputStream(fileCache);
        //output.write(time);
        output.write(data);
        output.close();
    }

    private int time() {
        return (int) (System.currentTimeMillis() / 60000L);
    }

    private class CacheFile {
        String fullPath;
        String dir;

        private CacheFile(String dir, String file) {
            this.fullPath = dir + file;
            this.dir = dir;
        }
    }
}
