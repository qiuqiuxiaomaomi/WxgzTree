package com.bonaparte.dao.mapper;

import com.bonaparte.entity.AccessToken;
import com.bonaparte.util.MyMapper;

public interface AccessTokenMapper extends MyMapper<AccessToken> {
    public AccessToken getValid(AccessToken query);
}