package com.cad.deltavcs;

import java.util.List;
import java.util.ArrayList;

public class CADFileDeltaMemento {
    private final List<String> addedVertices;
    private final int addedPolygons;
    private final String author;
    private final int versionId;

    public CADFileDeltaMemento(List<String> addedVertices,int addedPolygons,String author,int versionId){
        this.addedVertices=new ArrayList<>(addedVertices);
        this.addedPolygons=addedPolygons;
        this.author=author;
        this.versionId=versionId;
    }

    public List<String> getAddedVertices(){
        return this.addedVertices;
    }

    public int getAddedPolygons(){
        return this.addedPolygons;
    }

    public String getAuthor(){
        return this.author;
    }

    public int getVersionId(){
        return this.versionId;
    }
}