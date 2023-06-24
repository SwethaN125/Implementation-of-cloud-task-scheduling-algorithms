package HGSO1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

public class Glowworm {
    public List<Vm> vmList;
    public List<Cloudlet> cloudletList;
    public double[] position;
    private double luciferin;
    private double fitness;
    public Glowworm(List<Cloudlet> cloudletList,List<Vm> vmList) {
        this.cloudletList = cloudletList;
        this.vmList=vmList;
        Random random = new Random();
        this.position = new double[cloudletList.size()];
        for (int i = 0; i < cloudletList.size(); i++) {
            this.position[i] = Math.random()*50;
        }
        this.luciferin = random.nextDouble();
    }
    public double[] getPosition() {
        return position;
    }

    public double getLuciferin() {
        return luciferin;
    }

    public void setLuciferin(double luciferin) {
        this.luciferin = luciferin;
    }
    public void setFitness(double fitness) {
    	this.fitness=fitness;
    }
    public double getFitness() {
        double fitness = 0;
        for (int i = 0; i < cloudletList.size(); i++) {
            Cloudlet cloudlet = cloudletList.get(i);
            fitness += cloudlet.getCloudletLength() * position[i];
        }
        return fitness;
    }
    public List<Vm> getVMs() {
        return vmList;
    }
    public double distanceTo(Glowworm other) {
        double distance = 0;
        double[] otherPosition = other.getPosition();
        for (int i = 0; i < position.length; i++) {
            double diff = position[i] - otherPosition[i];
            distance += diff * diff;
        }
        return Math.sqrt(distance);
    }
    public void moveTo(Glowworm other) {
        double[] otherPosition = other.getPosition();
        double[] newPosition = new double[position.length];
        double stepSize = 0.1;
        for (int i = 0; i < position.length; i++) {
            double diff = otherPosition[i] - position[i];
            newPosition[i] = position[i] + stepSize * diff;
        }
        this.position = newPosition;
    }
}
