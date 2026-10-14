package lwy.study.spring.service.impl;

import lwy.study.spring.mapper.AccountMapper;
import lwy.study.spring.service.AccountService;
import lwy.study.spring.vo.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public List<Account> getAll() { return accountMapper.selectAll(); }

    @Override
    public Integer transfer(Account account, Account accin) {
        Integer result = accountMapper.updateById(account.getId(), account.getMoney().floatValue());
//        int i = 1 / 0;
        result = result + accountMapper.updateById(accin.getId(), accin.getMoney().floatValue());
        return result;
    }
}
