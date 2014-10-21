/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 * Teste para a leitura de CSV usando a API Apache CSV
 * Campos:
 *   CVE-ID - CVE identifier;
 *   Status - Entry or Candidate
 *   Description - a standardized text description of the issue(s)
 *   References - a list of URLs and other information (such as vendor advisory numbers) for this issue.
 *   Date Entry Created - the date the entry was created. 
 *   Phase - (legacy) the phase the CVE is in (e.g. CAN, CVE); this is no longer used.
 *   Votes - (legacy) previously board members would vote yea or nay on whether or not the CAN should be accepted and turned into a CVE; 
 *   Comments - on the issue, this is no longer used.
 * 
 * @author rodrigo
 */
public class CveCsvReader {

    public static void main(String[] args) {
        int contCandidate = 0,
            contEntry = 0,
            contReserved = 0,
            contReject = 0,
            contDisputed = 0,
            contEqual = 0;
        String last = "";
        
        
        try {
            File csvData = new File("dataset/base_dados_cve.csv");
//            File csvDataOut = new File("dataset/cve_out.csv");
//            FileWriter outFile = new FileWriter(csvDataOut);
//            CSVPrinter csvPrinter = new CSVPrinter((Appendable) outFile, CSVFormat.RFC4180);
            
            CSVParser parser = CSVParser.parse(csvData, Charset.forName("ISO-8859-1"),CSVFormat.RFC4180);
            for (CSVRecord csvRecord : parser) {
                //System.out.println("Número de campos: " + csvRecord.size());
                //System.out.println(csvRecord.get(0));
                if (csvRecord.get(1).equals("Candidate")) {
                    contCandidate++;
                } else if (csvRecord.get(1).equals("Entry")) {
                    contEntry++;
                }
                
                if (csvRecord.get(2).startsWith("** RESERVED **")) {
                    contReserved++;
                } else if (csvRecord.get(2).startsWith("** REJECT **")) {
                    contReject++;
                } else if (csvRecord.get(2).startsWith("** DISPUTED **")) {
                    contDisputed++;
                } else {
                    if (last.equals(csvRecord.get(2))) {
                        contEqual++;
                    } else {
  //                      csvPrinter.printRecord(csvRecord);
                    }
                    
                    last = csvRecord.get(2);
                }
            }
            System.out.println("Número de Registros: " + parser.getRecordNumber());
            //csvPrinter.close();

        } catch (IOException ex) {
            Logger.getLogger(CveCsvReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Número CANDIDATE: " + contCandidate);
        System.out.println("Número ENTRY: " + contEntry);
        
        System.out.println("Número REJECT: " + contReject);
        System.out.println("Número RESERVED: " + contReserved);
        System.out.println("Número DISPUTED: " + contDisputed);
        
        System.out.println("Número IGUAIS: " + contEqual);
    }
    
}
