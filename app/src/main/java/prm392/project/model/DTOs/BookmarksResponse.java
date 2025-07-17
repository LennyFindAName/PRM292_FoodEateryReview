package prm392.project.model.DTOs;

import java.util.List;

import prm392.project.model.Bookmark;

public class BookmarksResponse {
    private List<Bookmark> bookmarks;
    private int totalCount;

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public int getTotalCount() {
        return totalCount;
    }
}