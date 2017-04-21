package com.store.service.impl;

import java.io.InputStream;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.store.dao.CategoryDao;
import com.store.dao.ProductDao;
import com.store.domain.Category;
import com.store.domain.Product;
import com.store.service.CategoryService;
import com.store.utils.BeanFactory;
import com.store.utils.DataSourceUtils;
import com.store.web.servlet.CategoryServlet;

public class CategoryServiceImpl implements CategoryService {
	private CategoryDao cDao = (CategoryDao) BeanFactory.getBean("CategoryDao");
	private ProductDao pd = (ProductDao) BeanFactory.getBean("ProductDao");
	public List<Category> findAll() throws Exception {
		//��ȡ���������ļ�
		InputStream in = CategoryServlet.class.getClassLoader().getResourceAsStream("ehcache.xml");		
		//ͨ�������ļ���������
		CacheManager manage = CacheManager.create(in);
		Cache cache = manage.getCache("categoryCache");
		//��ȡ��������Ӧ������
		Element element = cache.get("clist");
		List<Category> clist = null;
		//�ж������Ƿ�Ϊ�գ���Ϊ�գ�������ݿ�ȡ��Ȼ����뻺��
		if(element == null)
		{
			//�����ݿ��в�ѯ������Ϣ
			clist = cDao.findAllCategory();
			cache.put(new Element("clist", clist));
			System.out.println("������û������");
		}
		else
		{
			clist = (List<Category>) element.getObjectValue(); 
			System.out.println("�򻺴���ȡ����");
		}
		
		return clist;
	}
	public void add(Category c) throws Exception {
		//����dao����
		cDao.add(c);
		//��ջ���
		//��ȡ���������ļ�
		InputStream in = CategoryServlet.class.getClassLoader().getResourceAsStream("ehcache.xml");		
		//ͨ�������ļ���������
		CacheManager manage = CacheManager.create(in);
		Cache cache = manage.getCache("categoryCache");
		cache.remove("clist");
	}
	public Category getById(String cid) throws Exception {
		//����dao��ѯ
		Category c = cDao.getById(cid);
		return c;
	}
	public void update(Category c) throws Exception {
		//����dao����
		cDao.update(c);
		//��ջ���
		//��ȡ���������ļ�
		InputStream in = CategoryServlet.class.getClassLoader().getResourceAsStream("ehcache.xml");		
		//ͨ�������ļ���������
		CacheManager manage = CacheManager.create(in);
		Cache cache = manage.getCache("categoryCache");
		cache.remove("clist");
	}
	
	public void delete(String cid) throws Exception {
		try{
		//��������
		DataSourceUtils.startTransaction();
		//���ø�����Ʒ����
		pd.updateByCid(cid);
		//����ɾ���������
		cDao.delete(cid);
		//��������
		DataSourceUtils.commitAndClose();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//�ع�����
			DataSourceUtils.rollbackAndClose();
			throw e;
		}
		//��ջ���
		//��ȡ���������ļ�
		InputStream in = CategoryServlet.class.getClassLoader().getResourceAsStream("ehcache.xml");		
		//ͨ�������ļ���������
		CacheManager manage = CacheManager.create(in);
		Cache cache = manage.getCache("categoryCache");
		cache.remove("clist");
	}

}
