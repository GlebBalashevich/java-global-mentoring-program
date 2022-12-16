package com.epam.mentoring.model;

public class Entry {

    private String data;

    public Entry(String data){
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Entry entry = (Entry) o;

        return data.equals(entry.data);
    }

    @Override public int hashCode() {
        return data.hashCode();
    }

    @Override public String toString() {
        return "Entry{" +
                "data='" + data + '\'' +
                '}';
    }
}
