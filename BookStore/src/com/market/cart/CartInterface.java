package com.market.cart;

import java.util.ArrayList;
import com.market.bookitem.Book;

public interface CartInterface {
	void printBookList(ArrayList<Book> p);

	boolean isCartInBook(String id);

	void insertBook(Book p);

	void removeCart(int numId);
    
    // 추가된 메서드
    int getCartItemIndex(String bookId);

    // 추가된 메서드
    void setCartItemQuantity(int index, int quantity);

	void deleteBook();
}