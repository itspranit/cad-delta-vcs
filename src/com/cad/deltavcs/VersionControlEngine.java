package com.cad.deltavcs;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class VersionControlEngine {
    private static volatile VersionControlEngine instance;
    private final Map<String,List<CADFileDeltaMemento>> history;
    private final Map<String,ReentrantLock> fileLocks;

    private VersionControlEngine(){
        this.history=new ConcurrentHashMap<>();
        this.fileLocks=new ConcurrentHashMap<>();
    }

    public static VersionControlEngine getInstance(){
        if(instance==null){
            synchronized(VersionControlEngine.class){
                if(instance==null){
                    instance=new VersionControlEngine();
                }
            }
        }
        return instance;
    }

    private ReentrantLock getFileLock(String fileId){
        this.fileLocks.putIfAbsent(fileId,new ReentrantLock());
        return this.fileLocks.get(fileId);
    }

    public void commit(CADFile cadFile){
        ReentrantLock lock=getFileLock(cadFile.getFileId());
        lock.lock();
        try{
            this.history.putIfAbsent(cadFile.getFileId(),new ArrayList<>());
            CADFileDeltaMemento memento=cadFile.save();
            this.history.get(cadFile.getFileId()).add(memento);
            System.out.println("[COMMIT] "+cadFile.getFileId()+" | v"+memento.getVersionId()+" | Diffs Added: "+memento.getAddedVertices().size()+" | By: "+memento.getAuthor());
        }finally{
            lock.unlock();
        }
    }

    public void rollback(CADFile cadFile,int stepsBack){
        ReentrantLock lock=getFileLock(cadFile.getFileId());
        lock.lock();
        try{
            List<CADFileDeltaMemento> fileHistory=this.history.get(cadFile.getFileId());
            if(fileHistory==null||fileHistory.size()<stepsBack){
                System.out.println("[ERROR] Cannot rollback "+cadFile.getFileId());
                return;
            }
            for(int i=0;i<stepsBack;i++){
                CADFileDeltaMemento memento=fileHistory.remove(fileHistory.size()-1);
                cadFile.undo(memento);
            }
            System.out.println("[ROLLBACK] "+cadFile.getFileId()+" reverted to v"+cadFile.getMetadata().get("version"));
        }finally{
            lock.unlock();
        }
    }

    public int getHistoryLength(String fileId){
        List<CADFileDeltaMemento> fileHistory=this.history.get(fileId);
        if(fileHistory==null){
            return 0;
        }
        return fileHistory.size();
    }
}