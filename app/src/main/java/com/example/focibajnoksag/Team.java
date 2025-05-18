package com.example.focibajnoksag;

public class Team {
    private String name;
    private int points;
    private int rank; // új mező

    public Team(String name, int points) {
        this.name = name;
        this.points = points;
    }

    // Getterek és setterek
    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
