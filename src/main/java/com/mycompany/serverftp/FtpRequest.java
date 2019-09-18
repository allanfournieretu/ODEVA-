/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serverftp;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe gérant les requêtes sous formes de threads.
 *
 * @author Fournier Allan Dausque Nicolas
 */
public class FtpRequest implements Runnable {

    //Sockets
    Socket cli; //Socket communicaton commande/réponse
    Socket cliData; //Socket client données
    ServerSocket dataSocket; // Socket serveur données
    //Strings
    String incMsg = null;
    String cmd = null;
    String param = null;
    String root = null;
    String currentDir = null;
    boolean running = true;
    File rename = null;
    // IO commande/reponse
    BufferedReader reader;
    PrintStream ps;
    //IO data
    PrintStream psdata;
    BufferedInputStream bufI;
    OutputStream outputStream;
    InputStream inputStream;

    /**
     * Constructeur du thread. Récupère la socket client,le dossier root puis
     * crée les I/O de la socket.
     *
     * @param client La socket client
     * @param rootString Le dossier root du server
     */
    public FtpRequest(Socket client, String rootString) {
        this.cli = client;
        this.root = rootString;
        this.currentDir = root;
        System.out.println(cli);
        try {
            reader = new BufferedReader(new InputStreamReader(cli.getInputStream()));
            ps = new PrintStream(cli.getOutputStream());
        } catch (Exception except) {
            //except.printStackTrace();
            System.out.println("Failed");
            System.err.println("Fail initialisation IO commande/reponse");
        }
    }

    /**
     * Méthode de lancement du thread. Envoie le message d'accueil au client FTP
     * et rentre dans une boucle d'écoute . Ferme les IO et la socket si la
     * connexion est interrompu.
     */
    public void run() {
        System.out.println("Start d'un thread");
        try {
            ps.print("220 Service ready for new user.\r\n");
            while (running) {
                System.out.println("Waiting for a request");
                processRequest();
            }
            reader.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Echec dans le thread ou fermeture IO");
        }
        try {
            cli.close();
        } catch (Exception e) {
            System.err.println("Failure closing socket commande/reponse");
        }
        System.out.println("Fin d'un thread");
    }

    /**
     * Répartit les commandes vers les processus coresspondants. Attend la
     * lecture d'une ligne puis vérifie que le formatage de la commande est
     * correct avant de la répartir vers la méthode correspondante.
     */
    public void processRequest() {
        try {
            incMsg = reader.readLine();
        } catch (Exception except) {
            System.err.println("Failed to readline");
        }
        System.out.println(incMsg);
        if (incMsg != null) {
            try {
                cmd = incMsg.substring(0, incMsg.indexOf(" "));
                param = incMsg.substring(incMsg.indexOf(" ") + 1);
                System.out.println("Nom commande : " + cmd);
                System.out.println("Parametre commande : " + param);
            } catch (Exception e) {
                {
                    System.out.println("Commande sans parametre");
                    cmd = incMsg;
                    param = "Pas de parametre";
                }
            }
            if (cmd.equalsIgnoreCase("USER")) {
                processUSER();
            } else if (cmd.equalsIgnoreCase("PASS")) {
                processPASS();
            } else if (cmd.equalsIgnoreCase("QUIT")) {
                processQUIT();
            } else if (cmd.equalsIgnoreCase("SYST")) {
                ps.println("215 Unix Type: L8");
            } else if (cmd.equalsIgnoreCase("FEAT")) {
                ps.println("202 Command not implemented, superfluous at this site.");
            } else if (cmd.equalsIgnoreCase("TYPE")) {
                ps.println("200 Command okay");
            } else if (cmd.equalsIgnoreCase("PWD")) {
                processPWD();
            } else if (cmd.equalsIgnoreCase("PASV")) {
                processPASV();
            } else if (cmd.equalsIgnoreCase("PORT")) {
                processPORT();
            } else if (cmd.equalsIgnoreCase("LIST")) {
                processLIST();
            } else if (cmd.equalsIgnoreCase("RETR")) {
                processRETR();
            } else if (cmd.equalsIgnoreCase("STOR")) {
                processSTOR();
            } else if (cmd.equalsIgnoreCase("CWD")) {
                processCWD();
            } else if (cmd.equalsIgnoreCase("MKD")) {
                processMKD();
            } else if (cmd.equalsIgnoreCase("RMD")) {
                processRMD();
            } else if (cmd.equalsIgnoreCase("DELE")) {
                processDELE();
            } else if (cmd.equalsIgnoreCase("RNFR")) {
                processRNFR();
            } else if (cmd.equalsIgnoreCase("RNTO")) {
                processRNTO();
            } else if (cmd.equalsIgnoreCase("CDUP")) {
                processCDUP();
            }
        } else {
            running = false;
        }
    }

