import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Project2 
{
    public static int People_in_line = 49;
    
    static Semaphore spaceInElevator = new Semaphore(7, true);
    static Semaphore Elevator = new Semaphore(1, true);
    
    static int ElevatorTrips = 0;
    static int counter = 0;
    static int[] persons = new int[7];
    static int[] dest_floors = new int[7];
    static int floor = 1;
    static Random random = new Random();
    static boolean exit = false;
    static boolean done = false;
    
    
    public static void main(String[] args) 
    {
        Thread elevator = new Thread(new Elevator());
        elevator.start();
        Thread riders[] = new Thread[People_in_line];
        for(int i = 0; i < People_in_line; i++)
        {
            riders[i] = new Thread(new Riders(i));
        }
        
        
        for(int k = 0; k < People_in_line; k++)
        {
            riders[k].start();
        }
    }
    
    public static class Elevator implements Runnable
    {
        @Override
        public void run()
        {
            
            try
            {
                
                while(true)
                {
                    Elevator.acquire();
                    if(ElevatorTrips == 7 && done == true)
                    {
                        System.out.println("Simulation Done");
                        System.exit(0);
                    }
                    if(spaceInElevator.availablePermits() == 7)
                    {
                        System.out.println("Elevator opens doors at 1 floor" );
                    }
                    else if(spaceInElevator.availablePermits() == 0)
                    {
                        
                        for(int y = 2; y < 10; y++)
                        {
                            for(int z = 0; z < 7; z++)
                            {
                                if(dest_floors[z] == y && exit == false)
                                {
                                    System.out.println("Elevator door opens at floor " + y);
                                    exit = true;
                                }
                                if(dest_floors[z] ==y)
                                {
                                    System.out.println("Person " + persons[z] + " leaves the elevator.");
                                }
                            }
                            if(exit == true)
                            {
                                System.out.println("Elevator doors close.");
                            }
                            exit = false;
                        }
                        done = true;
                        counter = 0;
                        System.out.println("Elevator opens doors at 1 floor" );
                        spaceInElevator.release(7);
                    }
                }
            }catch (InterruptedException e){
                
            }
        }
    }
    
    public static class Riders implements Runnable
    {
        int id;
        final int floor_dest;
        public Riders(int id)
        {
            this.id = id;
            floor_dest = random.nextInt(8)+2;
        }
        @Override
        public void run()
        {
            try
            {
                spaceInElevator.acquire();
                System.out.println("Person " + id + " enters elevator to go to floor " + floor_dest);
                persons[counter] = id;
                dest_floors[counter] = floor_dest;
                counter++;
                
                if(spaceInElevator.availablePermits() == 0)
                {
                    ElevatorTrips++;
                    Elevator.release();
                    System.out.println("Elevator doors close");
                }
            }catch (InterruptedException e) {
                
            }
        }
    }
}
