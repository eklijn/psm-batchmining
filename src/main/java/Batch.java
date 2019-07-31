import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

public class Batch {
    public int size;
    public int batchID;
    public List<Trace> traces;
    public List<String> caseIDs;
    public double[] interArrivalTimes;
    //mean and standard deviation inter-arrival time within batch
    public double meanInterArrivalTime; public double sdInterArrivalTime;
    public long startWaitFirstCase; public long startWaitLastCase; //segment entry of first and last case
    public long endWait; //batch processing start (simultaneous batch)
    public double[] waitingTimes;
    //mean-, standard deviation-, minimum- and maximum waiting time
    public double meanWaitingTime; public double sdWaitingTime; public double maxWaitingTime; public double minWaitingTime;
    //quartiles of waiting time
    public double q1WaitingTime;
    public double q2WaitingTime;
    public double q3WaitingTime;

    public Batch(List<Trace> traces) {
        this.size = traces.size();
        this.traces = new ArrayList<>();
        for (int i = 0; i < traces.size(); i++) {
            this.traces.add(traces.get(i));
        }
        this.caseIDs = new ArrayList<>();
        for (int i = 0; i < traces.size(); i++) {
            this.caseIDs.add(traces.get(i).getCaseID());
        }
        this.interArrivalTimes = new double[traces.size()-1];
        for (int i = 0; i < interArrivalTimes.length; i++) {
            this.interArrivalTimes[i] = 1.0*(traces.get(i+1).getStart() - traces.get(i).getStart())/3600000;
        }
        this.startWaitFirstCase = traces.get(0).getStart();
        for (int i = 0; i < traces.size(); i++) {
            if (traces.get(i).getStart() < this.startWaitFirstCase) {
                this.startWaitFirstCase = traces.get(i).getStart();
            }
        }
        this.startWaitLastCase = traces.get(0).getStart();
        for (int i = 0; i < traces.size(); i++) {
            if (traces.get(i).getStart() > this.startWaitLastCase) {
                this.startWaitLastCase = traces.get(i).getStart();
            }
        }
        this.endWait = traces.get(0).getEnd();
        for (int i = 0; i < traces.size(); i++) {
            if (traces.get(i).getEnd() > this.endWait) {
                this.endWait = traces.get(i).getEnd();
            }
        }
        this.waitingTimes = new double[traces.size()];
        for (int i = 0; i < traces.size(); i++) {
            waitingTimes[i] = 1.0*(traces.get(i).getDuration())/3600000;
        }
        OptionalDouble max = DoubleStream.of(this.waitingTimes).max();
        if (max.isPresent()) {
            this.maxWaitingTime = max.getAsDouble();
        } else {
            this.maxWaitingTime = Double.NaN;
        }
        OptionalDouble min = DoubleStream.of(this.waitingTimes).min();
        if (min.isPresent()) {
            this.minWaitingTime = min.getAsDouble();
        } else {
            this.minWaitingTime = Double.NaN;
        }

        Percentile q1 = new Percentile();
        Percentile q2 = new Percentile();
        Percentile q3 = new Percentile();
        this.q1WaitingTime = q1.evaluate(waitingTimes, 25);
        this.q2WaitingTime = q2.evaluate(waitingTimes, 50);
        this.q3WaitingTime = q3.evaluate(waitingTimes, 75);
    }

    /**
     *****************************************************************
     ********* METHODS FOR GETTING BASIC BATCH PARAMETERS ************
     *****************************************************************
     */
    public int getSize() {
        return size;
    }

    public void setBatchID(int batchID) {
        this.batchID = batchID;
    }

    public int getBatchID() {
        return batchID;
    }

    public List<Trace> getTraces() {
        return traces;
    }

    public List<String> getCaseIDs() {
        return caseIDs;
    }

    public long getStartWaitFirstCase() {
        return startWaitFirstCase;
    }

    public long getStartWaitLastCase() {
        return startWaitLastCase;
    }

    public long getEndWait() {
        return endWait;
    }

    public double getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public double getMinWaitingTime() {
        return minWaitingTime;
    }

    /**
     *****************************************************************
     ***************** METHODS FOR WAITING TIMES *********************
     *****************************************************************
     */
    public double[] getWaitingTimes() {
        return waitingTimes;
    }

    public double getMeanWaitingTime() {
        return meanWaitingTime;
    }

    public double getSdWaitingTime() {
        return sdWaitingTime;
    }

    public void setMeanWaitingTime(double meanWaitingTime) {
        this.meanWaitingTime = meanWaitingTime;
    }

    public void setSdWaitingTime(double sdWaitingTime) {
        this.sdWaitingTime = sdWaitingTime;
    }

    public int getQuartileWaitingTime(double waitingTime) {
        int quartileWaitingTime;
        if (waitingTime < this.q1WaitingTime) {
            quartileWaitingTime = 1;
        } else if (waitingTime < this.q2WaitingTime) {
            quartileWaitingTime = 2;
        } else if (waitingTime < this.q3WaitingTime) {
            quartileWaitingTime = 3;
        } else {
            quartileWaitingTime = 4;
        }
        return quartileWaitingTime;
    }

    /**
     *****************************************************************
     *************** METHODS FOR INTERARRIVAL TIMES ******************
     *****************************************************************
     */
    public double[] getInterArrivalTimes() {
        return interArrivalTimes;
    }

    public double getMeanInterArrivalTime() {
        return meanInterArrivalTime;
    }

    public double getSdInterArrivalTime() {
        return sdInterArrivalTime;
    }

    public void setMeanInterArrivalTime(double meanInterArrivalTime) {
        this.meanInterArrivalTime = meanInterArrivalTime;
    }

    public void setSdInterArrivalTime(double sdInterArrivalTime) {
        this.sdInterArrivalTime = sdInterArrivalTime;
    }
}