    /**
     * Traite la requête USER. Si le client est en mode anonyme ou rentre un
     * user valide(voir user.txt) comme Allan alors on demande le mot de passe.
     * Dans le cas contraire on renvoie un message d'erreur.
     *
     * @return String de retour pour les tests
     */
    public String processUSER() {
        String userName = param;
        String retour = null;
        Scanner scan = null;
        boolean LogSuccess = false;
        try {
            scan = new Scanner(new File("user.txt"));
            while (scan.hasNext()) {
                if (scan.nextLine().equals(userName)) {
                    LogSuccess = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to scan");
        }
        if (param.equalsIgnoreCase("anonymous")) {
            LogSuccess = true;
        }
        if (LogSuccess) {
            retour = "331 User name ok, need password";
            ps.print("331 User name ok, need password\r\n");
            return retour;
        } else {
            retour = "530 Incorrect user,not logged in";
            ps.print("530 Incorrect user,not logged in\r\n");
            return retour;
        }
    }

    /**
     * Traite la requête PASS. Si l'utilisateur entre le bon mot de passe ou est
     * en anonyme, la connexion est valide. Sinon on renvoie un message
     * d'erreur.
     *
     * @return String de retour pour les tests
     */
    public String processPASS() {
        String pass = param;
        String retour = null;
        boolean PassSucces = false;
        if (pass.equalsIgnoreCase("toto")) {
            PassSucces = true;
        }
        if (param.equalsIgnoreCase("anonymous@example.com")) {
            PassSucces = true;
        }
        if (PassSucces) {
            retour = "230 User logged in";
            ps.print("230 User logged in\r\n");
        } else {
            retour = "530 Incorrect pass,not logged in";
            ps.print("530 Incorrect pass,not logged in\r\n");
        }
        return retour;
    }

    /**
     * Réalise le lisitng du dossier courant. Equivalent du ls -la de linux qui
     * est transmit sur la socket data. On utilise pour cela un ProcessBuilder
     * dont le résultat sera lu et envoyer vers le client.
     */
    public void processLIST() {
        ps.println("150 Succes open");
        List<String> commands = new ArrayList<String>();
        commands.add("ls");
        commands.add("-la");
        commands.add("--time-style=+");
        commands.add(currentDir);
        System.out.println(commands);
        ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            psdata = new PrintStream(cliData.getOutputStream());
            Process process = pb.start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                psdata.println(s);
            }
        } catch (Exception exception) {
            ps.println("425 Requested LIST action not taken.");
            System.err.println("Failed to LIST");
        }
        try {
            psdata.close();
            cliData.close();
            ps.println("226 Sucessful listing");
        } catch (Exception e) {
            System.err.println("Fail close printer ou socket");
        }
    }

