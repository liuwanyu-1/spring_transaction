package lwy.study.spring.service;

import lwy.study.spring.vo.Account;

import java.util.List;

public interface AccountService {
    List<Account> getAll();
    Integer transfer(Account account, Account accin);
}
