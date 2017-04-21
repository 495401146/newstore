package com.store.utils;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
/**
 * ���÷���ʵ�ֽ����
 * @author �γ���
 *
 */
public class BeanFactory {
	public static Object getBean(String id)
	{
		try {
			//��ȡxml�ĵ�����
			Document doc = new SAXReader().read(BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml"));
			//��ȡָ��id��Ԫ��
			Element element = (Element) doc.selectSingleNode("//bean[@id='"+id+"']");
			//��ȡ��Ӧclass���Ե�ֵ
			String value = element.attributeValue("class");
			System.out.println(value);
			//���÷���õ��˶���
			final Object obj = Class.forName(value).newInstance();
			//�Է��������д���
			if(id.endsWith("Service"))
			{
				Object proxyInstance = Proxy.newProxyInstance(obj.getClass().getClassLoader(),obj.getClass().getInterfaces() 
						, new InvocationHandler() {
							public Object invoke(Object proxy, Method method, Object[] args)
									throws Throwable {
								//�����ж�
								if("add".equals(method.getName())||"register".equals(method.getName()))
								{
									System.out.println("��ִ������Ӳ���"+args[0].getClass().getName());
									return method.invoke(obj, args);
								}
								return method.invoke(obj, args);
							}
						});
				return proxyInstance;
			}
			return obj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
