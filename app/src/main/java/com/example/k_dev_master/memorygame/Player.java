package com.example.k_dev_master.memorygame;

public class Player implements Comparable<Player> {
    private String name;
    private long recordedTime;

    public Player(String name, long recordedTime) {
        this.name = name;
        this.recordedTime = recordedTime;
    }

    public long getRecordedTime() {
        return recordedTime;
    }

    public String getName() {
        return name;
    }

    public void setRecordedTime(long recordedTime) {
        this.recordedTime = recordedTime;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public int compareTo(Player p) {
        if (p != null) {
            return (int) (recordedTime - p.getRecordedTime());
        }
        return 9999;
    }
}
