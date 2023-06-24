package HGSO1;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import net.sourceforge.jswarm_pso.FitnessFunction;

import java.util.ArrayList;
import java.util.List;

public class HGSO {
    private static final int POPULATION_SIZE = 30;
    private static final int MAX_ITERATIONS = 30;
    private static final double RADIUS_INITIAL = 10.0;
    private static final double RADIUS_MINIMUM = 0.1;
    private static final double ALPHA = 0.05;
    private static final double BETA = 0.5;
    private static final double GAMMA = 0.1;
    public static Glowworm best=null;
    public static List<Cloudlet> schedule(List<Cloudlet> cloudletList, List<Vm> vmList) {
        List<Glowworm> glowworms = initializeGlowworms(cloudletList,vmList);
        double radius = RADIUS_INITIAL;
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            evaluateFitness(glowworms, cloudletList, vmList);
            glowworms.sort((gw1, gw2) -> Double.compare(gw2.getFitness(), gw1.getFitness()));
            radius = updateRadius(radius);
            updateLuciferinLevels(glowworms, radius);
            moveGlowworms(glowworms, radius);
            addNewGlowworms(glowworms,cloudletList, vmList);
        }
        evaluateFitness(glowworms, cloudletList, vmList);
        glowworms.sort((gw1, gw2) -> Double.compare(gw2.getFitness(), gw1.getFitness()));
        for(Glowworm glowworm:glowworms)
        	System.out.println("\nFitness: "+glowworm.getFitness()/1000);
        System.out.println();
        best=glowworms.get(0);
        for(int i=0;i<cloudletList.size();i++)
        	cloudletList.get(i).setVmId((int)best.position[i]);
        return cloudletList;
    }
    
    private static List<Glowworm> initializeGlowworms(List<Cloudlet> cloudletList,List<Vm> vmList) {
        List<Glowworm> glowworms = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Glowworm glowworm = new Glowworm(cloudletList,vmList);
            glowworms.add(glowworm);
        }
        return glowworms;
    }
    
    private static void evaluateFitness(List<Glowworm> glowworms, List<Cloudlet> cloudletList, List<Vm> vmList) {
        for (Glowworm glowworm : glowworms) {
            List<Cloudlet> vmCloudlets = new ArrayList<>();
            for (Cloudlet cloudlet : cloudletList) {
                if (glowworm.getVMs().contains(cloudlet.getVmId())) {
                    vmCloudlets.add(cloudlet);
                }
            }
            double fitness = glowworm.getFitness();
            glowworm.setFitness(fitness);
        }
    }
    
    private static double updateRadius(double radius) {
        radius = radius * (1 - ALPHA);
        if (radius < RADIUS_MINIMUM) {
            radius = RADIUS_MINIMUM;
        }
        return radius;
    }
    
    private static void updateLuciferinLevels(List<Glowworm> glowworms, double radius) {
        for (Glowworm glowworm : glowworms) {
            double luciferin = glowworm.getLuciferin();
            double newLuciferin = luciferin + BETA * (1 - luciferin);
            for (Glowworm otherGlowworm : glowworms) {
                if (!glowworm.equals(otherGlowworm)) {
                    double distance = glowworm.distanceTo(otherGlowworm);
                    if (distance <= radius) {
                        newLuciferin += GAMMA * (1 - newLuciferin);
                    }
                }
            }
            glowworm.setLuciferin(newLuciferin);
        }
    }
    
    private static void moveGlowworms(List<Glowworm> glowworms, double radius) {
        for (Glowworm glowworm : glowworms) {
            Glowworm brighterGlowworm = null;
            for (Glowworm otherGlowworm : glowworms) {
                if (!glowworm.equals(otherGlowworm)) {
                    double distance = glowworm.distanceTo(otherGlowworm);
                    if (distance <= radius) {
                        if (brighterGlowworm == null || otherGlowworm.getFitness() > brighterGlowworm.getFitness()) {
                            brighterGlowworm = otherGlowworm;
                        }
                    }
                }
            }
            if (brighterGlowworm != null) {
                glowworm.moveTo(brighterGlowworm);
            }
        }
    }
    
    private static void addNewGlowworms(List<Glowworm> glowworms,List<Cloudlet> cloudletList, List<Vm> vmList) {
        while (glowworms.size() < POPULATION_SIZE) {
            Glowworm glowworm = new Glowworm(cloudletList,vmList);
            glowworms.add(glowworm);
        }
    }
}