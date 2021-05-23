import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Ultilis {
    public static FileWriter generalWriter;
    public static FileWriter shelvesWriter;
    public static FileWriter gateOutWriter;
    public static FileWriter chargerWriter;
    public static FileWriter test_getAutoBot;

    public static List<FileWriter> manubotWriter = new LinkedList<>();

    public static void initFiles(Network network) {
        // Initialize file pointers
        try {
            // Separate files for each manubot
            for (ManuBot mb : network.getManuList()) {
                FileWriter manuWriter = new FileWriter(String.format("Results/Detail_ManuBot_%d.csv", mb.getId()), false);
                manuWriter.write("Time\tID\tXcord\tYcord\tEnergy\tState\tCNType\tDNType\tTaskID\tActive\n");
                manubotWriter.add(manuWriter);
            }

            generalWriter = new FileWriter("Results/General.csv", false);
            generalWriter.write("BotID\tEnergy\n");

            shelvesWriter = new FileWriter("Results/ShelvesDetail.csv", false);
            shelvesWriter.write("Time\tSID\tLSize\tRTaskID\tBotID\tMode\n");

            gateOutWriter = new FileWriter("Results/GateOutDetail.csv", false);
            gateOutWriter.write("Time\tGID\tBotID\tRTaskID\tTotal\n");

            chargerWriter = new FileWriter("Results/ChargerDetail.csv", false);
            chargerWriter.write("Time\tCID\tXcord\tYcord\tBotID\tEnergy\n");

            test_getAutoBot = new FileWriter("Test/testgetAutoBot.csv", false);
            test_getAutoBot.write("Time\tTaskID\tAutoBotID\n");

        } catch (IOException e) {
            System.out.println("Failed to open file!");
            e.printStackTrace();
        }
    }

    public static void chargerPrintFile(ManuBot mb, Charger ch, double timeNow) {
        try {
            chargerWriter.write(String.format("%.2f\t%d\t%.2f\t%.2f\t%d\t%.5f\n",
                    timeNow, ch.getID(), ch.getLocation().getX(), ch.getLocation().getY(), mb.getId(), mb.getResEnergy()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void manuPrintFile(ManuBot mb, Map map, double timeNow) {
        try {
            FileWriter manuWriter = manubotWriter.get(mb.getId());
            manuWriter.write(
                    String.format("%.2f\t%d\t%.2f\t%.2f\t%.3f\t%s\t%s\t%s\t%d\t%s\n",
                            timeNow, mb.getId(), mb.getLocationNow().getX(), mb.getLocationNow().getY(),
                            mb.getResEnergy(), mb.isTransporting, map.point2node(mb.getLocationNow()).getType(),
                            mb.workList.isEmpty() ? "REST" : map.point2node(mb.workList.get(0).getNextStop()).getType(),
                            mb.workList.isEmpty() ? 0 : mb.workList.get(0).getID(),
                            mb.workList.isEmpty() ? "NaN" : mb.workList.get(0).isActive)
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(120);
        }
    }

    public static void dumpFinal(Network network) {
        try {
            for (ManuBot mb : network.getManuList()) {
                generalWriter.write(String.format("%d\t%.2f\n", mb.getId(), mb.getResEnergy()));
            }
            generalWriter.write(String.format("Number of arrival task: %d\nNumber of request task: %d\n",
                    network.getArrivalListSize(), network.getActiveListSize()));

            int total = 0;
            for (TaskShelf tsh :network.getShelfList()) {
                total += tsh.getTaskList().size();
            }
            generalWriter.write(String.format("Number of task in shelves: %d\n",total));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeFile() {
        try {
            for (FileWriter fw : manubotWriter) {
                fw.close();
            }

            shelvesWriter.close();
            gateOutWriter.close();
            generalWriter.close();
            chargerWriter.close();
            test_getAutoBot.close();
        } catch (IOException e) {
            System.out.println("Failed to close file");
            e.printStackTrace();
        }
    }
}
