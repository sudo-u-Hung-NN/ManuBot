
import java.util.*;

public class ComputingCenter {
    // Some constant:
    private final double P_tb;
    private final double EC;
    private final double AverageLength_GiTS;
    private final double AverageLength_STGo;
    private final double AverageLength_STS;
    private final double AverageLength_GiTGo;
    private final double AverageWorkTrip;
    private final double AverageTimeTrip;
    private final Dictionary<String, Double> variables;
    /**
     * @author Nguyen Nang Hung
     * @return average length from a gate_in to a shelf
     */
    private double AverageLength_GiTS(List<GateIn> gateInList, List<TaskShelf> shelfList){
        double S = 0.0;
        for (GateIn gt : gateInList){
            for (TaskShelf ts : shelfList){
                S += gt.getLocation().getLength(ts.getLocation());
            }
        }
        S = 1.302 * S/(gateInList.size() * shelfList.size());
        return S;
    }

    /**
     * @author Nguyen Nang Hung
     * @return average length from a shelf to a gate_out
     */
    private double AverageLength_STGo(List<TaskShelf> shelfList, List<GateOut> gateOutList){
        double S = 0.0;
        for (TaskShelf ts: shelfList){
            for (GateOut gt : gateOutList){
                S += ts.getLocation().getLength(gt.getLocation());
            }
        }
        return 1.302 * S/(shelfList.size() * gateOutList.size());
    }

    /**
     * @author Nguyen Nang Hung
     * @param shelfList: list of all shelves
     * @return average length from a random shelf, to another random shelf
     */
    private double AverageLength_STS(List<TaskShelf> shelfList){
        double S = 0.0;
        for (TaskShelf ts1: shelfList) {
            for (TaskShelf ts2 : shelfList) {
                S += ts1.getLocation().getLength(ts2.getLocation());
            }
        }
        return 1.302 * S/(shelfList.size() * (shelfList.size() - 1));
    }

    /**
     * @author Nguyen Nang Hung
     * @param gateInList: list of gate_in
     * @param gateOutList: list of gate_out
     * @return average length from a random gate in to a random gate out
     */
    private double AverageLength_GiTGo(List<GateIn> gateInList, List<GateOut> gateOutList){
        double S = 0;
        for (GateIn gti: gateInList){
            for (GateOut gto: gateOutList){
                S += gti.getLocation().getLength(gto.getLocation());
            }
        }
        return 1.302 * S/(gateInList.size() * gateOutList.size());
    }

    /**
    * return: average take tasks from Gate_in to a Self trip length
     */

    private double AverageGiTSTrip(Network net){
        return (0.5 * (AverageLength_GiTGo(net.getGateInList(), net.getGateOutList())
                + AverageLength_GiTS(net.getGateInList(), net.getShelfList()))
                + AverageLength_GiTS(net.getGateInList(), net.getShelfList()));
    }

    /**
     * return: average take tasks from Self to a Gate out trip length
     */

    private double AverageSTGoTrip(Network net){
        return ((1.0/3) * (AverageLength_STS(net.getShelfList())
                + AverageLength_GiTS(net.getGateInList(), net.getShelfList())
                + AverageLength_STGo(net.getShelfList(), net.getGateOutList()))
                + AverageLength_STGo(net.getShelfList(), net.getGateOutList()));
    }

    /**
     * @author Nguyen Nang Hung
     * @param net
     * @return average work trip length:
     * including: ES1: take tasks from gate_in to a shelf, ES2: take tasks from shelf to gate_out
     */
    private double AverageWorkTrip(Network net){
        return AverageGiTSTrip(net) + AverageSTGoTrip(net);
    }

    /**
     * @author Nguyen Nang Hung
     * @return average time an autoBot finish one task
     */
    private double AverageTimeTrip(Network net){
        double v = net.getManuList().get(0).getSpeed();
        //System.out.println("Speed = " + v);
        return  1.0/v * AverageWorkTrip(net);
    }


