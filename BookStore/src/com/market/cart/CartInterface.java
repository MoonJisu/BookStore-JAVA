package com.market.cart;

import java.util.ArrayList;
import com.market.bookitem.Book;

public interface CartInterface {
	void printBookList(ArrayList<Book> p);

	boolean isCartInBook(String id);

	void insertBook(Book p);

	void removeCart(int numId);
   
    int getCartItemIndex(String bookId);
    
    void setCartItemQuantity(int index, int quantity);

	void deleteBook();
}