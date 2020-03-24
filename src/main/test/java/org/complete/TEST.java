package org.complete;

/*
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
 */

/*
 * Creates on 2020/3/24 0:17
 */

import com.alibaba.fastjson.JSONObject;
import org.complete.entity.User;
import org.complete.mapper.UserMapper;
import org.raniaia.poseidon.BeansManager;
import org.raniaia.poseidon.components.config.ConfigLoader;
import org.raniaia.poseidon.components.db.JdbcSupport;
import org.raniaia.poseidon.framework.provide.Valid;
import org.raniaia.poseidon.framework.sql.SqlMapper;

/**
 * @author tiansheng
 */
public class TEST {

    @Valid
    private JdbcSupport jdbc;

    UserMapper mapper = BeansManager.newInstance(UserMapper.class);

    public static void main(String[] args) {
        ConfigLoader.loadConfig("classpath:/compete/poseidon.jap");
        TEST test = BeansManager.newInstance(TEST.class);
        // 测试user对象insert
        test.userInsertByObject();
        // 测试user mapper映射器
        test.findUserById(1);
    }

    public void userInsertByObject(){
        jdbc.insert(new User("test1","test1","test1@poseidon.com"));
    }

    public void findUserById(int id){
        System.out.println(JSONObject.toJSONString(mapper.findUserById(id)));
    }

}
