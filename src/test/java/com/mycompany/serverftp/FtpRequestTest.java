/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.serverftp;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Fournier Allan Dausque Nicolas
 */
public class FtpRequestTest {

    public FtpRequestTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    /**
     * Test of processUSER method, of class FtpRequest.
     */
    @Test
    public void testProcessUSER() {
        System.out.println("processUSER");
        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        System.out.println(instance.root);
        instance.param = "anonymous";
        String expResult = "331 User name ok, need password";
        String result = instance.processUSER();
        assertEquals(expResult, result);
        instance.param = "mauvais";
        expResult = "530 Incorrect user,not logged in";
        result = instance.processUSER();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of processPASS method, of class FtpRequest.
     */
    @Test
    public void testProcessPASS() {
        System.out.println("processPASS");
        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.param = "toto";
        String result = instance.processPASS();
        String expResult = "230 User logged in";
        assertEquals(expResult, result);
        instance.param = "mauvais";
        result = instance.processPASS();
        expResult = "530 Incorrect pass,not logged in";
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
    /**
     * Test of processQUIT method, of class FtpRequest.
     */
    @Test
    public void testProcessQUIT() {
        System.out.println("processQUIT");
        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "fichiersServeur");
        String result = instance.processQUIT();
        String expResult = "221 Service closing control connection";
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of processPWD method, of class FtpRequest.
     */
    @Test
    public void testProcessPWD() {
        System.out.println("processPWD");
        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        String result = instance.processPWD();
        String expResult = "257 " + "/home/m1/dausque/Documents/fichiersServeur" + " is the CWD";
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of processCDUP method, of class FtpRequest.
     */
    @Test
    public void testProcessCDUP() {
        System.out.println("processCDUP");
        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        String result = instance.processCDUP();
        String expResult = "550 Illegal access";
        assertEquals(expResult, result);
        instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.currentDir = "/home/m1/dausque/Documents/fichiersServeur/dossier1";
        result = instance.processCDUP();
        expResult = "250 Requested file action okay, completed.";
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of processCWD method, of class FtpRequest.
     */
    @Test
    public void testProcessCWD() {
        System.out.println("processCWD");
        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.param = "/home/m1/dausque/Documents/fichiersServeur/dossier1";
        String result = instance.processCWD();
        String expResult = "250 Requested file action okay, completed.";
        assertEquals(expResult, result);
        instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.param = "/home/m1/dausque/Documents/";
        result = instance.processCWD();
        expResult = "550 Illegal access";
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of processMKD method, of class FtpRequest.
     */
    @Test
    public void testProcessMKD() {
        System.out.println("processMKD");
        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.param = "testdossier";
        String result = instance.processMKD();
        String expResult = "257 " + instance.currentDir + "/" + instance.param + " created";
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of processRMD method, of class FtpRequest.
     */
    @Test
    public void testProcessRMD() {
        System.out.println("processRMD");
        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.param = "testdossier";
        String result = instance.processRMD();
        String expResult = "250 " + instance.currentDir + "/" + "testdossier" + " deleted";
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of processDELE method, of class FtpRequest.
     */
    @Test
    public void testProcessDELE() {
        System.out.println("processDELE");

        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("/home/m1/dausque/Documents/fichiersServeur/text22.txt", "UTF-8");
        } catch (Exception e) {
        }
        writer.println("The first line");
        writer.close();
        instance.param = "text22.txt";
        String result = instance.processDELE();
        String expResult = "250 " + instance.param + " deleted";
        assertEquals(expResult, result);
        instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.param = "text400.txt";
        result = instance.processDELE();
        expResult = "550 File doesn't exist";
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of processRNFR method, of class FtpRequest.
     */
    @Test
    public void testProcessRNFR() {
        System.out.println("processRNFR");
        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.param = "text3.txt";
        String result = instance.processRNFR();
        String expResult = "350 Requested file action pending further information.";
        assertEquals(expResult, result);
        instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.param = "text9.txt";
        result = instance.processRNFR();
        expResult = "500 Requested file doesn't exist.";
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of processRNTO method, of class FtpRequest.
     */
    @Test
    public void testProcessRNTO() {
        System.out.println("processRNTO");
        Socket cliTest = null;
        try {
            cliTest = new Socket("fr.wikipedia.org", 80);
        } catch (Exception e) {
            System.out.println("Erreur dans le test");
        }
        FtpRequest instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.param = "text3.txt";
        String result = instance.processRNTO();
        String expResult = "503 You must use RNFR before RNTO";
        assertEquals(expResult, result);
        instance = new FtpRequest(cliTest, "/home/m1/dausque/Documents/fichiersServeur");
        instance.param = "text3.txt";
        instance.rename = new File(instance.currentDir + "/" + instance.param);
        result = instance.processRNTO();
        expResult = "250 Requested file action okay, completed.";
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
}
