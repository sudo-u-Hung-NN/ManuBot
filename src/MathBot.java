import sun.nio.ch.Net;

import java.util.List;

public class MathBot {
    // Some constant:
    private final double P_tb;
    private final double EC;
    private final double AverageLength_GiTS;
    private final double AverageLength_STGo;
    private final double AverageLength_STS;
    private final double AverageLength_GiTGo;
    private final double AverageWorkTrip;
    private final double AverageTimeTrip;
    /**
     * @author Nguyen Nang Hung
     * @return average length from a gate_in to a shelf
     */
    private double AverageLength_GiTS(List<Gate> gateInList, List<TaskShelf> shelfList){
        double S = 0.0;
        for (Gate gt : gateInList){
            for (TaskShelf ts : shelfList){
                S += gt.getLocation().getLength(ts.getLocation());
            }
        }
        S = S/(gateInList.size() * shelfList.size());
        return S;
    }

    /**
     * @author Nguyen Nang Hung
     * @return average length from a shelf to a gate_out
     */
    private double AverageLength_STGo(List<TaskShelf> shelfList, List<Gate> gateOutList){
        double S = 0.0;
        for (TaskShelf ts: shelfList){
            for (Gate gt : gateOutList){
                S += ts.getLocation().getLength(gt.getLocation());
            }
        }
        return S/(shelfList.size() * gateOutList.size());
    }

    /**
     * @author Nguyen Nang Hung
     * @param shelfList: list of all shelves
     * @return average length from a random shelf, to another random shelf
     */
    private double AverageLength_STS(List<TaskShelf> shelfList){
        double S = 0.0;
        int count = 0;
        for (TaskShelf ts1: shelfList) {
            for (TaskShelf ts2 : shelfList) {
                count++;
                S += ts1.getLocation().getLength(ts2.getLocation());
            }
        }
        return S/(count*count - shelfList.size());
    }

    /**
     * @author Nguyen Nang Hung
     * @param gateInList: list of gate_in
     * @param gateOutList: list of gate_out
     * @return average length from a random gate in to a random gate out
     */
    private double AverageLength_GiTGo(List<Gate> gateInList, List<Gate> gateOutList){
        double S = 0;
        for (Gate gti: gateInList){
            for (Gate gto: gateOutList){
                S += gti.getLocation().getLength(gto.getLocation());
            }
        }
        return S/(gateInList.size() * gateOutList.size());
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
        return  1.302/net.getManuList().get(0).getSpeed() * AverageWorkTrip(net);
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
    public double DeltaSigmaEnergy(Network network, double currentEnergy, double mu){
        return MuSigmaEnergy(network, currentEnergy, mu) - NowSigmaEnergy(network);
    }

    /**
     * @author Nguyen Nang Hung
     * @return the change in average energy of a network
     */
    public double DeltaAverageEnergy(Network net, double mu, int k){
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
    public double TaskDoneRate(Network net){
        int k = net.getChargingList().size();
        return (net.getManuList().size() - k)/AverageTimeTrip(net);
    }

    // Constructor
    public MathBot(Network network){
        this.P_tb = this.AverageWattage(network);
        this.EC = network.ChargerList.get(0).getECperSec();
        this.AverageLength_GiTGo = this.AverageLength_GiTGo(network.getGateInList(), network.getGateOutList());
        this.AverageLength_GiTS = this.AverageLength_GiTS(network.getGateInList(), network.getShelfList());
        this.AverageLength_STGo = this.AverageLength_STGo(network.getShelfList(), network.getGateOutList());
        this.AverageLength_STS = this.AverageLength_STS(network.getShelfList());
        this.AverageWorkTrip = this.AverageWorkTrip(network);
        this.AverageTimeTrip = this.AverageTimeTrip(network);
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
}
