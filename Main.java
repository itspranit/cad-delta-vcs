package com.cad.deltavcs;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args){
        System.out.println("--- Starting Delta-Encoded CAD Version Control ---");
        VersionControlEngine vcs=VersionControlEngine.getInstance();
        CADFile engineBlock=new CADFile("FILE-ENG-DELTA");

        System.out.println("\n--- Simulating Concurrent Delta Modifications ---");
        String[] engineers={"Alice","Bob","Charlie"};
        List<Thread> threads=new ArrayList<>();

        for(String name:engineers){
            Thread t=new Thread(()->{
                VersionControlEngine engine=VersionControlEngine.getInstance();
                Random rand=new Random();
                for(int i=0;i<3;i++){
                    try{
                        Thread.sleep(rand.nextInt(300)+50);
                    }catch(InterruptedException e){}
                    List<String> newVertices=new ArrayList<>();
                    for(int v=0;v<1000;v++){
                        newVertices.add(rand.nextInt(100)+","+rand.nextInt(100));
                    }
                    engineBlock.modify(newVertices,1000,name);
                    engine.commit(engineBlock);
                }
            });
            threads.add(t);
            t.start();
        }

        for(Thread t:threads){
            try{
                t.join();
            }catch(InterruptedException e){}
        }

        System.out.println("\n--- Verifying Integrity ---");
        int historyCount=vcs.getHistoryLength("FILE-ENG-DELTA");
        System.out.println("Total commits: "+historyCount);
        System.out.println("Current Version: v"+engineBlock.getMetadata().get("version"));
        System.out.println("Total Vertices in Memory: "+engineBlock.getVertexCount());

        System.out.println("\n--- Testing Rollback ---");
        vcs.rollback(engineBlock,2);
        System.out.println("Current Version after rollback: v"+engineBlock.getMetadata().get("version"));
        System.out.println("Total Vertices after rollback: "+engineBlock.getVertexCount());
    }
}