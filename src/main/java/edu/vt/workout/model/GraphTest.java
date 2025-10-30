package edu.vt.workout.model;

import java.util.Random;
import java.util.TimeZone;
import java.time.LocalDateTime;

public class GraphTest
{
    public static void main(String[] args)
    {
        
        Graph graph = new Graph(TimeZone.getDefault());
        
        int numLogs = 10;
        Log[] logbook = logbookGenerator(numLogs);
        
        graph.graphMaker(logbook);
        
        System.out.println("Done!");
    }
    
    
    private static Log[] logbookGenerator(int numLogs)
    {
        Random rand = new Random();
        LocalDateTime date;
        int yr;
        int mo;
        int da;
        int hr;
        int min;
        int sec;
        Log log;
        Log[] out = new Log[numLogs];
        
        for (int i = 0; i < numLogs; i++)
        {
            yr = rand.nextInt(20) + 2000;
            mo = rand.nextInt(11) + 1;
            da = rand.nextInt(28) + 1;
            hr = rand.nextInt(23);
            min = rand.nextInt(60);
            sec = rand.nextInt(60);
            date = LocalDateTime.of(yr, mo, da, hr, min, sec);
            log = new Log();
            log.setDate(date);
            log.setSets(rand.nextInt(6) + 6);
            log.setReps(rand.nextInt(2) + 2);
            log.setWeight(rand.nextDouble(150) + 50);
            out[i] = log;
        }
        return out;
    }
}
