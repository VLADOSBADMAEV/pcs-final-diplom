import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    private Map<String, List<PageEntry>> words;

    public BooleanSearchEngine(File pdfsDir) throws IOException {

        List<File> listOfPDFFiles = List.of(Objects.requireNonNull(pdfsDir.listFiles())); // список всех PDF-файлов

        words = new HashMap<>();

        for (File pdf : listOfPDFFiles) { // перебираем все PDF-файлы

            var doc = new PdfDocument(new PdfReader(pdf)); // создаем PDF-объект из каждого PDF-файла

            for (int i = 0; i < doc.getNumberOfPages(); i++) { // перебираем все страницы в каждом PDF-объекте

                var textOfOnePage = PdfTextExtractor.getTextFromPage(doc.getPage(i + 1)); // текст каждой страницы

                var allWordsOnPage = textOfOnePage.split("\\P{IsAlphabetic}+"); // делим текст на слова

                Map<String, Integer> freqs = new HashMap<>(); // список слов с частотой каждого слова, слова уникальны (HashMap)

                //  -- подсчет частоты слов
                for (var word : allWordsOnPage) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word.toLowerCase(), freqs.getOrDefault(word.toLowerCase(), 0) + 1);
                }
                // -- добавление в Map<String, List<PageEntry>> words пары из "слово-в-нижнем-регистре" : "список-страниц-где-встречается-слово"
                int count = 0;
                for (var word : freqs.keySet()) {
                    String wordToLowerCase = word.toLowerCase();
                    if (freqs.get(wordToLowerCase) != null) {
                        count = freqs.get(wordToLowerCase);
                        words.computeIfAbsent(wordToLowerCase, k -> new ArrayList<>()).add(new PageEntry(pdf.getName(), i + 1, count));
                    }
                }
                freqs.clear(); // чистим список слов с частотой каждого слова, тк. для следующей страницы он создается заново
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        // реализация поиска по слову
        List<PageEntry> result = new ArrayList<>(); // пустой список результатов
        String wordToLowerCase = word.toLowerCase(); // перевод слова в нижний регистр
        if (words.get(wordToLowerCase) != null) { // если слово есть в базе слов
            for (PageEntry pageEntry : words.get(wordToLowerCase)) { // перебираем результаты поиска
                result.add(pageEntry); // добавляем результат поиска в список результатов
            }
        }
        Collections.sort(result); // сортируем как умеем :)
        return result; // возвращаем результат
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanSearchEngine)) return false;
        BooleanSearchEngine that = (BooleanSearchEngine) o;
        return Objects.equals(words, that.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(words);
    }

    @Override
    public String toString() {
        return "BooleanSearchEngine{" +
                "words=" + words +
                '}';
    }
}