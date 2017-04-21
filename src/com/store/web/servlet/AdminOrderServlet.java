package com.store.web.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import com.store.domain.Category;
import com.store.domain.Order;
import com.store.domain.OrderItem;
import com.store.service.OrderService;
import com.store.service.impl.OrderServiceImpl;
import com.store.utils.JsonUtil;



public class AdminOrderServlet extends BaseServlet {
	private OrderService os = new OrderServiceImpl();
	/***
	 * ����״̬���Ҷ���
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String findAllByState(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String str = request.getParameter("state");
		//����service
		List<Order> list = os.findAllOrderByState(str);
		//�����ص�list��������
		request.setAttribute("list", list);
		return "/admin/order/list.jsp";
	}
	/***
	 * �ı䶩��״̬
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateState(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//���ܶ���id�Ͷ���״̬
		String oid = request.getParameter("oid");
		int state = Integer.parseInt(request.getParameter("state"));
		//��ȡ����
		Order order = os.getById(oid);
		//��װ����
		order.setState(state);
		//�ı䶩��״̬
		os.updateOrder(order);
		response.sendRedirect(request.getContextPath()+"/adminOrderServlet?method=findAllByState&state="+state);
		return null;
	}
	
	public String getDetailByOid(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//���ܶ���id�Ͷ���״̬
		String oid = request.getParameter("oid");
		//��ȡ����
		Order order = os.getById(oid);
		//ȡ�ö�����
		List<OrderItem> list = order.getItems();
		//�ı�json��װ
		JsonConfig config = JsonUtil.configJson(new String[]{"order","class","itemid"});
		JSONArray array = JSONArray.fromObject(list, config);
		System.out.println(array);
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().println(array);
		return null;
	}
}
