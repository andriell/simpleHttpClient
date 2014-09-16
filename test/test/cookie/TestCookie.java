package test.cookie;

import http.cookie.Cookie;

import java.text.ParseException;

/**
 * Created by arybalko on 25.08.14.
 */
public class TestCookie {
    public static void main(String[] args) {
        try {
            // == Server -> User Agent ==
            // Set-Cookie: SID=31d4d96e407aad42
            // == User Agent -> Server ==
            // Cookie: SID=31d4d96e407aad42
            System.out.println(new Cookie(new String("SID=31d4d96e407aad42").getBytes()));


            // == Server -> User Agent ==
            // Set-Cookie: SID=31d4d96e407aad42; Path=/; Domain=example.com
            // == User Agent -> Server ==
            // Cookie: SID=31d4d96e407aad42
            System.out.println(new Cookie(new String("  SID  =    31d4d96e407aad42  ;  Path  =  /   ;   Domain  =  example.com   ").getBytes()));

            // == Server -> User Agent ==
            // Set-Cookie: SID=31d4d96e407aad42; Path=/; Secure; HttpOnly
            // Set-Cookie: lang=en-US; Path=/; Domain=example.com
            // == User Agent -> Server ==
            // Cookie: SID=31d4d96e407aad42; lang=en-US
            System.out.println(new Cookie(new String("SID=31d4d96e407aad42; Path=/; Secure;    HttpOnly    ").getBytes()));
            System.out.println(new Cookie(new String("lang=en-US; Path=/; Domain=example.com").getBytes()));

            // == Server -> User Agent ==
            // Set-Cookie: lang=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT
            // == User Agent -> Server ==
            // Cookie: SID=31d4d96e407aad42; lang=en-US
            System.out.println(new Cookie(new String("lang=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT").getBytes()));

            // Удаление куки. Оно должно происходить только если удаляют куки из пути где они были созданы
            // == Server -> User Agent ==
            // Set-Cookie: lang=; Expires=Sun, 06 Nov 1994 08:49:37 GMT
            // == User Agent -> Server ==
            // Cookie: SID=31d4d96e407aad42
           System.out.println(new Cookie(new String("lang=; Expires=Sun, 06 Nov 1994 08:49:37 GMT").getBytes()));

            Cookie cookie1 = new Cookie(new String("lang=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT").getBytes());
            Cookie cookie2 = new Cookie(new String("lang=en-US; Expires=Wed, 09 Jun 2021 10:18:14 GMT").getBytes());

            System.out.println(cookie1.equals(cookie2));

            // NID=67=HOQZQUejQOlFexct_vl0HDso4g9pDEastDswWD4mjNJibBZJTwcaggDsT4TgeWprCeAaiHqGvwAseEMvieT3ebu3r1vCbAsKf34vs-nJi-cDN-VtukNaPTPf1BUESU6v; expires=Tue, 03-Mar-2015 13:52:25 GMT; path=/; domain=.google.ru; HttpOnly
            Cookie cookie = new Cookie(new String("NID=67=HOQZQUejQOlFexct_vl0HDso4g9pDEastDswWD4mjNJibBZJTwcaggDsT4TgeWprCeAaiHqGvwAseEMvieT3ebu3r1vCbAsKf34vs-nJi-cDN-VtukNaPTPf1BUESU6v;         expires   =  Tue, 03-Mar-2015 13:52:25 GMT; path=       /    ;     domain=         .google.ru; HttpOnly").getBytes());
            System.out.println(cookie);
            if (new String(cookie.getKey()).equals("NID")) {
                System.out.println("Ok");
            } else {
                System.out.println("Error");
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
