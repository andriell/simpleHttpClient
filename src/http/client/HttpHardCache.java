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

/**
 * Created by arybalko on 10.09.14.
 */
public class HttpHardCache {
    private boolean replaceSeparator = false;
    private String dirForDefaultUser = "dafault";
    File dir;

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

    public String get(HttpRequestProcess client) throws IOException {
        if (!client.getMethod().equals(HttpRequestMethod.GET)) {
            return null;
        }
        CacheFile cache = new CacheFile(client);
        if (cache.dir == null) {
            return null;
        }

        File fileInfo = new File(dir, cache.fileInfo);
        File fileData = new File(dir, cache.fileData);
        if (!(fileInfo.isFile() && fileData.isFile())) {
            if (fileInfo.isFile()) {
                fileInfo.delete();
            }
            if (fileData.isFile()) {
                fileData.delete();
            }
            return null;
        }

        byte[] dataInfo = Files.readAllBytes(fileInfo.toPath());
        int l = dataInfo.length;
        int time = -1;
        String charsetName = null;
        int lf = 0;
        for (int i = 0; i < l; i++) {
            if (dataInfo[i] == C.LF) {
                if (time < 0) {
                    time = ArrayHelper.parseInt(dataInfo, lf, i - 1, 10, 0);
                } else if (charsetName == null) {
                    charsetName = new String(dataInfo, lf + 1, i - lf - 1);
                    break;
                }
                lf = i;
            }
        }

        if (time < time()) {
            fileInfo.delete();
            fileData.delete();
            return null;
        }
        return new String(Files.readAllBytes(fileData.toPath()), charsetName);
    }

    public void save(HttpRequestProcess client, byte[] data, byte[] charset, int timeMin) throws IOException {
        if (!client.getMethod().equals(HttpRequestMethod.GET)) {
            return;
        }
        CacheFile cache = new CacheFile(client);

        if (cache.dir == null) {
            return;
        }
        File fileInfo = new File(dir, cache.fileInfo);
        File fileData = new File(dir, cache.fileData);
        new File(dir, cache.dir).mkdirs();

        FileOutputStream output = new FileOutputStream(fileInfo);
        output.write(ArrayHelper.intToArry(time() + timeMin));
        output.write(C.BS_LF);
        output.write(charset);
        output.write(C.BS_LF);
        output.write(client.getUrl().getBytes());
        output.write(C.BS_LF);
        output.close();

        output = new FileOutputStream(fileData);
        output.write(data);
        output.close();
    }

    private int time() {
        return (int) (System.currentTimeMillis() / 60000L);
    }

    private class CacheFile {
        String fileData;
        String fileInfo;
        String dir;

        private CacheFile(HttpRequestProcess client) {
            HttpUrl url = client.getUrl();
            if (url == null) {
                return;
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

            int hashCode = Arrays.hashCode(url.getQuery());

            dir = user + File.separator + url.getDomain() + "." + url.getPort() + File.separator + new String(path);
            fileData = dir + File.separator + hashCode + ".tmp";
            fileInfo = dir + File.separator + hashCode + ".info";
        }

        @Override
        public String toString() {
            return "CacheFile{" +
                    "fileData='" + fileData + '\'' +
                    ", fileInfo='" + fileInfo + '\'' +
                    ", dir='" + dir + '\'' +
                    '}';
        }
    }
}
