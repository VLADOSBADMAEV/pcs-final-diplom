package vladislav;

public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry p) {
        int result = Integer.compare(this.count, p.count);
        if (result == 0) {
            result = p.pdfName.compareTo(this.pdfName);
        }
        if (result == 0) {
            result = Integer.compare(p.page, this.page);
        }
        return result;
    }

    @Override
    public String toString() {
        return "\n{" + '\n' +
                "\"pdfName\": " + pdfName + ',' + "\n" +
                "\"page\": " + page + ',' + "\n" +
                "\"count\": " + count + "\n" +
                "}";
    }
}