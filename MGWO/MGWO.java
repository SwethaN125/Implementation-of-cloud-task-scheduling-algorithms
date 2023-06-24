//package night2;

import java.util.Arrays;
import java.util.Random;

public class MGWO {
  
private static final Random rand = new Random();
private static final int numMachines=50;
private static final int MAX_ITER = 30; // maximum number of iterations
private static final int POP_SIZE = 20; // population size
private static final double A = 2.0; // alpha value


private static final double[] X_MIN = new double[numMachines];
private static final double[] X_MAX = new double[numMachines];
private static final double[] X_LB = new double[numMachines]; // lower bound of decision variables
private static final double[] X_UB = new double[numMachines]; // upper bound of decision variables

static {
    Arrays.fill(X_MIN, -5.0); // update X_MIN with a lower limit of -5.0
    Arrays.fill(X_MAX, 5.0); // update X_MAX with an upper limit of 5.0
    Arrays.fill(X_LB, -5.0); // lower bound of decision variables
    Arrays.fill(X_UB, 5.0); // upper bound of decision variables
}

//static int numMachines=50;


public static void main(String[] args){
    int numJobs = 100; // jobs to be scheduled (tasks)
    //int numMachines = 50;
    int[][] jobs = new int[numJobs][numMachines];
    for (int i = 0; i < numJobs; i++) {
        for (int j = 0; j < numMachines; j++) {
            jobs[i][j] = rand.nextInt(10) + 1; // assign a random integer value between 1 and 10
        }
    }

   
    System.out.println("Number of machines: " + numMachines);// number of machines
 
    int[] bestSolution = new int[numMachines];
     // best solution found so far
    double bestFitness = Double.MAX_VALUE; // best fitness value found so far
 // initialize population
    double[][] population = new double[POP_SIZE][numMachines];

   
    for (int i = 0; i < POP_SIZE; i++) {
        for (int j = 0; j < numMachines; j++) {
            population[i][j] = X_MIN[j] + rand.nextDouble() * (X_MAX[j] - X_MIN[j]);
        }
    }
    int[] machines = new int[numMachines];
    Arrays.fill(machines, 50);
    
    int[][] schedule = findSchedule(jobs, machines);
    //System.out.println("Schedule: " + Arrays.deepToString(schedule));
    int makespan = calculateMakespan(schedule);
    System.out.println("Makespan: " + makespan);
    // main loop
        for (int iter = 0; iter < MAX_ITER; iter++) {
            // sort population by fitness
            Arrays.sort(population, (a, b) -> {
                double fitnessA = getFitness(a, jobs);
                double fitnessB = getFitness(b, jobs);
                return Double.compare(fitnessA, fitnessB);
            });
        


            // update best solution and fitness value
            double fitness = getFitness(population[0], jobs);
            if (fitness < bestFitness) {
                bestSolution = decode(population[0], X_LB, X_UB, X_MIN, X_MAX);
                bestFitness = fitness;
            }

            // calculate a and A matrices
            //alpha wolves
            double[][] aMatrix = new double[POP_SIZE][numMachines];
            double[][] AMatrix = new double[POP_SIZE][numMachines];
            for (int i = 0; i < POP_SIZE; i++) {
                for (int j = 0; j < numMachines; j++) {
                    double alpha = A - iter * ((A - 1.0) / MAX_ITER);
                    double a = 2.0 * alpha * rand.nextDouble() - alpha;
                    double A_i = 2.0 * a * rand.nextDouble();
                    aMatrix[i][j] = a;
                    AMatrix[i][j] = A_i;
                }
            }

            // calculate C matrix
            //delta wolves
            double[][] CMatrix = new double[POP_SIZE][numMachines];
            for (int i = 0; i < POP_SIZE; i++) {
                for (int j = 0; j < numMachines; j++) {
                    double X1 = population[i][j];
                    double X2 = population[rand.nextInt(POP_SIZE)][j];
                   
                    if (rand.nextDouble() < 0.5) {
                        CMatrix[i][j] = X_MIN[j] + rand.nextDouble() * (X_MAX[j] - X_MIN[j]);
                    } else {
                        CMatrix[i][j] = X1 - aMatrix[i][j] * (Math.abs(X2 - population[i][j]));
                    }

                }
            }
        
 

            // calculate D matrix
            //beta wolves
            double[][] DMatrix = new double[POP_SIZE][numMachines];
            for (int i = 0; i < POP_SIZE; i++) {
                for (int j = 0; j < numMachines; j++) {
                    double X1 = population[i][j];
                    double X2 = bestSolution[j];
                    double D_i = Math.abs(CMatrix[i][j] - X1 * Math.sin(10 * Math.PI * X1) - X2 * Math.sin(10 * Math.PI * X2));
                    DMatrix[i][j] = D_i;
                }
            }


            // update population
            for (int i = 0; i < POP_SIZE; i++) {
                double[] wolf = new double[numMachines];
                for (int j = 0; j < numMachines; j++) {
                    double X1 = population[i][j];
                    double a = aMatrix[i][j];
                    double C = CMatrix[i][j];
                    double D = DMatrix[i][j];
                    wolf[j] = clip(X1 + a * D * (C - X1), X_LB[j], X_UB[j]);
                }
                population[i] = wolf;
            }
        }

        //System.out.println("Best solution found: " + Arrays.toString(bestSolution));
        //System.out.println("Best fitness found: " + bestFitness);
    }


