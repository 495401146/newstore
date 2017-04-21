package com.store.web.servlet;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.store.domain.Cart;
import com.store.domain.CartItem;
import com.store.domain.Order;
import com.store.domain.OrderItem;
import com.store.domain.PageBean;
import com.store.domain.Product;
import com.store.domain.User;
import com.store.service.OrderService;
import com.store.service.impl.OrderServiceImpl;
import com.store.utils.BeanFactory;
import com.store.utils.PaymentUtils;
import com.store.utils.UUIDUtils;



public class OrderServlet extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	OrderService os = (OrderService) BeanFactory.getBean("OrderService");
	
	/***
	 * ��Ӷ���
	 * @param request
	 * @param response
	 * @return
	 */
	public String add(HttpServletRequest request, HttpServletResponse response)
	{
		HttpSession session = request.getSession();
		//1.�ж��û��Ƿ��¼
		User user = (User) session.getAttribute("user");
		if(user==null)
		{
			request.setAttribute("msg", "��ȥ��¼��");
			return "/jsp/msg.jsp";
		}
		//2.��ȡ���ﳵ����
		Cart cart = (Cart) session.getAttribute("cart");
		//3.��װ��������
		Order order = new Order();
		//3.1��ȡ���id
		order.setOid(UUIDUtils.getId());
		//3.2��ȡ��ǰʱ��
		order.setOrderTime(new Date());
		//3.3���ݹ��ﳵ��ȡ�ܼ�
		order.setTotal(cart.getTotal());
		//3.4����user��ȡ�û�
		order.setUser(user);
		//4.��װ����������
		//4.1��cart���б���
		for(CartItem cartItem:cart.getCartItem())
		{
			OrderItem orderItem = new OrderItem();
			//������Ʒ
			orderItem.setProduct(cartItem.getProduct());
			//������Ʒ����
			orderItem.setCount(cartItem.getCount());
			//����С��
			orderItem.setSubtotal(cartItem.getSubtotal());
			//���õ���id
			orderItem.setItemId(UUIDUtils.getId());
			//���������ĸ�����
			orderItem.setOrder(order);
			//����������ӵ�������
			order.getItems().add(orderItem);
		}
		//5.����service�������붩���Ͷ�����
		try {
			os.add(order);
		} catch (Exception e) {
			request.setAttribute("msg", "�����ύ�����쳣");
			return "/jsp/msg.jsp";
		}
		//6.����������request����
		request.setAttribute("order", order);
		return "/jsp/order_info.jsp";
	}
	
	/***
	 * ���ݷ�ҳ����order
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String findAllOrder(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		int currPage = 1;
		//��ȡ��ǰҳ��
		try{
			currPage= Integer.parseInt(request.getParameter("currPage"));
		}
		catch(NumberFormatException e)
		{
			currPage=1;
		}
		User user = (User) request.getSession().getAttribute("user");
		//�ж��û��Ƿ��½
		if(user==null)
		{
			request.setAttribute("msg", "���ȵ�¼");
			return "/jsp/msg.jsp";
		}
		//����pageSize
		int pageSize = 3;
		//��ѯ����������
		int totalCount = os.findOrderCount(user.getUid());
		//��ѯһҳ������ж���(���������еĶ�����)
		List<Order> orders = os.findAllOrder(currPage,pageSize,user);
		System.out.println("��������"+orders.size());
		//��ȡPageBean����
		PageBean<Order> pageBean = new PageBean<Order>(pageSize, currPage, totalCount, orders);
		//��request���з���pagebean
		request.setAttribute("pb", pageBean);
		return "/jsp/order_list.jsp";
	}
	
	/***
	 * ����id��ȡ��������
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getById(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		
		//��ȡoid
		String oid = request.getParameter("oid");
		//����oid��ȡ����Order����
		Order order = os.getById(oid);
		System.out.println(order);
		//����������request����
		request.setAttribute("order", order);
		System.out.println(order.getTotal());
		return "/jsp/order_info.jsp";
	}
	
	
	public String pay(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String address=request.getParameter("address");
		String name=request.getParameter("name");
		String telephone=request.getParameter("telephone");
		String oid=request.getParameter("oid");
		Order order = os.getById(oid);
		order.setAddress(address);
		order.setName(name);
		order.setTelephone(telephone);
		order.setState(1);
		System.out.println(order);
		//����order
		os.updateOrder(order);
		request.setAttribute("msg","֧���ɹ�" );
		return "/jsp/msg.jsp";
	}
	/***
	 * ��ȡ���ж��������Լ��������ݣ�����order�������͸��ױ�()û���ױ��˺ţ�������
	 * @param request
	 * @param respone
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public String pay1(HttpServletRequest request,HttpServletResponse respone) throws Exception{
		//���ܱ�����
		String address=request.getParameter("address");
		String name=request.getParameter("name");
		String telephone=request.getParameter("telephone");
		String oid=request.getParameter("oid");
		System.out.println(address);
		System.out.println(name);
		System.out.println(telephone);
		

		Order order = os.getById(oid);
		
		order.setAddress(address);
		order.setName(name);
		order.setTelephone(telephone);
		System.out.println(order);
		//����order
		os.updateOrder(order);
		

		// ��֯����֧����˾��Ҫ��Щ����
		String pd_FrpId = request.getParameter("pd_FrpId");
		String p0_Cmd = "Buy";
		String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
		String p2_Order = oid;
		String p3_Amt = "0.01";
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		// ֧���ɹ��ص���ַ ---- ������֧����˾����ʡ��û�����
		// ������֧�����Է�����ַ
		String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("responseURL");
		String p9_SAF = "";
		String pa_MP = "";
		String pr_NeedResponse = "1";
		// ����hmac ��Ҫ��Կ
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString("keyValue");
		String hmac = PaymentUtils.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);
	
		
		//���͸�������
		StringBuffer sb = new StringBuffer("https://www.yeepay.com/app-merchant-proxy/node?");
		sb.append("p0_Cmd=").append(p0_Cmd).append("&");
		sb.append("p1_MerId=").append(p1_MerId).append("&");
		sb.append("p2_Order=").append(p2_Order).append("&");
		sb.append("p3_Amt=").append(p3_Amt).append("&");
		sb.append("p4_Cur=").append(p4_Cur).append("&");
		sb.append("p5_Pid=").append(p5_Pid).append("&");
		sb.append("p6_Pcat=").append(p6_Pcat).append("&");
		sb.append("p7_Pdesc=").append(p7_Pdesc).append("&");
		sb.append("p8_Url=").append(p8_Url).append("&");
		sb.append("p9_SAF=").append(p9_SAF).append("&");
		sb.append("pa_MP=").append(pa_MP).append("&");
		sb.append("pd_FrpId=").append(pd_FrpId).append("&");
		sb.append("pr_NeedResponse=").append(pr_NeedResponse).append("&");
		sb.append("hmac=").append(hmac);
		
		respone.sendRedirect(sb.toString());
		
		return null;
	}
	/***
	 * ֧����Ļص�����
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String callback(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String p1_MerId = request.getParameter("p1_MerId");
		String r0_Cmd = request.getParameter("r0_Cmd");
		String r1_Code = request.getParameter("r1_Code");
		String r2_TrxId = request.getParameter("r2_TrxId");
		String r3_Amt = request.getParameter("r3_Amt");
		String r4_Cur = request.getParameter("r4_Cur");
		String r5_Pid = request.getParameter("r5_Pid");
		String r6_Order = request.getParameter("r6_Order");
		String r7_Uid = request.getParameter("r7_Uid");
		String r8_MP = request.getParameter("r8_MP");
		String r9_BType = request.getParameter("r9_BType");
		String rb_BankId = request.getParameter("rb_BankId");
		String ro_BankOrderId = request.getParameter("ro_BankOrderId");
		String rp_PayDate = request.getParameter("rp_PayDate");
		String rq_CardNo = request.getParameter("rq_CardNo");
		String ru_Trxtime = request.getParameter("ru_Trxtime");
		// ���У�� --- �ж��ǲ���֧����˾֪ͨ��
		String hmac = request.getParameter("hmac");
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
				"keyValue");

		// �Լ����������ݽ��м��� --- �Ƚ�֧����˾������hamc
		boolean isValid = PaymentUtils.verifyCallback(hmac, p1_MerId, r0_Cmd,
				r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid,
				r8_MP, r9_BType, keyValue);
		if (isValid) {
			// ��Ӧ������Ч
			if (r9_BType.equals("1")) {
				// ������ض���
				System.out.println("111");
				request.setAttribute("msg", "���Ķ�����Ϊ:"+r6_Order+",���Ϊ:"+r3_Amt+"�Ѿ�֧���ɹ�,�ȴ�����~~");
				
			} else if (r9_BType.equals("2")) {
				// ��������Ե� --- ֧����˾֪ͨ��
				System.out.println("����ɹ���222");
				// �޸Ķ���״̬ Ϊ�Ѹ���
				// �ظ�֧����˾
				response.getWriter().print("success");
			}
			
			//�޸Ķ���״̬
			OrderService s=(OrderService) BeanFactory.getBean("OrderService");
			Order order = s.getById(r6_Order);
			order.setState(1);
			
			s.updateOrder(order);
			
		} else {
			// ������Ч
			System.out.println("���ݱ��۸ģ�");
		}
		return "/jsp/msg.jsp";
	}
	
	public String updateState(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//���ܶ���id�Ͷ���״̬
		String oid = request.getParameter("oid");
		int state = Integer.parseInt(request.getParameter("state"));
		//��ȡ����
		Order order = os.getById(oid);
		//��װ����
		order.setState(state);
		//�ı䶩��״̬
		os.updateOrder(order);
		request.setAttribute("order", order);
		return "/jsp/order_info.jsp";
	}
	
}
