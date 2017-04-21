package com.store.web.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.store.domain.Category;
import com.store.domain.Product;
import com.store.service.CategoryService;
import com.store.service.ProductService;
import com.store.utils.BeanFactory;
import com.store.utils.UUIDUtils;
import com.store.utils.UploadUtils;



public class AdminProductServlet extends BaseServlet {
	private ProductService ps = (ProductService) BeanFactory.getBean("ProductService"); 
	private CategoryService cs = (CategoryService) BeanFactory.getBean("CategoryService");
	/***
	 * ����������Ʒ
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String findAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
	//����sevice���ҷ���list
	List<Product> list = ps.findAll();
	//��list��������
	request.setAttribute("list", list);
	return "/admin/product/list.jsp";
	}
	
	/***
	 * ת�������Ʒҳ��
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addUI(HttpServletRequest request, HttpServletResponse response) throws Exception {	
		getCategoryList(request);
		return "/admin/product/add.jsp";
	}

	private void getCategoryList(HttpServletRequest request) throws Exception {
		//��ȡ���з���
		List<Category> list = cs.findAll(); 
		//�������б����request����
		request.setAttribute("list", list);
	}
	/***
	 * ���һ����Ʒ
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String add(HttpServletRequest request, HttpServletResponse response) throws Exception {	
		Product p = getProductFrom(request);
		//����service�洢
		ps.add(p);
		//�ض���list.jsp
		response.sendRedirect(request.getContextPath()+"/adminProductServlet?method=findAll");
		return null;
	}

	
	/***
	 * �¼���Ʒ
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delete(HttpServletRequest request, HttpServletResponse response) throws Exception {	
		//��ȡ��Ʒid
		String pid = request.getParameter("pid");
		//���Ҹ���Ʒ
		Product p = ps.findById(pid);
		p.setPflag(1);
		//ֱ������Ʒ�¼�
		ps.update(p);
		//�ض���list����
		response.sendRedirect(request.getContextPath()+"/adminProductServlet?method=find&flag=0");
		return null;
	}
	
	public String updateUI(HttpServletRequest request, HttpServletResponse response) throws Exception {	
		String pid = request.getParameter("pid");
		System.out.println(pid);
		//��ȡ��Ʒ��Ϣ
		Product p = ps.getProductById(pid);
		//��ȡ������Ϣ,��ȡcid���ڻ��productͬʱ��ȥcid
		String cid = ps.getCidByPid(pid);
		Category c = new Category();
		c.setCid(cid);
		System.out.println(p.getPname());
		p.setCategory(c);
		
		getCategoryList(request);
		request.setAttribute("p", p);
		return "/admin/product/edit.jsp";
	}
	
	/***
	 * ��ȡ�������е�����
	 * @param request
	 * @return
	 * @throws FileUploadException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Product getProductFrom(HttpServletRequest request)
			throws Exception {
		//����map����
		HashMap<String, String> map = new HashMap<String, String>();
		//��ȡ������ʼ��װ
		//��ȡ���̹���
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//��ȡupload���
		ServletFileUpload upload = new ServletFileUpload(factory);
		//������
		List<FileItem> list = upload.parseRequest(request);
		//���������ļ��ͱ��ֱ��װ
		for (FileItem fileItem : list) {
			//����Ǳ�����
			if(fileItem.isFormField())
			{
				//��ȡ��name��value����,��װ��map
				map.put(fileItem.getFieldName(), fileItem.getString("utf-8"));
			}
			else
			{
				//��ȡ�ļ���ʵ���ƺ��������
				String name = fileItem.getName();
				String realName = UploadUtils.getRealName(name);
				String uuidName = UploadUtils.getUUIDName(realName);
				//��ȡ�ļ�·��
				String path = this.getServletContext().getRealPath("/products/1");
				//��ȡ�����
				File file = new File(path, uuidName);
				System.out.println(file.getPath());
				FileOutputStream os = new FileOutputStream(file);
				InputStream in = fileItem.getInputStream();
				//����
				IOUtils.copy(in, os);
				//����
				os.close();
				in.close();
				//ɾ����ʱ�ļ�
				fileItem.delete();
				//��map����������
				map.put(fileItem.getFieldName(), "products/1/"+uuidName);
			}
		}
		//ʹ��beanutils
		Product p = new Product();
		//��װpid
		p.setPid(UUIDUtils.getId());
		//��װ����
		p.setPdate(new Date());
		//��װ����
		Category c = new Category();
		c.setCid(map.get("cid"));
		p.setCategory(c);
		BeanUtils.populate(p, map);
		return p;
	}
	
	
	public String update(HttpServletRequest request, HttpServletResponse response) throws Exception {	
		String pid = request.getParameter("pid");
		Product p = getProductFrom(request);
		p.setPid(pid);
		p.getCategory().setCid(p.getCategory().getCid());
		//����service����
		ps.updateAll(p);
		System.out.println(p.getPid());
		//�ض���list.jsp
		response.sendRedirect(request.getContextPath()+"/adminProductServlet?method=findAll");
		return null;
	}
	public String find(HttpServletRequest request, HttpServletResponse response) throws Exception {	
		int flag = Integer.parseInt(request.getParameter("flag"));
		//����sevice���ҷ���list
		List<Product> list = ps.find(flag);
		//��list��������
		request.setAttribute("list", list);
		request.setAttribute("flag", flag);
		return "/admin/product/list.jsp";
	}
	
	
	public String under(HttpServletRequest request, HttpServletResponse response) throws Exception {	
		//��ȡ��Ʒid
		String pid = request.getParameter("pid");
		//���Ҹ���Ʒ
		Product p = ps.findById(pid);
		p.setPflag(0);
		//ֱ������Ʒ�¼�
		ps.update(p);
		//�ض���list����
		response.sendRedirect(request.getContextPath()+"/adminProductServlet?method=find&flag=1");
		return null;
	}
}
