package com.minipika.test;

/*
 * Creates on 2019/11/13.
 */

import com.minipika.mapper.UserMapper;
import groovy.lang.GroovyClassLoader;

/**
 * @author lts
 * @email jiakesiws@gmail.com
 */
public class minipika_test {

  public static void main(String[] args) {
    new GroovyClassLoader(UserMapper.class.getClassLoader());
  }

}