    /**
     * Sauvegarde le fichier du client sur le serveur. On tente de créer un
     * fichier dans le répertoire courant puis on lit les données sur la socket
     * data ouverte précédement dans PASV.
     *
     * @see FtpRequest#processPASV()
     */
    public void processSTOR() {
        String filename = currentDir + "/" + param;
        File dir = new File(currentDir);
        FileOutputStream fus = null;
        System.out.println("Tentative transfert vers serveur : " + filename);
        if (dir.exists()) {
            try {
                inputStream = cliData.getInputStream();
                fus = new FileOutputStream(filename);
            } catch (Exception exception) {
                System.err.println("Fail initialisation IO");
            }
        }
        ps.println("150 Initialisation réussi");
        BufferedReader lecture = new BufferedReader(new InputStreamReader(inputStream));
        PrintStream ecriture = new PrintStream(new BufferedOutputStream(fus), true);
        try {
            String s = null;
            while ((s = lecture.readLine()) != null) {
                ecriture.print(s);
                ecriture.print("\r\n");
            }
            ecriture.close();
            lecture.close();
            ps.println("226 Transfert reussi");
        } catch (Exception e) {
            System.err.println("Fail while writing");
            ps.println("425 Requested file action not taken. Write failure");
        }
    }

    /**
     * Envoie le fichier depuis le serveur vers le client. On tente de lire un
     * fichier dans le répertoire courant puis on envoie les données sur la
     * socket data ouverte précédement dans PASV.
     *
     * @see FtpRequest#processPASV()
     */
    public void processRETR() {
        System.out.println("On est dans le processRETR");
        String filename = currentDir + "/" + param;
        ps.println("150 Ouveture de la socket");
        try {
            outputStream = cliData.getOutputStream();
            FileInputStream fis = new FileInputStream(filename);
            bufI = new BufferedInputStream(fis);
        } catch (Exception exception) {
            System.err.println("Fail dans les IO");
        }
        System.out.println("Tentative transfert vers distant : " + filename);
        int count;
        byte[] grosbuffer = new byte[16 * 1024];
        try {
            while ((count = bufI.read(grosbuffer)) > 0) {
                System.out.println(count);
                outputStream.write(grosbuffer, 0, count);
            }
        } catch (Exception e) {
            System.err.println("Fail while writing");
            ps.println("425 Requested file action not taken. Write failure");
        }
        try {
            outputStream.close();
            bufI.close();
            cliData.close();
        } catch (Exception e) {
            System.err.println("Failure while closing IO and socket ");
        }
        System.out.println("IO et socket fermé");
        ps.println("226 Closing data connection");
        ps.println("250 Requested file action succes");
    }

    /**
     * Gère la connexion en mode passif à la socket data. On renvoie l'IP et le
     * port sur laquel doit se connecter le client pour échanger les données. On
     * est ensuite en attente sur la socket data.
     */
    public void processPASV() {
        try {
            dataSocket = new ServerSocket(0);
            String ipBis = cli.getLocalAddress().getHostAddress();
            int localPort = dataSocket.getLocalPort();
            System.out.println("227 (" + ipBis.replace('.', ',') + "," + String.valueOf(localPort / 256) + "," + String.valueOf(localPort % 256) + ") Pasv succeeded.\r\n");
            ps.print("227 (" + ipBis.replace('.', ',') + "," + String.valueOf(localPort / 256) + "," + String.valueOf(localPort % 256) + ") Pasv succeeded.\r\n");
        } catch (Exception e) {
            System.err.println("Couldn't init the client socket ");
            ps.print("500 Failed to create a server socket\r\n");
        }
        try {
            cliData = dataSocket.accept();
        } catch (Exception e) {
            System.err.println("Fail accept dans PASV");
        }
    }

    /**
     * Gère la connexion en mode actif à la socket data. On recoit l'IP et le
     * port sur le quel la socket doit être créer pour l'échange des données.
     */
    public void processPORT() {
        String[] port = param.split(",");
        String dataAdr = port[0] + '.' + port[1] + '.' + port[2] + '.' + port[3];
        int dataPort = Integer.parseInt(port[4]) * 256 + Integer.parseInt(port[5]);
        System.out.println("Adresse de connection : " + dataAdr);
        System.out.println("Port de connection : " + dataPort);
        try {
            cliData = new Socket(dataAdr, dataPort);
            ps.print("200 Command PORT okay");
        } catch (Exception e) {
            System.err.println("Couldn't create the socket");
            ps.println("425 Command PORT not okay");
        }
    }

