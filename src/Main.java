import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class Main extends Actions {

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        System.out.println("В папке с программой должно быть 2 файла: FIX.log и PFIX.log");
        Actions actions = new Actions();
        File fixReaderOriginalFile = new File("FIX.log");
        File pfixReaderOriginalFile = new File("PFIX.log");
        actions.checkFileExist(fixReaderOriginalFile);
        actions.checkFileExist(pfixReaderOriginalFile);
        System.out.println("Парсинг FIX файла...");
        BufferedReader fixReaderParsed = new BufferedReader(new FileReader(actions.parseOnlyValuableLogs(fixReaderOriginalFile, 0)), 1024);
        System.out.println("Парсинг PFIX файла...");
        BufferedReader pfixReaderparsed = new BufferedReader(new FileReader(actions.parseOnlyValuableLogs(pfixReaderOriginalFile, 1)),1024);
        System.out.println("Парсинг и вычисление дельты...");
        actions.logsMatcher(fixReaderParsed, pfixReaderparsed);
        actions.logDeltas(logs);
        long timeSpent = System.currentTimeMillis() - startTime;
        System.out.println("Программа выполнилась за " + timeSpent + "ms");
        System.out.println("Результаты в файле - Deltas.csv\n");
        System.out.println("Done!");

    }
}
