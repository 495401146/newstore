﻿<<<<<<< HEAD
项目概述
=======
﻿项目概述
>>>>>>> de782e76aa7f23e1535f62b57dd274ef3d9fa74f
    本项目是网上商城项目，其中包含前台和后台。前台主要负责用户的登录注册功能，浏览商品，商品分类，购买商品，订单和支付功能。
    后台主要负责用户管理，分类管理，商品管理以及订单管理。
------------------------------------------------------------------------
准备工作

    1.数据库和表
    
    2.项目
    
        包结构（导入依赖包）
        
          constant 包含常量的包
          
          dao    数据库访问层
          
          dao.impl 数据库访问层的实现
          
          domain  包含javabean的包
          
          service 业务逻辑层
          
          service.impl 业务逻辑层的实现
          
          web.servlet 所有的servlet
          
          web.filter 所有的过滤器
          
          utils     工具类
          
        需要的jar包
        
            驱动mysql-connector
            
            c3p0
            
            dbutils
            
            beanutils
            
            fileupload
            
            io
            
            logging
            
            jstl
            
            mail
            
            standard
            
        功能实现
        
          第一次提交：用户登录、注册、发送邮件激活、ajax实现检测用户名是否被占用
<<<<<<< HEAD
        
=======
       第二次提交：实现注册登陆时的验证码校验，商品分类（分离导航条），分页，ehcahe实现缓存机制，cookie记录浏览历史以及商品详情

>>>>>>> de782e76aa7f23e1535f62b57dd274ef3d9fa74f
	第三次提交：利用session实现加入购物车功能以及删除或者清空购物车，并实现生成订单的功能学会了将复杂问题分解，一下子就清晰了
