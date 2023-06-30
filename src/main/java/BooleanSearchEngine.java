import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import vladislav.IndexedStorage;
import vladislav.PageEntry;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class BooleanSearchEngine implements SearchEngine {
    protected Map<String, List<PageEntry>> wordIndexingStorage;

    //TODO обработать исключение в Main
    public BooleanSearchEngine() throws IOException {
        wordIndexingStorage = IndexedStorage.getIndexedStorage().getStorage();

        File[] arrOfPdfs = new File("pdfs").listFiles();

        for (int i = 0; i < Objects.requireNonNull(arrOfPdfs).length; i++) {
            var doc = new PdfDocument(new PdfReader(arrOfPdfs[i]));

            for (int j = 0; j < doc.getNumberOfPages(); j++) {
                var file = doc.getPage(j + 1);
                var text = PdfTextExtractor.getTextFromPage(file);

                String[] words = text.split("\\P{IsAlphabetic}+");

                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word.toLowerCase(), freqs.getOrDefault(word, 0) + 1);
                }

                String namePDFFile = doc.getDocumentInfo().getTitle();

                for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
                    String tmpWord = entry.getKey();
                    int tmpValue = entry.getValue();

                    List<PageEntry> listPageTmp = new ArrayList<>();
                    listPageTmp.add(new PageEntry(namePDFFile, j + 1, tmpValue));

                    if (wordIndexingStorage.containsKey(tmpWord)) {
                        wordIndexingStorage.get(tmpWord).add(new PageEntry(namePDFFile, j + 1, tmpValue));
                    } else {
                        wordIndexingStorage.put(tmpWord, listPageTmp);
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> result = new ArrayList<>();
        String wordToLowerCase = word.toLowerCase();
        if (wordIndexingStorage.get(wordToLowerCase) != null) {
            for (PageEntry pageEntry : wordIndexingStorage.get(wordToLowerCase)) {
                result.add(pageEntry);
            }
        }
        Collections.sort(result);
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanSearchEngine)) return false;
        BooleanSearchEngine that = (BooleanSearchEngine) o;
        return Objects.equals(wordIndexingStorage, that.wordIndexingStorage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordIndexingStorage);
    }

    @Override
    public String toString() {
        return "BooleanSearchEngine{" +
                "words=" + wordIndexingStorage +
                '}';
    }
}