package testes;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author rodrigo
 */
public class CveAnalyzer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Realizando testes como o Lucene");

        try {
            Analyzer analyzer = new StandardAnalyzer();

            // Store the index in memory:
            Directory directory = new RAMDirectory();
            // To store an index on disk, use this instead:
            //Directory directory = FSDirectory.open("/tmp/testindex");

            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
            IndexWriter iwriter = new IndexWriter(directory, config);
            
            Document doc = new Document();
            String text = "This is the text to be indexed.";
            doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
            iwriter.addDocument(doc);
            iwriter.close();
            
            // Now search the index:
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);

            // Parse a simple query that searches for "text":
            QueryParser parser = new QueryParser("fieldname", analyzer);
            Query query = parser.parse("text");
            ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
            System.out.println("Esperado 1 e " + "resultado:" + hits.length);

            // Iterate through the results:
            for (ScoreDoc hit : hits) {
                Document hitDoc = isearcher.doc(hit.doc);
                System.out.println("Documento: " + hitDoc.get("fieldname"));
                System.out.println("Escore: " + hit.score);
            }
            
            ireader.close();
            directory.close();

        } catch (IOException | ParseException e) {
            System.err.println(e);
        }
    }

}
