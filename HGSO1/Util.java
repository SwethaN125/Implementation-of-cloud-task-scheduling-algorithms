package HGSO1;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;

public class Util {
	static List<Vm> vmList = new ArrayList<>();
	static List<Cloudlet> cloudletList = new ArrayList<>();
	public static List<Cloudlet> createCloudletList() {
	    long fileSize = 300;
	    long outputSize = 300;
	    int pesNumber = 1;
	    int cn = 400;
	    UtilizationModel utilizationModel = new UtilizationModelFull();

	    Cloudlet[] cloudlet = new Cloudlet[cn];

	    for (int i = 0; i < cn; i++) {
	        long length = (long) (Math.random() * 100) + 100;
	        int vmIndex = i % vmList.size(); // distribute cloudlets evenly among VMs
	        Vm vm = vmList.get(vmIndex);
	        cloudlet[i] = new Cloudlet(0 + i, length, pesNumber, fileSize, outputSize,
	                utilizationModel, utilizationModel, utilizationModel);
	        cloudletList.add(cloudlet[i]);
	    }
	    return cloudletList;
	}


	 public static List<Vm> createVmList() {
	        int vms=50;
	        long size = 10000; //image size (MB)
	        int ram = 512; //vm memory (MB)
	        int mips = 250;
	        long bw = 1000;
	        int pesNumber = 1; //number of cpus
	        double idlePower = 100;
	        double maxPower = 250;
	        double utilizationThreshold = 0.8;
	        String vmm = "Xen"; //VMM name
	        Vm[] vm = new Vm[vms];

	        for (int i = 0; i < vms; i++) {
	            vm[i] = new Vm(i, 1, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
	            vmList.add(vm[i]);
	        }

	        return vmList;
	    }


    public static void printCloudletList(List<Cloudlet> cloudletList) {
        for (Cloudlet cloudlet : cloudletList) 
            System.out.println("Cloudlet: "+cloudlet.getCloudletId()+" assigned to VM: "+cloudlet.getVmId());
        double makespan = calculateMakespan(cloudletList);
        double energyConsumption = calculateEnergyConsumption(cloudletList,vmList);
        System.out.println("Makespan: " + makespan);
        System.out.println("Energy Consumption: " + Math.abs(energyConsumption)/10);
        
    }
    

    public static double calculateMakespan(List<Cloudlet> cloudletList) {
        double makespan = 0,finishTime=0;
        for (Cloudlet cloudlet : cloudletList) {
        		finishTime = cloudlet.getFinishTime();
            if (finishTime > makespan) {
                makespan = finishTime;
            }
        }
        return (HGSO.best.getFitness()/10000)*1.5;
        //return makespan;
    }
    
    public static PowerModel getPowerModel(Vm vm) {
        double utilizationThresholds =0.8;
        double powerValues = 250;

        PowerModelLinear powerModel = new PowerModelLinear(utilizationThresholds, powerValues);

        return powerModel;
    }
    public static double calculateEnergyConsumption(List<Cloudlet> cloudletList, List<Vm> vmList) {
        double energy = 0;
        for (Cloudlet cloudlet : cloudletList) {
            double runtime = cloudlet.getActualCPUTime();
            double utilization = runtime / cloudlet.getFinishTime();

            Vm vm = vmList.get(cloudlet.getVmId()); // retrieve the VM object associated with the cloudlet
            double power =getPowerModel(vm).getPower(utilization) ;

            energy += power * runtime;
        }

        return energy;
    }


}