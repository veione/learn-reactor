package com.think.reactor.asyncbridge.jdbc;

/**
 * @author veione
 * @version 1.0.0
 * @date 2023年06月21日 16:31:00
 */
public class Book {
    private int id;
    private String title;
    private int author_id;

    public Book(int id, String title, int author_id) {
        this.id = id;
        this.title = title;
        this.author_id = author_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author_id=" + author_id +
                '}';
    }
}

