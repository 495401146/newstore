package com.store.domain;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
public class Cart {
	private Map<String,CartItem> cartMap = new LinkedHashMap<String,CartItem>();
	private Double total = 0.0;
	
	
	public Collection<CartItem> getCartItem()
	{
		return cartMap.values();
	}
	//�����Ʒ
	/***
	 * �����Ʒ
	 * @param item
	 */
	public void add2Cart(CartItem item)
	{
		String pid = item.getProduct().getPid();
		//�жϹ��ﳵ�Ƿ��������Ʒ
		if(cartMap.containsKey(pid))
		{
			CartItem temp = cartMap.get(pid);
			temp.setCount(item.getCount()+temp.getCount());
		}
		else
		{
			cartMap.put(item.getProduct().getPid(),item);
		}
		//�ı��ܼ�
		total+=item.getSubtotal();
	}
	/**
	 * ɾ��һ�����ﳵ��
	 * @author �γ���
	 * @param pid��Ʒ���
	 */
	public void remove2Cart(String pid)
	{
		//ɾ������
		CartItem item = cartMap.remove(pid);
		//�ı��ܼ�
		total -= item.getSubtotal(); 
	}
	
	public void clearCart()
	{
		//���map����
		cartMap.clear();
		//�ܼ�����
		total = 0.0;
	}
	
	public Map<String, CartItem> getCartMap() {
		return cartMap;
	}
	public void setCartMap(Map<String, CartItem> cartMap) {
		this.cartMap = cartMap;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	
	
}
