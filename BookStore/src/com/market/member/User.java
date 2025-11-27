package com.market.member;

public class User extends Person {
	private int coupon = 0;	//쿠폰 보유 여부
   
	public User(String name, int phone){
		 super(name, phone);
		
	}
    public User(String username, int phone, String address) {
        super(username, phone, address);
    }   
    
    public int getCoupon() {
        return coupon;
    }

    public void setCoupon(int coupon) {
        this.coupon = coupon;
    }
}
