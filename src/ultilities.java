import java.util.List;

public class ultilities {
    /**
     * @author Nguyen Nang Hung
     * @return average length from a gate_in to a shelf
     */
    public static double AverageLength_GTS(){

        return 0;
    }

    /**
     * @author Nguyen Nang Hung
     * @return average length from a shelf to a gate_out
     */
    public static double AverageLength_STG(){
        return 0;
    }

    /**
     * @author Nguyen Nang Hung
     * @return average wattage of an autobot
     */
    public static double AverageWattage(){
        return 0;
    }

    /**
     * @author Nguyen Nang Hung
     * @return avergae energy of the net right now
     */
    public static double AverageEnergy(Network network){
        double S = 0;
        List<ManuBot> manulist = network.getManuList();
        if (manulist.isEmpty()) return 0;
        for (ManuBot bot:manulist){
            S += bot.getResEnergy();
        }
        return S/manulist.size();
    }

    public static double AverageChargingTimeLeft(Network network){
        double t = 0;
        List<ManuBot> manulist = network.getManuList();
        if (manulist.isEmpty()) return 0;
        for (ManuBot bot:manulist){
            t += bot.getChargingTimeLeft();
        }
        return t/manulist.size();
    }

    /**
     * @author Nguyen Nang Hung
     * @return the change in average energy of a network
     */
    public static double DeltaEnergy(Network net, double mu, int k){
        double EC = net.ChargerList.get(0).getECperSec();
        int NA = net.getManuList().size();
        if (NA == 0){
            System.out.println("NA = 0, in DeltaEnergy function");
            System.exit(-1);
        }
        double P_tb = net.getAverageWattage();
        double mu_bar = AverageChargingTimeLeft(net);
        return mu * (EC/NA - P_tb) + k * mu_bar * EC/NA;
    }

    /**
     * @author Nguyen Nang Hung
     * @return the change in standard deviation of a network
     */
    public static double DeltaSigmaEnergy(){
        return 0;
    }

    /**
     * @author Nguyen Nang Hung
     * ultilities for computing Delta sigma energy
     */
    public static double Qk(){
        return  0;
    }

    public static double Rk(){
        return 0;
    }

    public static double Uk(){
        return 0;
    }
}
