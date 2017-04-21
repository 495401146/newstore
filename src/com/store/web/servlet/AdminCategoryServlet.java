package com.store.web.servlet;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.store.domain.Category;
import com.store.domain.Product;
import com.store.service.CategoryService;
import com.store.service.impl.CategoryServiceImpl;
import com.store.utils.BeanFactory;
import com.store.utils.UUIDUtils;


/***
 * ����admin�еķ����servlet
 * @author �γ���
 *
 */
public class AdminCategoryServlet extends BaseServlet {
	private CategoryService cs = (CategoryService) BeanFactory.getBean("CategoryService");
	/***
	 * �������з���
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//���ò�ѯ���еķ���
		List<Category> list = cs.findAll();
		//��list����request����
		request.setAttribute("list", list);
		return "/admin/category/list.jsp";
	}
	/***
	 * ��ת����ӽ���
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addUI(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "/admin/category/add.jsp";
	}
	/***
	 * ��ӷ���
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//��ȡ����ķ�������
		String cname = request.getParameter("cname");
		if(cname==null||cname=="")
		{
			request.setAttribute("msg", "������������");
			return "/jsp/msg.jsp";
		}
		//��װ��bean������
		Category c = new Category();
		c.setCid(UUIDUtils.getId());
		c.setCname(cname);
		//����service���
		
		cs.add(c);
		response.sendRedirect(request.getContextPath()+"/adminCategoryServlet?method=findAll");
		return null;
	}
	
	/***
	 * ��ȡ�������
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//��ȡ����ķ�������id
		String cid = request.getParameter("cid");
		System.out.println(cid);
		//����service��ѯ	
		Category c = cs.getById(cid);
		//��������Ϣ����request��
		System.out.println(c.getCname());
		request.setAttribute("c", c);
		return "/admin/category/edit.jsp";
	}
	/***
	 * �༭������Ϣ
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String update(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//��ȡ����id������
		String cid = request.getParameter("cid");
		String cname = request.getParameter("cname");
		//��װcategory
		Category c = new Category();
		c.setCid(cid);
		c.setCname(cname);
		//����service��ѯ	
		cs.update(c);
		//�ض��򵽷������
		response.sendRedirect(request.getContextPath()+"/adminCategoryServlet?method=findAll");
		return null;
	}
	
	public String delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//��ȡ����id
		String cid = request.getParameter("cid");
		
		//����serviceɾ��	
		cs.delete(cid);
		//�ض��򵽷������
		response.sendRedirect(request.getContextPath()+"/adminCategoryServlet?method=findAll");
		return null;
	}
}