    /**
     * Ferme la connexion et termine le thread. Envoie un message de fermture de
     * la connexion puis termine le thread FtpRequest encours.
     *
     * @return String de retour pour les tests
     */
    public String processQUIT() {
        System.out.println("221 Service closing control connection\r\n");
        ps.print("221 Service closing control connection\r\n");
        running = false;
        return "221 Service closing control connection";
    }

    /**
     * Affiche le dossier courant.Envoie un message contenant le chemin du
     * dossier courant.
     *
     * @return String de retour pour les tests
     */
    public String processPWD() {
        System.out.println("Le dossier courant est : " + currentDir);
        ps.println("257 " + currentDir + " is the CWD");
        return "257 " + currentDir + " is the CWD";
    }

    /**
     * Remonte dans l'arborescence des fichiers. Permet de sortir d'un dossier
     * en remonant dans l'arborescense.
     *
     * @return String de retour pour les tests
     */
    public String processCDUP() {
        boolean upOK = false;
        String retour = null;
        int lastSlash = currentDir.lastIndexOf("/");
        String currentUp = currentDir.substring(0, lastSlash);
        System.out.println(currentUp);
        if (currentUp.contains(root)) {
            upOK = true;
        }
        File wanted = new File(currentUp);
        if (wanted.exists() && upOK) {
            System.out.println("File exist");
            currentDir = currentUp;
            retour = "250 Requested file action okay, completed.";
            ps.println("250 Requested file action okay, completed.");
        } else {
            if (!upOK) {
                System.out.println("Illegal acces");
                retour = "550 Illegal access";
                ps.println("550 Illegal access");
            } else {
                System.out.println("File doesn't exist");
                retour = "550 Doesn't exist";
                ps.println("550 Doesn't exist");
            }
        }
        return retour;
    }

    /**
     * Change le dossier courant. Vérifie que le dossier courant voulu existe et
     * modifie ensuite la variable globale du dossier courant.
     *
     * @return String de retour pour les tests
     */
    public String processCWD() {
        String wantedDir = param;
        String retour = null;
        boolean same = false;
        boolean upOK = false;
        System.out.println("Le CWD actuel : " + currentDir);
        if (param.equalsIgnoreCase(currentDir)) {
            System.out.println("Same");
            same = true;
        }
        if (!same) {
            wantedDir = param;
        }
        if ((wantedDir.contains("/")) == false) {
            wantedDir = currentDir + "/" + param;
        }
        if (wantedDir.contains(root)) {
            upOK = true;
        }
        System.out.println("Le CWD voulu : " + wantedDir);
        File wanted = new File(wantedDir);
        if (wanted.exists() && upOK) {
            System.out.println("File OK");
            currentDir = wantedDir;
            ps.println("250 Requested file action okay, completed.");
            retour = "250 Requested file action okay, completed.";
        } else {
            if (!upOK) {
                System.out.println("Illegal acces");
                ps.println("550 Illegal access");
                retour = "550 Illegal access";
            } else {
                System.out.println("File doesn't exist");
                ps.println("550 Doesn't exist");
                retour = "550 Doesn't exist";
            }
        }
        return retour;
    }

    /**
     * Crée un dossier dans le dossier courant. Vérifie si la création est un
     * succés avant de répondre au client sur le résultat de la création.
     *
     * @return String de retour pour les tests
     */
    public String processMKD() {
        String creatingDir = currentDir + "/" + param;
        String retour = null;
        System.out.println(creatingDir);
        File toCreate = new File(creatingDir);
        if (toCreate.exists()) {
            retour = "450 File already exist";
            ps.println("450 File already exist");
        } else {
            boolean creation = toCreate.mkdir();
            if (creation) {
                retour = "257 " + creatingDir + " created";
                System.out.println("Folder " + creatingDir + " created");
                ps.println("257 " + creatingDir + " created");
            } else {
                retour = "550 Couldn't create folder";
                System.out.println("Coudln't create the folder");
                ps.println("550 Couldn't create folder");
            }
        }
        return retour;
    }

