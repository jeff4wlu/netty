package rpc.version.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-15 14:07
 */
@Data
public class Person implements Serializable {
    private String name;
    private int age;
    private String phone;
}
