package testes;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

/**i
 * Teste com termos
 * @author rodrigo
 */
public class CveAnalyzerTest {

    public static void writeTextToIndex(IndexWriter iwriter, String text) throws IOException {
        FieldType vecTextField = new FieldType();
        vecTextField.setStored(true);
        vecTextField.setStoreTermVectors(true);
        vecTextField.setIndexed(true);
        vecTextField.setTokenized(true);

        Document doc = new Document();
        doc.add(new Field("fieldname", text, vecTextField));
        iwriter.addDocument(doc);
    }

    public static void main(String[] args) {
        System.out.println("Realizando testes como o Lucene");
 
        try {
            Analyzer analyzer = new StandardAnalyzer();

            // Store the index in memory:
            //Directory directory = new RAMDirectory();
            // To store an index on disk, use this instead:
            Directory directory = FSDirectory.open(new File("dataset/index"));

            if (!DirectoryReader.indexExists(directory)) {
                IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
                IndexWriter iwriter = new IndexWriter(directory, config);
            
                // indexa três documentos
                writeTextToIndex(iwriter, "This is the text to be indexed.");
                writeTextToIndex(iwriter, "Text is only text.");
                writeTextToIndex(iwriter, "Indexing is a good thing.");
                iwriter.close();
            }
            
            // Now search the index:
            DirectoryReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);

            // Parse a simple query that searches for "text":
            QueryParser parser = new QueryParser("fieldname", analyzer);
            Query query = parser.parse("text");
            ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
            System.out.println("Número de hits:" + hits.length);

            // Iterate through the results:
            for (ScoreDoc hit : hits) {
                Document hitDoc = isearcher.doc(hit.doc);
                System.out.println("Documento: " + hitDoc.get("fieldname"));
                System.out.println("Escore: " + hit.score);
            } //for
            
            // estatisticas
            long sumDocFreq = ireader.getSumDocFreq("fieldname");
            long sumTotalTermFreq = ireader.getSumTotalTermFreq("fieldname");
            System.out.println("SumDocFreq: " + sumDocFreq);
            System.out.println("SumTotalTermFreq: " + sumTotalTermFreq);
            
            Terms terms = ireader.getTermVector(1, "fieldname");
            System.out.println("Número de termos: " + terms.size());
            System.out.println("Frequência: "+ terms.hasFreqs());
            
            // alternativa 1
            Terms allTerms = SlowCompositeReaderWrapper.wrap(ireader).terms("fieldname");
            
            // alternativa 2
            Terms todosTermos = MultiFields.getTerms(ireader, "fieldname");
            TermsEnum te = todosTermos.iterator(TermsEnum.EMPTY);
            System.out.println("Total de termos: " + todosTermos.size());
            
            BytesRef valorTermo;
            while ( (valorTermo=te.next()) != null ) {
                System.out.println("Termo: " + valorTermo.utf8ToString());
                System.out.println("Número de documentos: " + te.docFreq());
                System.out.println("Frequência do termo: " + te.totalTermFreq());
            } //while
            
            // testando o misc - identificar a frequencia dos termos
            HighFreqTerms.TotalTermFreqComparator hfTermsComparator = new HighFreqTerms.TotalTermFreqComparator();
            TermStats[] stats = HighFreqTerms.getHighFreqTerms(ireader, (int)todosTermos.size(), "fieldname", hfTermsComparator);
            for (TermStats termstat : stats) {
                System.out.println("t: "+termstat.termtext.utf8ToString()+", f "+termstat.docFreq + ", tf " + termstat.totalTermFreq);
            }
            
            ireader.close();
            directory.close();

        } catch (IOException | ParseException e) {
            System.err.println(e);
        } catch (Exception ex) {
            Logger.getLogger(CveAnalyzerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
