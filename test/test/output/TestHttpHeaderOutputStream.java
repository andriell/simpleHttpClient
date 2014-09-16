package test.output;

import http.stream.output.HttpHeaderOutputStream;

/**
 * Created by arybalko on 02.09.14.
 */
public class TestHttpHeaderOutputStream {
    public static void main(String[] args) {
        try {
            new TestHttpHeaderOutputStream().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go() throws Exception {
        String standard = "HTTP/1.1 200 OK\r\n" +
                "Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0, no-store0, no-cache0, no-store1, no-cache1\r\n" +
                "Connection: close\r\n" +
                "Date: Mon, 25 Aug 2014 06:39:12 GMT\r\n" +
                "Pragma: no-cache\r\n" +
                "Server: nginx\r\n" +
                "Set-Cookie: PHPSESSID=c4fut815iilgpgmfccptt5aap3; expires=Thu, 28-Aug-2014 06:39:12 GMT; path=/; domain=officemag.ru; HttpOnly\r\n" +
                "Set-Cookie: OP_SAMSON_GUEST_ID=8200463; expires=Thu, 20-Aug-2015 06:39:12 GMT; path=/; domain=officemag.ru\r\n" +
                "Set-Cookie: OP_SAMSON_LAST_VISIT=25.08.2014+10%3A39%3A12; expires=Thu, 20-Aug-2015 06:39:12 GMT; path=/; domain=officemag.ru\r\n" +
                "Vary: Accept-Encoding\r\n" +
                "Content-Length: 1880\r\n" +
                "Content-Type: text/html; charset=Windows-1251\r\n" +
                "Expires: Thu, 19 Nov 1981 08:52:00 GMT\r\n" +
                "P3P: policyref=\"/bitrix/p3p.xml\", CP=\"NON DSP COR CUR ADM DEV PSA PSD OUR UNR BUS UNI COM NAV INT DEM STA\"\r\n" +
                "X-Powered-By: PHP/5.3.10-1ubuntu3.2ppa1~natty1\r\n" +
                "X-Powered-CMS: Bitrix Site Manager (d88ea2546bcb83becb2222575e15917f)\r\n" +
                "\r\n";

        String resp = "HTTP/1.1 200 OK\n" +
                "Server : nginx\n" +
                "Date: Mon, 25 Aug 2014 06:39:12 GMT\n" +
                "Content-Type: text/html; charset=Windows-1251\n" +
                "Content-Length:1880\n" +
                "Connection: close\n" +
                "X-Powered-By: PHP/5.3.10-1ubuntu3.2ppa1~natty1\n" +
                "P3P: policyref=\"/bitrix/p3p.xml\", CP=\"NON DSP COR CUR ADM DEV PSA PSD OUR UNR BUS UNI COM NAV INT DEM STA\"\n" +
                "X-Powered-CMS: Bitrix Site Manager (d88ea2546bcb83becb2222575e15917f)\n" +
                "Set-Cookie: PHPSESSID=c4fut815iilgpgmfccptt5aap3; expires=Thu, 28-Aug-2014 06:39:12 GMT; path=/; domain=officemag.ru; HttpOnly\n" +
                "Expires: Thu, 19 Nov 1981 08:52:00 GMT\n" +
                "Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0\n" +
                "Cache-Control: no-store0, no-cache0\n" +
                "               no-store1, no-cache1\n" +
                "Pragma: no-cache\n" +
                "Set-Cookie : OP_SAMSON_GUEST_ID=8200463; expires=Thu, 20-Aug-2015 06:39:12 GMT; path=/; domain=officemag.ru\n" +
                "             OP_SAMSON_LAST_VISIT=25.08.2014+10%3A39%3A12; expires=Thu, 20-Aug-2015 06:39:12 GMT; path=/; domain=officemag.ru\n" +
                "Vary :        Accept-Encoding\n" +
                "\n" +
                "100500";

        test(standard, resp);
        test(standard, resp.replaceAll("\n", "\r\n"));
    }
    public void test(String standard, String resp) throws Exception {
        byte[] respB = resp.getBytes();

        HttpHeaderOutputStream httpHeaderOutputStream = new HttpHeaderOutputStream(0, 10);

        int i = -1;
        for (byte b: respB) {
            i++;
            httpHeaderOutputStream.write((int) b);
            if (httpHeaderOutputStream.isEnd()) {
                String body = new String(respB, i + 1, respB.length - i - 1);
                if (body.equals("100500")) {
                    System.out.println("Ok");
                } else {
                    System.out.println("Error " + body);
                }
                break;
            }
        }
        httpHeaderOutputStream.write(1);
        httpHeaderOutputStream.write(1);
        httpHeaderOutputStream.write(1);

        String rebuild1 = new String(httpHeaderOutputStream.rebuild());

        if (rebuild1.equals(standard)) {
            System.out.println("Ok");
        } else {
            System.out.println("Error");
        }

        System.out.println(new String(httpHeaderOutputStream.getData()));
        System.out.println(rebuild1);

    }
}
