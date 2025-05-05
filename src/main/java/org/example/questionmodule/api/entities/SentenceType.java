package org.example.questionmodule.api.entities;

public enum SentenceType {
    AFFIRMATIVE("Khẳng định"),
    NEGATIVE("Phủ định"),
    QUESTION("Câu hỏi");

    private final String description;

    SentenceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

