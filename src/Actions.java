import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Actions {
    DateHelpers dateHelpers = new DateHelpers();
    protected static List<String> logs = new ArrayList<>();
    protected static int LOT_SIZE = 100000;

    //type: fix = 0; pfix=1
    protected File parseOnlyValuableLogs(File originFile, int type) {
        FileReader fr;
        BufferedReader reader;
        FileWriter fw;
        FileReader fr2;
        BufferedReader reader2;
        FileWriter fw2;
        String data;
        File parsedLogFile = null;

        if (type == 0) {
            try {
                parsedLogFile = new File("Parsed_Fix.log");
                fw = new FileWriter(parsedLogFile, true);
                fr = new FileReader(originFile);
                reader = new BufferedReader(fr, 1024);
                while (true) {
                    data = reader.readLine();
                    if (data != null && isLogLine(data, type))
                        fw.write(data + "\n");
                    else if (data == null)
                        break;
                }
                fw.close();
                fr.close();
                reader.close();

            } catch (IOException e) {
                e.getMessage();
            }
        } else if (type == 1) {
            try {
                parsedLogFile = new File("Parsed_Pfix.log");
                fw2 = new FileWriter(parsedLogFile, true);
                fr2 = new FileReader(originFile);
                reader2 = new BufferedReader(fr2, 1024);
                while (true) {
                    data = reader2.readLine();
                    if (data != null && isLogLine(data, type))
                        fw2.write(data + "\n");
                    else if (data == null) {
                        break;
                    }
                }
                fw2.close();
                fr2.close();
                reader2.close();

            } catch (IOException e) {
                e.getMessage();
            }
        }


        return parsedLogFile;
    }

    protected void checkFileExist(File name) throws InterruptedException {
        try {
            if (!name.isFile())
                throw new NoSuchFileException("Файл " + name.getName() + " не найден по указанному пути");
        } catch (NoSuchFileException e) {
            System.out.println(e.getMessage());
            TimeUnit.SECONDS.sleep(10);
            System.exit(9);
        }
    }

    protected void logsMatcher(BufferedReader reader1, BufferedReader reader2) throws Exception {
        String lineFirst = "";
        String lineSecond = "";
        String[] lineFirstData;
        String[] lineSecondData;
        String extOrIdFirst;
        String extOrIdSecond;
        String dateFix;
        String datePfix;
        int lineFirstQuantity = -1;
        int lineSecondQuantity = -1;
        int iterationCounter = 0;
        int iterationSkipCounter = 0;
        int iterationNumber = 1;
        boolean rememberLine = false;

        while (true) {
            if (iterationCounter != 0 && iterationCounter % 9 == 0) {
                gatherLogs("===================================================End of iteration #" + iterationNumber + "==================================================\n");
                iterationNumber++;
                iterationSkipCounter = 0;
            }
            lineFirst = reader1.readLine();
            if (lineFirst == null) {
                break;
            }
            lineFirstData = lineFirst.split("\\u0001");
            extOrIdFirst = getExternalOrderId(lineFirstData, 0);
            lineFirstQuantity = getQuantity(lineFirstData, 0);
            if (!rememberLine) {
                lineSecond = reader2.readLine();
            }
            if (lineSecond == null) {
                break;
            }
            for (int i = 0; i < lineFirstData.length; i++) {
                if (lineFirstData[i].equals("39=1")) {
                    lineSecondData = lineSecond.split("\\u0005");
                    extOrIdSecond = getExternalOrderId(lineSecondData, 1);
                    lineSecondQuantity = getQuantity(lineSecondData, 1);
                    if (extOrIdFirst.equals(extOrIdSecond)) {
                        lineSecond = reader2.readLine();
                        lineSecondData = lineSecond.split("\\u0005");
                        extOrIdSecond = getExternalOrderId(lineSecondData, 1);
                        if (extOrIdSecond.equals("")) {
                            lineSecond = reader2.readLine();
                            lineSecondData = lineSecond.split("\\u0005");
                            if (lineFirstQuantity != lineSecondQuantity) {
                                gatherLogs("Error, missing event: " + getOrderStatusValue(getOrderStatusId(lineFirstData)) + " "
                                        + extOrIdFirst + " " + extOrIdSecond + " " + lineFirstQuantity + " " + lineSecondQuantity + "\n");
                                iterationCounter++;
                                iterationSkipCounter++;
                                rememberLine = false;
                                break;
                            }

                            dateFix = dateHelpers.formatStringDate(lineFirstData[0], "dd.MM.yyyy-HH:mm:ss.SSS", DateHelpers.DEFAULT_DATE_FORMAT);
                            datePfix = dateHelpers.formatStringDate(lineSecondData[0], "dd.MM.yyyy-HH:mm:ss.SSS", DateHelpers.DEFAULT_DATE_FORMAT);
                            gatherLogs("FIX time: " + dateFix + ", PFIX time: " + datePfix + ", " + dateHelpers.dateDifferenceInMs(dateFix, datePfix) + ", Event type: " + getOrderStatusValue(getOrderStatusId(lineFirstData)) + "\n");
                            iterationCounter++;
                            iterationSkipCounter++;
                            rememberLine = false;
                            break;
                        }
                    }
                } else if (lineFirstData[i].contains("39=2")) {
                    lineSecondData = lineSecond.split("\\u0005");
                    extOrIdSecond = getExternalOrderId(lineSecondData, 1);
                    if (extOrIdSecond.equals(extOrIdFirst)) {
                        lineSecond = reader2.readLine();
                        lineSecondQuantity = getQuantity(lineSecondData, 1);
                        if (!extOrIdFirst.equals("")) {
                            lineSecond = reader2.readLine();
                            lineSecondData = lineSecond.split("\\u0005");
                            if (lineFirstQuantity != lineSecondQuantity) {
                                gatherLogs("Error, missing event: " + getOrderStatusValue(getOrderStatusId(lineFirstData)) + " "
                                        + extOrIdFirst + " " + extOrIdSecond + " " + lineFirstQuantity + " " + lineSecondQuantity + "\n");
                                iterationCounter++;
                                iterationSkipCounter++;
                                rememberLine = false;
                                break;
                            }

                            dateFix = dateHelpers.formatStringDate(lineFirstData[0], "dd.MM.yyyy-HH:mm:ss.SSS", DateHelpers.DEFAULT_DATE_FORMAT);
                            datePfix = dateHelpers.formatStringDate(lineSecondData[0], "dd.MM.yyyy-HH:mm:ss.SSS", DateHelpers.DEFAULT_DATE_FORMAT);

                            gatherLogs("FIX time: " + dateFix + ", PFIX time: " + datePfix + ", " + dateHelpers.dateDifferenceInMs(dateFix, datePfix) + ", Event type: " + getOrderStatusValue(getOrderStatusId(lineFirstData)) + "\n");
                            iterationCounter++;
                            iterationSkipCounter++;
                            rememberLine = false;
                            break;
                        }
                    }
                } else if (lineFirstData[i].contains("39=")) {
                lineSecondData = lineSecond.split("\\u0005");
                extOrIdSecond = getExternalOrderId(lineSecondData, 1);
                lineSecondQuantity = getQuantity(lineSecondData, 1);
                if (lineFirstQuantity == lineSecondQuantity) {
                    if (extOrIdFirst.equals(extOrIdSecond)) {
                        dateFix = dateHelpers.formatStringDate(lineFirstData[0], "dd.MM.yyyy-HH:mm:ss.SSS", DateHelpers.DEFAULT_DATE_FORMAT);
                        datePfix = dateHelpers.formatStringDate(lineSecondData[0], "dd.MM.yyyy-HH:mm:ss.SSS", DateHelpers.DEFAULT_DATE_FORMAT);

                        gatherLogs("FIX time: " + dateFix + ", PFIX time: " + datePfix + ", " + dateHelpers.dateDifferenceInMs(dateFix, datePfix) + ", Event type: " + getOrderStatusValue(getOrderStatusId(lineFirstData)) + "\n");
                        iterationCounter++;
                        iterationSkipCounter++;
                        rememberLine = false;
                        break;
                    }
                } else if (lineFirstQuantity != lineSecondQuantity) {

                    gatherLogs("Error, missing event: " + getOrderStatusValue(getOrderStatusId(lineFirstData)) + " "
                            + extOrIdFirst + " " + extOrIdSecond + " " + lineFirstQuantity + " " + lineSecondQuantity + "\n");
                    iterationCounter++;
                    iterationSkipCounter++;
                    if (iterationSkipCounter == 2) {
                        rememberLine = true;
                    }
                    break;
                }
            }
            }
        }
    }

    //type: fix = 0; pfix=1
    protected String getExternalOrderId(String[] array, int type) {
        String extOrderId = "";
        if (type == 0) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].contains("37=marketreffid")) {
                    extOrderId = array[i].substring(3);
                } else if (array[i].contains("37=")) {
                    extOrderId = array[i].substring(3);
                }
            }
        } else if (type == 1) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].contains("513=marketreffid")) {
                    extOrderId = array[i].substring(4);
                } else if (array[i].contains("513=")) {
                    extOrderId = array[i].substring(4);
                }
            }
        }
        return extOrderId;
    }

    //type: fix = 0; pfix=1
    protected int getQuantity(String[] array, int type) {
        String quantityString = "";
        double quantityD = -1.0;
        int quantity = -1;
        if (type == 0) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].contains("38=")) {
                    quantityString = array[i].substring(3);
                    quantity = Integer.parseInt(quantityString);
                    break;
                }
            }
        } else if (type == 1) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].contains("7=")) {
                    quantityString = array[i].substring(2);
                    quantityD = Double.parseDouble(quantityString);
                    quantity = (int) quantityD * LOT_SIZE;
                    break;
                }
            }
        }
        return quantity;
    }

    protected int getOrderStatusId(String[] array) {
        String orderStatus;
        int orderStatusId = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i].contains("39=")) {
                orderStatus = array[i].substring(3);
                orderStatusId = Integer.parseInt(orderStatus);
            }
        }
        return orderStatusId;
    }

    protected String getOrderStatusValue(int orderStatusId) {
        String value;
        switch (orderStatusId) {
            case 0: {
                value = "Market/Limit placed";
                break;
            }
            case 1: {
                value = "Partial filled";
                break;
            }
            case 2: {
                value = "Market filled, close position";
                break;
            }
            case 4: {
                value = "Canceled";
                break;
            }
            case 5: {
                value = "Modify";
                break;
            }
            default:
                value = "Undefined order type!";
                break;

        }
        return value;
    }

    protected boolean isLogLine(String line, int type) {
        String[] array;
        boolean isLog = false;
        if (type == 0) {
            array = line.split("\\u0001");
            for (int i = 0; i < array.length; i++) {
                if (array[i].contains("37=")) {
                    isLog = true;
                } else {
                    continue;
                }
            }
        } else if (type == 1) {
            array = line.split("\\u0005");
            for (int i = 0; i < array.length; i++) {
                if (array[i].contains("out:0=17") || array[i].contains("out:0=27") || array[i].contains("out:0=50")) {
                    isLog = true;

                } else {
                    continue;
                }
            }
        }
        return isLog;
    }

    protected List<String> gatherLogs(String lineToAdd) {
        logs.add(lineToAdd);
        return logs;
    }

    protected void logDeltas(List<String> listToBeWritten) {
        File file = new File("Deltas.csv");
        FileWriter fr = null;
        BufferedWriter br = null;
        try {
            fr = new FileWriter(file, true);
            br = new BufferedWriter(fr, 1024);
            for (String aList : listToBeWritten) {
                br.write(aList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
