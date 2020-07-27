package org.jiakesimk.minipika.components.mql;

import org.jiakesimk.minipika.components.annotation.Batch;
import org.jiakesimk.minipika.components.annotation.Insert;
import org.jiakesimk.minipika.components.annotation.QueryOf;
import org.jiakesimk.minipika.components.annotation.Update;
import org.jiakesimk.minipika.components.cache.Cache;
import org.jiakesimk.minipika.components.jdbc.Executor;
import org.jiakesimk.minipika.components.logging.Log;
import org.jiakesimk.minipika.components.logging.LogFactory;
import org.jiakesimk.minipika.framework.annotations.Component;
import org.jiakesimk.minipika.framework.factory.Factorys;
import org.jiakesimk.minipika.framework.utils.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/* ************************************************************************
 *
 * Copyright (C) 2020 tiansheng All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ************************************************************************/

/*
 * Creates on 2020/6/14.
 */

/**
 * 动态SQL构建
 *
 * @author tiansheng
 * @email jiakesiws@gmail.com
 */
@SuppressWarnings("unchecked")
public class MqlCallback<T> extends BaseBuilder implements InvocationHandler {

  private final Class<?> virtual;

  @Component
  private Executor executor;

  private final Map<String, Configuration> configurations = Maps.newConcurrentHashMap();

  private static final Log LOG = LogFactory.getLog(MqlCallback.class);

  private static final String MQL_PROXY_CLASSNAME = "org.jiakesimk.minipika.components.proxy.$";

  public MqlCallback(Class<T> virtual) {
    super(MQL_PROXY_CLASSNAME.concat(virtual.getSimpleName()));
    this.virtual = virtual;
    initialization();
    buildEnd();
  }

  /**
   * 初始化
   */
  private void initialization() {
    Method[] methods = virtual.getDeclaredMethods();
    for (Method method : methods) {
      if (method.isAnnotationPresent(Update.class)) {
        Update update = method.getDeclaredAnnotation(Update.class);
        createMethod(method, update.value());
      }
      if (method.isAnnotationPresent(Insert.class)) {
        Insert insert = method.getDeclaredAnnotation(Insert.class);
        createMethod(method, insert.value());
      }
      if (method.isAnnotationPresent(QueryOf.class)) {
        QueryOf queryOf = method.getDeclaredAnnotation(QueryOf.class);
        createMethod(method, queryOf.value());
      }
      if (method.isAnnotationPresent(Batch.class)) {
        Batch batch = method.getDeclaredAnnotation(Batch.class);
        createMethod(method, batch.value());
      }
    }
  }

  public T bind() {
    return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
            new Class[]{virtual}, this);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
    String name = method.getName();
    if (!"toString".equals(name)) {
      Configuration configuration;
      Object[] objects = invoke(method, args);
      String key = UniqueId.genKey(name, method.getParameters());
      if (configurations.containsKey(key)) {
        configuration = configurations.get(key);
      } else {
        configuration = Factorys.forClass(Configuration.class,
                new Class[]{Method.class, Object.class}, method, instance);
        configuration.setConfigurationId(key);
        configurations.put(key, configuration);
      }
      return configuration.execute((String) objects[0], Arrays.toArray(objects[1]));
    }
    return method.invoke(proxy, method, args);
  }

  /**
   * 做条件查询
   *
   * @param method    查询方法
   * @param sql       sql语句
   * @param arguments 参数列表
   * @return 返回对象
   */
  private Object doSelectQuery(Method method, String sql, Object[] arguments) throws Exception {
    Class<?> returnType = method.getReturnType();
    if (returnType == List.class) {
      return executor.queryForList(sql, Class.forName(Lists.getGenericType(method)), arguments);
    }
    return executor.queryForObject(sql, returnType, arguments);
  }

}
