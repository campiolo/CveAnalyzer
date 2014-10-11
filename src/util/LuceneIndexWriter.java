package util;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Métodos para gravar as saídas do Lucene
 * @author rodrigo
 */
public class LuceneIndexWriter {
    private final IndexWriter iwriter;
    
    /**
     * Cria um objeto para gerar o índice
     * @param dir diretório de destino do índice
     * @param analyzer analisador Lucene
     * @throws java.io.IOException
     */
    public LuceneIndexWriter(Directory dir, Analyzer analyzer) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(ConfigDefaults.LUCENE_VERSION, analyzer);
        iwriter = new IndexWriter(dir, config);
    }

    /**
     * Cria um objeto para gerar o índice no diretório padrão definido em ConfigDefaults
     * @param analyzer analisador Lucene
     * @throws java.io.IOException
     */
    public LuceneIndexWriter(Analyzer analyzer) throws IOException {
        Directory dir = FSDirectory.open(new File(ConfigDefaults.INDEX_DIR_PATH));
        
        IndexWriterConfig config = new IndexWriterConfig(ConfigDefaults.LUCENE_VERSION, analyzer);
        iwriter = new IndexWriter(dir, config);
    }
    
    /**
     * Fecha o objeto iwriter
     * @throws IOException 
     */
    public void close() throws IOException {
        iwriter.close();
    }
    
    /**
     * Escreve um objeto como string no índice do Lucene
     * @param obj objeto a ser processado
     * @throws java.io.IOException
     */
    public void writeObjectToIndex(Object obj) throws IOException {
        Document doc = new Document();
        
        // adiciona o objeto como string no campo "test"
        doc.add(new VectorTextField("texto", obj.toString()));
        
        iwriter.addDocument(doc);
    } // writeObjectToIndex
   
    // TODO: pode-se implementar outros write  para campos específicos //
    
} //class
