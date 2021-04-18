import java.util.List;

public class MathBot {
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
     * @author Nguyen Nang Hung
     * @param net
     * @return average work trip length:
     * including: ES1: take tasks from gate_in to a shelf, ES2: take tasks from shelf to gate_out
     */
    private double AverageWorkTrip(Network net){
        double ES1 = (0.5 * (AverageLength_GiTGo(net.getGateInList(), net.getGateOutList())
                + AverageLength_GiTS(net.getGateInList(), net.getShelfList()))
                + AverageLength_GiTS(net.getGateInList(), net.getShelfList()));

        double ES2 = (0.33 * (AverageLength_STS(net.getShelfList())
                + AverageLength_GiTS(net.getGateInList(), net.getShelfList())
                + AverageLength_STGo(net.getShelfList(), net.getGateOutList()))
                + AverageLength_STGo(net.getShelfList(), net.getGateOutList()));
        return ES1 + ES2;
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
     * @return the change in average energy of a network
     */
    private double DeltaAverageEnergy(Network net, double mu, int k){
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
    private double DeltaSigmaEnergy(){
        return 0;
    }

    /**
     * @author Nguyen Nang Hung
     * ultilities for computing Delta sigma energy
     */
    private double Qk(List<MathBot> ChargingAutoBot, Network network){
        return  0;
    }

    private double Rk(){
        return 0;
    }

    private double Uk(){
        return 0;
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
    
    public MathBot(){
        
    }
}
