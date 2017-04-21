package com.store.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.omg.CORBA.Request;

import com.store.domain.Cart;
import com.store.domain.CartItem;
import com.store.domain.Product;
import com.store.service.ProductService;
import com.store.service.impl.ProductServiceImpl;
import com.store.utils.BeanFactory;


/***
 * ���ﳵ���servlet
 * @author �γ���
 *
 */
public class CartServlet extends BaseServlet {
	ProductService ps = (ProductService) BeanFactory.getBean("ProductService");
	
	/***
	 *�����Ʒ�����ﳵ�� 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		System.out.println(request.getParameterMap());
		//��ȡ��Ʒid������
		String pid = request.getParameter("pid");
		int count = Integer.parseInt(request.getParameter("count"));
		
		//����service�õ��÷���
		Product product = ps.getProductById(pid);
		CartItem item = new CartItem(product, count);
		//����getCart��������ȡ
		Cart cart = getCart(request);
		//��cartItem���뵽cart��map��
		cart.add2Cart(item);
		//��cartItem����session
		request.getSession().setAttribute("cart", cart);
		//�ض���cart��
		response.sendRedirect(request.getContextPath()+"/jsp/cart.jsp");
		return null;
	}
	/***
	 * ɾ��һ�����ﳵ��
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		//1.��ȡ��Ʒ���
		String pid = request.getParameter("pid");
		//2.��ȡcart����
		Cart cart = getCart(request);
		//3.�����Ƴ�����
		cart.remove2Cart(pid);
		//�ض��򵽴�ҳ��
		response.sendRedirect(request.getContextPath()+"/jsp/cart.jsp");
		
		return null;
	}
	/***
	 * ��չ��ﳵ
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String clear(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		
		//1.��ȡcart����
		Cart cart = getCart(request);
		//3.������շ���
		cart.clearCart();
		//�ض��򵽴�ҳ��
		response.sendRedirect(request.getContextPath()+"/jsp/cart.jsp");
		
		return null;
	}
	
	/***
	 * ��session�л��cart��add�����Ĺ�����
	 * @param request
	 * @return
	 */
	private Cart getCart(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart==null)
		{
			//����һ��cart
			cart = new Cart();
			//��ӵ�session��
			session.setAttribute("cart", cart);
		}
		return cart;
	}
	
	
	public String cartUI(HttpServletRequest request, HttpServletResponse response)
	{
		return "/jsp/cart.jsp";
	}
}
