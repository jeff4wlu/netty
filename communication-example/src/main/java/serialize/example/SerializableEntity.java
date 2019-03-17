package serialize.example;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lu Weijian
 * @description 可被串行化的对象
 * @email lwj@kapark.cn
 * @date 2019-03-17 9:07
 */
@Data
public class SerializableEntity implements Serializable {
    private static final long serialVersionId = 3452345234L;
    private String name = "Jeff";
    public static String teacher = "George";
    private final int bornYear = 1990;
    public static final String mother = "Mary";
    private transient int age = 12;
    private transient List<String> friends;

    public SerializableEntity(){
        List<String> friends = new ArrayList<>();
        friends.add("Tom");
        friends.add("Larry");
        this.setFriends(friends);
    }
}
