package http.ssl;

import http.ssl.Ssl;

import java.io.InputStream;
import java.net.Socket;

/**
 * Created by arybalko on 22.08.14.
 */
public class HTTPSClient {
    // первый аргумент - имя файла, содержащего HTTP запрос
    // предполагается, что запрос не будет больше 64 килобайт
    // второй - имя файла, куда будет слит ответ сервера
    public static void main(String args[]) {
        String host = "google.com";
        int port = 443;
        try {
            // открываем сокет до сервера
            Socket s = new Socket(host, port);

            s.getOutputStream().write(Ssl.send("CLIENT-HELLO"));

            // получаем поток данных от сервера
            InputStream is = s.getInputStream();
            int b = 1;

            for(int i = 0; i<10; i++) {
                b = is.read();
                System.out.print(b);
            }
            s.close();
        } catch(Exception e) {
            // вывод исключений
            e.printStackTrace();
        }
    }


    public static void print(String header, String host, int port) {
        try {
            // открываем сокет до сервера
            Socket s = new Socket(host, port);

            // пишем туда HTTP request
            s.getOutputStream().write(header.getBytes());

            // получаем поток данных от сервера
            InputStream is = s.getInputStream();
            int b = 1;
            byte[] b1 = new byte[1024*1024];
            int i = 0;
            while (b > 0) {
                b = is.read();
                b1[i] = (byte) b;
                i++;
            }
            s.close();
            //System.out.println(new String(b1, 0, i - 1));
        } catch(Exception e) {
            // вывод исключений
            e.printStackTrace();
        }
    }




}