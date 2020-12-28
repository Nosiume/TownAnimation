package com.townanim.path;

import org.bukkit.util.Vector;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Path {

    private List<Vector> points = new ArrayList<Vector>();
    private String name;

    public Path(String name) {
        this.name = name;
    }

    public boolean isAlreadyInPath(Vector p) {
        return points.contains(p);
    }

    //Adds a point to our path
    public void addPoint(Vector p) {
        points.add(p);
    }

    //Removes a point from our path
    public void removePoint(Vector p) {
        points.remove(p);
    }

    //Returns the name of our path (used to identify)
    public String getName() {
        return name;
    }

    //Gets the point array
    public List<Vector> getPoints() {
        return points;
    }

    //Returns a JSONArray object that represents the current path object
    //This is made in such a way that the path object can be reversed from the JSONArray using the static function Path.fromJSONArray(String, JSONArray);
    public JSONArray getAsJSONArray() {
        JSONArray array = new JSONArray();
        points.forEach((point) -> array.add(point.getX() + " " + point.getY() + " " + point.getZ()));
        return array;
    }

    //============ STATIC METHODS ============ //

    //Returns a PATH object from a JSONArray object.
    //Format of JSONArray object will be a list of string such as "x y z" where each point is a float.
    public static Path fromJSONArray(String pathName, JSONArray array) {
        Path path = new Path(pathName);
        Iterator<String> itr = array.iterator();
        while(itr.hasNext()) {
            String[] rawPoints = itr.next().split(" ");
            float x = Float.parseFloat(rawPoints[0]);
            float y = Float.parseFloat(rawPoints[1]);
            float z = Float.parseFloat(rawPoints[2]);
            path.addPoint(new Vector(x, y, z));
        }
        return path;
    }

}
