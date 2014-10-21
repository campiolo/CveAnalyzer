package cve;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import util.ConfigDefaults;
import util.LuceneIndexWriter;
import util.LuceneStats;

/**
 * Gera o arquivo com a frequência de termos do CVE
 * @author rodrigo
 */
public class CveAnalyzer {

    public static void main(String[] args) {
        System.out.println("Processando o arquivo do CVE");
 
        try {
            Analyzer analyzer = new StandardAnalyzer();

            Directory directory = FSDirectory.open(new File(ConfigDefaults.INDEX_DIR_PATH));
            LuceneIndexWriter iwriter= new LuceneIndexWriter(directory, analyzer);
            
            // Escreve lê cveid e description do csv e escreve para o índice
            if (!DirectoryReader.indexExists(directory)) {
                File csvData = new File("dataset/cve_out.csv");
                CSVParser parser = CSVParser.parse(csvData, Charset.forName("UTF-8"), CSVFormat.RFC4180);

                for (CSVRecord csvRecord : parser) {
                    iwriter.writeCveEntryToIndex(csvRecord.get(0), csvRecord.get(2));
                }
                
                iwriter.close();
            } 
            
            // Lê o índice
            DirectoryReader ireader = DirectoryReader.open(directory);
            
            // Calcula os termos frequentes
            LuceneStats stats = new LuceneStats(ireader);
            stats.saveTermsFrequency("cvedescription");
            
            ireader.close();
            directory.close();

        } catch (IOException | ParseException e) {
            System.err.println(e);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

}
