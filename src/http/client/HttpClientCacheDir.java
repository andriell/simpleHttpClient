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
public class HttpClientCacheDir implements HttpClientCache {
    private static String extInfo = ".info";
    private static String extData = ".tmp";

    private String dirForDefaultUser = "dafault";
    File dir;

    public HttpClientCacheDir(String dir) throws IOException {
        this(new File(dir));
    }

    public HttpClientCacheDir(File dir) throws IOException {
        this.dir = dir;
        if (!this.dir.isDirectory()) {
            throw new IOException("Is not directory " + this.dir);
        }
        if (!this.dir.canWrite()) {
            throw new IOException("Cannot write to a directory " + this.dir);
        }
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
        InfoFile infoFile = new InfoFile(dataInfo, false);

        if (infoFile.time < time()) {
            fileInfo.delete();
            fileData.delete();
            return null;
        }
        if (infoFile.charsetName != null && !infoFile.charsetName.isEmpty()) {
            return new String(Files.readAllBytes(fileData.toPath()), infoFile.charsetName);
        }
        return new String(Files.readAllBytes(fileData.toPath()));
    }

    public void save(HttpRequestProcess client, byte[] data, byte[] charset, int time) throws IOException {
        int code = client.getHeaderResponse().getStatusCode();
        if (!(
            client.getMethod().equals(HttpRequestMethod.GET)
            && 200 <= code && code < 300
            && 0 < data.length
        )) {
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
        output.write(ArrayHelper.intToArry(time() + time));
        output.write(C.BS_LF);
        if (charset != null) {
            output.write(charset);
        }
        output.write(C.BS_LF);
        output.write(client.getUrl().getBytes());
        output.write(C.BS_LF);
        output.close();

        output = new FileOutputStream(fileData);
        output.write(data);
        output.close();
    }

    /**
     * Количество секунд прошедших с начала 2000 года
     * @return
     */
    private int time() {
        return (int) (System.currentTimeMillis() / 1000L - 946684800L);
    }

    public void clearDir() throws IOException {
        clearDir(dir, time());
    }

    private int clearDir(File dir, int time) throws IOException {
        int deleteCount = 0;
        File[] list = dir.listFiles();

        if (list == null) {
            return 1;
        }

        for (File f: list) {
            if (f.isDirectory()) {
                deleteCount += clearDir(f, time);
            } else {
                String absPath = f.getAbsolutePath();
                if (!absPath.endsWith(extInfo)) {
                    continue;
                }
                byte[] dataInfo = Files.readAllBytes(f.toPath());
                InfoFile infoFile = new InfoFile(dataInfo, false);
                if (infoFile.time > time) {
                    continue;
                }
                String tmp = absPath.substring(0, absPath.length() - extInfo.length());
                new File(tmp + extData).delete();
                f.delete();
                deleteCount += 2;
            }
        }

        if (deleteCount == list.length) {
            dir.delete();
            return 1;
        }

        return 0;
    }

    //<editor-fold desc="Getters and Setters">
    public String getDirForDefaultUser() {
        return dirForDefaultUser;
    }

    public void setDirForDefaultUser(String dirForDefaultUser) {
        this.dirForDefaultUser = dirForDefaultUser;
    }

    public File getDir() {
        return dir;
    }
    //</editor-fold>

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

            String path = new String(client.getUrl().getPath().getBytes());

            int hashCode = Arrays.hashCode(url.getQuery());

            dir = user + File.separator + url.getDomain() + "." + url.getPort() + File.separator + path;
            fileData = dir + File.separator + hashCode + extData;
            fileInfo = dir + File.separator + hashCode + extInfo;
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

    private class InfoFile {
        int time = - 1;
        String charsetName;
        String url;

        private InfoFile(byte[] dataInfo, boolean url) throws IOException {
            int l = dataInfo.length;
            int lf = 0;
            for (int i = 0; i < l; i++) {
                if (dataInfo[i] == C.LF) {
                    if (time < 0) {
                        time = ArrayHelper.parseInt(dataInfo, lf, i - 1, 10, 0);
                    } else if (charsetName == null) {
                        charsetName = new String(dataInfo, lf + 1, i - lf - 1);
                        if (url) {
                            this.url = new String(dataInfo, i + 1, l - i - 2);
                        }
                        break;
                    }
                    lf = i;
                }
            }
        }

        @Override
        public String toString() {
            return "InfoFile{" +
                    "time=" + time +
                    ", charsetName='" + charsetName + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