    /**
     * Supprime le dossier et ce qu'il contient. Utilise une fonction récursive
     * pour supprimer le dossier passé en paramètre.
     *
     * @return String de retour pour les tests
     * @see FtpRequest#deleteDirRecursively
     */
    public String processRMD() {
        String deletingDir = currentDir + "/" + param;
        String retour = null;
        File toDelete = new File(deletingDir);
        System.out.println(deletingDir);
        if (toDelete.exists()) {
            try {
                deleteDirRecursively(toDelete);
                System.out.println("Folder " + deletingDir + " deleted");
                retour = "250 " + deletingDir + " deleted";
                ps.println("250 " + deletingDir + " deleted");
            } catch (Exception e) {
                System.err.println("Falure in delete function");
                retour = "550 Couldn't delete folder";
                ps.println("550 Couldn't delete folder");
            }
        } else {
            retour = "550 File doesn't exist";
            ps.println("550 File doesn't exist");
        }
        return retour;
    }

    /**
     * Supprime le fichier. Renvoie une réponse positive ou négative au serveur
     * en fonction du résultat de la suppresion.
     *
     * @return String de retour pour les tests
     */
    public String processDELE() {
        String delete = param;
        String retour = null;
        File toDel = new File(currentDir + "/" + delete);
        if (toDel.exists()) {
            try {
                toDel.delete();
                System.out.println("File " + delete + " deleted");
                retour = "250 " + delete + " deleted";
                ps.println("250 " + delete + " deleted");
            } catch (Exception e) {
                System.err.println("Failed to delete");
                retour = "550 Couldn't delete file";
                ps.println("550 Couldn't delete file");
            }
        } else {
            retour = "550 File doesn't exist";
            ps.println("550 File doesn't exist");
        }
        return retour;
    }

    /**
     * Récupère le nom du fichier à renommer. Modifie la variable globale de
     * renommage par une instance File du fichier à renommer.
     *
     * @return String de retour pour les tests
     */
    public String processRNFR() {
        String renameFrom = param;
        String retour = null;
        rename = null; // On reset
        rename = new File(currentDir + "/" + renameFrom);
        if (rename.exists()) {
            System.out.println("Existe");
            retour = "350 Requested file action pending further information.";
            ps.println("350 Requested file action pending further information.");
        } else {
            System.out.println("Existe pas");
            rename = null;
            retour = "500 Requested file doesn't exist.";
            ps.println("500 Requested file doesn't exist.");
        }
        return retour;
    }

    /**
     * Renomme le fichier à renommer. Utilise la variable globale de renommage
     * pour renommer le fichier qui s'y trouve.
     *
     * @return String de retour pour les tests
     */
    public String processRNTO() {
        String renameTo = param;
        String retour = null;
        File newName = new File(currentDir + "/" + renameTo);
        if (rename != null) {
            boolean success = rename.renameTo(newName);
            if (success) {
                retour = "250 Requested file action okay, completed.";
                ps.println("250 Requested file action okay, completed.");
            } else {
                retour = "553 Requested file action not taken. Failed to rename";
                ps.println("553 Requested file action not taken. Failed to rename");
            }
        } else {
            retour = "503 You must use RNFR before RNTO";
            ps.println("503 You must use RNFR before RNTO");
        }
        return retour;
    }

    /**
     * Supprime de facon récursive les fichiers/dossiers.
     *
     * @param file Le dossier a supprmier.
     * @throws IOException
     */
    public void deleteDirRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    deleteDirRecursively(entry);
                }
            }
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }

}
