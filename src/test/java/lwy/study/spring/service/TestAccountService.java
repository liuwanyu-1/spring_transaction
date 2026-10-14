package lwy.study.spring.service;

import lwy.study.spring.vo.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class TestAccountService {

    @Autowired
    AccountService accountService;

    @Test
    public void testGetAll(){
        List<Account> all = accountService.getAll();
        System.out.println(all);
    }

    @Test
    public void testTransfer(){
        Account accout = new Account(1, "王五", -200.0f);
        Account accin = new Account(2, "赵六", 200.0f);
        Integer result = accountService.transfer(accout, accin);
        if (result > 1) {
            System.out.println("转账成功！");
        }else{
            System.out.println("转账失败");
        }
    }
}