    /**
     * @author Nguyen Nang Hung
     * @param net
     * @return average wattage P_tb
     */
    private double AverageWattage(Network net){
        double ESW = AverageLength_STGo(net.getShelfList(), net.getGateOutList()) +
                AverageLength_GiTS(net.getGateInList(), net.getShelfList());
        
        double ESR = AverageWorkTrip(net) - ESW;

        double PW = net.getManuList().get(0).getEWperSec();
        double PR = net.getManuList().get(0).getERperSec();
        
        return (ESW * PW + ESR * PR) / (ESR + ESW);
    }

    /**
     * @author Nguyen Nang Hung
     * @return avergae energy of the net right now
     */
    private double AverageEnergy(Network network){
        double S = 0;
        List<ManuBot> manulist = network.getManuList();
        if (manulist.isEmpty()) return 0;
        for (ManuBot bot:manulist){
            S += bot.getResEnergy();
        }
        return S/manulist.size();
    }

    private double AverageChargingTimeLeft(Network network){
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
     * @param currentEnergy: current energy of the AutoBot doing this calculation
     * @param network: Network
     * @return the estimate standard deviation of energy levels at the time t + mu
     */
    private double MuSigmaEnergy(Network network, double currentEnergy, double mu){
        List<ManuBot> ChargingAutoBot = network.getChargingList();
        double mu_bar = AverageChargingTimeLeft(network);
        int NA = network.getManuList().size();
        int k = ChargingAutoBot.size();
        double Etb = AverageEnergy(network);

        /**
         * Computing Qk
         */
        double firstTerm = 0;
        for (ManuBot mb : ChargingAutoBot){
            firstTerm += Math.pow(mb.getResEnergy() + mu_bar * EC,2);
        }
        // Second term computing
        double secondTerm = 0;
        for (ManuBot mb: network.getManuList()){
            if (!ChargingAutoBot.contains(mb)){
                secondTerm += Math.pow(mb.getResEnergy(), 2);
            }
        }
        // Third term computing
        double thirdTerm = NA * Math.pow(Etb + 1.0/NA * k * mu_bar * EC, 2);
        double Qk = firstTerm + secondTerm + thirdTerm;

        /**
         * Computing Rk
         */
        double Rk = (NA * Etb + k * mu_bar * EC)*(2 * P_tb - EC * 1.0/NA) - EC * currentEnergy;

        /**
         * Computing Uk
         */
        double Uk = (NA - 1) * Math.pow(P_tb, 2) + Math.pow(EC - P_tb, 2) - Math.pow(EC*1.0/NA - P_tb, 2);

        assert Qk > 0;
        assert Rk > 0;
        assert Uk > 0;
        return Math.sqrt( 1.0/NA * (Qk - 2 * mu * Rk + mu * mu * Uk));
    }

    /**
     * @author Nguyen Nang Hung
     * @param network
     * @return current standard deviation of energy levels
     */
    private double NowSigmaEnergy(Network network){
        int NA = network.getManuList().size();
        double Etb = AverageEnergy(network);
        double Sum = 0;
        for (ManuBot mb: network.getManuList()){
            Sum += Math.pow(mb.getResEnergy() - Etb, 2);
        }
        return Math.sqrt(1.0/NA * Sum);
    }

    /**
     * @author Nguyen Nang Hung
     * @param network: Network
     * @param currentEnergy: check upper function
     * @param mu: the time in the future we wish to look ahead
     * @return the change in the standard energy deviation
     */
    private double DeltaSigmaEnergy(Network network, double currentEnergy, double mu){
        return MuSigmaEnergy(network, currentEnergy, mu) - NowSigmaEnergy(network);
    }

    /**
     * @author Nguyen Nang Hung
     * @return the change in average energy of a network
     */
    private double DeltaAverageEnergy(Network net, double mu, int k){
        double EC = net.ChargerList.get(0).getECperSec();
        int NA = net.getManuList().size();
        if (NA == 0){
            System.out.println("NA = 0, in DeltaEnergy function");
            System.exit(-1);
        }
        double mu_bar = AverageChargingTimeLeft(net);
        return mu * (EC/NA - P_tb) + k * mu_bar * EC/NA;
    }

    /**
     * @author Nguyen Nang Hung
     * @param net
     * @return F(n, k)
     */
    private double TaskDoneRate(Network net){
        int k = net.getChargingList().size();
        return (net.getManuList().size() - k)/AverageTimeTrip(net);
    }

    private void printDictionary() {
        System.out.println("Printing Computation values ...");
        Enumeration<String> keys = this.variables.keys();
        for (; keys.hasMoreElements();){
            String E = keys.nextElement();
            System.out.println(E + " = " + this.variables.get(E));

        }
        System.out.println("Done printing dictionary");
    }

    // Constructor
    public ComputingCenter(Network network){
        this.variables = new Hashtable<>();

        this.P_tb = this.AverageWattage(network);
        this.variables.put("P_tb", this.P_tb);

        this.EC = network.ChargerList.get(0).getECperSec();
        this.variables.put("E_C", this.EC);

        this.AverageLength_GiTGo = this.AverageLength_GiTGo(network.getGateInList(), network.getGateOutList());
        this.variables.put("Average length from a Gate_in to a Gate_out", this.AverageLength_GiTGo);

        this.AverageLength_GiTS = this.AverageLength_GiTS(network.getGateInList(), network.getShelfList());
        this.variables.put("Average length from a Gate_in to a Shelf", this.AverageLength_GiTS);

        this.AverageLength_STGo = this.AverageLength_STGo(network.getShelfList(), network.getGateOutList());
        this.variables.put("Average length from a Shelf to a Gate_out", this.AverageLength_STGo);

        this.AverageLength_STS = this.AverageLength_STS(network.getShelfList());
        this.variables.put("Average length from a shelf to another shelf", this.AverageLength_STS);

        this.AverageWorkTrip = this.AverageWorkTrip(network);
        this.variables.put("Average length to finish a package/task", this.AverageWorkTrip);

        this.AverageTimeTrip = this.AverageTimeTrip(network);
        this.variables.put("Average time to finish a package/task", this.AverageTimeTrip);

        // Print information
        this.printDictionary();
    }

    // Getter

    public double getP_tb() {
        return P_tb;
    }

    public double getEC() {
        return EC;
    }

    public double getAverageLength_GiTS() {
        return AverageLength_GiTS;
    }

    public double getAverageLength_STGo() {
        return AverageLength_STGo;
    }

    public double getAverageLength_STS() {
        return AverageLength_STS;
    }

    public double getAverageLength_GiTGo() {
        return AverageLength_GiTGo;
    }

    public double getAverageWorkTrip() {
        return AverageWorkTrip;
    }

    public double getAverageTimeTrip() {
        return AverageTimeTrip;
    }


    // Selecting section
    /**
     * @author Le Minh An
     * function choose the autoBot to get the new occured task
     * @param PointX: location of the packet
     * @param PointY: destination of the packet
     * @return id of the chosen autoBot
     */
    public int getAutoBotFromXTY(Network net, point PointX, point PointY){
        // thứ tự ưu tiên:  Không sạc và đủ năng lượng đi lấy hàng và chuyển hàng
        //                  Không sạc và đi được đến cổng(hoặc ngăn chứa)
//                          Đang sạc và đủ năng lượng hoàn thành lấy hàng và chuyển hàng
//                          đang sạc và chỉ đủ đến cổng
//                          trả về -1

        int[] chooseID = new int[4];
        double[] optimalEnergy = new double[4];
        for (int  i = 0; i < 4; i++){
            chooseID[i] = -1;
            optimalEnergy[i] = 0.0;
        }
//        int chooseID = -1;              // The uncharged manubot can complete task
//        int chooseID1 = -1;             // The uncharged manubot can go to gate
//        int chooseID2 = -1;             // The charging manubot can complete task
//        int chooseID3 = -1;             // The charging manubot can go to gate
//        double optimalEnergy = 0;       // Optimize time that uncharged manubot can complete task
//        double optimalEnergy1 = 0;
//        double optimalEnergy2 = 0;
//        double optimalEnergy3 = 0;

        for(ManuBot mb: net.getManuList()) {
            if (!mb.workList.isEmpty()){
//                System.out.println(mb.getID());
                continue;
            }
            double lengthTX = mb.getLocation().getLength(PointX)*1.302;          // length from current manubot's location to Gate
            double lengthXTY = PointX.getLength(PointY)*1.302;                      // length from gate to shelf
            double estimateTimeX = lengthTX / mb.getSpeed();                             // estimate time to Gate
            double estimateTimeY = lengthXTY / mb.getSpeed();
//            System.out.println("1.TX: " + lengthTX);
//            System.out.println("XTY: " + lengthXTY);
//            System.out.println("EX: " + estimateTimeX);
//            System.out.println("EY: " + estimateTimeY);
//            System.out.println(mb.getResEnergy());
//            System.out.println(((estimateTimeX + estimateTimeY) * mb.getERperSec()));
            double energyCompleteTask = estimateTimeX * mb.getERperSec() + estimateTimeY * mb.getEWperSec();
            double diffEnergyComTask = (mb.getResEnergy() - energyCompleteTask) * 0.8;

            double energyGetTask = estimateTimeX * mb.getERperSec();
            double diffEnergyGetTask = mb.getResEnergy() - energyGetTask;
            if (diffEnergyComTask >= 0) {                                                   // Manubot can take task and complete mission
                if (mb.getChargingTimeLeft() == 0) {                                      // Neu robot khong sac thi se di lay hang
                    if (diffEnergyComTask > optimalEnergy[0]){
                        chooseID[0] = mb.getID();
                        optimalEnergy[0] = diffEnergyComTask;
                    }
                }

                if (diffEnergyComTask > optimalEnergy[2]) {
                    chooseID[2] = mb.getID();
                    optimalEnergy[2] = diffEnergyComTask;
                }
            }
            if (diffEnergyGetTask >= 0){
                if (mb.getChargingTimeLeft() == 0){
                    if (diffEnergyGetTask > optimalEnergy[1]){
                        chooseID[1] = mb.getID();
                        optimalEnergy[1] = diffEnergyGetTask;
                    }
                }
                if (diffEnergyGetTask > optimalEnergy[3]) {
                    chooseID[3] = mb.getID();
                    optimalEnergy[3] = diffEnergyGetTask;
                }
            }
        }
        if (chooseID[0] != -1)
            return chooseID[0];
        if (chooseID[1] != -1)
            return chooseID[1];
        if (chooseID[2] != -1)
            return chooseID[2];
        return chooseID[3];
    }

    // Timing section
    public double getChargingTime(Network network, ManuBot mb, double cycleTime) {
        double mu_bar = AverageChargingTimeLeft(network);
        int NA = network.getManuList().size();
        int k = network.getChargingList().size();
        double Etb = AverageEnergy(network);

//        double Rk = (NA * Etb + k * mu_bar * EC)*(2 * P_tb - EC * 1.0/NA) - EC * mb.getResEnergy();
//        if (Rk <= 0) {
//            System.out.println("False Rk computed");
//            System.exit(8);
//        }
//
//        double Uk = (NA - 1) * Math.pow(P_tb, 2) + Math.pow(EC - P_tb, 2) - Math.pow(EC*1.0/NA - P_tb, 2);
//        if (Uk <= 0) {
//            System.out.println("False Uk computed");
//            System.exit(9);
//        }

        // "Gradient" Descent
//        double mu_atsk = Rk/Uk;
        double mu_atsk = 30;
        double deltaF = 0;
        double F_old = this.TargetFunction(network, mu_atsk, mb.getResEnergy(), k);
        while (deltaF >= 0 && mu_atsk >= cycleTime) {
            double F_new = this.TargetFunction(network, mu_atsk - cycleTime, mb.getResEnergy(), k);
            deltaF = F_new - F_old;
            mu_atsk = mu_atsk - cycleTime;
            F_old = F_new;
        }

        if (mu_atsk <= 0) {
            System.out.println("getChargingTime return false !");
            System.exit(10);
        }
        // Print to file
//        try {
//            Ultilis.test_SCA.write(String.format("%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%d\t%.3f\n",
//                    mb.getResEnergy(), Rk, Uk, Rk/Uk, Etb, k, mu_atsk));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return mu_atsk;
    }

    private double TargetFunction(Network network, double mu, double currentEnergy, int k) {
//        double deltaSE = DeltaSigmaEnergy(network, currentEnergy, mu);
        double deltaAE = DeltaAverageEnergy(network, mu, k);
        double taskDR = TaskDoneRate(network);

        return deltaAE + taskDR;
    }
}
