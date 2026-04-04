package com.cad.deltavcs;

import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.IOException;

public class MainCLI {
    public static void main(String[] args){
        Scanner scanner=new Scanner(System.in);
        VersionControlEngine vcs=VersionControlEngine.getInstance();
        System.out.print("Enter a unique ID for your new CAD file: ");
        String fileId=scanner.nextLine();
        CADFile myFile=new CADFile(fileId);
        boolean running=true;
        
        System.out.println("\n--- Interactive Delta VCS Initialized ---");

        while(running){
            System.out.println("\n[1] Upload/Commit File  [2] View Status  [3] Rollback  [4] Exit");
            System.out.print("Action: ");
            String choice=scanner.nextLine();

            if(choice.equals("1")){
                System.out.print("Enter absolute file path (e.g., /Users/name/Desktop/test.txt): ");
                String filePath=scanner.nextLine();
                try{
                    List<String> fileLines=Files.readAllLines(Paths.get(filePath));
                    myFile.modify(fileLines,1,System.getProperty("user.name"));
                    vcs.commit(myFile);
                    System.out.println("Success! Read "+fileLines.size()+" lines/vertices.");
                }catch(IOException e){
                    System.out.println("[ERROR] Could not read file. Check the path and try again.");
                }
            }else if(choice.equals("2")){
                System.out.println("--- File Status ---");
                System.out.println("File ID: "+myFile.getFileId());
                System.out.println("Current Version: v"+myFile.getMetadata().get("version"));
                System.out.println("Total Commits in History: "+vcs.getHistoryLength(myFile.getFileId()));
                System.out.println("Total Vertices (Lines) in Memory: "+myFile.getVertexCount());
            }else if(choice.equals("3")){
                System.out.print("How many versions to rollback? ");
                try{
                    int steps=Integer.parseInt(scanner.nextLine());
                    vcs.rollback(myFile,steps);
                }catch(NumberFormatException e){
                    System.out.println("Please enter a valid number.");
                }
            }else if(choice.equals("4")){
                running=false;
                System.out.println("Shutting down engine...");
            }else{
                System.out.println("Invalid choice.");
            }
        }
        scanner.close();
    }
}