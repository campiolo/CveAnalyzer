package testes;

import java.io.File;
import java.io.IOException;
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

/**i
 * Teste com termos
 * @author rodrigo
 */
public class CveAnalyzerTestUtil {

    public static void main(String[] args) {
        System.out.println("Realizando testes com o pacote util");
 
        try {
            Analyzer analyzer = new StandardAnalyzer();

            Directory directory = FSDirectory.open(new File(ConfigDefaults.INDEX_DIR_PATH));
            LuceneIndexWriter iwriter= new LuceneIndexWriter(directory, analyzer);
            
            if (!DirectoryReader.indexExists(directory)) {
                iwriter.writeObjectToIndex("This is the text to be indexed.");
                iwriter.writeObjectToIndex("Text is only text.");
                iwriter.writeObjectToIndex("Indexing is a good thing.");
                iwriter.close();
            } 
            
            // Realiza a busca no índice
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);

            // Procura pela palavra "text":
            QueryParser parser = new QueryParser("texto", analyzer);
            Query query = parser.parse("index*");
            //Query query = parser.parse("index");
            ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
            System.out.println("Número de hits:" + hits.length);

            // Verifica os resultados (recupera o documento e exibe o escore)
            for (ScoreDoc hit : hits) {
                Document hitDoc = isearcher.doc(hit.doc);
                System.out.println("Documento: " + hitDoc.get("texto"));
                System.out.println("Escore: " + hit.score);
            } //for
            
            LuceneStats stats = new LuceneStats(ireader);
            stats.saveTermsFrequency("texto");
            
            ireader.close();
            directory.close();

        } catch (IOException | ParseException e) {
            System.err.println(e);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

}
