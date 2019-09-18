/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serverftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe gérant la création du serveur FTP.
 * 
 * @author Fournier Allan Dausque Nicolas
 */
public class Serveur {

    /**
     * Le main crée un serveur avec le port et le dossier racine spécifié.
     * 
     * @param args Le dossier racine, le port.
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        ServerSocket srv = null;
        Socket client = null;
        String root = null;
        int port = 1036;
        boolean running = true;
        try {
            if (args.length > 0) {
                root = args[0];
                System.out.println("root : " + root);
            }
            if(args.length > 1) {
                port = Integer.parseInt(args[1]);
                System.out.println("port : " + port);
            }
            srv = new ServerSocket(port);
            System.out.println(srv.getInetAddress());
            System.out.println("Successful server socket creation");
        } catch (IOException exception) {
            System.err.println("Failed server socket creation");
        }
        try {
            while (running) {
                client = srv.accept();
                System.out.println("Client connection success");
                new Thread(new FtpRequest(client, root)).start();
                System.out.println("End of thread");
            }
        } catch (IOException exception) {
            System.err.println("Failed accept");
        } finally {
            System.out.println("Closing the server");
            srv.close();
        }
    }
}
