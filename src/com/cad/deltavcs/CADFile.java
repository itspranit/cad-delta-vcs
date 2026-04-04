package com.cad.deltavcs;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class CADFile {
    private String fileId;
    private List<String> vertices;
    private int polygons;
    private Map<String,String> metadata;
    private List<String> uncommittedVertices;
    private int uncommittedPolygons;
    private String lastAuthor;

    public CADFile(String fileId){
        this.fileId=fileId;
        this.vertices=new ArrayList<>();
        this.polygons=0;
        this.metadata=new HashMap<>();
        this.metadata.put("version","0");
        this.uncommittedVertices=new ArrayList<>();
        this.uncommittedPolygons=0;
        this.lastAuthor="System";
    }

    public String getFileId(){
        return this.fileId;
    }

    public Map<String,String> getMetadata(){
        return this.metadata;
    }

    public int getVertexCount(){
        return this.vertices.size();
    }

    public void modify(List<String> newVertices,int newPolygons,String author){
        this.uncommittedVertices.addAll(newVertices);
        this.uncommittedPolygons+=newPolygons;
        this.lastAuthor=author;
    }

    public CADFileDeltaMemento save(){
        int nextVersion=Integer.parseInt(this.metadata.get("version"))+1;
        CADFileDeltaMemento memento=new CADFileDeltaMemento(this.uncommittedVertices,this.uncommittedPolygons,this.lastAuthor,nextVersion);
        this.vertices.addAll(this.uncommittedVertices);
        this.polygons+=this.uncommittedPolygons;
        this.metadata.put("version",String.valueOf(nextVersion));
        this.metadata.put("lastAuthor",this.lastAuthor);
        this.uncommittedVertices.clear();
        this.uncommittedPolygons=0;
        return memento;
    }

    public void undo(CADFileDeltaMemento memento){
        this.vertices.removeAll(memento.getAddedVertices());
        this.polygons-=memento.getAddedPolygons();
        int prevVersion=Integer.parseInt(this.metadata.get("version"))-1;
        this.metadata.put("version",String.valueOf(prevVersion));
    }
}