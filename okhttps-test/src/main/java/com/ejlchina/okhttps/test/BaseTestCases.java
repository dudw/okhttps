package com.ejlchina.okhttps.test;

import com.ejlchina.data.Array;
import com.ejlchina.data.Mapper;
import com.ejlchina.data.TypeRef;
import com.ejlchina.okhttps.MsgConvertor;
import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

public abstract class BaseTestCases {

    MsgConvertor msgConvertor;

    public BaseTestCases(MsgConvertor msgConvertor) {
        this.msgConvertor = msgConvertor;
    }

    public void run() throws Exception {
        testToMapper();
        testToArray();
        testSerializeBean();
        testToBean();
        testToResult();
        testToList();
    }

    abstract String getUserObjectStr();

    abstract String getResultUserObjectStr();

    abstract String getUserListStr();

    void testToMapper() {
        String json = getUserObjectStr();
        InputStream in = new ByteArrayInputStream(json.getBytes());
        Mapper mapper = msgConvertor.toMapper(in, StandardCharsets.UTF_8);

        mapper.forEach((key, data) -> {
            if ("id".equals(key)) {
                Assert.assertEquals(1, data.toInt());
            }
            if ("name".equals(key)) {
                Assert.assertEquals("Jack", data.toString());
            }
        });

        Set<String> keys = mapper.keySet();
        Assert.assertEquals(2, keys.size());
        Assert.assertTrue(keys.contains("id"));
        Assert.assertTrue(keys.contains("name"));
        Assert.assertFalse(mapper.isEmpty());
        Assert.assertEquals(2, mapper.size());
        Assert.assertEquals(1, mapper.getInt("id"));
        Assert.assertFalse(mapper.has("age"));
        Assert.assertEquals(0, mapper.getInt("age"));
        Assert.assertEquals("Jack", mapper.getString("name"));
        System.out.println("case 1 passed!");
    }

    void testToArray() {
        String json = getUserListStr();
        InputStream in = new ByteArrayInputStream(json.getBytes());

        Array array = msgConvertor.toArray(in, StandardCharsets.UTF_8);

        array.forEach((index, data) -> {
            System.out.println("index = " + index);
            System.out.println("data = " + data.toMapper());
        });

        Assert.assertFalse(array.isEmpty());
        Assert.assertEquals(2, array.size());
        Mapper json1 = array.getMapper(0);
        Mapper json2 = array.getMapper(1);
        Assert.assertEquals(1, json1.getInt("id"));
        Assert.assertEquals("Jack", json1.getString("name"));
        Assert.assertEquals(2, json2.getInt("id"));
        Assert.assertEquals("Tom", json2.getString("name"));

        System.out.println("case 2 passed!");
    }

    void testSerializeBean() {
        byte[] data = msgConvertor.serialize(new User(1, "Jack"), StandardCharsets.UTF_8);
        String json = new String(data, StandardCharsets.UTF_8);
        Assert.assertEquals(getUserObjectStr(), json);
        System.out.println("case 3 passed!");
    }

    void testToBean() {
        String json = getUserObjectStr();
        InputStream in = new ByteArrayInputStream(json.getBytes());
        User user = msgConvertor.toBean(User.class, in, StandardCharsets.UTF_8);
        Assert.assertEquals(1, user.getId());
        Assert.assertEquals("Jack", user.getName());
        System.out.println("case 6 passed!");
    }

    void testToResult() {
        String json = getResultUserObjectStr();
        InputStream in = new ByteArrayInputStream(json.getBytes());
        Result<User> result = msgConvertor.toBean(new TypeRef<Result<User>>(){}.getType(), in, StandardCharsets.UTF_8);
        Assert.assertEquals(200, result.getCode());
        Assert.assertEquals("ok", result.getMsg());
        User user = result.getData();
        Assert.assertEquals(1, user.getId());
        Assert.assertEquals("Jack", user.getName());
        System.out.println("case 7 passed!");
    }

    void testToList() {
        String json = getUserListStr();
        InputStream in = new ByteArrayInputStream(json.getBytes());
        List<User> users = msgConvertor.toList(User.class, in, StandardCharsets.UTF_8);
        Assert.assertEquals(2, users.size());
        User u1 = users.get(0);
        User u2 = users.get(1);
        Assert.assertEquals(1, u1.getId());
        Assert.assertEquals("Jack", u1.getName());
        Assert.assertEquals(2, u2.getId());
        Assert.assertEquals("Tom", u2.getName());
        System.out.println("case 8 passed!");
    }

}
