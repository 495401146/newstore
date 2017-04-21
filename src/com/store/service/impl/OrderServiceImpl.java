package com.store.service.impl;

import org.apache.commons.beanutils.BeanUtils;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.store.dao.OrderDao;
import com.store.domain.Order;
import com.store.domain.OrderItem;
import com.store.domain.Product;
import com.store.domain.User;
import com.store.service.OrderService;
import com.store.utils.BeanFactory;
import com.store.utils.DataSourceUtils;

public class OrderServiceImpl implements OrderService {
	private OrderDao od = (OrderDao) BeanFactory.getBean("OrderDao");
	public void add(Order order) throws Exception {
		try{
			//1.����������Ϊ�����Ͷ�������ӵ����Ȼ��ͬ������
			DataSourceUtils.startTransaction();
			//2.���������붩����
			od.addOrder(order);
			//3.��������������붩�����
			for(OrderItem oitem:order.getItems())
			{
				od.addOrderItem(oitem);
			}
			//4.�ύ���ͷ�����
			DataSourceUtils.commitAndClose();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//�ع����ﲢ�ͷ�����
			DataSourceUtils.rollbackAndClose();
			throw e;
		}
	}
	/***
	 * ��ȡ�����������
	 * @exception Exception
	 */
	public int findOrderCount(String uid) throws Exception {
		int count = od.findOrderCount(uid);
		return count;
	}
	
	/***
	 * ��һҳ�е����ж����Լ����еĶ�����
	 * @param currPage
	 * @param pageSize
	 * @param user
	 */
	public List<Order> findAllOrder(int currPage, int pageSize, User user)
			throws Exception {
		//�ҵ����ж�����
		List<Order> orders = od.findAllOrder(currPage,pageSize,user.getUid());
		//�ҵ����еĶ�����
		for (Order order : orders) {
			List<Map<String,Object>> map = od.findOrderItemsByOrderId(order.getOid());
			//�������ҵ���orderitem��product��map����
			for (Map<String, Object> item : map) {
				//����product���󣬷���װ��orderitem��
				Product p = new Product();
				BeanUtils.populate(p,item);
				//����oitem�����Բ���װ��order����Ҫװ��product
				OrderItem oi = new OrderItem();
				BeanUtils.populate(oi, item);
				oi.setProduct(p);
				//�����ɵ�oitemװ��order��list��
				order.getItems().add(oi);
			}
			order.setUser(user);
		}
		return orders;
	}
	public Order getById(String oid) throws Exception {
		//��ȡ����order����
		Order order = od.getOrder(oid);
		//����oid��ȡ���е�orderitem��map�ļ���
		List<Map<String,Object>> list = od.findOrderItemsByOrderId(oid);
		//����map
		for (Map<String, Object> map : list) {
			//����product���󣬷���װ��orderitem��
			Product p = new Product();
			BeanUtils.populate(p, map);
			//����oitem�����Բ���װ��order����Ҫװ��product
			OrderItem oi = new OrderItem();
			BeanUtils.populate(oi, map);
			oi.setProduct(p);
			//�����ɵ�oitemװ��order��list��
			order.getItems().add(oi);
		}
		return order;
	}
	public void updateOrder(Order order) throws Exception {
		//����order
		od.update(order);
	}
	public List<Order> findAllOrderByState(String str) throws Exception {
		List<Order> list = od.findAllOrderByState(str);
		return list;
	}

}
