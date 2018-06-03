package other;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.DigestUtils;

import java.util.Date;

/**
 * Create by Wang Mingzhen om 2018/4/25
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class springMD5Test {

    @Test
    public void test(){
        String a = "wmz";
        for (int i = 0; i < 3; i++) {
            String name = String.valueOf(new Date().getTime());
            System.out.println("-----"+i+"-----");
            System.out.println(name);
            System.out.println("md5DigestAsHex-" + DigestUtils.md5DigestAsHex(name.getBytes()));
            System.out.println("md5Digest     -"+ DigestUtils.md5DigestAsHex(DigestUtils.md5Digest(name.getBytes())));
            System.out.println("");
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void test1() {
        Integer a = 10;
        Long b = 10L;
        Number c = 10.00;

        System.out.println(a.equals(b));
        System.out.println(a==c);
        System.out.println(b==c);
        System.out.println(b.equals(c));
        System.out.println(c.equals(b));

        System.out.println("aa:");
        Long bb = 10L;
        System.out.println(b==bb);
        System.out.println(b.equals(bb));

        System.out.println("String");
        String x = new String("wmz");
        String y = new String("wmz");
        System.out.println(x==y);
        System.out.println(x.equals(y));

    }
}
