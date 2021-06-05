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
    public static FileWriter RLWriter;

    public static final String generalFile = Config.getInstance().getAsString("generalFile");
    public static final String shelvesFile = Config.getInstance().getAsString("shelfFile");
    public static final String gateOutFile = Config.getInstance().getAsString("gateOutFile");
    public static final String chargerFile = Config.getInstance().getAsString("chargerFile");
    public static final String getAutoBotFile = Config.getInstance().getAsString("getAutoBotFile");
    public static final String RLfile = Config.getInstance().getAsString("RLFile");

    public static List<FileWriter> manubotWriter = new LinkedList<>();

    public static void initFiles(Network network) {
        // Initialize file pointers
        try {
            // Separate files for each manubot
            for (ManuBot mb : network.getManuList()) {
                FileWriter manuWriter = new FileWriter(String.format("Results/Detail_ManuBot_%d.csv", mb.getID()), false);
                manuWriter.write("Time\tID\tXcord\tYcord\tEnergy\tState\tCNType\tDNType\tTaskID\tActive\n");
                manubotWriter.add(manuWriter);
            }

            generalWriter = new FileWriter(generalFile, true);

            shelvesWriter = new FileWriter(shelvesFile, false);
            shelvesWriter.write("Time\tSID\tLSize\tRTaskID\tBotID\tMode\n");

            gateOutWriter = new FileWriter(gateOutFile, false);
            gateOutWriter.write("Time\tGID\tBotID\tRTaskID\tTotal\n");

            chargerWriter = new FileWriter(chargerFile, false);
            chargerWriter.write("Time\tCID\tXcord\tYcord\tBotID\tEnergy\n");

            test_getAutoBot = new FileWriter(getAutoBotFile, false);
            test_getAutoBot.write("Time\tTaskID\tAutoBotID\tPurpose\n");

            RLWriter = new FileWriter(RLfile, false);

        } catch (IOException e) {
            System.out.println("Failed to open file!");
            e.printStackTrace();
        }
    }

    public static void chargerPrintFile(ManuBot mb, Charger ch, double timeNow) {
        try {
            chargerWriter.write(String.format("%.2f\t%d\t%.2f\t%.2f\t%d\t%.5f\n",
                    timeNow, ch.getID(), ch.getLocation().getX(), ch.getLocation().getY(), mb.getID(), mb.getResEnergy()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void manuPrintFile(ManuBot mb, Map map, double timeNow) {
        try {
            FileWriter manuWriter = manubotWriter.get(mb.getID());
            manuWriter.write(
                    String.format("%.2f\t%d\t%.2f\t%.2f\t%.3f\t%s\t%s\t%s\t%d\t%s\n",
                            timeNow, mb.getID(), mb.getLocation().getX(), mb.getLocation().getY(),
                            mb.getResEnergy(), mb.isTransporting, map.point2node(mb.getLocation()).getType(),
                            mb.workList.isEmpty() ? "REST" : map.point2node(mb.workList.get(0).getNextStop()).getType(),
                            mb.workList.isEmpty() ? -1 : mb.workList.get(0).getID(),
                            mb.workList.isEmpty() ? "NaN" : mb.workList.get(0).activate(timeNow))
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(120);
        }
    }

    public static void dumpFinal(Network network) {
        try {
            double sum = 0;
            int dead = 0;
            for (ManuBot mb : network.getManuList()) {
                sum += mb.isFunctional() ? mb.getResEnergy() : 0;
                if (!mb.isFunctional()) {
                    dead += 1;
                }
            }

            generalWriter.write(String.format(
                    "%d\t%d\t%d\t%d\t%d\t%s\t%.2f\t%.2f\t%.2f\t%d\t%d\t%d\t%d\t%.2f\n",
                    network.getManuList().size(),
                    network.getGateInList().size(),
                    network.getGateOutList().size(),
                    network.getShelfList().size(),
                    network.getChargerList().size(),
                    Config.getInstance().getAsBoolean("adaptive_charging") ? "RL" : Config.getInstance().getAsDouble("fixed_energy_charge_level"),
                    Double.parseDouble(Config.getInstance().getAsString("Gate_task_range").split(";")[0]),
                    Double.parseDouble(Config.getInstance().getAsString("Task_active_range").split(";")[0]),
                    Config.getInstance().getAsDouble("Factory_size"),
                    Config.getInstance().getAsInteger("Map_size"),
                    Config.getInstance().getAsInteger("Simulation_time"),
                    GateOut.count,
                    dead,
                    sum/network.getManuList().size()
            ));

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
            RLWriter.close();
        } catch (IOException e) {
            System.out.println("Failed to close file");
            e.printStackTrace();
        }
    }
}
