package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Facilita o acesso a estatísticas do Lucene
 *
 * @author rodrigo
 */
public class LuceneStats {

    private final DirectoryReader ireader;

    public LuceneStats(DirectoryReader ireader) {
        this.ireader = ireader;
    }

    public LuceneStats(Directory dir) throws IOException {
        ireader = DirectoryReader.open(dir);
    }

    public LuceneStats() throws IOException {
        this(FSDirectory.open(new File(ConfigDefaults.INDEX_DIR_PATH)));
    }

    public Terms getAllTerms(String field) throws IOException {
            // forma alternativa
        // Terms allTerms = SlowCompositeReaderWrapper.wrap(ireader).terms("field");

        return MultiFields.getTerms(ireader, field);
    }

    public TermStats[] getHighTermsFrequency(String field) throws IOException, Exception {
        Terms allTerms = getAllTerms(field);

        HighFreqTerms.TotalTermFreqComparator hfComp;
        hfComp = new HighFreqTerms.TotalTermFreqComparator();
        TermStats[] stats = HighFreqTerms.getHighFreqTerms(ireader, (int) allTerms.size(), field, hfComp);

        return stats;
    }

    public Map<String, TermStats> getTermsFrequencyMap(String field) throws IOException, Exception {
        Map<String, TermStats> freq = new HashMap<>();
        TermStats[] stats = getHighTermsFrequency(field);
        for (TermStats termstat : stats) {
            freq.put(termstat.termtext.utf8ToString(), termstat);
        } //for

        return freq;
    }

    public void saveTermsFrequency(String field) throws FileNotFoundException, Exception {
        String fileName = ConfigDefaults.STATS_PATH + "freq_" + field + ".csv";
        PrintStream fileFreq = new PrintStream(new FileOutputStream(fileName));
        
        //cabeçalho do csv (campo, número de documentos com o termo, frequência do termo)
        fileFreq.println(field +",docFreq,totalTermFreq");
        
        TermStats[] stats = getHighTermsFrequency(field);
        for (TermStats termstat : stats) {
            fileFreq.println(termstat.termtext.utf8ToString()+","+ termstat.docFreq+","+termstat.totalTermFreq);
        } //for
        
        fileFreq.close();
    }

} //class