    private static int[][] findSchedule(int[][] jobs, int[] machines) {
    	 int[][] schedule = new int[jobs.length][machines.length];
         int[] machineTimes = new int[machines.length];

         for (int i = 0; i < jobs.length; i++) {
             for (int j = 0; j < machines.length; j++) {
                 int machine = machines[j] - 1;//convert the 1-indexed machine IDs stored in the "machines" array to 0-indexed array indices that can be used to access the "machineTimes" array.
                 int jobTime = jobs[i][machine];
                 int start = machineTimes[machine];
                 int end = start + jobTime;
                 schedule[i][j] = end;
                 machineTimes[machine] = end;
             }
         }
         return schedule;
     }
    public static int calculateMakespan(int[][] schedule) {
        int[] machineTimes = new int[schedule[0].length];
        for (int i = 0; i < schedule.length; i++) {
            for (int j = 0; j < schedule[i].length; j++) {
                int machine = j;
                int jobTime = schedule[i][machine] - machineTimes[machine];
                machineTimes[machine] += jobTime;
            }
        }
        int makespan = machineTimes[0];
        for (int i = 1; i < machineTimes.length; i++) {
            if (machineTimes[i] > makespan) {
                makespan = machineTimes[i]/100;
            }
        }
        return makespan;
    }



/*public static int getFitness(double[] solution, int[][] jobs) {

}*/
    public static int getFitness(double[] solution, int[][] jobs) {
        int numJobs = jobs.length;
        int numMachines = jobs[0].length;
        int[] machineTime = new int[numMachines];
        for (int i = 0; i < numJobs; i++) {
            for (int j = 0; j < numMachines; j++) {
                int processingTime = jobs[i][j];
                int startTime = i == 0 ? 0 : machineTime[j];
                int completionTime = (int) Math.ceil(solution[j] * processingTime) + startTime;
                machineTime[j] = completionTime;
            }
        }
        int fitness = Arrays.stream(machineTime).max().getAsInt();
        return fitness;
    }


    // decode a solution to obtain actual values of decision variables
    private static int[] decode(double[] solution, double[] lb, double[] ub, double[] xmin, double[] xmax) {
        int n = solution.length;
        int[] x = new int[n];
        for (int i = 0; i < n; i++) {
            double a = (solution[i] - xmin[i]) / (xmax[i] - xmin[i]);
            x[i] = (int) Math.round(lb[i] + a * (ub[i] - lb[i]));
        }
        return x;
    }

    // clip a value to be within the lower and upper bounds
    private static double clip(double x, double lb, double ub) {
        return Math.max(Math.min(x, ub), lb);
    }
}


