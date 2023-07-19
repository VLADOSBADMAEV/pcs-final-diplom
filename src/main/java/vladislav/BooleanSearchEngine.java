package vladislav;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;



public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> wordsMap;

    public BooleanSearchEngine( File pdfsDir) throws IOException {
        wordsMap = new HashMap<>();
        List<PageEntry> pList;
        for (File file : pdfsDir.listFiles()) {
            try (var doc = new PdfDocument(new PdfReader(file))) {
                for (int i = 1; i < doc.getPageNumber(doc.getLastPage()) - 1; i++) {
                    var text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                    var words = text.split("\\P{IsAlphabetic}+");
                    Set<String> setWords = Arrays.stream(words)
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet());
                    for (String word : setWords) {
                        int count = Collections.frequency(Arrays.stream(words).collect(Collectors.toList()), word);
                        PageEntry pageEntry = new PageEntry(file.getName(), i, count);
                        if (!wordsMap.containsKey(word)) {
                            pList = new ArrayList<>();
                            pList.add(pageEntry);
                            wordsMap.put(word, pList);
                        } else {
                            wordsMap.get(word).add(pageEntry);
                            Collections.sort(wordsMap.get(word), Collections.reverseOrder());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public List<PageEntry> search(String word) {
        return wordsMap.get(word.toLowerCase());
    }
}