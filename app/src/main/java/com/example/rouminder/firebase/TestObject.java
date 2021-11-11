package com.example.rouminder.firebase;

public class TestObject {
    private String body;

    public TestObject(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return body;
    }
}
