package lwy.study.spring.mapper;

import lwy.study.spring.vo.Account;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AccountMapper {
    List<Account> selectAll();
    Integer updateAccount(Account account);
    Integer updateById(@Param("id") Integer id,
                       @Param("money") Float money);
}
