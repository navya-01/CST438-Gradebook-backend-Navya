package com.cst438;

import org.springframework.data.repository.CrudRepository;

public interface UserAliasRepository
        extends CrudRepository<UserAlias, Integer> {


    UserAlias findByAlias(String alias);
}

