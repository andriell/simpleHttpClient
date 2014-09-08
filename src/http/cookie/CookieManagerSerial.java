package http.cookie;

import http.datatypes.Domain;
import http.datatypes.HttpDate;
import http.datatypes.Path;

import java.io.*;
import java.text.ParseException;
import java.util.*;

/**
 * Created by arybalko on 26.08.14.
 */
public class CookieManagerSerial implements CookieManager {
    protected File file;

    // User, domain, HttpCookie
    TreeMap<String, TreeMap<Domain, HashSet<Cookie>>> data = new TreeMap<String, TreeMap<Domain, HashSet<Cookie>>>();

    public CookieManagerSerial(File file) throws ParseException, IOException, ClassNotFoundException, IndexOutOfBoundsException {
        this.file = file;
    }

    public CookieManagerSerial(String fileName) throws ParseException, IOException, ClassNotFoundException, IndexOutOfBoundsException {
        file = new File(fileName);
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
            set(new String(bytes[0]), new Cookie(bytes[1]));
        }
    }

    @Override
    public void set(String user, Cookie httpCookie) {
        long now = new HttpDate().getTime();
        HttpDate expires = httpCookie.getExpires();
        if (expires != null && expires.getTime() <= now) {
            return;
        }
        if (!data.containsKey(user)) {
            data.put(user, new TreeMap<Domain, HashSet<Cookie>>());
        }
        TreeMap<Domain, HashSet<Cookie>> dataUser = data.get(user);

        Domain domain = httpCookie.getDomain();

        if (!dataUser.containsKey(domain)) {
            dataUser.put(domain, new HashSet<Cookie>());
        }
        dataUser.get(domain).add(httpCookie);
    }

    @Override
    public Iterable<Cookie> get(String user, Domain domain, Path path, boolean isHttps) {
        ArrayList<Cookie> r = new ArrayList<Cookie>();

        if (!data.containsKey(user)) {
            return r;
        }
        TreeMap<Domain, HashSet<Cookie>> domainMap = data.get(user);

        long now = new HttpDate().getTime();
        for(Map.Entry<Domain, HashSet<Cookie>> domainEntry: domainMap.entrySet()) {
            /*
             * domain example.com
             * subdomain cs.example.com
             * subdomain www.example.com
             */
            if (!domainEntry.getKey().isSubDomain(domain)) {
                continue;
            }
            HashSet<Cookie> cookieSet = domainEntry.getValue();
            for(Cookie cookie: cookieSet) {
                // Удаляем старые куки
                HttpDate expires = cookie.getExpires();
                if (expires != null && expires.getTime() < now) {
                    cookieSet.remove(cookie);
                    continue;
                }
                if (!cookie.getPath().isSubPath(path, true)) {
                    continue;
                }
                if (!isHttps && cookie.isSecure()) {
                    continue;
                }
                r.add(cookie);
            }
        }

        return r;
    }

    @Override
    public int sessionEnd() {
        return removeOldCookie(true);
    }

    private int removeOldCookie(boolean sessionComplite) {
        int countRemove = 0;
        long now = new HttpDate().getTime();

        Iterator<Map.Entry<String,TreeMap<Domain,HashSet<Cookie>>>> userIterator = data.entrySet().iterator();
        // User, domain, HttpCookie
        while (userIterator.hasNext()) {
            Map.Entry<String, TreeMap<Domain, HashSet<Cookie>>> userEntry = userIterator.next();
            Iterator<Map.Entry<Domain, HashSet<Cookie>>> domainIterator = userEntry.getValue().entrySet().iterator();

            while (domainIterator.hasNext()) {

                Map.Entry<Domain, HashSet<Cookie>> domainEntry = domainIterator.next();
                Iterator<Cookie> cookieIterator = domainEntry.getValue().iterator();
                while (cookieIterator.hasNext()) {
                    Cookie cookie = cookieIterator.next();

                    HttpDate expires = cookie.getExpires();
                    if (expires == null) {
                        if (sessionComplite) {
                            cookieIterator.remove();
                            countRemove++;
                        }
                    } else if (expires.getTime() <= now) {
                        cookieIterator.remove();
                        countRemove++;
                    }
                }
                if (domainEntry.getValue().isEmpty()) {
                    domainIterator.remove();
                }
            }
            if (userEntry.getValue().isEmpty()) {
                userIterator.remove();
            }
        }
        return countRemove;
    }

    public synchronized int save() throws IOException {
        removeOldCookie(false);

        int l = 0;
        // Выясняем длинну
        // User, domain, HttpCookie
        for(TreeMap<Domain, HashSet<Cookie>> domainCollection: data.values()) {
            for(HashSet<Cookie> cookieCollection: domainCollection.values()) {
                l += cookieCollection.size();
            }
        }

        byte[][][] dataToSave = new byte[l][][];

        int i = 0;
        // User, domain, HttpCookie
        for(Map.Entry<String, TreeMap<Domain, HashSet<Cookie>>> userEntry: data.entrySet()) {
            TreeMap<Domain, HashSet<Cookie>> domainMap = userEntry.getValue();
            for(HashSet<Cookie> cookieCollection: domainMap.values()) {
                for(Cookie cookie: cookieCollection) {
                    dataToSave[i] = new byte[2][];
                    dataToSave[i][0] = userEntry.getKey().getBytes();
                    dataToSave[i][1] = cookie.buildSetCookie();
                    i++;
                }
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(dataToSave);
        objectOutputStream.flush();
        objectOutputStream.close();
        fileOutputStream.flush();
        fileOutputStream.close();
        return l;
    }

    public void printAll() {
        // Выясняем длинну
        // User, domain, HttpCookie
        for(TreeMap<Domain, HashSet<Cookie>> domainCollection: data.values()) {
            for(HashSet<Cookie> cookieCollection: domainCollection.values()) {
                for(Cookie cookie: cookieCollection) {
                    System.out.println(cookie);
                }
            }
        }
    }
}
